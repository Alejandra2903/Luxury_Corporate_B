package com.example.luxury.dominios.umbral.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.umbral.model.Umbral;

public interface UmbralRepository extends JpaRepository<Umbral, Long> {

	@Query("""
			select u from Umbral u
			where u.sede.id = :sedeId
			  and u.tipoRecurso.id = :tipoRecursoId
			  and u.estado = :estado
			  and u.fechaInicio <= :fecha
			  and (u.fechaFin is null or u.fechaFin >= :fecha)
			order by u.fechaInicio desc
			limit 1
			""")
	Optional<Umbral> findVigente(
			@Param("sedeId") Long sedeId,
			@Param("tipoRecursoId") Long tipoRecursoId,
			@Param("fecha") LocalDate fecha,
			@Param("estado") EstadoRegistro estado);
}
