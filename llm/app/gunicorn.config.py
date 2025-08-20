import multiprocessing

workers = multiprocessing.cpu_count() * 2 + 1

# Gunicorn에서 사용할 워커 클래스 (비동기 ASGI 서버로 Uvicorn 사용)
worker_class = "uvicorn.workers.UvicornWorker"

# 실행할 WSGI 애플리케이션 (main.py의 app 객체)
wsgi_app = "main:app"

# 서버 바인딩 주소 및 포트 (모든 네트워크 인터페이스에서 8000포트로 수신)
bind = "0.0.0.0:8000"

# 로깅 레벨 설정: debug, info(기본값), warning, error, critical
loglevel = "info"

# 각 워커가 처리할 최대 요청 수
max_requests = 500