package br.com.everrise.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Configuration
public class MqttConfig {

    @Bean
    public MqttConnectOptions mqttConnectOptions(
            @Value("${app.mqtt.username}") String username,
            @Value("${app.mqtt.password}") String password
    ) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        return options;
    }

    @Bean
    public MqttClient mqttClient(
            @Value("${app.mqtt.broker-url}") String brokerUrl,
            @Value("${app.mqtt.client-id:everrise-backend}") String clientId,
            MqttConnectOptions options
    ) throws Exception {
        MqttClient client = new MqttClient(brokerUrl, clientId + "-" + UUID.randomUUID(), new MemoryPersistence());
        if (!client.isConnected()) {
            client.connect(options);
        }
        return client;
    }

    @Bean(name = "mqttInboundChannel")
    public SubscribableChannel mqttInboundChannel() {
        return new ExecutorSubscribableChannel();
    }

    @Bean(name = "mqttOutboundChannel")
    public MessageChannel mqttOutboundChannel() {
        return new ExecutorSubscribableChannel();
    }

    @Bean
    public ApplicationRunner mqttTelemetrySubscriber(MqttClient mqttClient, MessageChannel mqttInboundChannel) {
        return args -> {
            mqttClient.subscribe("guincho/+/telemetry");
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // A reconexao automatica ja esta habilitada.
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                    mqttInboundChannel.send(MessageBuilder.createMessage(
                            payload,
                            new MessageHeaders(Map.of("mqtt_receivedTopic", topic))
                    ));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Sem acao para mensagens de entrada.
                }
            });
        };
    }
}
