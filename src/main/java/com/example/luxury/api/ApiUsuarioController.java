package com.example.luxury.api;

import java.util.List;

import com.example.luxury.api.dto.UsuarioApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.luxury.dominios.auditoria.service.AuditoriaService;
import com.example.luxury.dominios.seguridad.dto.request.UsuarioRequest;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;
import com.example.luxury.dominios.seguridad.services.AuthenticatedUserService;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class ApiUsuarioController {

    private final GestionUsuarioService usuarioService;
    private final AuditoriaService auditoriaService;
    private final AuthenticatedUserService authenticatedUserService;

    public ApiUsuarioController(GestionUsuarioService usuarioService, AuditoriaService auditoriaService,
            AuthenticatedUserService authenticatedUserService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    public List<UsuarioApiResponse> listar() {
        return usuarioService.listar(null).stream().map(ApiMapper::usuarioDto).toList();
    }

    @GetMapping("/{id}")
    public UsuarioApiResponse obtener(@PathVariable Long id) {
        return ApiMapper.usuarioDto(usuarioService.obtener(id));
    }

    @PostMapping
    public UsuarioApiResponse crear(@Valid @RequestBody UsuarioApiRequest request) {
        UsuarioResponse creado = usuarioService.crear(toRequest(request, true));
        auditar("ADMIN", "CREAR", "usuarios", creado.getId(), "Alta de usuario " + creado.getCorreo());
        return ApiMapper.usuarioDto(creado);
    }

    @PutMapping
    public UsuarioApiResponse actualizar(@Valid @RequestBody UsuarioApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio.");
        }
        UsuarioResponse actualizado = usuarioService.actualizar(request.id(), toRequest(request, false));
        auditar("ADMIN", "ACTUALIZAR", "usuarios", actualizado.getId(), "Actualizacion de usuario " + actualizado.getCorreo());
        return ApiMapper.usuarioDto(actualizado);
    }

    @PatchMapping
    public UsuarioApiResponse cambiarEstado(@RequestBody CambiarEstadoRequest request) {
        UsuarioResponse actual = usuarioService.obtener(request.id());
        if (actual.isActivo() != request.activo()) {
            usuarioService.cambiarEstado(request.id());
        }
        return ApiMapper.usuarioDto(usuarioService.obtener(request.id()));
    }

    private void auditar(String modulo, String accion, String tabla, Long registroId, String descripcion) {
        try {
            auditoriaService.registrar(authenticatedUserService.actual(), modulo, accion, tabla, registroId, descripcion);
        } catch (RuntimeException ignored) {
            // El registro de auditoria no debe interrumpir la operacion principal.
        }
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

    public record UsuarioApiRequest(
            Long id,
            @NotBlank(message = "Los nombres son obligatorios.") String nombres,
            @NotBlank(message = "Los apellidos son obligatorios.") String apellidos,
            @NotBlank(message = "El tipo de documento es obligatorio.")
            @Pattern(regexp = "DNI|CE|PASAPORTE", message = "Tipo de documento invalido.") String tipoDocumento,
            @NotBlank(message = "El numero de documento es obligatorio.")
            @Pattern(regexp = "\\d{8,20}", message = "El numero de documento debe tener entre 8 y 20 digitos.") String numeroDocumento,
            @NotBlank(message = "El telefono es obligatorio.")
            @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos.") String telefono,
            @NotBlank(message = "El correo es obligatorio.")
            @Email(message = "El correo no es valido.") String correo,
            @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres.") String contrasena,
            @NotBlank(message = "El rol es obligatorio.")
            @Pattern(regexp = "ADMIN|GERENTE|ANALISTA|AUDITOR|OPERADOR", message = "Rol invalido.") String roles,
            Long sedeId,
            Boolean activo) {
    }

    public record CambiarEstadoRequest(@NotNull Long id, @NotNull Boolean activo) {
    }
}
