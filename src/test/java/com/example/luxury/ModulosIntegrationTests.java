package com.example.luxury;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc
class ModulosIntegrationTests {

	@Autowired
	private MockMvc mockMvc;



	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void sedesListaMvcRenderizaVista() throws Exception {
		mockMvc.perform(get("/sedes"))
				.andExpect(status().isOk())
				.andExpect(view().name("sedes/lista"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void sedesRegistrarPorFormularioRedirige() throws Exception {
		mockMvc.perform(post("/sedes")
				.param("nombre", "Sede Arequipa")
				.param("ciudad", "Arequipa")
				.param("direccion", "Av. Test 123")
				.param("estado", "ACTIVO"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/sedes*"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiSedesDevuelveArray() throws Exception {
		mockMvc.perform(get("/api/sedes"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@WithMockUser(username = "12345678", roles = "AUDITOR")
	void apiSedesAuditorRecibe403() throws Exception {
	
		mockMvc.perform(get("/api/sedes"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403));
	}


	@Test
	@WithMockUser(username = "00000000", roles = "OPERADOR")
	void tiposRecursoListaMvcFallaPorTemplateFaltante() {
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
				() -> mockMvc.perform(get("/tipos-recurso")));
		org.junit.jupiter.api.Assertions.assertTrue(
				ex.getMessage() != null && ex.getMessage().contains("tipos-recurso/lista"),
				"Se esperaba un error de resolucion de plantilla 'tipos-recurso/lista', pero fue: " + ex.getMessage());
	}


	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiTiposRecursoCrearExitoso() throws Exception {
		mockMvc.perform(post("/api/tipos-recurso")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "nombre": "Internet", "unidad": "Mbps" }
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nombre").value("Internet"));
	}

	

	@Test
	@WithMockUser(username = "00000000", roles = "GERENTE")
	void tarifasListaMvcGerentePermitido() throws Exception {
		mockMvc.perform(get("/tarifas"))
				.andExpect(status().isOk())
				.andExpect(view().name("tarifas/lista"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiTarifaVigenteDevuelveDatos() throws Exception {
		mockMvc.perform(get("/api/tarifas/vigente").param("sedeId", "1").param("tipoRecursoId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.costoUnitario").exists())
				.andExpect(jsonPath("$.vigente").value(true));
	}

	@Test
	@WithMockUser(username = "12345678", roles = "OPERADOR")
	void apiTarifasOperadorRecibe403() throws Exception {
		mockMvc.perform(get("/api/tarifas"))
				.andExpect(status().isForbidden());
	}



	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiUmbralesCrearExitoso() throws Exception {
		mockMvc.perform(post("/api/umbrales")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"sedeId": 1,
							"tipoRecursoId": 1,
							"minimo": 100.00,
							"maximo": 500.00,
							"periodo": "2026-06",
							"activo": true
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber());
	}

	

	@Test
	@WithMockUser(username = "00000000", roles = "GERENTE")
	void alertasListaMvcRenderiza() throws Exception {
		mockMvc.perform(get("/alertas"))
				.andExpect(status().isOk())
				.andExpect(view().name("alertas/lista"));
	}

	

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiMonedasDevuelveArrayConSemillas() throws Exception {
		mockMvc.perform(get("/api/monedas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[?(@.codigo == 'PEN')]").exists());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiMonedaCodigoInvalidoDevuelve400() throws Exception {
		mockMvc.perform(post("/api/monedas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "codigo": "dolar", "nombre": "Dolar mal formado" }
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fields").exists());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiTiposCambioCrearExitoso() throws Exception {
		
		mockMvc.perform(post("/api/tipos-cambio")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"monedaOrigenId": 1,
							"monedaDestinoId": 2,
							"tasa": 3.75,
							"fechaVigencia": "2026-06-01",
							"activo": true
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber());
	}

	@Test
	@WithMockUser(username = "12345678", roles = "ANALISTA")
	void apiMonedasAnalistaRecibe403() throws Exception {
		
		mockMvc.perform(get("/api/monedas"))
				.andExpect(status().isForbidden());
	}


	@Test
	@WithMockUser(username = "00000000", roles = "AUDITOR")
	void auditoriasListaMvcAuditorPermitido() throws Exception {
		mockMvc.perform(get("/auditorias"))
				.andExpect(status().isOk())
				.andExpect(view().name("auditorias/lista"));
	}

	@Test
	@WithMockUser(username = "12345678", roles = "GERENTE")
	void auditoriasMvcGerenteRedirigeAError() throws Exception {
		
		mockMvc.perform(get("/auditorias"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/error*"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiEventosAccesoDevuelveArray() throws Exception {
		mockMvc.perform(get("/api/eventos-acceso"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}



	@Test
	@WithMockUser(username = "00000000", roles = "ANALISTA")
	void apiDashboardConsumoPorSedeDevuelveArray() throws Exception {
		mockMvc.perform(get("/api/dashboard/consumo-por-sede"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "AUDITOR")
	void apiDashboardCostosPorMesDevuelveArray() throws Exception {
		mockMvc.perform(get("/api/dashboard/costos-por-mes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "OPERADOR")
	void dashboardVistaOperadorRedirigeAError() throws Exception {
		
		mockMvc.perform(get("/dashboard"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/error*"));
	}

	

	@Test
	@WithMockUser(username = "00000000", roles = "ANALISTA")
	void apiReporteMensualJsonDevuelveKpis() throws Exception {
		mockMvc.perform(get("/api/reportes/mensual").param("periodo", "2026-05"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.periodo").value("2026-05"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "GERENTE")
	void reportesVistaMvcRenderiza() throws Exception {
		mockMvc.perform(get("/reportes/mensual").param("periodo", "2026-05"))
				.andExpect(status().isOk())
				.andExpect(view().name("reportes/mensual"));
	}

	

	@Test
	@WithMockUser(username = "00000000", roles = "ANALISTA")
	void apiSessionsEventsAceptaCualquierUsuarioAutenticado() throws Exception {
		mockMvc.perform(post("/api/sessions/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"sesionId": "sess-test-1",
							"usuarioId": 1,
							"tipo": "NAVEGACION",
							"ruta": "/dashboard",
							"descripcion": "Prueba de integracion"
						}
						"""))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "12345678", roles = "GERENTE")
	void apiSessionMonitoringEventosSoloAdminRecibe403ParaGerente() throws Exception {
		mockMvc.perform(get("/api/session-monitoring/eventos"))
				.andExpect(status().isForbidden());
	}



	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiUsuariosPatchCambiaEstadoActivo() throws Exception {
		
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": 1, \"activo\": false }"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activo").value(false));

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"id\": 1, \"activo\": true }"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activo").value(true));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void sedeEditarYEliminarPorFormularioFuncionan() throws Exception {
		
		mockMvc.perform(get("/sedes/2/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("sedes/formulario"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Piura")));

		mockMvc.perform(post("/sedes/2/editar")
				.param("nombre", "Sede Piura Norte")
				.param("ciudad", "Piura")
				.param("direccion", "Av. Grau 200")
				.param("estado", "ACTIVO"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/sedes*"));

		mockMvc.perform(post("/sedes/2/eliminar"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/sedes*"));

		
		mockMvc.perform(get("/api/sedes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == 2)].activa").value(
						org.hamcrest.Matchers.hasItem(false)));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void consumoQueSuperaUmbralGeneraAlertaAutomatica() throws Exception {
	
		mockMvc.perform(post("/api/consumos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"sedeId": 1,
							"tipoRecursoId": 1,
							"periodo": "2026-07",
							"cantidad": 5000
						}
						"""))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/alertas/sede/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.severidad == 'ALTA')]").exists())
				.andExpect(jsonPath("$[?(@.severidad == 'CRITICA')]").exists())
				.andExpect(jsonPath("$[?(@.mensaje == 'El consumo supera el limite configurado')]").exists())
				.andExpect(jsonPath("$[?(@.mensaje == 'El costo PEN supera el presupuesto configurado')]").exists());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiAlertaAtenderCambiaEstado() throws Exception {
		String body = mockMvc.perform(post("/api/alertas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "severidad": "MEDIA", "mensaje": "Alerta para atender" }
						"""))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Long id = com.jayway.jsonpath.JsonPath.read(body, "$.id").toString().matches("\\d+")
				? Long.valueOf(com.jayway.jsonpath.JsonPath.read(body, "$.id").toString())
				: null;

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/alertas/" + id + "/atender"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.atendida").value(true));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "GERENTE")
	void reportesVistaConPeriodoInvalidoMuestraErrorSinRomper() throws Exception {
		mockMvc.perform(get("/reportes/mensual").param("periodo", "no-es-un-periodo"))
				.andExpect(status().isOk())
				.andExpect(view().name("reportes/mensual"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(
						"El formato del periodo es incorrecto")));
	}




	@Test
	void apiRegistroPublicoCreaUsuarioOperador() throws Exception {
		mockMvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"nombres": "Registro",
							"apellidos": "Publico",
							"tipoDocumento": "DNI",
							"numeroDocumento": "99988877",
							"telefono": "955512345",
							"correo": "registro.publico@luxury.com",
							"contrasena": "clave123"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.roles").value("OPERADOR"));
	}
}