# Multi-stage build для уменьшения размера образа
# Этап 1: Сборка приложения
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Создание runtime образа
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем curl для healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/target/PET_project_MQTT_broker-0.0.1-SNAPSHOT.jar app.jar

# Создаем директорию для логов
RUN mkdir -p /app/logs

# Переменные окружения
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8081
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV LOGGING_FILE_PATH=/app/logs

# Открываем порты
EXPOSE 8081 1883

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -Dlogging.file.path=${LOGGING_FILE_PATH} -jar app.jar"]