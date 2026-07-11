package com.example.luxury.dominios.recurso.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.recurso.model.TipoRecurso;

public interface TipoRecursoRepository extends JpaRepository<TipoRecurso, Long> {

	Optional<TipoRecurso> findByNombre(String nombre);
}
