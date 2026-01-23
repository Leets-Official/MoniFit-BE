# build stage (Gradle)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# 1) Gradle wrapper & 설정 먼저 복사 (캐시 효율)
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* gradle.properties* ./

# gradlew 실행권한
RUN chmod +x gradlew

# (선택) 의존성 먼저 받아 캐시 태우기
RUN ./gradlew --no-daemon dependencies || true

# 2) 소스 복사 후 빌드
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
