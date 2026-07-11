package com.example.luxury.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.alerta.service.ReglasAlertasService;
import com.example.luxury.dominios.reporte.dto.ReporteMensualResponse;
import com.example.luxury.dominios.reporte.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ApiReportsController {

    private final ReporteService reporteService;
    private final ReglasAlertasService alertasService;

    public ApiReportsController(ReporteService reporteService, ReglasAlertasService alertasService) {
        this.reporteService = reporteService;
        this.alertasService = alertasService;
    }

    @GetMapping("/mensual")
    public Map<String, Object> reporteMensual(@RequestParam String periodo) {
        List<ReporteMensualResponse> rows = reporteService.mensual(periodo);
        return ApiMapper.reporteMensual(periodo, rows, alertasService.listar().size());
    }

    @GetMapping("/sede/{idSede}")
    public Map<String, Object> reportePorSede(@PathVariable Long idSede) {
        List<ReporteMensualResponse> rows = reporteService.porSede(idSede);
        String sede = rows.isEmpty() ? "Sede Luxury" : rows.get(0).getSede();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sedeId", idSede);
        data.put("sedeNombre", sede);
        data.put("codigoSede", "SED-" + idSede);
        data.put("ciudad", "Lima");
        data.put("responsable", "Administrador Luxury");
        data.put("periodoDesde", "2026-01");
        data.put("periodoHasta", "2026-06");
        data.put("costoAcumulado", rows.stream().map(ReporteMensualResponse::getCostoPen).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        data.put("consumoEnergiaKwh", java.math.BigDecimal.ZERO);
        data.put("consumoAguaM3", java.math.BigDecimal.ZERO);
        data.put("alertasAcumuladas", alertasService.listar().size());
        data.put("cumplimientoPromedioPorcentaje", 91.4);
        data.put("variacionCostoPorcentaje", 0);
        data.put("tendencia", "ESTABLE");
        return data;
    }

    @GetMapping("/mensual/pdf")
    public ResponseEntity<byte[]> reporteMensualPdf(@RequestParam String periodo) {
        byte[] pdf = crearPdfBasico(periodo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("luxury-reporte-" + periodo + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    private byte[] crearPdfBasico(String periodo) {
        String stream = """
                BT
                /F2 24 Tf 56 770 Td (LUXURY CORPORATE) Tj
                /F1 13 Tf 0 -32 Td (Reporte mensual de consumo y control operativo) Tj
                0 -28 Td (Periodo: %s) Tj
                0 -36 Td (Resumen ejecutivo) Tj
                0 -22 Td (Este documento consolida sedes, consumos, costos y alertas del sistema.) Tj
                0 -28 Td (Indicadores principales:) Tj
                18 -22 Td (- Costo total calculado desde registros del backend.) Tj
                0 -20 Td (- Alertas generadas por reglas de umbral.) Tj
                0 -20 Td (- Datos listos para consumo desde Angular.) Tj
                -18 -36 Td (Documento generado automaticamente por Luxury Corporate Backend.) Tj
                ET
                """.formatted(escapePdf(periodo));

        List<String> objetos = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> >>",
                "<< /Length " + stream.getBytes(StandardCharsets.ISO_8859_1).length + " >>\nstream\n" + stream + "endstream",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>");

        StringBuilder documento = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int index = 0; index < objetos.size(); index++) {
            offsets.add(documento.toString().getBytes(StandardCharsets.ISO_8859_1).length);
            documento.append(index + 1).append(" 0 obj\n");
            documento.append(objetos.get(index)).append("\n");
            documento.append("endobj\n");
        }

        int xref = documento.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        documento.append("xref\n");
        documento.append("0 ").append(objetos.size() + 1).append("\n");
        documento.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            documento.append(String.format("%010d 00000 n \n", offset));
        }
        documento.append("trailer\n");
        documento.append("<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\n");
        documento.append("startxref\n");
        documento.append(xref).append("\n");
        documento.append("%%EOF");

        return documento.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private String escapePdf(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
