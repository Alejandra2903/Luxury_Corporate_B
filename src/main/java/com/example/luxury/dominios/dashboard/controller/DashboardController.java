package com.example.luxury.dominios.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.luxury.dominios.dashboard.service.DashboardService;

@Controller
@RequestMapping
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping({"/", "/dashboard"})
	public String mostrarDashboard(Model model) {
		model.addAttribute("resumen", dashboardService.resumenGeneral());
		model.addAttribute("consumosPorSede", dashboardService.consumoPorSede());
		model.addAttribute("costosPorMes", dashboardService.costosPorMes());
		return "dashboard/index";
	}
}
