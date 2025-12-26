package com.example.pet_project_mqtt_broker;



import com.example.pet_project_mqtt_broker.controller.TelegramBotApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class SensorMessageHandler {

    @Autowired
    private TelegramBotApiController botApiController;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            Object payload = message.getPayload();

            String payloadStr;
            if (payload instanceof byte[]) {
                payloadStr = new String((byte[]) payload);
            } else {
                payloadStr = payload.toString();
            }

            System.out.println("=".repeat(60));
            System.out.println("📡 MQTT MESSAGE RECEIVED");
            System.out.println("Topic: " + topic);
            System.out.println("Payload: " + payloadStr);
            System.out.println("=".repeat(60));

            // Извлекаем sensorId из топика
            String sensorId = extractSensorId(topic);

            // Обработка температурных данных
            if (topic.contains("temperature")) {
                try {
                    double temperature = Double.parseDouble(payloadStr.trim());
                    System.out.printf("🌡️ Temperature from %s: %.1f°C%n", sensorId, temperature);

                    // Сохраняем данные для Telegram бота
                    botApiController.updateSensorData(sensorId, temperature, null);

                    // Проверяем на критические значения
                    if (temperature > 30) {
                        System.out.println("🔥 HIGH TEMPERATURE ALERT!");
                    } else if (temperature < 10) {
                        System.out.println("❄️ LOW TEMPERATURE ALERT!");
                    }

                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Invalid temperature format: " + payloadStr);
                }
            }

            // Обработка данных влажности
            else if (topic.contains("humidity")) {
                try {
                    double humidity = Double.parseDouble(payloadStr.trim());
                    System.out.printf("💧 Humidity from %s: %.1f%%%n", sensorId, humidity);

                    // Сохраняем данные для Telegram бота
                    botApiController.updateSensorData(sensorId, null, humidity);

                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Invalid humidity format: " + payloadStr);
                }
            }

            // Обработка JSON данных
            else if (payloadStr.startsWith("{") && payloadStr.contains("}")) {
                System.out.println("📊 JSON data received: " + payloadStr);
                // Здесь можно добавить парсинг JSON
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing MQTT message: " + e.getMessage());
        }
    }

    private String extractSensorId(String topic) {
        // Пример: "sensors/temperature/room1" → "room1"
        // или "sensors/room1/temperature" → "room1"

        String[] parts = topic.split("/");
        if (parts.length >= 2) {
            // Пытаемся найти идентификатор
            for (String part : parts) {
                if (!part.equals("sensors") &&
                        !part.equals("temperature") &&
                        !part.equals("humidity") &&
                        !part.isEmpty()) {
                    return part;
                }
            }
        }
        return "unknown-sensor";
    }
}
