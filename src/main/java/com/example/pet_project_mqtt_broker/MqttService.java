package com.example.pet_project_mqtt_broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    @Autowired
    private MessageChannel mqttOutboundChannel;

    // Отправка сообщения
    public void sendMessage(String topic, String message, int qos) {
        Message<String> mqttMessage = MessageBuilder
                .withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, qos)
                .build();

        mqttOutboundChannel.send(mqttMessage);
        System.out.println("Message sent to topic: " + topic);
    }

    // Отправка сообщения с retained флагом
    public void sendRetainedMessage(String topic, String message) {
        Message<String> mqttMessage = MessageBuilder
                .withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .setHeader(MqttHeaders.RETAINED, true)
                .build();

        mqttOutboundChannel.send(mqttMessage);
    }
}
