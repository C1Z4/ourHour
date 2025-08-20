# Single stage build for development
FROM python:3.9-slim

# Install runtime dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN useradd --create-home --shell /bin/bash app

WORKDIR /app

# Copy and install Python dependencies
COPY requirements.txt .

# pip 캐시 경로 설정
ENV PIP_CACHE_DIR=/root/.cache/pip

# 의존성 설치 (캐시 활용)
RUN pip install --upgrade pip \
    && pip install -r requirements.txt

# Copy application code
COPY --chown=app:app . .

# Set environment variables
ENV PYTHONPATH=/app
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

# Switch to non-root user
USER app

EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/ || exit 1

# Use uvicorn for development (gunicorn for production)
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]