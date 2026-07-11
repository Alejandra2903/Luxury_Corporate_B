package com.example.luxury.dominios.reporte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.luxury.dominios.reporte.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

	@Autowired
	private ReporteService reporteService;

	@GetMapping("/mensual")
	public String mensual(@RequestParam(required = false) String periodo, Model model) {
		if (periodo != null && !periodo.isBlank()) {
			if (periodo.matches("^\\d{4}-\\d{2}$")) {
				model.addAttribute("reportes", reporteService.mensual(periodo));
			} else {
				model.addAttribute("errorForm", "El formato del periodo es incorrecto. Debe ser YYYY-MM.");
			}
		}
		model.addAttribute("periodo", periodo);
		return "reportes/mensual";
	}

	@GetMapping("/sede/{idSede}")
	public String porSede(@PathVariable Long idSede, Model model) {
		model.addAttribute("reportes", reporteService.porSede(idSede));
		return "reportes/mensual";
	}
}
