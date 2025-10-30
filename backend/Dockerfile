FROM tomcat:9.0-jdk17-temurin

# 기본 웹앱 제거(선택)
RUN rm -rf /usr/local/tomcat/webapps/*

# CI에서 만든 WAR 복사 (build context = backend)
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]

# 헬스체크는 /api/ping 준비 후 활성화 권장
# HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
#   CMD curl -fs http://localhost:8080/api/ping || exit 1
