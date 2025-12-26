package com.example.pet_project_mqtt_broker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/broker")
public class BrokerInfoController {

    @GetMapping("/info")
    public Map<String, Object> getBrokerInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();

            info.put("status", "running");
            info.put("application", "MQTT Telegram Gateway");
            info.put("mqtt_port", 1883);
            info.put("websocket_port", 8083);
            info.put("http_port", 8081);
            info.put("local_ip", localIP);
            info.put("hostname", hostName);
            info.put("mqtt_url", "tcp://" + localIP + ":1883");
            info.put("mqtt_local_url", "tcp://localhost:1883");
            info.put("websocket_url", "ws://" + localIP + ":8083");
            info.put("timestamp", java.time.LocalDateTime.now().toString());
            info.put("java_version", System.getProperty("java.version"));

        } catch (Exception e) {
            info.put("status", "error");
            info.put("message", e.getMessage());
        }

        return info;
    }
}