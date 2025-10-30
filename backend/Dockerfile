# === 런타임만: Tomcat 컨테이너 ===
FROM tomcat:9.0-jdk17-temurin

# (선택) 기본 웹앱 제거 – 우리 ROOT만 배포할 거면 유지
RUN rm -rf /usr/local/tomcat/webapps/*

# CI에서 만든 WAR 복사 (build/libs/*.war 가 반드시 존재해야 함)
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# (선택) 타임존
ENV TZ=Asia/Seoul

# (선택) curl 설치가 꼭 필요하면 아래 유지, 아니면 삭제 가능
# RUN apt-get update && apt-get install -y --no-install-recommends curl \
#  && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# (선택) 헬스체크 – 지금은 404일 수 있으니 일단 주석 권장, /api/ping 생기면 그걸로 활성화
# HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
#   CMD curl -fs http://localhost:8080/api/ping || exit 1

CMD ["catalina.sh", "run"]
