package com.example.luxury;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
