package com.example.pet_project_mqtt_broker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;

import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class MqttInboundConfig {

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "spring-boot-mqtt-consumer-" + System.currentTimeMillis(),
                        mqttClientFactory,
                        "test/topic", "test/topic2", "sensors/#");

        adapter.setCompletionTimeout(5000);

        // КРИТИЧЕСКИ ВАЖНО: получаем payload как строку
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(false); // <- ЭТО РЕШАЕТ ПРОБЛЕМУ!

        adapter.setConverter(converter);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // УДАЛИТЕ ЭТОТ МЕТОД ВООБЩЕ! НЕТ ОБРАБОТЧИКА ЗДЕСЬ!
    // Обработчик должен быть в отдельном классе SensorMessageHandler
}
