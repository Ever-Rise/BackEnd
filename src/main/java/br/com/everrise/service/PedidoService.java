package br.com.everrise.service;

import br.com.everrise.dto.request.CheckoutRequest;
import br.com.everrise.dto.response.PedidoResponse;

import java.util.List;

public interface PedidoService {

    PedidoResponse checkout(CheckoutRequest request);

    List<PedidoResponse> myOrders();

    void processWebhook(String paymentId, String paymentStatus);
}
