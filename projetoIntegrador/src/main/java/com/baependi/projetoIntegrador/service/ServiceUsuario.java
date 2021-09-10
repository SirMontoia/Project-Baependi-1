package com.baependi.projetoIntegrador.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import com.baependi.projetoIntegrador.models.Usuario;
import com.baependi.projetoIntegrador.models.utilidades.UsuarioEspelho;
import com.baependi.projetoIntegrador.repository.RepositorioUsuario;

@Service
public class ServiceUsuario {

	@Autowired
	private RepositorioUsuario repository;

	public Usuario cadastraUsuario(Usuario usuario) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String senhaEncoder = encoder.encode(usuario.getSenha());
		usuario.setSenha(senhaEncoder);

		return repository.save(usuario);

	}

	public Optional<UsuarioEspelho> Logar(Optional<UsuarioEspelho> user) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = repository.buscarNome(user.get().getNomeUsuario());

		if (usuario.isPresent()) {
			if (encoder.matches(user.get().getSenha(), usuario.get().getSenha())) {

				String auth = user.get().getNomeUsuario() + ":" + user.get().getSenha();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic" + new String(encodedAuth);

				user.get().setToken(authHeader);
				user.get().setNomeUsuario(usuario.get().getNomeUsuario());
				user.get().setSenha(usuario.get().getSenha());
				user.get().setEmail(usuario.get().getEmail());

				return user;

			}

		}
		return null;

	}
}
