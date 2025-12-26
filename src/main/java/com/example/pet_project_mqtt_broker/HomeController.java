package com.example.pet_project_mqtt_broker;


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
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>MQTT Telegram Gateway</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        max-width: 800px;
                        margin: 50px auto;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .container {
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    h1 {
                        color: #2c3e50;
                        border-bottom: 3px solid #3498db;
                        padding-bottom: 10px;
                    }
                    .status {
                        background: #27ae60;
                        color: white;
                        padding: 10px;
                        border-radius: 5px;
                        font-weight: bold;
                        margin: 20px 0;
                    }
                    .endpoints {
                        background: #ecf0f1;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .endpoint {
                        margin: 10px 0;
                        padding: 8px;
                        background: white;
                        border-left: 4px solid #3498db;
                    }
                    code {
                        background: #2c3e50;
                        color: #ecf0f1;
                        padding: 2px 6px;
                        border-radius: 3px;
                        font-family: monospace;
                    }
                    .mqtt-info {
                        background: #e8f4fc;
                        border: 1px solid #3498db;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🚀 MQTT Telegram Gateway</h1>
                    
                    <div class="status">
                        ✅ Application is running successfully!
                    </div>
                    
                    <div class="mqtt-info">
                        <h3>📡 MQTT Broker Information</h3>
                        <p><strong>MQTT Port:</strong> <code>1883</code></p>
                        <p><strong>WebSocket Port:</strong> <code>8083</code></p>
                        <p><strong>Connect URL:</strong> <code>tcp://localhost:1883</code></p>
                        <p><strong>WebSocket URL:</strong> <code>ws://localhost:8083</code></p>
                    </div>
                    
                    <h2>🔗 Available Endpoints</h2>
                    <div class="endpoints">
                        <div class="endpoint">
                            <strong>GET</strong> <a href="/health">/health</a> - Health check
                        </div>
                        <div class="endpoint">
                            <strong>GET</strong> <a href="/api/broker/info">/api/broker/info</a> - Broker information
                        </div>
                        <div class="endpoint">
                            <strong>GET</strong> <a href="/api/mqtt/test">/api/mqtt/test</a> - Test MQTT connection
                        </div>
                        <div class="endpoint">
                            <strong>POST</strong> <code>/api/mqtt/send?topic=test&message=hello&qos=1</code> - Send MQTT message
                        </div>
                    </div>
                    
                    <h2>🎯 How to Test</h2>
                    <ol>
                        <li>Use <a href="/api/mqtt/test">/api/mqtt/test</a> to send a test message</li>
                        <li>Connect MQTT client to <code>tcp://localhost:1883</code></li>
                        <li>Subscribe to topic <code>test/topic</code></li>
                        <li>You should see the test message</li>
                    </ol>
                    
                    <h2>🔧 Sensor Integration</h2>
                    <p>Configure your temperature sensor to publish to:</p>
                    <ul>
                        <li><code>sensors/temperature</code> - for temperature data</li>
                        <li><code>sensors/humidity</code> - for humidity data</li>
                        <li><code>sensors/#</code> - to subscribe to all sensor topics</li>
                    </ul>
                    
                    <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd;">
                        <p><strong>Server Time:</strong> %s</p>
                        <p><strong>Port:</strong> 8081</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(java.time.LocalDateTime.now());
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"mqtt-telegram-gateway\",\"timestamp\":\"" +
                java.time.LocalDateTime.now() + "\"}";
    }
}