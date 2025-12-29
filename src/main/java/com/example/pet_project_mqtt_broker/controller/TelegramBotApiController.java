package com.example.pet_project_mqtt_broker.controller;

import com.example.pet_project_mqtt_broker.model.SensorData;
import com.example.pet_project_mqtt_broker.service.SensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bot")
public class TelegramBotApiController {

    private final SensorDataService sensorDataService;

    public TelegramBotApiController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<SensorData> getSensorData(
            @PathVariable String sensorId
    ) {
        log.info("Request to get data for sensorId={}", sensorId);

        try {
            SensorData data = sensorDataService.getLastBySensorId(sensorId);

            if (data == null) {
                log.warn("No data found for sensorId={}", sensorId);
                return ResponseEntity.notFound().build();
            }

            log.debug("Sensor data found: {}", data);
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error("Error while getting data for sensorId={}", sensorId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sensors")
    public ResponseEntity<List<SensorData>> getAllSensors() {
        log.info("Request to get data for all sensors");

        try {
            List<SensorData> sensors = new ArrayList<>(sensorDataService.getLastSensorsData().values());

            if (sensors.isEmpty()) {
                log.warn("No sensor data available");
                return ResponseEntity.noContent().build();
            }

            log.debug("Found {} sensors", sensors.size());
            return ResponseEntity.ok(sensors);

        } catch (Exception e) {
            log.error("Error while getting all sensors data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
