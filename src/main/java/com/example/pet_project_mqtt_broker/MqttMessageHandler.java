package com.example.pet_project_mqtt_broker;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageHandler {

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        System.out.println("\n" + "✨".repeat(30));
        System.out.println("🎉 MQTT MESSAGE SUCCESSFULLY RECEIVED!");
        System.out.println("✨".repeat(30));

        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            Object payload = message.getPayload();

            // Безопасное преобразование payload
            String payloadStr;
            if (payload instanceof byte[]) {
                payloadStr = new String((byte[]) payload);
                System.out.println("📦 Payload was received as byte[], converted to string");
            } else {
                payloadStr = payload.toString();
                System.out.println("📦 Payload was received as: " + payload.getClass().getSimpleName());
            }

            System.out.println("📌 Topic: " + topic);
            System.out.println("📝 Message: " + payloadStr);
            System.out.println("⚡ QoS: " + message.getHeaders().get("mqtt_receivedQos"));
            System.out.println("🕐 Time: " + java.time.LocalTime.now());

            // Обработка разных типов сообщений
            if (topic != null) {
                if (topic.startsWith("test/")) {
                    System.out.println("✅ Test message processed successfully!");
                } else if (topic.startsWith("sensors/")) {
                    System.out.println("📡 Sensor data received!");
                    processSensorData(topic, payloadStr);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✨".repeat(30) + "\n");
    }

    private void processSensorData(String topic, String payload) {
        try {
            if (topic.contains("temperature")) {
                double temp = Double.parseDouble(payload.trim());
                System.out.printf("🌡️ Temperature: %.1f°C%n", temp);

                if (temp > 30) {
                    System.out.println("🔥 WARNING: High temperature!");
                } else if (temp < 10) {
                    System.out.println("❄️ WARNING: Low temperature!");
                }
            } else if (topic.contains("humidity")) {
                double humidity = Double.parseDouble(payload.trim());
                System.out.printf("💧 Humidity: %.1f%%%n", humidity);
            }
        } catch (NumberFormatException e) {
            System.out.println("📊 Sensor data (raw): " + payload);
        }
    }
}
