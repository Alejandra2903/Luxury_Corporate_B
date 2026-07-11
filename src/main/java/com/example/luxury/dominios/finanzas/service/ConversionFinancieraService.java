package com.example.luxury.dominios.finanzas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.consumo.model.Consumo;
import com.example.luxury.dominios.finanzas.model.ConsumoCosto;
import com.example.luxury.dominios.finanzas.model.Moneda;
import com.example.luxury.dominios.finanzas.model.TipoCambio;
import com.example.luxury.dominios.finanzas.repository.ConsumoCostoRepository;
import com.example.luxury.dominios.finanzas.repository.MonedaRepository;
import com.example.luxury.dominios.finanzas.repository.TipoCambioRepository;

@Service
public class ConversionFinancieraService {

	@Autowired
	private ConsumoCostoRepository consumoCostoRepository;

	@Autowired
	private MonedaRepository monedaRepository;

	@Autowired
	private TipoCambioRepository tipoCambioRepository;

	public List<ConsumoCosto> calcularYGuardarCostos(Consumo consumo, BigDecimal costoPen) {
		Moneda pen = buscarMoneda("PEN");
		List<ConsumoCosto> costos = new ArrayList<>();
		costos.add(crearCosto(consumo, pen, null, costoPen));
		costos.add(convertir(consumo, costoPen, "USD", consumo.getFechaConsumo()));
		costos.add(convertir(consumo, costoPen, "EUR", consumo.getFechaConsumo()));
		return consumoCostoRepository.saveAll(costos);
	}

	public List<ConsumoCosto> listarCostos(Long consumoId) {
		return consumoCostoRepository.findByConsumoId(consumoId);
	}

	private ConsumoCosto convertir(Consumo consumo, BigDecimal costoPen, String monedaDestino, LocalDate fecha) {
		Optional<TipoCambio> tipoCambioOptional = tipoCambioRepository.findVigente("PEN", monedaDestino, fecha, EstadoRegistro.ACTIVO);
		if (tipoCambioOptional.isEmpty()) {
			throw new ResourceNotFoundException("No existe tipo de cambio vigente PEN -> " + monedaDestino);
		}
		TipoCambio tipoCambio = tipoCambioOptional.get();
		BigDecimal monto = costoPen.multiply(tipoCambio.getValor()).setScale(4, RoundingMode.HALF_UP);
		return crearCosto(consumo, tipoCambio.getMonedaDestino(), tipoCambio, monto);
	}

	private ConsumoCosto crearCosto(Consumo consumo, Moneda moneda, TipoCambio tipoCambio, BigDecimal monto) {
		ConsumoCosto costo = new ConsumoCosto();
		costo.setConsumo(consumo);
		costo.setMoneda(moneda);
		costo.setTipoCambio(tipoCambio);
		costo.setMontoCalculado(monto.setScale(4, RoundingMode.HALF_UP));
		return costo;
	}

	private Moneda buscarMoneda(String codigo) {
		Optional<Moneda> monedaOptional = monedaRepository.findByCodigo(codigo);
		if (monedaOptional.isPresent()) {
			return monedaOptional.get();
		}
		throw new ResourceNotFoundException("Moneda no encontrada: " + codigo);
	}
}
