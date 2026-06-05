package br.com.everrise.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MqttConfig {

    @Value("${mqtt.broker.url:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${mqtt.client.id:everrise-backend}")
    private String clientId;

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true")
    @Primary
    public MqttClient mqttClient() throws MqttException {
        log.info("Creating MQTT client connecting to: {}", brokerUrl);
        MqttClient client = new MqttClient(brokerUrl, clientId, null);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.connect(options);
        log.info("MQTT client connected successfully");
        return client;
    }

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "false", matchIfMissing = true)
    @Primary
    public MqttClient mockMqttClient() throws MqttException {
        log.warn("MQTT is disabled. Using mock MQTT client. To enable MQTT, set mqtt.enabled=true and configure a broker.");
        // Use a mock URI to create the client without actually connecting
        MqttClient mockClient = new MqttClient("tcp://127.0.0.1:1883", "mock-everrise-" + System.currentTimeMillis(), null);
        log.info("Mock MQTT client created (will not connect to any real broker)");
        return mockClient;
    }
}

