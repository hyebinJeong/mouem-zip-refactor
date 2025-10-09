# Gradle 빌드용 이미지
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 복사
COPY . .

# Gradle 캐시 활용 안 함 (확실히 새로 빌드)
RUN gradle clean build -x test

# 실행용 JDK 이미지를 별도로 지정 (Slim)
FROM openjdk:17-jdk-slim

#  컨테이너 내 작업 디렉토리
WORKDIR /app

# builder에서 결과물만 복사 (WAR or JAR 선택)
# 만약 Spring Legacy WAR 프로젝트면 build/libs/*.war 로 변경
COPY --from=builder /app/build/libs/*.war app.war

# 포트 설정 (Tomcat 내장 시 8080)
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.war"]
