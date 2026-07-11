package com.example.luxury.dominios.seguridad.services;

import java.util.HashSet;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.luxury.dominios.seguridad.dto.request.RegistroUsuarioRequest;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.models.Rol;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.seguridad.repositories.RolRepository;
import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;

@Service
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AutenticacionService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegistroUsuarioRequest request) {
        validarRegistro(request);

        Rol rol = obtenerRol(NombreRol.ANALISTA);

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setNumeroDocumento(request.getNumeroDocumento().trim());
        usuario.setTelefono(request.getTelefono().trim());
        usuario.setCorreo(request.getCorreo().trim().toLowerCase());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        usuario.setRoles(new HashSet<>(List.of(rol)));

        return usuarioRepository.save(usuario);
    }

    private Rol obtenerRol(NombreRol nombreRol) {
        return rolRepository.findByNombre(nombreRol)
                .orElseGet(() -> rolRepository.save(new Rol(nombreRol)));
    }

    private void validarRegistro(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new IllegalArgumentException("El numero de documento ya esta registrado.");
        }

        if (usuarioRepository.existsByTelefono(request.getTelefono())) {
            throw new IllegalArgumentException("El telefono ya esta registrado.");
        }

        if (usuarioRepository.existsByCorreo(request.getCorreo().trim().toLowerCase())) {
            throw new IllegalArgumentException("El correo ya esta registrado.");
        }

        if (usuarioRepository.existsByNombresAndApellidos(request.getNombres(), request.getApellidos())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese nombre y apellido.");
        }

        if (!request.getTelefono().trim().matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener 9 dígitos numéricos.");
        }

        if (request.getTipoDocumento() == com.example.luxury.dominios.seguridad.enums.TipoDocumento.DNI) {
            if (!request.getNumeroDocumento().trim().matches("\\d{8}")) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
            }
        } else if (request.getTipoDocumento() == com.example.luxury.dominios.seguridad.enums.TipoDocumento.CE) {
            if (request.getNumeroDocumento().trim().length() < 9) {
                throw new IllegalArgumentException("El Carné de Extranjería (CE) debe tener al menos 9 caracteres.");
            }
        }
    }
}

