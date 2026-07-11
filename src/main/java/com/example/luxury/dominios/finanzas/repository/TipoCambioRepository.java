package com.example.luxury.dominios.finanzas.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.finanzas.model.TipoCambio;

public interface TipoCambioRepository extends JpaRepository<TipoCambio, Long> {

	@Query("""
			select tc from TipoCambio tc
			where tc.monedaOrigen.codigo = :origen
			  and tc.monedaDestino.codigo = :destino
			  and tc.estado = :estado
			  and tc.fecha <= :fecha
			order by tc.fecha desc
			limit 1
			""")
	Optional<TipoCambio> findVigente(
			@Param("origen") String origen,
			@Param("destino") String destino,
			@Param("fecha") LocalDate fecha,
			@Param("estado") EstadoRegistro estado);
}
