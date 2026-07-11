package com.example.luxury.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.consumo.dto.ConsumoRequest;
import com.example.luxury.dominios.consumo.service.ConsumoService;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.sede.dto.SedeRequest;
import com.example.luxury.dominios.sede.service.SedeService;

@RestController
@RequestMapping("/api")
public class ApiResourcesController {

    private final SedeService sedeService;
    private final TipoRecursoService tipoRecursoService;
    private final ConsumoService consumoService;

    public ApiResourcesController(SedeService sedeService, TipoRecursoService tipoRecursoService,
            ConsumoService consumoService) {
        this.sedeService = sedeService;
        this.tipoRecursoService = tipoRecursoService;
        this.consumoService = consumoService;
    }

    @GetMapping("/sedes")
    public List<Map<String, Object>> listarSedes() {
        return sedeService.listar().stream().map(ApiMapper::sede).toList();
    }

    @PostMapping("/sedes")
    public Map<String, Object> crearSede(@RequestBody SedeApiRequest request) {
        SedeRequest sedeRequest = new SedeRequest(request.nombre(), request.ciudad(), request.direccion(), EstadoRegistro.ACTIVO);
        return ApiMapper.sede(sedeService.crear(sedeRequest));
    }

    @GetMapping("/tipos-recurso")
    public List<Map<String, Object>> listarTiposRecurso() {
        return tipoRecursoService.listar().stream().map(ApiMapper::tipoRecurso).toList();
    }

    @GetMapping("/consumos")
    public List<Map<String, Object>> listarConsumos() {
        return consumoService.listar().stream().map(ApiMapper::consumo).toList();
    }

    @PostMapping("/consumos")
    public Map<String, Object> crearConsumo(@RequestBody ConsumoApiRequest request) {
        ConsumoRequest consumoRequest = new ConsumoRequest(
                request.sedeId(),
                request.tipoRecursoId(),
                request.cantidad(),
                fechaDesdePeriodo(request.periodo()),
                request.periodo());
        return ApiMapper.consumo(consumoService.registrar(consumoRequest));
    }

    @GetMapping("/consumos/{id}")
    public Map<String, Object> obtenerConsumo(@PathVariable Long id) {
        return ApiMapper.consumo(consumoService.obtener(id));
    }

    @GetMapping("/consumos/sede/{idSede}")
    public List<Map<String, Object>> listarConsumosPorSede(@PathVariable Long idSede) {
        return consumoService.listarPorSede(idSede).stream().map(ApiMapper::consumo).toList();
    }

    @GetMapping("/consumos/periodo/{periodo}")
    public List<Map<String, Object>> listarConsumosPorPeriodo(@PathVariable String periodo) {
        return consumoService.listarPorPeriodo(periodo).stream().map(ApiMapper::consumo).toList();
    }

    private LocalDate fechaDesdePeriodo(String periodo) {
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}")) {
            return LocalDate.now();
        }
        return LocalDate.parse(periodo + "-01");
    }

    public record SedeApiRequest(String nombre, String codigo, String direccion, String ciudad, String responsable) {
    }

    public record ConsumoApiRequest(Long sedeId, Long tipoRecursoId, String periodo, BigDecimal cantidad,
            BigDecimal costo, String observacion) {
    }
}
