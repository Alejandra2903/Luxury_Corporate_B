package com.example.luxury.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.alerta.dto.AlertaResponse;
import com.example.luxury.dominios.alerta.service.ReglasAlertasService;
import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.enums.NivelAlerta;
import com.example.luxury.dominios.tarifa.dto.TarifaRequest;
import com.example.luxury.dominios.tarifa.service.TarifaService;
import com.example.luxury.dominios.umbral.dto.UmbralRequest;
import com.example.luxury.dominios.umbral.service.UmbralService;

@RestController
@RequestMapping("/api")
public class ApiBusinessRulesController {

    private final TarifaService tarifaService;
    private final UmbralService umbralService;
    private final ReglasAlertasService alertasService;

    public ApiBusinessRulesController(TarifaService tarifaService, UmbralService umbralService,
            ReglasAlertasService alertasService) {
        this.tarifaService = tarifaService;
        this.umbralService = umbralService;
        this.alertasService = alertasService;
    }

    @GetMapping("/tarifas")
    public List<Map<String, Object>> listarTarifas() {
        return tarifaService.listar().stream().map(ApiMapper::tarifa).toList();
    }

    @PostMapping("/tarifas")
    public Map<String, Object> crearTarifa(@RequestBody TarifaApiRequest request) {
        return ApiMapper.tarifa(tarifaService.crear(toTarifaRequest(request)));
    }

    @PutMapping("/tarifas")
    public Map<String, Object> actualizarTarifa(@RequestBody TarifaApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id de tarifa es obligatorio.");
        }
        return ApiMapper.tarifa(tarifaService.actualizar(request.id(), toTarifaRequest(request)));
    }

    @GetMapping("/tarifas/vigente")
    public Map<String, Object> obtenerTarifaVigente(@RequestParam Long sedeId, @RequestParam Long tipoRecursoId) {
        return ApiMapper.tarifa(tarifaService.obtenerVigente(sedeId, tipoRecursoId));
    }

    @GetMapping("/umbrales")
    public List<Map<String, Object>> listarUmbrales() {
        return umbralService.listar().stream().map(ApiMapper::umbral).toList();
    }

    @PostMapping("/umbrales")
    public Map<String, Object> crearUmbral(@RequestBody UmbralApiRequest request) {
        return ApiMapper.umbral(umbralService.crear(toUmbralRequest(request)));
    }

    @PutMapping("/umbrales")
    public Map<String, Object> actualizarUmbral(@RequestBody UmbralApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id del umbral es obligatorio.");
        }
        return ApiMapper.umbral(umbralService.actualizar(request.id(), toUmbralRequest(request)));
    }

    @DeleteMapping("/umbrales/{id}")
    public void eliminarUmbral(@PathVariable Long id) {
        umbralService.eliminar(id);
    }

    @GetMapping("/alertas")
    public List<Map<String, Object>> listarAlertas() {
        return alertasService.listar().stream().map(AlertaResponse::from).map(ApiMapper::alerta).toList();
    }

    @PostMapping("/alertas")
    public Map<String, Object> crearAlerta(@RequestBody AlertaApiRequest request) {
        return ApiMapper.alerta(AlertaResponse.from(alertasService.crearManual(request.mensaje(), NivelAlerta.valueOf(request.severidad()))));
    }

    @PatchMapping("/alertas/{id}/atender")
    public Map<String, Object> atenderAlerta(@PathVariable Long id) {
        return ApiMapper.alerta(AlertaResponse.from(alertasService.atender(id)));
    }

    @GetMapping("/alertas/sede/{idSede}")
    public List<Map<String, Object>> listarAlertasPorSede(@PathVariable Long idSede) {
        return alertasService.listarPorSede(idSede).stream().map(AlertaResponse::from).map(ApiMapper::alerta).toList();
    }

    private TarifaRequest toTarifaRequest(TarifaApiRequest request) {
        return new TarifaRequest(
                request.sedeId(),
                request.tipoRecursoId(),
                request.costoUnitario(),
                LocalDate.parse(request.fechaInicio()),
                request.fechaFin() == null || request.fechaFin().isBlank() ? null : LocalDate.parse(request.fechaFin()),
                request.vigente() == null || request.vigente() ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
    }

    private UmbralRequest toUmbralRequest(UmbralApiRequest request) {
        return new UmbralRequest(
                request.sedeId(),
                request.tipoRecursoId(),
                request.maximo(),
                null,
                LocalDate.now(),
                null,
                request.activo() == null || request.activo() ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
    }

    public record TarifaApiRequest(Long id, Long sedeId, Long tipoRecursoId, Long monedaId, BigDecimal costoUnitario,
            String fechaInicio, String fechaFin, Boolean vigente) {
    }

    public record UmbralApiRequest(Long id, Long sedeId, Long tipoRecursoId, BigDecimal minimo, BigDecimal maximo,
            String periodo, Boolean activo) {
    }

    public record AlertaApiRequest(Long sedeId, Long tipoRecursoId, String severidad, String mensaje) {
    }
}
