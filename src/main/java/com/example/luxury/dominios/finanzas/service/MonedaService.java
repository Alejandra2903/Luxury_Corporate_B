package com.example.luxury.dominios.finanzas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.finanzas.dto.MonedaRequest;
import com.example.luxury.dominios.finanzas.dto.MonedaResponse;
import com.example.luxury.dominios.finanzas.model.Moneda;
import com.example.luxury.dominios.finanzas.repository.MonedaRepository;

@Service
public class MonedaService {

	@Autowired
	private MonedaRepository monedaRepository;

	public List<MonedaResponse> listar() {
		return monedaRepository.findAll().stream().map(MonedaResponse::from).toList();
	}

	public MonedaResponse crear(MonedaRequest request) {
		Moneda moneda = new Moneda();
		moneda.setCodigo(request.getCodigo().toUpperCase());
		moneda.setNombre(request.getNombre());
		return MonedaResponse.from(monedaRepository.save(moneda));
	}

	public Moneda buscarPorCodigo(String codigo) {
		Optional<Moneda> monedaOptional = monedaRepository.findByCodigo(codigo.toUpperCase());
		if (monedaOptional.isPresent()) {
			return monedaOptional.get();
		}
		throw new ResourceNotFoundException("Moneda no encontrada: " + codigo);
	}
}
