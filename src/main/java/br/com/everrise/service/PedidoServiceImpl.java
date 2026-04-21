package br.com.everrise.service;

import br.com.everrise.domain.entity.Pedido;
import br.com.everrise.domain.entity.Plano;
import br.com.everrise.domain.entity.User;
import br.com.everrise.domain.enums.PedidoStatus;
import br.com.everrise.dto.request.CheckoutRequest;
import br.com.everrise.dto.response.PedidoResponse;
import br.com.everrise.exception.ResourceNotFoundException;
import br.com.everrise.mapper.PedidoMapper;
import br.com.everrise.repository.PedidoRepository;
import br.com.everrise.repository.PlanoRepository;
import br.com.everrise.repository.UserRepository;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PlanoRepository planoRepository;
    private final PedidoMapper pedidoMapper;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PedidoResponse checkout(CheckoutRequest request) {
        User user = securityUtils.getCurrentUser();
        Plano plano = planoRepository.findById(request.getPlanoId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado"));

        Pedido pedido = Pedido.builder()
                .user(user)
                .plano(plano)
                .status(PedidoStatus.PENDENTE)
                .valorTotal(plano.getPreco())
                .cupomDesconto(request.getCupomDesconto())
                .descontoAplicado(BigDecimal.ZERO)
                .build();

        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public List<PedidoResponse> myOrders() {
        Long userId = securityUtils.getCurrentUser().getId();
        return pedidoRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void processWebhook(String paymentId, String paymentStatus) {
        Pedido pedido = pedidoRepository.findByMercadoPagoPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado para o pagamento informado"));

        pedido.setMercadoPagoStatus(paymentStatus);
        if ("approved".equalsIgnoreCase(paymentStatus)) {
            pedido.setStatus(PedidoStatus.PAGO);
            User user = pedido.getUser();
            user.setPlano(pedido.getPlano().getTipo());
            userRepository.save(user);
        } else if ("cancelled".equalsIgnoreCase(paymentStatus) || "rejected".equalsIgnoreCase(paymentStatus)) {
            pedido.setStatus(PedidoStatus.CANCELADO);
        }

        pedidoRepository.save(pedido);
    }
}
