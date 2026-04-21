package br.com.everrise.service;

import br.com.everrise.domain.entity.GuinchoSession;
import br.com.everrise.domain.entity.User;
import br.com.everrise.domain.enums.UserRole;
import br.com.everrise.dto.request.BindDeviceRequest;
import br.com.everrise.dto.request.LoginRequest;
import br.com.everrise.dto.request.RefreshTokenRequest;
import br.com.everrise.dto.request.RegisterRequest;
import br.com.everrise.dto.response.AuthResponse;
import br.com.everrise.dto.response.GuinchoSessionResponse;
import br.com.everrise.exception.UnauthorizedException;
import br.com.everrise.mapper.UserMapper;
import br.com.everrise.repository.UserRepository;
import br.com.everrise.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final SessionService sessionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UnauthorizedException("Senha e confirmacao de senha nao conferem");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email ja cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais invalidas"));
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshBlacklistKey(refreshToken)))) {
            throw new UnauthorizedException("Refresh token invalido ou expirado");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

        return buildAuthResponse(user);
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return;
        }
        String token = bearerToken.substring(7);
        redisTemplate.opsForValue().set(refreshBlacklistKey(token), true, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public GuinchoSessionResponse bindDevice(BindDeviceRequest request) {
        GuinchoSession session = sessionService.bindDevice(request.getGuinchoId(), request.getDeviceFingerprint());
        return GuinchoSessionResponse.builder()
                .id(session.getId())
                .guinchoId(session.getGuincho().getId())
                .userId(session.getUser().getId())
                .deviceId(session.getDeviceId())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .active(session.getActive())
                .build();
    }

    @Override
    public void unbindDevice(Long guinchoId) {
        sessionService.unbindDevice(guinchoId);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .expiresIn(jwtExpiration)
                .user(userMapper.toResponse(user))
                .build();
    }

    private String refreshBlacklistKey(String token) {
        return "auth:refresh:blacklist:" + token;
    }
}
