package com.example.luxury;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndConsumoIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void muestraFormularioLogin() throws Exception {
		mockMvc.perform(get("/auth/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/auth/login\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"identificador\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"contrasena\"")));
	}

	@Test
	void muestraFormularioRegistro() throws Exception {
		mockMvc.perform(get("/auth/registro"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/registro"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/auth/registro\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"nombres\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"contrasena\"")));
	}

	@Test
	void loginCorrectoRedirigeAlDashboard() throws Exception {
		mockMvc.perform(post("/auth/login")
				.param("identificador", "admin@luxury.com")
				.param("contrasena", "admin123"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/dashboard"))
				.andExpect(cookie().exists("tokenAcceso"));
	}

	@Test
	void apiLoginDevuelveTokenYUsuario() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"identificador":"admin@luxury.com","contrasena":"admin123"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isString())
				.andExpect(jsonPath("$.tipo").value("Bearer"))
				.andExpect(jsonPath("$.usuario.roles").value("ADMIN"));
	}

	@Test
	void apiProtegidaSinTokenDevuelveJsonNoAutorizado() throws Exception {
		mockMvc.perform(get("/api/dashboard/resumen"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	void apiLoginCredencialesInvalidasDevuelve401() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"identificador":"admin@luxury.com","contrasena":"claveIncorrecta"}
						"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiDashboardResumenDevuelvePeriodoDinamico() throws Exception {
		mockMvc.perform(get("/api/dashboard/resumen"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.periodo").isString())
				.andExpect(jsonPath("$.monedaBase").value("PEN"))
				.andExpect(jsonPath("$.sedesActivas").isNumber());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiConsumosDevuelveArray() throws Exception {
		mockMvc.perform(get("/api/consumos"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiCrearUsuarioExitoso() throws Exception {
		mockMvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"nombres":"API",
							"apellidos":"Test",
							"tipoDocumento":"DNI",
							"numeroDocumento":"11223344",
							"telefono":"988877766",
							"correo":"api.test@luxury.com",
							"contrasena":"apitest123",
							"roles":"ANALISTA",
							"activo":true
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.correo").value("api.test@luxury.com"))
				.andExpect(jsonPath("$.roles").value("ANALISTA"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiCrearUsuarioConDatosInvalidosDevuelveErrorDeCampo() throws Exception {
		mockMvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"nombres":"",
							"apellidos":"Test",
							"tipoDocumento":"DNI",
							"numeroDocumento":"abc",
							"telefono":"12",
							"correo":"correo-mal",
							"contrasena":"123",
							"roles":"ANALISTA"
						}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.fields").exists());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiCrearConsumoExitoso() throws Exception {
		mockMvc.perform(post("/api/consumos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"sedeId":1,
							"tipoRecursoId":1,
							"periodo":"2026-05",
							"cantidad":42.5
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.sedeId").value(1))
				.andExpect(jsonPath("$.cantidad").value(42.5));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiCrearAlertaExitosa() throws Exception {
		mockMvc.perform(post("/api/alertas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"severidad":"ALTA",
							"mensaje":"Alerta de prueba desde tests"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.severidad").value("ALTA"));
	}

	@Test
	@WithMockUser(username = "12345678", roles = "OPERADOR")
	void apiUsuariosOperadorRecibe403() throws Exception {
		mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403));
	}

	@Test
	@WithMockUser(username = "12345678", roles = "OPERADOR")
	void apiConsumosOperadorPermitido() throws Exception {
		mockMvc.perform(get("/api/consumos"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "12345678", roles = "AUDITOR")
	void apiTarifasAuditorRecibe403() throws Exception {
		mockMvc.perform(get("/api/tarifas"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void apiReportePdfDevuelveContenidoPdf() throws Exception {
		byte[] pdf = mockMvc.perform(get("/api/reportes/mensual/pdf")
				.param("periodo", "2026-05"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
				.andReturn().getResponse().getContentAsByteArray();

		org.junit.jupiter.api.Assertions.assertTrue(pdf.length > 100, "PDF debe tener contenido.");
		String header = new String(pdf, 0, 4);
		org.junit.jupiter.api.Assertions.assertEquals("%PDF", header, "Debe empezar con %PDF");
	}

	@Test
	void rutaProtegidaRedirigeALoginSinSesion() throws Exception {
		mockMvc.perform(get("/sedes"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/auth/login"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void gestionUsuariosListaCorrectamente() throws Exception {
		mockMvc.perform(get("/usuarios"))
				.andExpect(status().isOk())
				.andExpect(view().name("usuarios/lista"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void gestionUsuariosDetalleCorrectamente() throws Exception {
		mockMvc.perform(get("/usuarios/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("usuarios/detalle"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void gestionUsuariosEditarCorrectamente() throws Exception {
		mockMvc.perform(get("/usuarios/1/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("usuarios/formulario"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/usuarios/1/editar\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"nombres\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"correo\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"activo\"")));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void gestionUsuariosActualizaAInactivoCorrectamente() throws Exception {
		mockMvc.perform(post("/usuarios")
				.param("nombres", "Usuario")
				.param("apellidos", "Prueba")
				.param("tipoDocumento", "DNI")
				.param("numeroDocumento", "12345678")
				.param("telefono", "123456789")
				.param("correo", "usuario.prueba@luxury.com")
				.param("contrasena", "usuario123")
				.param("rol", "ANALISTA")
				.param("activo", "true"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/usuarios"));

		Long usuarioId = usuarioRepository.buscarPorIdentificador("usuario.prueba@luxury.com").orElseThrow().getId();

		mockMvc.perform(post("/usuarios/" + usuarioId + "/editar")
				.param("nombres", "Usuario")
				.param("apellidos", "Prueba")
				.param("tipoDocumento", "DNI")
				.param("numeroDocumento", "12345678")
				.param("telefono", "123456789")
				.param("correo", "usuario.prueba@luxury.com")
				.param("contrasena", "")
				.param("rol", "ANALISTA")
				.param("activo", "false"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/usuarios"));
	}

	@Test
	@WithMockUser(username = "00000000", roles = "ADMIN")
	void registrarConsumoPorFormularioRedirigeAlDetalle() throws Exception {
		mockMvc.perform(post("/consumos/registrar")
				.param("sedeId", "1")
				.param("tipoRecursoId", "1")
				.param("cantidadConsumida", "100")
				.param("fechaConsumo", "2026-05-01")
				.param("periodo", "2026-05"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("/consumos/*"));
	}
}
