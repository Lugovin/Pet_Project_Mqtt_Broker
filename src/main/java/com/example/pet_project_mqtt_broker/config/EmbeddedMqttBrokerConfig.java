package com.example.pet_project_mqtt_broker.config;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class EmbeddedMqttBrokerConfig {

    private Server mqttBroker;

    @PostConstruct
    public void startBroker() throws Exception {
        // Создаем конфигурацию прямо в методе
        Properties properties = new Properties();
        properties.setProperty("host", "0.0.0.0");
        properties.setProperty("port", "1883");
        properties.setProperty("allow_anonymous", "true");
        properties.setProperty("persistent_store", "false");
        properties.setProperty("websocket_port", "8083");
        properties.setProperty("max_connections", "100");
        properties.setProperty("message_size_limit", "65536");

        IConfig config = new MemoryConfig(properties);
        mqttBroker = new Server();
        mqttBroker.startServer(config);

        System.out.println("=".repeat(60));
        System.out.println("✅ ВСТРОЕННЫЙ MQTT БРОКЕР ЗАПУЩЕН!");
        System.out.println("📍 MQTT порт: 1883");
        System.out.println("📍 WebSocket порт: 8083");
        System.out.println("📍 Доступ: tcp://localhost:1883");
        System.out.println("📍 WebSocket: ws://localhost:8083");
        System.out.println("=".repeat(60));
    }

    @PreDestroy
    public void stopBroker() {
        if (mqttBroker != null) {
            mqttBroker.stopServer();
            System.out.println("✅ MQTT брокер остановлен");
        }
    }
}