package com.example.luxury.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;

@RestController
@RequestMapping("/api")
public class ApiSessionMonitoringController {

    private static final List<Map<String, Object>> EVENTOS = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicLong SECUENCIA = new AtomicLong(1);

    private final GestionUsuarioService usuarioService;

    public ApiSessionMonitoringController(GestionUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/sessions/events")
    public Map<String, Object> crearEvento(@RequestBody SessionEventRequest request, HttpServletRequest servletRequest) {
        UsuarioResponse usuario = usuarioService.obtener(request.usuarioId());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", SECUENCIA.getAndIncrement());
        data.put("sesionId", request.sesionId());
        data.put("usuarioId", usuario.getId());
        data.put("usuarioNombre", usuario.getNombreCompleto());
        data.put("usuarioRol", primerRol(usuario.getRoles()));
        data.put("tipo", request.tipo());
        data.put("severidad", severidad(request.tipo()));
        data.put("fechaEvento", LocalDateTime.now());
        data.put("ruta", request.ruta());
        data.put("ipOrigen", servletRequest.getRemoteAddr());
        data.put("userAgent", servletRequest.getHeader("User-Agent"));
        data.put("descripcion", request.descripcion());
        data.put("metadata", request.metadata());

        EVENTOS.add(0, data);
        return data;
    }

    @GetMapping("/session-monitoring/eventos")
    public List<Map<String, Object>> listarEventos() {
        return new ArrayList<>(EVENTOS);
    }

    @GetMapping("/session-monitoring/eventos/usuario/{id}")
    public List<Map<String, Object>> listarPorUsuario(@PathVariable Long id) {
        return EVENTOS.stream().filter(evento -> id.equals(evento.get("usuarioId"))).toList();
    }

    @GetMapping("/session-monitoring/eventos/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        return EVENTOS.stream().filter(evento -> tipo.equals(evento.get("tipo"))).toList();
    }

    @GetMapping("/session-monitoring/eventos/sesion/{sesionId}")
    public List<Map<String, Object>> listarPorSesion(@PathVariable String sesionId) {
        return EVENTOS.stream().filter(evento -> sesionId.equals(evento.get("sesionId"))).toList();
    }

    private String primerRol(String roles) {
        if (roles == null || roles.isBlank()) {
            return "OPERADOR";
        }
        return roles.split(",")[0].trim();
    }

    private String severidad(String tipo) {
        if ("MANIPULACION_DATOS_FINANCIEROS".equals(tipo) || "GESTION_USUARIOS".equals(tipo)) {
            return "ALTA";
        }
        if ("INACTIVIDAD".equals(tipo) || "SALIDA_VIEWPORT".equals(tipo)) {
            return "MEDIA";
        }
        return "INFO";
    }

    public record SessionEventRequest(String sesionId, Long usuarioId, String tipo, String ruta, String descripcion,
            Map<String, Object> metadata) {
    }
}
