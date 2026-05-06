package br.com.everrise.messaging;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.util.MqttTopicBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttPublisher {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final MqttTopicBuilder topicBuilder;

    public void publishCommand(Long guinchoId, ComandoRequest request) {
        try {
            String topic = topicBuilder.commandTopic(guinchoId);
            byte[] payload = objectMapper.writeValueAsBytes(request);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(1);
            mqttClient.publish(topic, message);
        } catch (Exception ex) {
            log.error("Falha ao publicar comando MQTT", ex);
            throw new IllegalStateException("Nao foi possivel enviar comando MQTT");
        }
    }
}

