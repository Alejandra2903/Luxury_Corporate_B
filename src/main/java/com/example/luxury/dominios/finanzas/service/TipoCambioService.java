package com.example.luxury.dominios.finanzas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.finanzas.dto.TipoCambioRequest;
import com.example.luxury.dominios.finanzas.dto.TipoCambioResponse;
import com.example.luxury.dominios.finanzas.model.TipoCambio;
import com.example.luxury.dominios.finanzas.repository.TipoCambioRepository;

@Service
public class TipoCambioService {

	@Autowired
	private TipoCambioRepository tipoCambioRepository;

	@Autowired
	private MonedaService monedaService;

	public List<TipoCambioResponse> listar() {
		return tipoCambioRepository.findAll().stream().map(TipoCambioResponse::from).toList();
	}

	public TipoCambioResponse crear(TipoCambioRequest request) {
		TipoCambio tipoCambio = new TipoCambio();
		aplicar(tipoCambio, request);
		return TipoCambioResponse.from(tipoCambioRepository.save(tipoCambio));
	}

	public TipoCambioResponse actualizar(Long id, TipoCambioRequest request) {
		TipoCambio tipoCambio = buscar(id);
		aplicar(tipoCambio, request);
		return TipoCambioResponse.from(tipoCambioRepository.save(tipoCambio));
	}

	public TipoCambio buscar(Long id) {
		Optional<TipoCambio> tipoCambioOptional = tipoCambioRepository.findById(id);
		if (tipoCambioOptional.isPresent()) {
			return tipoCambioOptional.get();
		}
		throw new ResourceNotFoundException("Tipo de cambio no encontrado");
	}

	private void aplicar(TipoCambio tipoCambio, TipoCambioRequest request) {
		tipoCambio.setMonedaOrigen(monedaService.buscarPorCodigo(request.getMonedaOrigen()));
		tipoCambio.setMonedaDestino(monedaService.buscarPorCodigo(request.getMonedaDestino()));
		tipoCambio.setValor(request.getValor());
		tipoCambio.setFecha(request.getFecha());
		tipoCambio.setEstado(request.getEstado() == null ? EstadoRegistro.ACTIVO : request.getEstado());
	}
}
