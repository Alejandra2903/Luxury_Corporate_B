package com.example.luxury.dominios.dashboard.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.dashboard.dto.ConsumoPorSedeResponse;
import com.example.luxury.dominios.dashboard.dto.CostoPorMesResponse;
import com.example.luxury.dominios.dashboard.dto.DashboardResumenResponse;

import jakarta.persistence.EntityManager;

@Service
public class DashboardService {

	@Autowired
	private EntityManager entityManager;

	public DashboardResumenResponse resumenGeneral() {
		long totalSedes = entityManager.createQuery("select count(s) from Sede s", Long.class).getSingleResult();
		long totalConsumos = entityManager.createQuery("select count(c) from Consumo c", Long.class).getSingleResult();
		long totalAlertas = entityManager.createQuery("select count(a) from Alerta a", Long.class).getSingleResult();
		BigDecimal pen = sumaPorMoneda("PEN");
		BigDecimal usd = sumaPorMoneda("USD");
		BigDecimal eur = sumaPorMoneda("EUR");
		return new DashboardResumenResponse(totalSedes, totalConsumos, totalAlertas, pen, usd, eur);
	}

	public List<ConsumoPorSedeResponse> consumoPorSede() {
		return entityManager.createQuery("""
				select new com.example.luxury.dominios.dashboard.dto.ConsumoPorSedeResponse(c.sede.nombre, coalesce(sum(c.cantidadConsumida), 0))
				from Consumo c
				group by c.sede.nombre
				order by c.sede.nombre
				""", ConsumoPorSedeResponse.class).getResultList();
	}

	public List<CostoPorMesResponse> costosPorMes() {
		return entityManager.createQuery("""
				select new com.example.luxury.dominios.dashboard.dto.CostoPorMesResponse(c.consumo.periodo, c.moneda.codigo, coalesce(sum(c.montoCalculado), 0))
				from ConsumoCosto c
				group by c.consumo.periodo, c.moneda.codigo
				order by c.consumo.periodo, c.moneda.codigo
				""", CostoPorMesResponse.class).getResultList();
	}

	private BigDecimal sumaPorMoneda(String codigo) {
		BigDecimal value = entityManager.createQuery("""
				select coalesce(sum(c.montoCalculado), 0)
				from ConsumoCosto c
				where c.moneda.codigo = :codigo
				""", BigDecimal.class)
				.setParameter("codigo", codigo)
				.getSingleResult();
		return value == null ? BigDecimal.ZERO : value;
	}
}
