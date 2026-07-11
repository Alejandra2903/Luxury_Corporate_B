package com.example.luxury.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.seguridad.dto.request.UsuarioRequest;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class ApiUsuarioController {

    private final GestionUsuarioService usuarioService;

    public ApiUsuarioController(GestionUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return usuarioService.listar(null).stream().map(ApiMapper::usuario).toList();
    }

    @PostMapping
    public Map<String, Object> crear(@RequestBody UsuarioApiRequest request) {
        return ApiMapper.usuario(usuarioService.crear(toRequest(request, true)));
    }

    @PutMapping
    public Map<String, Object> actualizar(@RequestBody UsuarioApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio.");
        }
        return ApiMapper.usuario(usuarioService.actualizar(request.id(), toRequest(request, false)));
    }

    @PatchMapping
    public Map<String, Object> cambiarEstado(@RequestBody CambiarEstadoRequest request) {
        UsuarioResponse actual = usuarioService.obtener(request.id());
        if (actual.isActivo() != request.activo()) {
            usuarioService.cambiarEstado(request.id());
        }
        return ApiMapper.usuario(usuarioService.obtener(request.id()));
    }

    private UsuarioRequest toRequest(UsuarioApiRequest request, boolean crear) {
        String contrasena = crear ? request.contrasena() : null;
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            contrasena = request.contrasena();
        }
        return new UsuarioRequest(
                request.nombres(),
                request.apellidos(),
                TipoDocumento.valueOf(request.tipoDocumento()),
                request.numeroDocumento(),
                request.telefono(),
                request.correo(),
                contrasena,
                NombreRol.valueOf(request.roles()),
                request.activo() == null || request.activo());
    }

    public record UsuarioApiRequest(Long id, String nombres, String apellidos, String tipoDocumento,
            String numeroDocumento, String telefono, String correo, String contrasena, String roles, Long sedeId,
            Boolean activo) {
    }

    public record CambiarEstadoRequest(Long id, Boolean activo) {
    }
}
