package com.example.pet_project_mqtt_broker.controller;


import com.example.pet_project_mqtt_broker.model.SensorData;
import com.example.pet_project_mqtt_broker.service.SensorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class TelegramBotApiController {

    private final SensorDataService sensorDataService;

    public TelegramBotApiController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }


    // Получение последней температуры
    @GetMapping("/temperature")
    public ResponseEntity<String> getTemperature() {
        try {
            // Получаем последние данные с датчика
            SensorData lastData = sensorDataService.getLastSensorRoom1Data();

            // Если данных нет
            if (lastData == null || lastData.temperature() == null) {
                String noDataMessage = "❌ Нет данных о температуре\n" +
                        "Датчик еще не отправлял данные или произошла ошибка.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(noDataMessage);
            }
            return ResponseEntity.ok(lastData.temperature().toString());

        } catch (Exception e) {
            // В случае ошибки
            String errorMessage = "⚠️ Ошибка при получении температуры:\n" +
                    e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }

    @GetMapping("/sensors")
    public ResponseEntity<SensorData> getSensors() {
        try {
            SensorData lastData = sensorDataService.getLastSensorRoom1Data();

            if (lastData == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(lastData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
