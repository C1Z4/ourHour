def create_final_prompt(task_context: str, task_guidelines: str, user_message: str) -> str:
    """핸들러에서 사용할 최종 프롬프트를 생성합니다."""
    
    final_prompt = f"""
[배경 정보]
{task_context}

[작업 지침]
{task_guidelines}

---
위 배경 정보와 작업 지침을 반드시 참고하여 아래 사용자 질문에 답변하세요.
---

[사용자 질문]
{user_message}
"""
    return final_prompt