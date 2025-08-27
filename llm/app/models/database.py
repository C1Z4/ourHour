from sqlalchemy import create_engine, Column, Integer, String, Text, DateTime, text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from datetime import datetime
import os
from google.cloud.sql.connector import Connector
import pymysql
from ..utils.secret_manager import load_secret_env

# 환경변수 로드
load_secret_env()

Base = declarative_base()

def get_cloud_sql_connection():
    """Cloud SQL Python Connector를 사용한 연결 함수"""
    def getconn():
        connector = Connector()
        conn = connector.connect(
            os.getenv("CLOUD_SQL_INSTANCE_CONNECTION_NAME"),
            "pymysql",
            user=os.getenv("CLOUD_SQL_USER"),
            password=os.getenv("CLOUD_SQL_PASSWORD"),
            db=os.getenv("CLOUD_SQL_DATABASE")
        )
        return conn
    return getconn

def get_database_engine():
    """데이터베이스 엔진을 지연 초기화로 생성"""    
    try:
        # Cloud SQL Python Connector 사용
        getconn = get_cloud_sql_connection()
        engine = create_engine(
            "mysql+pymysql://",
            creator=getconn,
            pool_pre_ping=True,
            pool_recycle=3600,  # 1시간마다 연결 재생성
            echo=False
        )
        
        # 연결 테스트
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        
        print("Cloud SQL 데이터베이스 연결 성공")
        return engine
        
    except Exception as e:
        print(f"Cloud SQL 연결 실패: {e}")
        
        # fallback: 기존 URL 방식 시도
        database_url = os.getenv("CHATBOT_DATABASE_URL")
        if database_url:
            try:
                print("기존 URL 방식으로 연결 시도...")
                engine = create_engine(database_url, pool_pre_ping=True)
                return engine
            except Exception as e2:
                print(f"기존 URL 연결도 실패: {e2}")
        
        print("데이터베이스 연결 불가 - 기능 비활성화")
        return None

def get_session_local():
    """데이터베이스 세션 팩토리 생성"""
    engine = get_database_engine()
    if engine is None:
        return None
    return sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 전역 변수로 지연 초기화
_engine = None
_SessionLocal = None

def get_db_session():
    """데이터베이스 세션 가져오기"""
    global _SessionLocal
    if _SessionLocal is None:
        _SessionLocal = get_session_local()
    
    if _SessionLocal is None:
        return None
    
    return _SessionLocal()

class ChatHistory(Base):
    __tablename__ = "chat_history"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(String(100), index=True)
    message = Column(Text)
    response = Column(Text)
    created_at = Column(DateTime, default=datetime.utcnow)

class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(100), unique=True, index=True)
    department = Column(String(100))
    created_at = Column(DateTime, default=datetime.utcnow)

def create_tables():
    """테이블 생성 (지연 실행)"""
    engine = get_database_engine()
    if engine is not None:
        try:
            Base.metadata.create_all(bind=engine)
            print("데이터베이스 테이블이 성공적으로 생성되었습니다.")
        except Exception as e:
            print(f"테이블 생성 실패: {e}")

# 필요시에만 테이블 생성 (앱 시작시 자동 호출하지 않음)
