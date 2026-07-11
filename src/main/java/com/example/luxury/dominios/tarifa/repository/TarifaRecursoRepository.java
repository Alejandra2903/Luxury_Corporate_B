package com.example.luxury.dominios.tarifa.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.tarifa.model.TarifaRecurso;

public interface TarifaRecursoRepository extends JpaRepository<TarifaRecurso, Long> {

	@Query("""
			select t from TarifaRecurso t
			where t.sede.id = :sedeId
			  and t.tipoRecurso.id = :tipoRecursoId
			  and t.estado = :estado
			  and t.fechaInicio <= :fecha
			  and (t.fechaFin is null or t.fechaFin >= :fecha)
			order by t.fechaInicio desc
			limit 1
			""")
	Optional<TarifaRecurso> findVigente(
			@Param("sedeId") Long sedeId,
			@Param("tipoRecursoId") Long tipoRecursoId,
			@Param("fecha") LocalDate fecha,
			@Param("estado") EstadoRegistro estado);
}
