package com.example.luxury.dominios.seguridad.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.luxury.dominios.seguridad.models.Usuario;

@Service
public class AuthenticatedUserService {

    private final UsuarioService usuarioService;

    public AuthenticatedUserService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario actual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String numeroDocumento = ((UserDetails) principal).getUsername();
            return usuarioService.obtenerUsuarioPorIdentificador(numeroDocumento);
        }
        throw new IllegalStateException("Usuario no autenticado");
    }
}
