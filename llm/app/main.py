from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from dotenv import load_dotenv
import os
from .routes.chat import router as chat_router

load_dotenv()

app = FastAPI(
    title="OURHOUR AI 챗봇",
    description="JWT 인증이 필요한 AI 챗봇 서비스",
    version="1.0.0"
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000", "https://www.ourhour.cloud", "https://our-hour-test.vercel.app"],  # 프론트엔드 개발 서버 주소
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(chat_router, prefix="/api", tags=["Chat"])

@app.get("/")
def read_root():
    return {"message": "OURHOUR AI 챗봇 서버가 실행 중입니다!"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
