package com.example.pet_project_mqtt_broker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @GetMapping("/test")
    public Map<String, Object> testMqtt() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Отправляем тестовое сообщение
            Message<String> message = MessageBuilder
                    .withPayload("Hello from MQTT Gateway! Timestamp: " +
                            java.time.LocalDateTime.now())
                    .setHeader(MqttHeaders.TOPIC, "test/topic")
                    .setHeader(MqttHeaders.QOS, 1)
                    .build();

            boolean sent = mqttOutboundChannel.send(message);

            response.put("status", sent ? "success" : "error");
            response.put("message", sent ? "Test message sent to MQTT broker" : "Failed to send");
            response.put("topic", "test/topic");
            response.put("timestamp", java.time.LocalDateTime.now().toString());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Exception: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(
            @RequestParam String topic,
            @RequestParam String message,
            @RequestParam(defaultValue = "1") int qos) {

        Map<String, Object> response = new HashMap<>();

        try {
            Message<String> mqttMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .setHeader(MqttHeaders.QOS, qos)
                    .build();

            boolean sent = mqttOutboundChannel.send(mqttMessage);

            response.put("status", sent ? "success" : "error");
            response.put("topic", topic);
            response.put("message", sent ? "Message sent successfully" : "Failed to send");
            response.put("qos", qos);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            // Если отправляем в sensors/topic, логируем
            if (topic.startsWith("sensors/")) {
                System.out.println("📡 Sensor data sent to topic: " + topic + " - " + message);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/send-sensor-test")
    public Map<String, Object> sendSensorTest() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Имитация данных датчика температуры
            double temperature = 22.5 + (Math.random() * 5); // 22.5-27.5
            String tempPayload = String.format("%.1f", temperature);

            Message<String> tempMessage = MessageBuilder
                    .withPayload(tempPayload)
                    .setHeader(MqttHeaders.TOPIC, "sensors/temperature")
                    .setHeader(MqttHeaders.QOS, 1)
                    .build();

            boolean tempSent = mqttOutboundChannel.send(tempMessage);

            // Имитация данных датчика влажности
            double humidity = 50.0 + (Math.random() * 20); // 50-70
            String humPayload = String.format("%.1f", humidity);

            Message<String> humMessage = MessageBuilder
                    .withPayload(humPayload)
                    .setHeader(MqttHeaders.TOPIC, "sensors/humidity")
                    .setHeader(MqttHeaders.QOS, 1)
                    .build();

            boolean humSent = mqttOutboundChannel.send(humMessage);

            response.put("status", "success");
            response.put("temperature", tempPayload + "°C");
            response.put("humidity", humPayload + "%");
            response.put("temperature_sent", tempSent);
            response.put("humidity_sent", humSent);
            response.put("message", "Sensor test data sent");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}
