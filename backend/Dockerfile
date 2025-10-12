# ==== 1) 빌드 스테이지: Gradle로 WAR 만들기 ====
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app

# 전체 복사 (간단하게 진행)
COPY . .

# 래퍼를 건너뛰고 gradle CLI로 바로 빌드 (wrapper 문제 우회)
RUN gradle --no-daemon clean war -x test --stacktrace --info


# ==== 2) 실행 스테이지: Tomcat에 WAR 배포 ====
FROM tomcat:9.0-jdk17-temurin

# 기본 ROOT 앱 제거(선택)
RUN rm -rf /usr/local/tomcat/webapps/*

# 우리 WAR을 ROOT로 배치 → 컨텍스트 경로 '/'
COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# 타임존(선택)
ENV TZ=Asia/Seoul

# healthcheck에서 쓸 curl 설치 (tomcat 이미지는 기본적으로 curl 없음)
RUN apt-get update && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# 헬스체크: 200/302 등 정상 응답이면 OK. 400+면 실패(-f)
HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
  CMD curl -fs http://localhost:8080/ || exit 1
