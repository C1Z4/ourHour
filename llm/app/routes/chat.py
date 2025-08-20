from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.services.chatbot import chatbot_service
from app.models.database import SessionLocal, ChatHistory

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/chat")
def chat_endpoint(message: str, user_id: str, db: Session = Depends(get_db)):
    # AI 응답 생성
    response = chatbot_service.get_response(message, user_id)
    
    # 대화 이력 저장
    chat_record = ChatHistory(
        user_id=user_id,
        message=message,
        response=response
    )
    db.add(chat_record)
    db.commit()
    
    return {"response": response}
