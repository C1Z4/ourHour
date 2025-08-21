#!/bin/bash
# 사용방법:
# AWS: ./generate-env.sh test aws
# GCP: ./generate-env.sh test gcp

PROFILE=$1   # test 또는 release
PROVIDER=$2  # aws 또는 gcp

if [ -z "$PROFILE" ] || [ -z "$PROVIDER" ]; then
  echo "Usage: $0 <profile> <provider>"
  exit 1
fi

echo "Generating .env for PROFILE=$PROFILE, PROVIDER=$PROVIDER"

# ---------------- Profile별 포트 설정 ----------------
if [ "$PROFILE" = "test" ]; then
  BACKEND_PORT=8082
  DB_PORT=3309
  REDIS_PORT=6381
elif [ "$PROFILE" = "release" ]; then
  BACKEND_PORT=8080
  DB_PORT=3306
  REDIS_PORT=6380
else
  echo "Unknown PROFILE: $PROFILE"
  exit 1
fi

# ---------------- .env 파일 생성 ----------------
# 모든 값은 큰따옴표로 감싸서 안전하게 처리
cat > .env <<EOF
# ---------------- 공통 ----------------
SPRING_PROFILES_ACTIVE="$PROFILE"
BACKEND_PORT="$BACKEND_PORT"
DB_PORT="$DB_PORT"
REDIS_PORT="$REDIS_PORT"
EOF

# ---------------- Provider별 ----------------
if [ "$PROVIDER" = "aws" ]; then
  echo "# AWS variables" >> .env

  MYSQL_ROOT_PASSWORD=$(aws ssm get-parameter --name "/ourhour/db/root-password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
  MYSQL_DATABASE=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
  MYSQL_USER=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/username" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
  MYSQL_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
  REDIS_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/redis/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')

  cat >> .env <<EOF
BACKEND_IMAGE="ourhour-backend:$PROFILE"
MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD"
MYSQL_DATABASE="$MYSQL_DATABASE"
MYSQL_USER="$MYSQL_USER"
MYSQL_PASSWORD="$MYSQL_PASSWORD"
REDIS_PASSWORD="$REDIS_PASSWORD"
EOF
fi

if [ "$PROVIDER" = "gcp" ]; then
  echo "# GCP variables" >> .env

  CHATBOT_DATABASE_URL=$(gcloud secrets versions access latest --secret="ourhour-${PROFILE}-chatbot-db" | tr -d '\n')
  OURHOUR_API_URL=$(gcloud secrets versions access latest --secret="ourhour-${PROFILE}-ourhour-api-url" | tr -d '\n')
  PYTHON_SERVER_URL=$(gcloud secrets versions access latest --secret="ourhour-python-server-url" | tr -d '\n')
  OPENAI_API_KEY=$(gcloud secrets versions access latest --secret="ourhour-llm-openai-api-key" | tr -d '\n')
  PINECONE_API_KEY=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-api-key" | tr -d '\n')
  PINECONE_INDEX_NAME=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-index-name" | tr -d '\n')
  PINECONE_ENVIRONMENT=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-environment" | tr -d '\n')
  JWT_SECRET=$(gcloud secrets versions access latest --secret="ourhour-llm-jwt-secret" | tr -d '\n')

  cat >> .env <<EOF
CHATBOT_IMAGE="ourhour-chatbot:$PROFILE"
CHATBOT_DATABASE_URL="$CHATBOT_DATABASE_URL"
OURHOUR_API_URL="$OURHOUR_API_URL"
PYTHON_SERVER_URL="$PYTHON_SERVER_URL"
OPENAI_API_KEY="$OPENAI_API_KEY"
PINECONE_API_KEY="$PINECONE_API_KEY"
PINECONE_INDEX_NAME="$PINECONE_INDEX_NAME"
PINECONE_ENVIRONMENT="$PINECONE_ENVIRONMENT"
JWT_SECRET="$JWT_SECRET"
EOF
fi

echo ".env generated successfully"
