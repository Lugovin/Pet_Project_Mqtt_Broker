package com.example.pet_project_mqtt_broker;


import com.example.pet_project_mqtt_broker.service.SensorDataService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class SensorMessageHandler {

    private final SensorDataService sensorDataService;

    public SensorMessageHandler(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }


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
            sensorDataService.parseAndConvert(topic, payloadStr); // Подсохраняем данные с датчика
            System.out.println("=".repeat(60));
            System.out.println("📡 MQTT MESSAGE RECEIVED");
            System.out.println("Topic: " + topic);
            System.out.println("Payload: " + payloadStr);
            System.out.println("=".repeat(60));


        } catch (Exception e) {
            System.err.println("❌ Error processing MQTT message: " + e.getMessage());
        }
    }

}
