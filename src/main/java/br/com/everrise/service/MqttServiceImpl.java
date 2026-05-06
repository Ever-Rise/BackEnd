package br.com.everrise.service;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.messaging.MqttPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttServiceImpl implements MqttService {

    private final MqttPublisher mqttPublisher;

    @Override
    public void publishCommand(Long guinchoId, ComandoRequest request) {
        mqttPublisher.publishCommand(guinchoId, request);
    }
}

