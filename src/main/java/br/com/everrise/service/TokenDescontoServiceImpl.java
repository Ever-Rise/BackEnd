package br.com.everrise.service;

import br.com.everrise.dto.request.ValidarTokenDescontoRequest;
import br.com.everrise.dto.response.ValidacaoTokenDescontoResponse;
import br.com.everrise.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenDescontoServiceImpl implements TokenDescontoService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:259200000}")  // 72 horas em ms
    private long descontoTokenExpiration;

    private static final String CLAIM_PLANO_ID = "planoId";
    private static final String CLAIM_PERCENTUAL_DESCONTO = "percentualDesconto";

    @Override
    public ValidacaoTokenDescontoResponse validarToken(ValidarTokenDescontoRequest request) {
        try {
            // Validar assinatura e expiração
            Claims claims = extractAllClaims(request.getToken());

            // Verificar se token expirou
            if (claims.getExpiration().before(new Date())) {
                return ValidacaoTokenDescontoResponse.builder()
                    .valido(false)
                    .mensagem("Token de desconto expirado (validade máxima 72h)")
                    .build();
            }

            // Extrair dados do token
            Long planoIdDoToken = Long.parseLong(claims.get(CLAIM_PLANO_ID, String.class));
            BigDecimal percentualDesconto = new BigDecimal(
                claims.get(CLAIM_PERCENTUAL_DESCONTO, String.class)
            );
            String jti = claims.getId();

            // Validar se plano do token corresponde ao plano do request
            if (!planoIdDoToken.equals(request.getPlanoId())) {
                return ValidacaoTokenDescontoResponse.builder()
                    .valido(false)
                    .mensagem("Token de desconto não é válido para este plano")
                    .build();
            }

            // Validar percentual de desconto
            if (percentualDesconto.compareTo(BigDecimal.ZERO) < 0 || 
                percentualDesconto.compareTo(new BigDecimal("100")) > 0) {
                return ValidacaoTokenDescontoResponse.builder()
                    .valido(false)
                    .mensagem("Percentual de desconto inválido")
                    .build();
            }

            return ValidacaoTokenDescontoResponse.builder()
                .valido(true)
                .mensagem("Token de desconto válido")
                .percentualDesconto(percentualDesconto)
                .planoId(planoIdDoToken)
                .jti(jti)
                .build();

        } catch (JwtException | IllegalArgumentException ex) {
            return ValidacaoTokenDescontoResponse.builder()
                .valido(false)
                .mensagem("Token de desconto inválido ou malformado: " + ex.getMessage())
                .build();
        } catch (Exception ex) {
            return ValidacaoTokenDescontoResponse.builder()
                .valido(false)
                .mensagem("Erro ao validar token de desconto")
                .build();
        }
    }

    @Override
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
