package br.com.everrise.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttTopicBuilder {

    @Value("${app.mqtt.topic.command}")
    private String commandPattern;

    @Value("${app.mqtt.topic.telemetry}")
    private String telemetryPattern;

    public String commandTopic(Long guinchoId) {
        return commandPattern.replace("{id}", String.valueOf(guinchoId));
    }

    public String telemetryTopic(Long guinchoId) {
        return telemetryPattern.replace("{id}", String.valueOf(guinchoId));
    }
}

