package com.example.luxury.dominios.finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.finanzas.model.ConsumoCosto;

public interface ConsumoCostoRepository extends JpaRepository<ConsumoCosto, Long> {

	List<ConsumoCosto> findByConsumoId(Long consumoId);
}
