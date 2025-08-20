# Build Stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY .. .

# Runtime Stage - 배포용
FROM openjdk:17-jdk-slim AS release
WORKDIR /app

# 필수 패키지 설치
RUN apt-get update && apt-get install -y procps curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# JVM 옵션 적용
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

CMD ["java", "-jar", "app.jar"]

# Runtime Stage - 개발용
FROM gradle:7.6.2-jdk17 AS dev
WORKDIR /app
EXPOSE 8080
CMD ["./gradlew", "bootRun"]