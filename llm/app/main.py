from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from dotenv import load_dotenv
from typing import Optional
import os
from .services.chatbot import chatbot_service
from .services.pinecone_service import pinecone_service

load_dotenv()

app = FastAPI(title="OURHOUR AI 챗봇")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],  # 프론트엔드 개발 서버 주소
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers=["*"],
)

class ChatRequest(BaseModel):
    message: str
    user_id: Optional[str] = None
    auth_token: Optional[str] = None  # 사용자의 JWT 토큰

class ChatResponse(BaseModel):
    response: str

@app.get("/")
def read_root():
    return {"message": "OURHOUR AI 챗봇 서버가 실행 중입니다!"}

@app.post("/chat", response_model=ChatResponse)
async def chat_with_bot(request: ChatRequest):
    try:
        response = await chatbot_service.get_response(request.message, request.user_id, request.auth_token)
        return ChatResponse(response=response)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"챗봇 오류: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
