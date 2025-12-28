

# Multi-stage build для уменьшения размера образа
# Этап 1: Сборка приложения
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем pom.xml для кеширования зависимостей
COPY pom.xml .
# Скачиваем зависимости (кешируется если pom.xml не менялся)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение (создаем JAR с зависимостями)
RUN mvn clean package -DskipTests

# Этап 2: Создание runtime образа
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="your-email@example.com"
LABEL version="1.0"
LABEL description="Spring Boot MQTT Broker with Telegram Bot"

# Устанавливаем curl для healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Создаем пользователя для безопасности (не root)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем собранный JAR из предыдущего этапа
# Обратите внимание на имя JAR - измените если нужно
COPY --from=builder --chown=spring:spring /app/target/PET_project_MQTT_broker-0.0.1-SNAPSHOT.jar app.jar

# Создаем директории для логов
RUN mkdir -p /app/logs && chown spring:spring /app/logs

# Переменные окружения
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8081
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV LOGGING_FILE_PATH=/app/logs

# Открываем порты:
# 8081 - HTTP API
# 1883 - MQTT (стандартный порт для MQTT)
# 8883 - MQTT over SSL (опционально)
EXPOSE 8081 1883

# Healthcheck (проверяем доступность HTTP API)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -Dlogging.file.path=${LOGGING_FILE_PATH} -jar app.jar"]