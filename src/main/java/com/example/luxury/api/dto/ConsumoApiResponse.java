package com.example.luxury.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO tipado para un registro de consumo (Consumo del frontend).
 */
public record ConsumoApiResponse(
        Long id,
        Long sedeId,
        String sedeNombre,
        Long tipoRecursoId,
        String tipoRecursoCodigo,
        String tipoRecursoNombre,
        String unidad,
        String periodo,
        LocalDateTime fechaRegistro,
        BigDecimal cantidad,
        BigDecimal costo,
        String moneda,
        String estado,
        String observacion) {
}
