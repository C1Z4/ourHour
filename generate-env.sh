#!/bin/bash
# 사용방법:
# ./generate-env.sh test
# ./generate-env.sh release

PROFILE=$1   # test 또는 release

if [ -z "$PROFILE" ]; then
  echo "Usage: $0 <profile>"
  exit 1
fi

echo "Generating .env for PROFILE=$PROFILE"

# ---------------- Profile별 포트 설정 ----------------
if [ "$PROFILE" = "test" ]; then
  BACKEND_PORT=8081
  DB_PORT=3307
  REDIS_PORT=6379
elif [ "$PROFILE" = "release" ]; then
  BACKEND_PORT=8080
  DB_PORT=3308
  REDIS_PORT=6380
else
  echo "Unknown PROFILE: $PROFILE"
  exit 1
fi

# ---------------- .env 파일 생성 ----------------
cat > .env <<EOF
# ---------------- 공통 ----------------
SPRING_PROFILES_ACTIVE=$PROFILE
BACKEND_PORT=$BACKEND_PORT
DB_PORT=$DB_PORT
REDIS_PORT=$REDIS_PORT
EOF

# ---------------- AWS Secrets Manager ----------------
echo "# AWS variables" >> .env

MYSQL_ROOT_PASSWORD=$(aws ssm get-parameter --name "/ourhour/db/root-password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
MYSQL_DATABASE=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
MYSQL_USER=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/username" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
MYSQL_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/db/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')
REDIS_HOST=$(aws ssm get-parameter --name "/ourhour/$PROFILE/redis/host" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
REDIS_PORT=$(aws ssm get-parameter --name "/ourhour/$PROFILE/redis/port" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2)
REDIS_PASSWORD=$(aws ssm get-parameter --name "/ourhour/$PROFILE/redis/password" --with-decryption --query 'Parameter.Value' --output text --region ap-northeast-2 | tr -d '\n')

cat >> .env <<EOF
BACKEND_IMAGE=ourhour-backend:$PROFILE
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
MYSQL_DATABASE=$MYSQL_DATABASE
MYSQL_USER=$MYSQL_USER
MYSQL_PASSWORD=$MYSQL_PASSWORD
REDIS_HOST=$REDIS_HOST
REDIS_PORT=$REDIS_PORT
REDIS_PASSWORD=$REDIS_PASSWORD
EOF

echo ".env generated successfully"

