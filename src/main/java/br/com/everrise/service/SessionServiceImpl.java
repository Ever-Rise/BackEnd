package br.com.everrise.service;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.entity.GuinchoSession;
import br.com.everrise.domain.entity.User;
import br.com.everrise.exception.GuinchoOccupiedException;
import br.com.everrise.exception.ResourceNotFoundException;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.repository.GuinchoSessionRepository;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final GuinchoRepository guinchoRepository;
    private final GuinchoSessionRepository guinchoSessionRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public GuinchoSession bindDevice(Long guinchoId, String deviceFingerprint) {
        Guincho guincho = guinchoRepository.findByIdAndAtivoTrue(guinchoId)
                .orElseThrow(() -> new ResourceNotFoundException("Guincho nao encontrado"));

        User currentUser = securityUtils.getCurrentUser();

        // RN08/RN09: Validar limite de dispositivos por plano
        if (currentUser.getPlano() != null && currentUser.getPlano().getMaxDispositivos() != null) {
            // Contar guinchos já vinculados (ativos) que não sejam este
            long guinchosAtivos = guinchoRepository.countByOwnerIdAndAtivoTrueAndIdNot(currentUser.getId(), guinchoId);
            
            if (guinchosAtivos >= currentUser.getPlano().getMaxDispositivos()) {
                throw new RuntimeException(
                    "Limite de dispositivos atingido para seu plano. " +
                    "Máximo permitido: " + currentUser.getPlano().getMaxDispositivos() + 
                    ", Atuais: " + guinchosAtivos
                );
            }
        }

        guinchoSessionRepository.findFirstByGuinchoIdAndActiveTrue(guinchoId)
                .ifPresent(active -> {
                    if (!active.getUser().getId().equals(currentUser.getId())) {
                        throw new GuinchoOccupiedException("Guincho ja esta em uso por outra sessao ativa");
                    }
                });

        GuinchoSession session = guinchoSessionRepository
                .findFirstByGuinchoIdAndUserIdAndDeviceId(guinchoId, currentUser.getId(), deviceFingerprint)
                .orElseGet(() -> GuinchoSession.builder()
                        .guincho(guincho)
                        .user(currentUser)
                        .deviceId(deviceFingerprint)
                        .startedAt(LocalDateTime.now())
                        .active(true)
                        .build());

        session.setStartedAt(LocalDateTime.now());
        session.setEndedAt(null);
        session.setActive(true);
        return guinchoSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void unbindDevice(Long guinchoId) {
        Long userId = securityUtils.getCurrentUser().getId();
        GuinchoSession session = guinchoSessionRepository
                .findFirstByGuinchoIdAndUserIdAndActiveTrue(guinchoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessao ativa nao encontrada para este guincho"));

        session.setActive(false);
        session.setEndedAt(LocalDateTime.now());
        guinchoSessionRepository.save(session);
    }
}
