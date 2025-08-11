# Build Stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .

# Runtime Stage - 배포용
FROM openjdk:17-jdk-slim AS release
WORKDIR /app
COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

# Runtime Stage - 개발용
FROM gradle:7.6.2-jdk17 AS dev
WORKDIR /app
EXPOSE 8080
CMD ["./gradlew", "bootRun"]