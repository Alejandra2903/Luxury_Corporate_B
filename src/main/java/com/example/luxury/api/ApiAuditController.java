package com.example.luxury.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
