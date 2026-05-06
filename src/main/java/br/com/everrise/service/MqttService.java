package br.com.everrise.service;

import br.com.everrise.dto.request.ComandoRequest;

public interface MqttService {

    void publishCommand(Long guinchoId, ComandoRequest request);
}

