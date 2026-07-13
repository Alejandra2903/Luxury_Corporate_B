package com.example.luxury.dominios.sesion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoSesionRepository extends JpaRepository<EventoSesion, Long> {

    List<EventoSesion> findByUsuarioIdOrderByFechaEventoDesc(Long usuarioId);

    List<EventoSesion> findByTipoOrderByFechaEventoDesc(String tipo);

    List<EventoSesion> findBySesionIdOrderByFechaEventoDesc(String sesionId);
}
