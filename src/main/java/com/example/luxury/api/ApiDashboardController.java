package com.example.luxury.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.dashboard.dto.CostoPorMesResponse;
import com.example.luxury.dominios.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class ApiDashboardController {

    private final DashboardService dashboardService;

    public ApiDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        var resumen = dashboardService.resumenGeneral();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("periodo", "2026-06");
        data.put("monedaBase", "PEN");
        data.put("costoTotal", resumen.getCostoTotalPen());
        data.put("variacionCostoPorcentaje", -7.8);
        data.put("consumoEnergiaKwh", 312840);
        data.put("consumoAguaM3", 4820);
        data.put("sedesActivas", resumen.getTotalSedes());
        data.put("alertasActivas", resumen.getTotalAlertas());
        data.put("cumplimientoUmbralesPorcentaje", 91.4);
        data.put("ultimaActualizacion", LocalDateTime.now());
        return data;
    }

    @GetMapping("/consumo-por-sede")
    public List<Map<String, Object>> consumoPorSede() {
        return dashboardService.consumoPorSede().stream().map(item -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("sedeId", 0L);
            data.put("sede", item.getSede());
            data.put("energiaKwh", item.getTotalConsumido());
            data.put("aguaM3", BigDecimal.ZERO);
            data.put("costoTotal", BigDecimal.ZERO);
            data.put("alertas", 0);
            return data;
        }).toList();
    }

    @GetMapping("/costos-por-mes")
    public List<Map<String, Object>> costosPorMes() {
        return dashboardService.costosPorMes().stream()
                .filter(item -> "PEN".equalsIgnoreCase(item.getMoneda()))
                .map(this::costoPorMes)
                .toList();
    }

    private Map<String, Object> costoPorMes(CostoPorMesResponse item) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("periodo", item.getPeriodo());
        data.put("etiqueta", item.getPeriodo());
        data.put("costoEnergia", item.getTotal());
        data.put("costoAgua", BigDecimal.ZERO);
        data.put("costoTotal", item.getTotal());
        return data;
    }
}
