package com.example.luxury.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.finanzas.dto.MonedaRequest;
import com.example.luxury.dominios.finanzas.dto.MonedaResponse;
import com.example.luxury.dominios.finanzas.dto.TipoCambioRequest;
import com.example.luxury.dominios.finanzas.service.MonedaService;
import com.example.luxury.dominios.finanzas.service.TipoCambioService;

@RestController
@RequestMapping("/api")
public class ApiFinancialController {

    private final MonedaService monedaService;
    private final TipoCambioService tipoCambioService;

    public ApiFinancialController(MonedaService monedaService, TipoCambioService tipoCambioService) {
        this.monedaService = monedaService;
        this.tipoCambioService = tipoCambioService;
    }

    @GetMapping("/monedas")
    public List<Map<String, Object>> listarMonedas() {
        return monedaService.listar().stream().map(ApiMapper::moneda).toList();
    }

    @PostMapping("/monedas")
    public Map<String, Object> crearMoneda(@RequestBody MonedaApiRequest request) {
        return ApiMapper.moneda(monedaService.crear(new MonedaRequest(request.codigo(), request.nombre())));
    }

    @GetMapping("/tipos-cambio")
    public List<Map<String, Object>> listarTiposCambio() {
        List<MonedaResponse> monedas = monedaService.listar();
        return tipoCambioService.listar().stream().map(cambio -> ApiMapper.tipoCambio(cambio, monedas)).toList();
    }

    @PostMapping("/tipos-cambio")
    public Map<String, Object> crearTipoCambio(@RequestBody TipoCambioApiRequest request) {
        List<MonedaResponse> monedas = monedaService.listar();
        return ApiMapper.tipoCambio(tipoCambioService.crear(toRequest(request, monedas)), monedas);
    }

    @PutMapping("/tipos-cambio")
    public Map<String, Object> actualizarTipoCambio(@RequestBody TipoCambioApiRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("El id del tipo de cambio es obligatorio.");
        }
        List<MonedaResponse> monedas = monedaService.listar();
        return ApiMapper.tipoCambio(tipoCambioService.actualizar(request.id(), toRequest(request, monedas)), monedas);
    }

    private TipoCambioRequest toRequest(TipoCambioApiRequest request, List<MonedaResponse> monedas) {
        return new TipoCambioRequest(
                codigoMoneda(request.monedaOrigenId(), monedas),
                codigoMoneda(request.monedaDestinoId(), monedas),
                request.tasa(),
                LocalDate.parse(request.fechaVigencia()),
                request.activo() == null || request.activo() ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
    }

    private String codigoMoneda(Long id, List<MonedaResponse> monedas) {
        return monedas.stream()
                .filter(moneda -> moneda.getId().equals(id))
                .map(MonedaResponse::getCodigo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Moneda no encontrada."));
    }

    public record MonedaApiRequest(String codigo, String nombre, String simbolo) {
    }

    public record TipoCambioApiRequest(Long id, Long monedaOrigenId, Long monedaDestinoId, BigDecimal tasa,
            String fechaVigencia, String fuente, Boolean activo) {
    }
}
