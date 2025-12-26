package com.example.pet_project_mqtt_broker.controller;



import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotApiController {

    private final Map<String, SensorData> sensorData = new HashMap<>();

    // Инициализация тестовых данных
    public BotApiController() {
        // Тестовые данные для демонстрации
        sensorData.put("room1", new SensorData("room1", "Гостиная", 22.5, 65.0));
        sensorData.put("room2", new SensorData("room2", "Спальня", 21.0, 60.0));
        sensorData.put("kitchen", new SensorData("kitchen", "Кухня", 24.0, 55.0));
    }

    @GetMapping("/temperature")
    public Map<String, Object> getTemperature(@RequestParam(required = false) String sensorId) {
        Map<String, Object> response = new HashMap<>();

        if (sensorId == null || sensorId.isEmpty()) {
            List<Map<String, Object>> temps = new ArrayList<>();

            for (SensorData data : sensorData.values()) {
                if (data.temperature != null) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("sensorId", data.id);
                    temp.put("location", data.location);
                    temp.put("temperature", data.temperature);
                    temp.put("timestamp", data.timestamp.toString());
                    temps.add(temp);
                }
            }

            response.put("status", "success");
            response.put("temperatures", temps);
            response.put("count", temps.size());

        } else {
            SensorData data = sensorData.get(sensorId);
            if (data != null && data.temperature != null) {
                response.put("status", "success");
                response.put("sensorId", data.id);
                response.put("location", data.location);
                response.put("temperature", data.temperature);
                response.put("timestamp", data.timestamp.toString());
            } else {
                response.put("status", "not_found");
                response.put("message", "Sensor not found: " + sensorId);
            }
        }

        return response;
    }

    @GetMapping("/humidity")
    public Map<String, Object> getHumidity(@RequestParam(required = false) String sensorId) {
        Map<String, Object> response = new HashMap<>();

        if (sensorId == null || sensorId.isEmpty()) {
            List<Map<String, Object>> hums = new ArrayList<>();

            for (SensorData data : sensorData.values()) {
                if (data.humidity != null) {
                    Map<String, Object> hum = new HashMap<>();
                    hum.put("sensorId", data.id);
                    hum.put("location", data.location);
                    hum.put("humidity", data.humidity);
                    hum.put("timestamp", data.timestamp.toString());
                    hums.add(hum);
                }
            }

            response.put("status", "success");
            response.put("humidities", hums);
            response.put("count", hums.size());

        } else {
            SensorData data = sensorData.get(sensorId);
            if (data != null && data.humidity != null) {
                response.put("status", "success");
                response.put("sensorId", data.id);
                response.put("location", data.location);
                response.put("humidity", data.humidity);
                response.put("timestamp", data.timestamp.toString());
            } else {
                response.put("status", "not_found");
                response.put("message", "Sensor not found: " + sensorId);
            }
        }

        return response;
    }

    @GetMapping("/alerts")
    public Map<String, Object> getAlerts() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> alerts = new ArrayList<>();

        for (SensorData data : sensorData.values()) {
            if (data.temperature != null) {
                if (data.temperature > 30) {
                    alerts.add(createAlert(data, "temperature", "HIGH", data.temperature));
                } else if (data.temperature < 10) {
                    alerts.add(createAlert(data, "temperature", "LOW", data.temperature));
                }
            }

            if (data.humidity != null && data.humidity > 80) {
                alerts.add(createAlert(data, "humidity", "HIGH", data.humidity));
            }
        }

        response.put("status", "success");
        response.put("alerts", alerts);
        response.put("count", alerts.size());

        return response;
    }

    @GetMapping("/sensor")
    public Map<String, Object> getSensorData(@RequestParam String sensorId) {
        Map<String, Object> response = new HashMap<>();
        SensorData data = sensorData.get(sensorId);

        if (data != null) {
            response.put("status", "success");
            response.put("sensorId", data.id);
            response.put("location", data.location);
            response.put("temperature", data.temperature);
            response.put("humidity", data.humidity);
            response.put("timestamp", data.timestamp.toString());
        } else {
            response.put("status", "not_found");
            response.put("message", "Sensor not found: " + sensorId);
        }

        return response;
    }

    @GetMapping("/sensors")
    public Map<String, Object> getAllSensors() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> sensors = new ArrayList<>();

        for (SensorData data : sensorData.values()) {
            Map<String, Object> sensor = new HashMap<>();
            sensor.put("id", data.id);
            sensor.put("location", data.location);
            sensor.put("temperature", data.temperature);
            sensor.put("humidity", data.humidity);
            sensor.put("lastUpdate", data.timestamp.toString());
            sensors.add(sensor);
        }

        response.put("status", "success");
        response.put("sensors", sensors);
        response.put("count", sensors.size());

        return response;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "success");
        response.put("totalSensors", sensorData.size());
        response.put("lastUpdate", LocalDateTime.now().toString());
        response.put("service", "mqtt-broker-api");

        return response;
    }

    // Внутренний метод для обновления данных от реальных датчиков
    public void updateSensorData(String sensorId, Double temperature, Double humidity) {
        SensorData data = sensorData.get(sensorId);
        if (data == null) {
            data = new SensorData(sensorId, sensorId, temperature, humidity);
            sensorData.put(sensorId, data);
        } else {
            data.update(temperature, humidity);
        }
    }

    private Map<String, Object> createAlert(SensorData data, String type, String status, Double value) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("sensorId", data.id);
        alert.put("location", data.location);
        alert.put("type", type);
        alert.put("status", status);
        alert.put("value", value);
        alert.put("timestamp", data.timestamp.toString());
        return alert;
    }

    // Внутренний класс для хранения данных датчика
    private static class SensorData {
        String id;
        String location;
        Double temperature;
        Double humidity;
        LocalDateTime timestamp;

        SensorData(String id, String location, Double temperature, Double humidity) {
            this.id = id;
            this.location = location;
            this.temperature = temperature;
            this.humidity = humidity;
            this.timestamp = LocalDateTime.now();
        }

        void update(Double temperature, Double humidity) {
            if (temperature != null) this.temperature = temperature;
            if (humidity != null) this.humidity = humidity;
            this.timestamp = LocalDateTime.now();
        }
    }
}