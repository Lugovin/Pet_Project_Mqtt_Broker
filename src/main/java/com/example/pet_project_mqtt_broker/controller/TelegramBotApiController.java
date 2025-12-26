package com.example.pet_project_mqtt_broker.controller;


import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/telegram-bot")
public class TelegramBotApiController {

    // Здесь будем хранить последние данные от датчиков
    private final Map<String, SensorData> lastSensorData = new HashMap<>();

    @PostMapping("/register-sensor")
    public Map<String, Object> registerSensor(
            @RequestParam String sensorId,
            @RequestParam String sensorType) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("sensorId", sensorId);
        response.put("type", sensorType);
        response.put("timestamp", LocalDateTime.now().toString());

        System.out.println("✅ Sensor registered: " + sensorId + " (" + sensorType + ")");

        return response;
    }

    // Telegram бот может вызвать этот endpoint для получения данных
    @GetMapping("/sensor-data")
    public Map<String, Object> getSensorData(
            @RequestParam(required = false) String sensorId,
            @RequestParam(defaultValue = "temperature") String dataType) {

        Map<String, Object> response = new HashMap<>();

        if (sensorId == null || sensorId.isEmpty()) {
            // Возвращаем данные всех датчиков
            response.put("status", "success");
            response.put("data", lastSensorData);
            response.put("count", lastSensorData.size());
        } else {
            // Возвращаем данные конкретного датчика
            SensorData data = lastSensorData.get(sensorId);
            if (data != null) {
                response.put("status", "success");
                response.put("sensorId", sensorId);
                response.put("data", data);
            } else {
                response.put("status", "error");
                response.put("message", "Sensor not found: " + sensorId);
            }
        }

        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    // Получение последней температуры
    @GetMapping("/temperature")
    public Map<String, Object> getTemperature(
            @RequestParam(required = false) String sensorId) {

        Map<String, Object> response = new HashMap<>();

        if (sensorId == null) {
            // Найти все температурные датчики
            List<Map<String, Object>> temps = new ArrayList<>();
            for (Map.Entry<String, SensorData> entry : lastSensorData.entrySet()) {
                if (entry.getValue().getTemperature() != null) {
                    Map<String, Object> tempData = new HashMap<>();
                    tempData.put("sensorId", entry.getKey());
                    tempData.put("temperature", entry.getValue().getTemperature());
                    tempData.put("timestamp", entry.getValue().getTimestamp());
                    temps.add(tempData);
                }
            }
            response.put("temperatures", temps);
        } else {
            SensorData data = lastSensorData.get(sensorId);
            if (data != null && data.getTemperature() != null) {
                response.put("sensorId", sensorId);
                response.put("temperature", data.getTemperature());
                response.put("timestamp", data.getTimestamp());
            } else {
                response.put("status", "error");
                response.put("message", "Temperature data not available for sensor: " + sensorId);
            }
        }

        response.put("status", "success");
        return response;
    }

    // Получение последней влажности
    @GetMapping("/humidity")
    public Map<String, Object> getHumidity(
            @RequestParam(required = false) String sensorId) {

        Map<String, Object> response = new HashMap<>();

        if (sensorId == null) {
            // Найти все датчики влажности
            List<Map<String, Object>> hums = new ArrayList<>();
            for (Map.Entry<String, SensorData> entry : lastSensorData.entrySet()) {
                if (entry.getValue().getHumidity() != null) {
                    Map<String, Object> humData = new HashMap<>();
                    humData.put("sensorId", entry.getKey());
                    humData.put("humidity", entry.getValue().getHumidity());
                    humData.put("timestamp", entry.getValue().getTimestamp());
                    hums.add(humData);
                }
            }
            response.put("humidities", hums);
        } else {
            SensorData data = lastSensorData.get(sensorId);
            if (data != null && data.getHumidity() != null) {
                response.put("sensorId", sensorId);
                response.put("humidity", data.getHumidity());
                response.put("timestamp", data.getTimestamp());
            } else {
                response.put("status", "error");
                response.put("message", "Humidity data not available for sensor: " + sensorId);
            }
        }

        response.put("status", "success");
        return response;
    }

    // Получение критических значений (алерты)
    @GetMapping("/alerts")
    public Map<String, Object> getAlerts() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> alerts = new ArrayList<>();

        for (Map.Entry<String, SensorData> entry : lastSensorData.entrySet()) {
            SensorData data = entry.getValue();

            // Проверяем на критические значения
            if (data.getTemperature() != null) {
                double temp = data.getTemperature();
                if (temp > 30 || temp < 10) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("sensorId", entry.getKey());
                    alert.put("type", "temperature");
                    alert.put("value", temp);
                    alert.put("status", temp > 30 ? "HIGH" : "LOW");
                    alert.put("timestamp", data.getTimestamp());
                    alerts.add(alert);
                }
            }
        }

        response.put("status", "success");
        response.put("alerts", alerts);
        response.put("count", alerts.size());

        return response;
    }

    // Получение статистики
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "success");
        response.put("totalSensors", lastSensorData.size());
        response.put("lastUpdate", LocalDateTime.now().toString());
        response.put("endpoints", Arrays.asList(
                "/api/telegram-bot/sensor-data",
                "/api/telegram-bot/temperature",
                "/api/telegram-bot/humidity",
                "/api/telegram-bot/alerts",
                "/api/telegram-bot/stats"
        ));

        return response;
    }

    // Внутренний метод для обновления данных от датчиков
    public void updateSensorData(String sensorId, Double temperature, Double humidity) {
        SensorData data = lastSensorData.getOrDefault(sensorId, new SensorData(sensorId));
        data.update(temperature, humidity);
        lastSensorData.put(sensorId, data);
    }

    // Вспомогательный класс для хранения данных датчика
    private static class SensorData {
        private final String sensorId;
        private Double temperature;
        private Double humidity;
        private LocalDateTime timestamp;

        public SensorData(String sensorId) {
            this.sensorId = sensorId;
            this.timestamp = LocalDateTime.now();
        }

        public void update(Double temperature, Double humidity) {
            if (temperature != null) this.temperature = temperature;
            if (humidity != null) this.humidity = humidity;
            this.timestamp = LocalDateTime.now();
        }

        // Getters
        public String getSensorId() {
            return sensorId;
        }

        public Double getTemperature() {
            return temperature;
        }

        public Double getHumidity() {
            return humidity;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
