package com.example.luxury.api.dto;

import java.time.LocalDateTime;

/**
 * DTO tipado que expone la representacion de un usuario para Angular.
 * Los campos coinciden con Usuario del frontend (src/app/core/models/usuario.model.ts).
 */
public record UsuarioApiResponse(
        Long id,
        String nombres,
        String apellidos,
        String nombreCompleto,
        Long sedeId,
        String sedeNombre,
        String tipoDocumento,
        String numeroDocumento,
        String telefono,
        String correo,
        boolean activo,
        String estado,
        String roles,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaActualizacion) {
}
