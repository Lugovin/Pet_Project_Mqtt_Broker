package com.example.pet_project_mqtt_broker.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>MQTT Telegram Gateway</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; }
                .endpoint { margin: 10px 0; padding: 10px; background: #f5f5f5; }
                .method { font-weight: bold; color: green; }
            </style>
        </head>
        <body>
            <h1>🌡️ MQTT Telegram Gateway</h1>
            <p>✅ System is running!</p>
            
            <h2>📡 MQTT Broker</h2>
            <p>Connect sensors to: <code>tcp://localhost:1883</code></p>
            
            <h2>🤖 Telegram Bot API Endpoints</h2>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/telegram-bot/sensor-data">/api/telegram-bot/sensor-data</a>
                <p>Get all sensor data</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/telegram-bot/temperature">/api/telegram-bot/temperature</a>
                <p>Get temperature data</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/telegram-bot/humidity">/api/telegram-bot/humidity</a>
                <p>Get humidity data</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/telegram-bot/alerts">/api/telegram-bot/alerts</a>
                <p>Get critical alerts</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/telegram-bot/stats">/api/telegram-bot/stats</a>
                <p>Get system statistics</p>
            </div>
            
            <h2>🔧 Testing</h2>
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/mqtt/test">/api/mqtt/test</a>
                <p>Test MQTT send/receive</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> 
                <a href="/api/test/simulate-sensor">/api/test/simulate-sensor</a>
                <p>Simulate sensor data</p>
            </div>
            
            <h2>📊 Status</h2>
            <p><a href="/health">/health</a> - Health check</p>
            <p><a href="/api/broker/info">/api/broker/info</a> - Broker info</p>
            
        </body>
        </html>
        """;
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"mqtt-telegram-gateway\",\"timestamp\":\"" +
                java.time.LocalDateTime.now() + "\"}";
    }
}