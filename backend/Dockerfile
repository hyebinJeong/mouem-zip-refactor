# ==== 1) 빌드 스테이지: Gradle로 WAR 만들기 ====
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app
COPY . .
# 테스트는 일단 건너뛰고 WAR 생성
RUN gradle clean war -x test

# ==== 2) 실행 스테이지: Tomcat에 WAR 배포 ====
FROM tomcat:9.0-jdk17-temurin

# 기본 ROOT 앱 제거(선택이지만 깔끔함)
RUN rm -rf /usr/local/tomcat/webapps/*

# 우리 WAR을 ROOT로 배치 → 컨텍스트 경로 '/'
COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# 타임존(선택)
ENV TZ=Asia/Seoul

EXPOSE 8080

# 헬스체크(원하면 엔드포인트 바꿔도 됨)
HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
  CMD curl -fs http://localhost:8080/ || exit 1
