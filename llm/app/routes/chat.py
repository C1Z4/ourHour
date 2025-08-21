from fastapi import APIRouter, Depends, HTTPException, Header
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import Optional
import jwt
import os
import base64
from dotenv import load_dotenv
from app.services.chatbot import chatbot_service
from app.models.database import SessionLocal, ChatHistory

# .env 파일 로드 확인
load_dotenv()


router = APIRouter()
security = HTTPBearer(description="JWT Access Token")


class ChatRequest(BaseModel):
    org_id: int
    message: str


class ChatResponse(BaseModel):
    response: str


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def extract_and_verify_jwt_token(credentials: HTTPAuthorizationCredentials = Depends(security)) -> dict:
    """JWT 토큰 추출 및 검증"""
    token = credentials.credentials
    
    try:
        # JWT 토큰 검증 및 디코딩 (백엔드와 동일한 설정 사용)
        jwt_secret_base64 = os.getenv("JWT_SECRET")
        jwt_algorithm = "HS512"  # 백엔드에서 실제 사용하는 알고리즘
        
        if not jwt_secret_base64:
            raise HTTPException(status_code=500, detail="JWT secret key not configured")
        
        # 백엔드와 동일하게 Base64 디코딩해서 사용
        import base64
        jwt_secret = base64.b64decode(jwt_secret_base64)
        
        payload = jwt.decode(token, jwt_secret, algorithms=[jwt_algorithm])
        return {"token": token, "payload": payload}
        
    except jwt.ExpiredSignatureError as e:
        print(f"Token expired: {str(e)}")
        raise HTTPException(status_code=401, detail="Token expired")
    except jwt.InvalidTokenError as e:
        print(f"Invalid token error: {str(e)}")
        raise HTTPException(status_code=401, detail=f"Invalid token: {str(e)}")
    except Exception as e:
        print(f"Unexpected error: {str(e)}")
        raise HTTPException(status_code=401, detail=f"Token validation error: {str(e)}")


@router.post("/chat", response_model=ChatResponse)
async def chat_endpoint(
    request: ChatRequest,
    db: Session = Depends(get_db),
    auth_data: dict = Depends(extract_and_verify_jwt_token)
):
    """인증된 사용자의 채팅 API 엔드포인트"""
    try:
        # JWT 토큰에서 사용자 정보 추출
        token = auth_data["token"]
        payload = auth_data["payload"]
        
        # 백엔드 JWT 구조에 맞춰 사용자 정보 추출
        user_id = payload.get("userId")
        email = payload.get("email")
        org_authority_list = payload.get("orgAuthorityList", [])  # 올바른 키명
        
        if not user_id:
            raise HTTPException(status_code=401, detail="User ID not found in token")
        
        # 요청한 orgId에 해당하는 memberId 찾기
        member_id = None
        for org_auth in org_authority_list:
            if org_auth.get("orgId") == request.org_id:
                member_id = org_auth.get("memberId")
                break
        
        if not member_id:
            raise HTTPException(status_code=403, detail="User not authorized for this organization")
        
        # AI 응답 생성 (orgId와 JWT 토큰 전달)
        response = await chatbot_service.get_response(
            user_message=request.message,
            member_id=str(member_id),
            org_id=request.org_id,
            auth_token=token
        )
        
        # 대화 이력 저장
        chat_record = ChatHistory(
            user_id=str(member_id),
            message=request.message,
            response=response
        )
        db.add(chat_record)
        db.commit()
        
        return ChatResponse(response=response)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
