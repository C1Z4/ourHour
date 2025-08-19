from fastapi import FastAPI
from pydantic import BaseModel
from .pinecone_service import pinecone_service

# FastAPI 앱 생성
app = FastAPI()

# --- API 엔드포인트 정의 ---
@app.get("/")
def read_root():
    return {"message": "LLM Gunicorn App is running!"}
