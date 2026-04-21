package br.com.everrise.websocket;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.service.GuinchoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GuinchoWebSocketController {

    private final GuinchoService guinchoService;

    @MessageMapping("/guincho/{id}/comando")
    public void relayCommand(@DestinationVariable("id") Long guinchoId, ComandoRequest request) {
        guinchoService.enviarComando(guinchoId, request);
    }
}

