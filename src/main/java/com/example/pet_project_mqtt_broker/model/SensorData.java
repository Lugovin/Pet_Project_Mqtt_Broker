package com.example.pet_project_mqtt_broker.model;

import java.time.LocalDateTime;

public record SensorData(String sensorId,
                         Double temperature,
                         Double humidity,
                         Double pressure,
                         Long messId,
                         LocalDateTime timestamp) {


    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='" + sensorId + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", messId=" + messId +
                ", timestamp=" + timestamp +
                '}';
    }
}

