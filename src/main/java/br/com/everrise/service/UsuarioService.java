package br.com.everrise.service;

import br.com.everrise.domain.Usuario;
import br.com.everrise.domain.enums.Role;
import br.com.everrise.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) {
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
	}

	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	}

	public Usuario buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public List<Usuario> listarPorRole(Role role) {
		return usuarioRepository.findAllByRole(role);
	}

	@Transactional
	public void desativar(Long id) {
		Usuario usuario = buscarPorId(id);
		usuario.setAtivo(false);
		usuarioRepository.save(usuario);
	}

	@Transactional
	public void alterarSenha(Long id, String novaSenha) {
		Usuario usuario = buscarPorId(id);
		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);
	}
}

