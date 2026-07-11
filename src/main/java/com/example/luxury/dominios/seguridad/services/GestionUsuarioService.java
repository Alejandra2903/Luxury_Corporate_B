package com.example.luxury.dominios.seguridad.services;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.seguridad.dto.request.UsuarioRequest;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;
import com.example.luxury.dominios.seguridad.models.Rol;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.seguridad.repositories.RolRepository;
import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;

@Service
public class GestionUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public GestionUsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar(String estado) {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> filtrarPorEstado(usuario, estado))
                .map(UsuarioResponse::from)
                .toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        validar(request, null, true);

        Usuario usuario = new Usuario();
        aplicarDatos(usuario, request);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        usuario.setRoles(new HashSet<>(List.of(obtenerRol(request.getRol()))));
        usuario.setActivo(request.isActivo());

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        validar(request, id, false);

        aplicarDatos(usuario, request);
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        }
        usuario.getRoles().clear();
        usuario.getRoles().add(obtenerRol(request.getRol()));
        usuario.setActivo(request.isActivo());

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarEstado(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private boolean filtrarPorEstado(Usuario usuario, String estado) {
        if (estado == null || estado.isBlank()) {
            return true;
        }
        if ("ACTIVO".equalsIgnoreCase(estado)) {
            return usuario.isActivo();
        }
        if ("INACTIVO".equalsIgnoreCase(estado)) {
            return !usuario.isActivo();
        }
        return true;
    }

    private void aplicarDatos(Usuario usuario, UsuarioRequest request) {
        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setNumeroDocumento(request.getNumeroDocumento().trim());
        usuario.setTelefono(request.getTelefono().trim());
        usuario.setCorreo(request.getCorreo().trim().toLowerCase());
    }

    private Rol obtenerRol(NombreRol nombreRol) {
        return rolRepository.findByNombre(nombreRol)
                .orElseGet(() -> rolRepository.save(new Rol(nombreRol)));
    }

    private void validar(UsuarioRequest request, Long usuarioIdActual, boolean requiereContrasena) {
        if (requiereContrasena && (request.getContrasena() == null || request.getContrasena().isBlank())) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }
        if (!request.getTelefono().trim().matches("\\d{9}")) {
            throw new IllegalArgumentException("El telefono debe tener 9 digitos numericos.");
        }
        if (request.getTipoDocumento() == TipoDocumento.DNI
                && !request.getNumeroDocumento().trim().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 digitos numericos.");
        }
        if (request.getTipoDocumento() == TipoDocumento.CE && request.getNumeroDocumento().trim().length() < 9) {
            throw new IllegalArgumentException("El Carne de Extranjeria (CE) debe tener al menos 9 caracteres.");
        }

        String numeroDocumento = request.getNumeroDocumento().trim();
        String telefono = request.getTelefono().trim();
        String correo = request.getCorreo().trim().toLowerCase();
        String nombres = request.getNombres().trim();
        String apellidos = request.getApellidos().trim();

        boolean duplicado = usuarioRepository.findAll().stream()
                .filter(usuario -> usuarioIdActual == null || !usuario.getId().equals(usuarioIdActual))
                .anyMatch(usuario -> usuario.getNumeroDocumento().equals(numeroDocumento)
                        || usuario.getTelefono().equals(telefono)
                        || usuario.getCorreo().equalsIgnoreCase(correo)
                        || (usuario.getNombres().trim().equalsIgnoreCase(nombres)
                                && usuario.getApellidos().trim().equalsIgnoreCase(apellidos)));

        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con esos datos.");
        }
    }
}
