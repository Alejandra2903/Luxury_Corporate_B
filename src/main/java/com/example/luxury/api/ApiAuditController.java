package com.example.luxury.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.auditoria.service.AuditoriaService;
import com.example.luxury.dominios.eventoacceso.service.EventoAccesoService;

@RestController
@RequestMapping("/api")
public class ApiAuditController {

    private final AuditoriaService auditoriaService;
    private final EventoAccesoService eventoAccesoService;

    public ApiAuditController(AuditoriaService auditoriaService, EventoAccesoService eventoAccesoService) {
        this.auditoriaService = auditoriaService;
        this.eventoAccesoService = eventoAccesoService;
    }

    @GetMapping("/auditorias")
    public List<Map<String, Object>> listarAuditorias() {
        return auditoriaService.listar().stream().map(ApiMapper::auditoria).toList();
    }

    @GetMapping("/auditorias/paginado")
    public Map<String, Object> listarAuditoriasPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 0);
        List<Map<String, Object>> todos = auditoriaService.listar().stream().map(ApiMapper::auditoria).toList();
        int from = Math.min(safePage * safeSize, todos.size());
        int to = Math.min(from + safeSize, todos.size());
        int totalPages = (int) Math.ceil((double) todos.size() / safeSize);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", todos.subList(from, to));
        data.put("page", safePage);
        data.put("size", safeSize);
        data.put("totalElements", todos.size());
        data.put("totalPages", totalPages);
        data.put("first", safePage == 0);
        data.put("last", safePage >= totalPages - 1);
        return data;
    }

    @GetMapping("/auditorias/usuario/{id}")
    public List<Map<String, Object>> listarAuditoriasPorUsuario(@PathVariable Long id) {
        return auditoriaService.listarPorUsuario(id).stream().map(ApiMapper::auditoria).toList();
    }

    @GetMapping("/auditorias/modulo/{modulo}")
    public List<Map<String, Object>> listarAuditoriasPorModulo(@PathVariable String modulo) {
        return auditoriaService.listarPorModulo(modulo).stream().map(ApiMapper::auditoria).toList();
    }

    @GetMapping("/eventos-acceso")
    public List<Map<String, Object>> listarEventosAcceso() {
        return eventoAccesoService.listar().stream().map(ApiMapper::eventoAcceso).toList();
    }
}
