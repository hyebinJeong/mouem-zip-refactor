# backend/Dockerfile
FROM tomcat:9.0-jdk17-temurin

# 기본 앱 제거(선택)
RUN rm -rf /usr/local/tomcat/webapps/*

# CI에서 생성된 WAR 복사 (context가 backend 이므로 경로 그대로 사용)
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# (선택) 타임존
ENV TZ=Asia/Seoul

EXPOSE 8080
CMD ["catalina.sh", "run"]

# (선택) 헬스체크는 /api/ping 준비되면 아래 주석 해제
# HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
#   CMD curl -fs http://localhost:8080/api/ping || exit 1
