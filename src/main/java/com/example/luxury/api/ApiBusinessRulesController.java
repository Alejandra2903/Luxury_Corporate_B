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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import com.example.luxury.dominios.alerta.dto.AlertaResponse;
import com.example.luxury.dominios.alerta.service.ReglasAlertasService;
import com.example.luxury.dominios.auditoria.service.AuditoriaService;
import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.enums.NivelAlerta;
import com.example.luxury.dominios.seguridad.services.AuthenticatedUserService;
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
    private final AuditoriaService auditoriaService;
    private final AuthenticatedUserService authenticatedUserService;

    public ApiBusinessRulesController(TarifaService tarifaService, UmbralService umbralService,
            ReglasAlertasService alertasService, AuditoriaService auditoriaService,
            AuthenticatedUserService authenticatedUserService) {
        this.tarifaService = tarifaService;
        this.umbralService = umbralService;
        this.alertasService = alertasService;
        this.auditoriaService = auditoriaService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/tarifas")
    public List<Map<String, Object>> listarTarifas() {
        return tarifaService.listar().stream().map(ApiMapper::tarifa).toList();
    }

    @PostMapping("/tarifas")
    public Map<String, Object> crearTarifa(@Valid @RequestBody TarifaApiRequest request) {
        return ApiMapper.tarifa(tarifaService.crear(toTarifaRequest(request)));
    }

    @PutMapping("/tarifas")
    public Map<String, Object> actualizarTarifa(@Valid @RequestBody TarifaApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id de tarifa es obligatorio.");
        }
        return ApiMapper.tarifa(tarifaService.actualizar(request.id(), toTarifaRequest(request)));
    }

    @DeleteMapping("/tarifas/{id}")
    public void eliminarTarifa(@PathVariable Long id) {
        tarifaService.eliminar(id);
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
    public Map<String, Object> crearUmbral(@Valid @RequestBody UmbralApiRequest request) {
        return ApiMapper.umbral(umbralService.crear(toUmbralRequest(request)));
    }

    @PutMapping("/umbrales")
    public Map<String, Object> actualizarUmbral(@Valid @RequestBody UmbralApiRequest request) {
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
    public Map<String, Object> crearAlerta(@Valid @RequestBody AlertaApiRequest request) {
        return ApiMapper.alerta(AlertaResponse.from(alertasService.crearManual(request.mensaje(), NivelAlerta.valueOf(request.severidad()))));
    }

    @PatchMapping("/alertas/{id}/atender")
    public Map<String, Object> atenderAlerta(@PathVariable Long id) {
        Map<String, Object> resultado = ApiMapper.alerta(AlertaResponse.from(alertasService.atender(id)));
        auditar("ALERTAS", "ATENCION_ALERTA", "alertas", id, "Atencion de alerta " + id);
        return resultado;
    }

    @DeleteMapping("/alertas/{id}")
    public void eliminarAlerta(@PathVariable Long id) {
        alertasService.eliminar(id);
    }

    @GetMapping("/alertas/sede/{idSede}")
    public List<Map<String, Object>> listarAlertasPorSede(@PathVariable Long idSede) {
        return alertasService.listarPorSede(idSede).stream().map(AlertaResponse::from).map(ApiMapper::alerta).toList();
    }

    private void auditar(String modulo, String accion, String tabla, Long registroId, String descripcion) {
        try {
            auditoriaService.registrar(authenticatedUserService.actual(), modulo, accion, tabla, registroId, descripcion);
        } catch (RuntimeException ignored) {
            // El registro de auditoria no debe interrumpir la operacion principal.
        }
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

    public record TarifaApiRequest(
            Long id,
            @NotNull(message = "sedeId es obligatorio.") Long sedeId,
            @NotNull(message = "tipoRecursoId es obligatorio.") Long tipoRecursoId,
            Long monedaId,
            @NotNull(message = "costoUnitario es obligatorio.")
            @Positive(message = "El costo debe ser mayor a cero.") BigDecimal costoUnitario,
            @NotBlank(message = "fechaInicio es obligatoria.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Formato YYYY-MM-DD.") String fechaInicio,
            String fechaFin,
            Boolean vigente) {
    }

    public record UmbralApiRequest(
            Long id,
            @NotNull(message = "sedeId es obligatorio.") Long sedeId,
            @NotNull(message = "tipoRecursoId es obligatorio.") Long tipoRecursoId,
            BigDecimal minimo,
            @NotNull(message = "maximo es obligatorio.")
            @Positive(message = "El limite maximo debe ser mayor a cero.") BigDecimal maximo,
            String periodo,
            Boolean activo) {
    }

    public record AlertaApiRequest(
            Long sedeId,
            Long tipoRecursoId,
            @NotBlank(message = "La severidad es obligatoria.")
            @Pattern(regexp = "CRITICA|ALTA|MEDIA|BAJA", message = "Severidad invalida.") String severidad,
            @NotBlank(message = "El mensaje es obligatorio.") String mensaje) {
    }
}
