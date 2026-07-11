package com.example.luxury.dominios.reporte.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.reporte.dto.ReporteMensualResponse;

import jakarta.persistence.EntityManager;

@Service
public class ReporteService {

	@Autowired
	private EntityManager entityManager;

	public List<ReporteMensualResponse> mensual(String periodo) {
		return consultaBase("where c.periodo = :periodo \n", periodo, null);
	}

	public List<ReporteMensualResponse> porSede(Long sedeId) {
		return consultaBase("where c.sede.id = :sedeId \n", null, sedeId);
	}

	private List<ReporteMensualResponse> consultaBase(String where, String periodo, Long sedeId) {
		var query = entityManager.createQuery("""
				select new com.example.luxury.dominios.reporte.dto.ReporteMensualResponse(
					c.periodo,
					c.sede.nombre,
					c.tipoRecurso.nombre,
					coalesce(sum(c.cantidadConsumida), 0),
					coalesce(sum(case when cc.moneda.codigo = 'PEN' then cc.montoCalculado else 0 end), 0),
					coalesce(sum(case when cc.moneda.codigo = 'USD' then cc.montoCalculado else 0 end), 0),
					coalesce(sum(case when cc.moneda.codigo = 'EUR' then cc.montoCalculado else 0 end), 0)
				)
				from Consumo c
				left join ConsumoCosto cc on cc.consumo = c
				""" + where + """
				group by c.periodo, c.sede.nombre, c.tipoRecurso.nombre
				order by c.periodo, c.sede.nombre, c.tipoRecurso.nombre
				""", ReporteMensualResponse.class);
		if (periodo != null) {
			query.setParameter("periodo", periodo);
		}
		if (sedeId != null) {
			query.setParameter("sedeId", sedeId);
		}
		return query.getResultList();
	}
}
