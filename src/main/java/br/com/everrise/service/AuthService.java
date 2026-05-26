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

}
