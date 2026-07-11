package com.example.luxury.dominios.auditoria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luxury.dominios.auditoria.model.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

	List<Auditoria> findByUsuarioId(Long usuarioId);

	List<Auditoria> findByModuloIgnoreCase(String modulo);
}
