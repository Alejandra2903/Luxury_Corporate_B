package com.example.luxury.dominios.consumo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.consumo.model.Consumo;

public interface ConsumoRepository extends JpaRepository<Consumo, Long> {

	List<Consumo> findBySedeId(Long sedeId);

	List<Consumo> findByPeriodo(String periodo);

	Page<Consumo> findAll(Pageable pageable);
}
