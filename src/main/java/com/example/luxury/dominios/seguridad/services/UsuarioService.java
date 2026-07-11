package com.example.luxury.dominios.seguridad.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador) {
        Usuario usuario = usuarioRepository.buscarPorIdentificador(identificador)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        return construirUserDetails(usuario);
    }

    public UserDetails loadUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        return construirUserDetails(usuario);
    }

    public Usuario obtenerUsuarioPorIdentificador(String identificador) {
        return usuarioRepository.buscarPorIdentificador(identificador)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    private UserDetails construirUserDetails(Usuario usuario) {
        List<GrantedAuthority> autoridades = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()))
                .collect(Collectors.toList());

        return User.withUsername(usuario.getNumeroDocumento())
                .password(usuario.getContrasenaHash())
                .authorities(autoridades)
                .disabled(!usuario.isActivo())
                .build();
    }
}

