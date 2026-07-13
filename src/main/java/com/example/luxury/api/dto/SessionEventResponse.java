package com.example.luxury.api.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO tipado para eventos de monitoreo de sesion.
 * Coincide con SessionMonitoringEvent del frontend.
 */
public record SessionEventResponse(
        Long id,
        String sesionId,
        Long usuarioId,
        String usuarioNombre,
        String usuarioRol,
        String tipo,
        String severidad,
        LocalDateTime fechaEvento,
        String ruta,
        String ipOrigen,
        String userAgent,
        String descripcion,
        Map<String, Object> metadata) {
}
