package com.example.pet_project_mqtt_broker.controller;


import com.example.pet_project_mqtt_broker.model.SensorData;
import com.example.pet_project_mqtt_broker.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        try {
            SensorData data = sensorDataService.getLastBySensorId(sensorId);
            if (data == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/sensors")
    public ResponseEntity<List<SensorData>> getAllSensors() {
        try {
            List<SensorData> sensors = new ArrayList<>(sensorDataService.getLastSensorsData().values());
            if (sensors == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(sensors);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
