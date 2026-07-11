package com.example.luxury.dominios.alerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.alerta.model.Alerta;
import com.example.luxury.dominios.common.enums.NivelAlerta;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

	List<Alerta> findByConsumoSedeId(Long sedeId);

	long countByNivel(NivelAlerta nivel);
}
