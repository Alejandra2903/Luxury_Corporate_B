package com.example.luxury.dominios.eventoacceso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.eventoacceso.model.EventoAcceso;

public interface EventoAccesoRepository extends JpaRepository<EventoAcceso, Long> {
}
