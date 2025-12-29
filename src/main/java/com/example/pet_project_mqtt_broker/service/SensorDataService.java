package com.example.pet_project_mqtt_broker.service;

import com.example.pet_project_mqtt_broker.model.SensorData;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensorDataService {

    private Map<String, SensorData> lastSensorsData = new HashMap<>();

    public void parseAndConvert(String topic, String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            String sensorId = extractSensorIdFromTopic(topic);
            Double temperature = root.get("temperature").asDouble();
            Double humidity = root.get("humidity").asDouble();
            Double pressure = root.get("pressure").asDouble();
            long messId = root.get("messId").asLong();
            LocalDateTime timestamp = LocalDateTime.now();
            SensorData sensorData = new SensorData(
                    sensorId, temperature, humidity, pressure, messId, timestamp
            );
            // Сохраняем как последние данные
            lastSensorsData.put(sensorId, sensorData);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse sensor data", e);
        }
    }

    private String extractSensorIdFromTopic(String topic) {
        return topic.split("/")[1];
    }

    public Map<String, SensorData> getLastSensorsData() {
        return lastSensorsData;
    }

    public SensorData getLastBySensorId(String sensorId){
        return lastSensorsData.get(sensorId);
    }
}
