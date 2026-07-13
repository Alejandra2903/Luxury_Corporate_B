package com.example.luxury.dominios.dashboard.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.dashboard.dto.ConsumoPorSedeResponse;
import com.example.luxury.dominios.dashboard.dto.CostoPorMesResponse;
import com.example.luxury.dominios.dashboard.dto.CostoPorMesSeparadoResponse;
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
				select new com.example.luxury.dominios.dashboard.dto.ConsumoPorSedeResponse(
					c.sede.id,
					c.sede.nombre,
					coalesce(sum(case when upper(c.tipoRecurso.nombre) like '%LUZ%' or upper(c.tipoRecurso.nombre) like '%ENERG%' then c.cantidadConsumida else 0 end), 0),
					coalesce(sum(case when upper(c.tipoRecurso.nombre) like '%AGUA%' then c.cantidadConsumida else 0 end), 0)
				)
				from Consumo c
				group by c.sede.id, c.sede.nombre
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

	public List<CostoPorMesSeparadoResponse> costosPorMesSeparado() {
		return entityManager.createQuery("""
				select new com.example.luxury.dominios.dashboard.dto.CostoPorMesSeparadoResponse(
					cc.consumo.periodo,
					coalesce(sum(case when upper(cc.consumo.tipoRecurso.nombre) like '%LUZ%' or upper(cc.consumo.tipoRecurso.nombre) like '%ENERG%' then cc.montoCalculado else 0 end), 0),
					coalesce(sum(case when upper(cc.consumo.tipoRecurso.nombre) like '%AGUA%' then cc.montoCalculado else 0 end), 0),
					coalesce(sum(cc.montoCalculado), 0)
				)
				from ConsumoCosto cc
				where cc.moneda.codigo = 'PEN'
				group by cc.consumo.periodo
				order by cc.consumo.periodo
				""", CostoPorMesSeparadoResponse.class).getResultList();
	}

	public double cumplimientoUmbralesPorcentaje() {
		long total = entityManager.createQuery("select count(c) from Consumo c", Long.class).getSingleResult();
		if (total == 0) {
			return 100.0;
		}
		long dentroDelLimite = entityManager.createQuery("""
				select count(c)
				from Consumo c, Umbral u
				where u.sede.id = c.sede.id
				  and u.tipoRecurso.id = c.tipoRecurso.id
				  and u.estado = com.example.luxury.dominios.common.enums.EstadoRegistro.ACTIVO
				  and u.limiteConsumo is not null
				  and c.cantidadConsumida <= u.limiteConsumo
				""", Long.class).getSingleResult();
		return Math.round((dentroDelLimite * 1000.0) / total) / 10.0;
	}

	public String periodoMasReciente() {
		return entityManager.createQuery("""
				select c.periodo from Consumo c order by c.periodo desc
				""", String.class)
				.setMaxResults(1)
				.getResultList()
				.stream()
				.findFirst()
				.orElse(YearMonth.now().toString());
	}

	public BigDecimal consumoKwhPorPeriodo(String tipoBuscar, String periodo) {
		BigDecimal value = entityManager.createQuery("""
				select coalesce(sum(c.cantidadConsumida), 0)
				from Consumo c
				where upper(c.tipoRecurso.nombre) like :tipo
				  and c.periodo = :periodo
				""", BigDecimal.class)
				.setParameter("tipo", "%" + tipoBuscar.toUpperCase() + "%")
				.setParameter("periodo", periodo)
				.getSingleResult();
		return value == null ? BigDecimal.ZERO : value;
	}

	public BigDecimal costoPenPorPeriodo(String periodo) {
		BigDecimal value = entityManager.createQuery("""
				select coalesce(sum(cc.montoCalculado), 0)
				from ConsumoCosto cc
				where cc.moneda.codigo = 'PEN'
				  and cc.consumo.periodo = :periodo
				""", BigDecimal.class)
				.setParameter("periodo", periodo)
				.getSingleResult();
		return value == null ? BigDecimal.ZERO : value;
	}

	public BigDecimal variacionCostoPorcentaje(String periodo) {
		YearMonth ym = YearMonth.parse(periodo);
		String periodoAnterior = ym.minusMonths(1).toString();
		BigDecimal costoActual = costoPenPorPeriodo(periodo);
		BigDecimal costoAnterior = costoPenPorPeriodo(periodoAnterior);
		if (costoAnterior.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return costoActual.subtract(costoAnterior)
				.divide(costoAnterior, 4, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100))
				.setScale(1, RoundingMode.HALF_UP);
	}

	public Map<Long, Long> alertasPorSede() {
		List<Object[]> rows = entityManager.createQuery("""
				select a.consumo.sede.id, count(a)
				from Alerta a
				where a.consumo is not null
				group by a.consumo.sede.id
				""", Object[].class).getResultList();
		return rows.stream().collect(Collectors.toMap(
				r -> (Long) r[0],
				r -> (Long) r[1]));
	}

	public Map<Long, BigDecimal> costoPenPorSede() {
		List<Object[]> rows = entityManager.createQuery("""
				select cc.consumo.sede.id, coalesce(sum(cc.montoCalculado), 0)
				from ConsumoCosto cc
				where cc.moneda.codigo = 'PEN'
				group by cc.consumo.sede.id
				""", Object[].class).getResultList();
		return rows.stream().collect(Collectors.toMap(
				r -> (Long) r[0],
				r -> (BigDecimal) r[1]));
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
