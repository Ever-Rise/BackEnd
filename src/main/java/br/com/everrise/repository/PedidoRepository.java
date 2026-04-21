package br.com.everrise.repository;

import br.com.everrise.domain.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Pedido> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);
}
