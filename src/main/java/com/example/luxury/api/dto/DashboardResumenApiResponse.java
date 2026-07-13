package com.example.luxury.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO tipado para el resumen del dashboard.
 * Coincide con DashboardResumen del frontend.
 */
public record DashboardResumenApiResponse(
        String periodo,
        String monedaBase,
        BigDecimal costoTotal,
        BigDecimal variacionCostoPorcentaje,
        BigDecimal consumoEnergiaKwh,
        BigDecimal consumoAguaM3,
        long sedesActivas,
        long alertasActivas,
        double cumplimientoUmbralesPorcentaje,
        LocalDateTime ultimaActualizacion) {
}
