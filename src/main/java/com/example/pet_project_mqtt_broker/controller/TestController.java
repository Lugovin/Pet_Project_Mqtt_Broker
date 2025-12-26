package com.example.pet_project_mqtt_broker.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/simulate-sensor")
    public Map<String, Object> simulateSensor() {
        Map<String, Object> response = new HashMap<>();

        // Симуляция данных датчика
        double temperature = 20.0 + Math.random() * 10; // 20-30°C
        double humidity = 50.0 + Math.random() * 20;    // 50-70%

        // Здесь можно имитировать получение MQTT сообщения
        response.put("status", "success");
        response.put("message", "Sensor data simulated");
        response.put("temperature", String.format("%.1f°C", temperature));
        response.put("humidity", String.format("%.1f%%", humidity));
        response.put("suggested_urls", new String[] {
                "http://localhost:8081/api/telegram-bot/temperature",
                "http://localhost:8081/api/telegram-bot/humidity",
                "http://localhost:8081/api/telegram-bot/alerts"
        });

        return response;
    }
}