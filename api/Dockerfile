# Multi-stage build for GoDutch Spring Boot (web module)
# 1. Build stage
FROM maven:3.9-eclipse-temurin-22 AS build

WORKDIR /workspace
# Copy only pom files first for efficient layer caching
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY database/pom.xml database/pom.xml
COPY app/pom.xml app/pom.xml
COPY web/pom.xml web/pom.xml

# Go offline (optional best effort)
RUN mvn -q -e -DskipTests dependency:go-offline || true

# Copy sources
COPY core/src core/src
COPY database/src database/src
COPY app/src app/src
COPY web/src web/src

# Build (skip tests for image build speed; adjust if you want tests)
RUN mvn -q -DskipTests clean package

# 2. Runtime stage
FROM eclipse-temurin:22-jre-alpine AS runtime

ENV TZ=Asia/Tokyo \
    APP_HOME=/app \
    JAVA_OPTS="-Dserver.port=\$PORT"

# Add non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR ${APP_HOME}

# ログディレクトリ作成（logback が参照する logs/ 配下）
RUN mkdir -p logs && chown spring:spring logs

# Copy fat jar from build stage (web module is the Spring Boot entry point)
COPY --from=build /workspace/web/target/*.jar app.jar

# エントリポイントスクリプトを配置（DB URL から SPRING_DATASOURCE_* を導出）
COPY docker-heroku-entrypoint.sh /usr/local/bin/docker-heroku-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-heroku-entrypoint.sh

# Expose port (Spring Boot default 8080)
EXPOSE 8080

# Healthcheck (basic) - optional
HEALTHCHECK --interval=30s --timeout=3s --retries=5 CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

USER spring

ENTRYPOINT ["/usr/local/bin/docker-heroku-entrypoint.sh"]
