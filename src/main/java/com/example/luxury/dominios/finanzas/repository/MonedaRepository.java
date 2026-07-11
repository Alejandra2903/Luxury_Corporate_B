package com.example.luxury.dominios.finanzas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.finanzas.model.Moneda;

public interface MonedaRepository extends JpaRepository<Moneda, Long> {

	Optional<Moneda> findByCodigo(String codigo);
}
