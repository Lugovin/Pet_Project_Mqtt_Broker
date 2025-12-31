package com.example.pet_project_mqtt_broker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorData(String sensorId,
                         Double temperature,
                         Double temperatureOutside, // будет null если поля нет в JSON
                         Double humidity,
                         Double pressure,
                         Long messId,
                         LocalDateTime timestamp) {


}

