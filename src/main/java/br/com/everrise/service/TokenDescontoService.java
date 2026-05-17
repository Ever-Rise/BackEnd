package br.com.everrise.service;

import br.com.everrise.domain.TokenDesconto;
import br.com.everrise.repository.TokenDescontoRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenDescontoService {

    private static final String CLAIM_PLANO_ID = "planoId";
    private static final String CLAIM_PERCENTUAL_DESCONTO = "percentualDesconto";

    private final TokenDescontoRepository tokenDescontoRepository;
    private final PacienteService pacienteService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:259200000}")
    private long descontoTokenExpiration;

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

    public boolean isValido(String codigo) {
        return tokenDescontoRepository.findByCodigo(codigo)
                .map(token -> !Boolean.TRUE.equals(token.getUtilizado()) && token.getExpiraEm().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public br.com.everrise.dto.response.ValidacaoTokenDescontoResponse validarToken(br.com.everrise.dto.request.ValidarTokenDescontoRequest request) {
        try {
            Claims claims = extractAllClaims(request.getToken());

            if (claims.getExpiration().before(new Date())) {
                return br.com.everrise.dto.response.ValidacaoTokenDescontoResponse.builder()
                        .valido(false)
                        .mensagem("Token de desconto expirado (validade máxima 72h)")
                        .build();
            }

            Long planoIdDoToken = Long.parseLong(claims.get(CLAIM_PLANO_ID, String.class));
            BigDecimal percentualDesconto = new BigDecimal(claims.get(CLAIM_PERCENTUAL_DESCONTO, String.class));
            String jti = claims.getId();

            if (!planoIdDoToken.equals(request.getPlanoId())) {
                return br.com.everrise.dto.response.ValidacaoTokenDescontoResponse.builder()
                        .valido(false)
                        .mensagem("Token de desconto não é válido para este plano")
                        .build();
            }

            if (percentualDesconto.compareTo(BigDecimal.ZERO) < 0 || percentualDesconto.compareTo(new BigDecimal("100")) > 0) {
                return br.com.everrise.dto.response.ValidacaoTokenDescontoResponse.builder()
                        .valido(false)
                        .mensagem("Percentual de desconto inválido")
                        .build();
            }

            return br.com.everrise.dto.response.ValidacaoTokenDescontoResponse.builder()
                    .valido(true)
                    .mensagem("Token de desconto válido")
                    .percentualDesconto(percentualDesconto)
                    .planoId(planoIdDoToken)
                    .jti(jti)
                    .build();
        } catch (Exception ex) {
            return br.com.everrise.dto.response.ValidacaoTokenDescontoResponse.builder()
                    .valido(false)
                    .mensagem("Token de desconto inválido ou malformado: " + ex.getMessage())
                    .build();
        }
    }

    public String gerarToken(Long planoId, BigDecimal percentualDesconto) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + descontoTokenExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_PLANO_ID, planoId.toString());
        claims.put(CLAIM_PERCENTUAL_DESCONTO, percentualDesconto.toString());

        return Jwts.builder()
                .claims(claims)
                .subject("desconto_" + planoId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
