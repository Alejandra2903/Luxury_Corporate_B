package com.example.luxury.dominios.consumo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.luxury.dominios.consumo.dto.ConsumoForm;
import com.example.luxury.dominios.consumo.dto.ConsumoRequest;
import com.example.luxury.dominios.consumo.service.ConsumoService;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.sede.service.SedeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/consumos")
public class ConsumoController {

	@Autowired
	private ConsumoService consumoService;

	@Autowired
	private SedeService sedeService;

	@Autowired
	private TipoRecursoService tipoRecursoService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("consumos", consumoService.listar());
		return "consumos/lista";
	}

	@GetMapping("/{id}")
	public String obtener(@PathVariable Long id, Model model) {
		model.addAttribute("consumo", consumoService.obtener(id));
		return "consumos/detalle";
	}

	@GetMapping("/sede/{idSede}")
	public String listarPorSede(@PathVariable Long idSede, Model model) {
		model.addAttribute("consumos", consumoService.listarPorSede(idSede));
		return "consumos/lista";
	}

	@GetMapping("/periodo/{periodo}")
	public String listarPorPeriodo(@PathVariable String periodo, Model model) {
		model.addAttribute("consumos", consumoService.listarPorPeriodo(periodo));
		return "consumos/lista";
	}

	@GetMapping("/registrar")
	public String mostrarFormulario(Model model) {
		agregarCatalogos(model);
		model.addAttribute("consumoForm", new ConsumoForm());
		return "consumos/formulario";
	}

	@PostMapping("/registrar")
	public String registrar(
			@Valid @ModelAttribute("consumoForm") ConsumoForm formulario,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			agregarCatalogos(model);
			return "consumos/formulario";
		}
		ConsumoRequest request = formulario.toRequest();
		Long consumoId = consumoService.registrar(request).getId();
		redirectAttributes.addFlashAttribute("successMessage", "Consumo registrado correctamente.");
		return "redirect:/consumos/" + consumoId;
	}

	private void agregarCatalogos(Model model) {
		model.addAttribute("sedes", sedeService.listar());
		model.addAttribute("tiposRecurso", tipoRecursoService.listar());
	}
}
