FROM tomcat:9.0-jdk17-temurin

# 기본 ROOT 앱 제거
RUN rm -rf /usr/local/tomcat/webapps/*

# backend/build/libs 안에 있는 war를 ROOT로 배포
# (build context = backend 이기 때문에 이 경로 기준)
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
