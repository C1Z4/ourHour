#!/bin/bash
# 사용방법 (테스트 환경 기준)
# AWS: ./generate-env.sh test aws
# GCP: ./generate-env.sh test gcp

# test 또는 release
PROFILE=$1
# aws 또는 gcp
PROVIDER=$2

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

cat > .env <<EOF
# ---------------- 공통 ----------------
SPRING_PROFILES_ACTIVE=$PROFILE
BACKEND_PORT=$BACKEND_PORT
DB_PORT=$DB_PORT
REDIS_PORT=$REDIS_PORT
EOF

# ---------------- Provider별 ----------------
if [ "$PROVIDER" = "aws" ]; then
  echo "# AWS variables" >> .env
  cat >> .env <<EOF
BACKEND_IMAGE=ourhour-backend:$PROFILE
MYSQL_ROOT_PASSWORD=$(aws ssm get-parameter --name "/ourhour/db/root-password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
MYSQL_DATABASE=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
MYSQL_USER=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/username" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
MYSQL_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
REDIS_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/redis/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
EOF
fi

if [ "$PROVIDER" = "gcp" ]; then
  echo "# GCP variables" >> .env
  cat >> .env <<EOF
CHATBOT_IMAGE=ourhour-chatbot:$PROFILE
CHATBOT_DATABASE_URL=$(gcloud secrets versions access latest --secret="ourhour-${PROFILE}-chatbot-db")
OURHOUR_API_URL=$(gcloud secrets versions access latest --secret="ourhour-${PROFILE}-ourhour-api-url")
PYTHON_SERVER_URL=$(gcloud secrets versions access latest --secret="ourhour-python-server-url")
OPENAI_API_KEY=$(gcloud secrets versions access latest --secret="ourhour-llm-openai-api-key")
PINECONE_API_KEY=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-api-key")
PINECONE_INDEX_NAME=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-index-name")
PINECONE_ENVIRONMENT=$(gcloud secrets versions access latest --secret="ourhour-llm-pinecone-environment")
JWT_SECRET=$(gcloud secrets versions access latest --secret="ourhour-llm-jwt-secret")
EOF
fi

echo ".env generated successfully"
