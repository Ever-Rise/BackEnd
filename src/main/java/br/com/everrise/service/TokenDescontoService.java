package br.com.everrise.service;

import br.com.everrise.domain.TokenDesconto;
import br.com.everrise.repository.TokenDescontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenDescontoService {

    private final TokenDescontoRepository tokenDescontoRepository;
    private final PacienteService pacienteService;

    @Transactional
    public TokenDesconto gerar(Long pacienteId, BigDecimal desconto, int validadeDias) {
        var paciente = pacienteService.buscarPorId(pacienteId);
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (tokenDescontoRepository.existsByCodigo(codigo));

        TokenDesconto token = TokenDesconto.builder()
                .codigo(codigo)
                .desconto(desconto)
                .paciente(paciente)
                .emitidoEm(LocalDateTime.now())
                .expiraEm(LocalDateTime.now().plusDays(validadeDias))
                .utilizado(false)
                .build();

        return tokenDescontoRepository.save(token);
    }

    public TokenDesconto buscarPorCodigo(String codigo) {
        return tokenDescontoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Token de desconto não encontrado"));
    }

    @Transactional
    public TokenDesconto utilizar(String codigo) {
        TokenDesconto token = buscarPorCodigo(codigo);
        if (Boolean.TRUE.equals(token.getUtilizado())) {
            throw new RuntimeException("Token de desconto já utilizado");
        }
        if (!token.getExpiraEm().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Token de desconto expirado");
        }

        token.setUtilizado(true);
        return tokenDescontoRepository.save(token);
    }

    public java.util.List<TokenDesconto> listarValidosPorPaciente(Long pacienteId) {
        return tokenDescontoRepository.findTokensValidosByPaciente(pacienteId, LocalDateTime.now());
    }

}
