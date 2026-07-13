package com.example.luxury.api;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.api.dto.SessionEventResponse;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;
import com.example.luxury.dominios.sesion.EventoSesion;
import com.example.luxury.dominios.sesion.EventoSesionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
public class ApiSessionMonitoringController {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final GestionUsuarioService usuarioService;
    private final EventoSesionRepository eventoSesionRepository;

    public ApiSessionMonitoringController(GestionUsuarioService usuarioService,
            EventoSesionRepository eventoSesionRepository) {
        this.usuarioService = usuarioService;
        this.eventoSesionRepository = eventoSesionRepository;
    }

    @PostMapping("/sessions/events")
    public SessionEventResponse crearEvento(@Valid @RequestBody SessionEventRequest request, HttpServletRequest servletRequest) {
        UsuarioResponse usuario = usuarioService.obtener(request.usuarioId());

        EventoSesion evento = new EventoSesion();
        evento.setSesionId(request.sesionId());
        evento.setUsuarioId(usuario.getId());
        evento.setUsuarioNombre(usuario.getNombreCompleto());
        evento.setUsuarioRol(primerRol(usuario.getRoles()));
        evento.setTipo(request.tipo());
        evento.setSeveridad(severidad(request.tipo()));
        evento.setFechaEvento(LocalDateTime.now());
        evento.setRuta(request.ruta());
        evento.setIpOrigen(servletRequest.getRemoteAddr());
        evento.setUserAgent(servletRequest.getHeader("User-Agent"));
        evento.setDescripcion(request.descripcion());
        evento.setMetadata(serializarMetadata(request.metadata()));

        EventoSesion guardado = eventoSesionRepository.save(evento);
        return toDto(guardado, request.metadata());
    }

    @GetMapping("/session-monitoring/eventos")
    public List<SessionEventResponse> listarEventos() {
        return eventoSesionRepository.findAll().stream()
                .sorted((a, b) -> b.getFechaEvento().compareTo(a.getFechaEvento()))
                .map(e -> toDto(e, deserializarMetadata(e.getMetadata())))
                .toList();
    }

    @GetMapping("/session-monitoring/eventos/usuario/{id}")
    public List<SessionEventResponse> listarPorUsuario(@PathVariable Long id) {
        return eventoSesionRepository.findByUsuarioIdOrderByFechaEventoDesc(id).stream()
                .map(e -> toDto(e, deserializarMetadata(e.getMetadata())))
                .toList();
    }

    @GetMapping("/session-monitoring/eventos/tipo/{tipo}")
    public List<SessionEventResponse> listarPorTipo(@PathVariable String tipo) {
        return eventoSesionRepository.findByTipoOrderByFechaEventoDesc(tipo).stream()
                .map(e -> toDto(e, deserializarMetadata(e.getMetadata())))
                .toList();
    }

    @GetMapping("/session-monitoring/eventos/sesion/{sesionId}")
    public List<SessionEventResponse> listarPorSesion(@PathVariable String sesionId) {
        return eventoSesionRepository.findBySesionIdOrderByFechaEventoDesc(sesionId).stream()
                .map(e -> toDto(e, deserializarMetadata(e.getMetadata())))
                .toList();
    }

    private SessionEventResponse toDto(EventoSesion evento, Map<String, Object> metadata) {
        return new SessionEventResponse(
                evento.getId(),
                evento.getSesionId(),
                evento.getUsuarioId(),
                evento.getUsuarioNombre(),
                evento.getUsuarioRol(),
                evento.getTipo(),
                evento.getSeveridad(),
                evento.getFechaEvento(),
                evento.getRuta(),
                evento.getIpOrigen(),
                evento.getUserAgent(),
                evento.getDescripcion(),
                metadata);
    }

    private String serializarMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return JSON.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializarMetadata(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return JSON.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
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

    public record SessionEventRequest(
            @NotBlank(message = "sesionId es obligatorio.") String sesionId,
            @NotNull(message = "usuarioId es obligatorio.") Long usuarioId,
            @NotBlank(message = "tipo es obligatorio.") String tipo,
            String ruta,
            String descripcion,
            Map<String, Object> metadata) {
    }
}
