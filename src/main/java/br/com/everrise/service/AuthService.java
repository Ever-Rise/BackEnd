package br.com.everrise.service;

import br.com.everrise.domain.Paciente;
import br.com.everrise.domain.Usuario;
import br.com.everrise.domain.enums.Role;
import br.com.everrise.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.expiration}")
    private long expiration;

    @Transactional
    public Usuario registrar(String nome, String email, String senha, Role role) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        Paciente usuario = Paciente.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .role(role == null ? Role.FAMILIA : role)
                .criadoEm(LocalDateTime.now())
                .ativo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    public String login(String email, String senha) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return jwtService.gerarToken(usuario);
    }

    public br.com.everrise.dto.response.AuthResponse register(br.com.everrise.dto.request.RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Senha e confirmação de senha não conferem");
        }

        Usuario usuario = registrar(request.getName(), request.getEmail(), request.getPassword(), Role.FAMILIA);
        return br.com.everrise.dto.response.AuthResponse.builder()
                .token(jwtService.gerarToken(usuario))
                .expiresIn(expiration)
                .build();
    }

    public br.com.everrise.dto.response.AuthResponse login(br.com.everrise.dto.request.LoginRequest request) {
        String token = login(request.email(), request.senha());
        return br.com.everrise.dto.response.AuthResponse.builder()
                .token(token)
                .expiresIn(expiration)
                .build();
    }

    public br.com.everrise.dto.response.AuthResponse refresh(br.com.everrise.dto.request.RefreshTokenRequest request) {
        String email = jwtService.extrairEmail(request.getRefreshToken());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return br.com.everrise.dto.response.AuthResponse.builder()
                .token(jwtService.gerarToken(usuario))
                .refreshToken(request.getRefreshToken())
                .expiresIn(expiration)
                .build();
    }

    public void logout(String bearerToken) {
        // logout stateless nesta camada
    }

    public br.com.everrise.dto.response.GuinchoSessionResponse bindDevice(br.com.everrise.dto.request.BindDeviceRequest request) {
        return br.com.everrise.dto.response.GuinchoSessionResponse.builder()
                .guinchoId(request.getGuinchoId())
                .deviceId(request.getDeviceFingerprint())
                .startedAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    public void unbindDevice(Long guinchoId) {
        // compatibilidade com controller legado
    }
}
