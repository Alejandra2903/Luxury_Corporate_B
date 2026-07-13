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
        List<ReporteMensualResponse> rows = reporteService.mensual(periodo);
        long alertas = alertasService.listar().size();
        byte[] pdf = crearPdfBasico(periodo, rows, alertas);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("luxury-reporte-" + periodo + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    private byte[] crearPdfBasico(String periodo, List<ReporteMensualResponse> rows, long alertas) {
        java.math.BigDecimal costoTotal = rows.stream()
                .map(ReporteMensualResponse::getCostoPen)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal consumoTotal = rows.stream()
                .map(ReporteMensualResponse::getTotalConsumido)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        long sedesEvaluadas = rows.stream().map(ReporteMensualResponse::getSede).distinct().count();
        String fechaGeneracion = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Layout A4: 595 x 842. Header: 790, tabla desde 640 hacia abajo.
        StringBuilder cuerpo = new StringBuilder();

        // Barra superior negra (rectangulo relleno).
        cuerpo.append("0.05 0.10 0.20 rg\n"); // color casi negro azulado
        cuerpo.append("0 795 595 47 re f\n");
        cuerpo.append("1 1 1 rg\n"); // texto blanco

        cuerpo.append("BT\n");
        cuerpo.append("/F2 22 Tf 40 820 Td (LUXURY CORPORATE) Tj\n");
        cuerpo.append("/F1 11 Tf 0 -18 Td (Reporte mensual de consumo y control operativo) Tj\n");
        cuerpo.append("ET\n");

        // Volver a color negro.
        cuerpo.append("0 0 0 rg\n");

        // Metadata bajo el header
        cuerpo.append("BT\n");
        cuerpo.append("/F2 12 Tf 40 770 Td (Periodo: ").append(escapePdf(periodo)).append(") Tj\n");
        cuerpo.append("/F1 10 Tf 300 0 Td (Generado: ").append(escapePdf(fechaGeneracion)).append(") Tj\n");
        cuerpo.append("ET\n");

        // KPIs en cajas
        cuerpo.append("0.95 0.95 0.98 rg\n");
        cuerpo.append("40 720 165 40 re f\n");
        cuerpo.append("215 720 165 40 re f\n");
        cuerpo.append("390 720 165 40 re f\n");
        cuerpo.append("0 0 0 rg\n");
        cuerpo.append("BT /F1 9 Tf 50 748 Td (COSTO TOTAL PEN) Tj ET\n");
        cuerpo.append("BT /F2 14 Tf 50 730 Td (S/. ").append(escapePdf(costoTotal.toPlainString())).append(") Tj ET\n");
        cuerpo.append("BT /F1 9 Tf 225 748 Td (SEDES EVALUADAS) Tj ET\n");
        cuerpo.append("BT /F2 14 Tf 225 730 Td (").append(sedesEvaluadas).append(") Tj ET\n");
        cuerpo.append("BT /F1 9 Tf 400 748 Td (ALERTAS ACTIVAS) Tj ET\n");
        cuerpo.append("BT /F2 14 Tf 400 730 Td (").append(alertas).append(") Tj ET\n");

        // Titulo tabla
        cuerpo.append("BT /F2 12 Tf 40 695 Td (Detalle por sede y recurso) Tj ET\n");

        // Header tabla (fila gris)
        float tableTop = 680;
        float rowH = 20;
        cuerpo.append("0.90 0.90 0.93 rg\n");
        cuerpo.append("40 ").append(tableTop - rowH).append(" 515 ").append(rowH).append(" re f\n");
        cuerpo.append("0 0 0 rg\n");
        cuerpo.append("BT /F2 10 Tf 48 ").append(tableTop - 14).append(" Td (SEDE) Tj ET\n");
        cuerpo.append("BT /F2 10 Tf 200 ").append(tableTop - 14).append(" Td (RECURSO) Tj ET\n");
        cuerpo.append("BT /F2 10 Tf 330 ").append(tableTop - 14).append(" Td (CONSUMO) Tj ET\n");
        cuerpo.append("BT /F2 10 Tf 450 ").append(tableTop - 14).append(" Td (COSTO PEN) Tj ET\n");

        // Filas
        float y = tableTop - rowH;
        if (rows.isEmpty()) {
            y -= rowH;
            cuerpo.append("BT /F1 10 Tf 48 ").append(y + 6).append(" Td (Sin registros de consumo para el periodo indicado.) Tj ET\n");
        } else {
            for (ReporteMensualResponse row : rows) {
                y -= rowH;
                cuerpo.append("BT /F1 10 Tf 48 ").append(y + 6).append(" Td (")
                        .append(escapePdf(recortar(row.getSede(), 25))).append(") Tj ET\n");
                cuerpo.append("BT /F1 10 Tf 200 ").append(y + 6).append(" Td (")
                        .append(escapePdf(recortar(row.getTipoRecurso(), 20))).append(") Tj ET\n");
                cuerpo.append("BT /F1 10 Tf 330 ").append(y + 6).append(" Td (")
                        .append(escapePdf(row.getTotalConsumido().toPlainString())).append(") Tj ET\n");
                cuerpo.append("BT /F1 10 Tf 450 ").append(y + 6).append(" Td (")
                        .append(escapePdf(row.getCostoPen().toPlainString())).append(") Tj ET\n");
                // Linea inferior de fila
                cuerpo.append("0.85 0.85 0.85 RG\n");
                cuerpo.append("40 ").append(y).append(" m 555 ").append(y).append(" l S\n");
                cuerpo.append("0 0 0 RG\n");
            }
        }

        // Fila total
        y -= rowH;
        cuerpo.append("0.05 0.10 0.20 rg\n");
        cuerpo.append("40 ").append(y).append(" 515 ").append(rowH).append(" re f\n");
        cuerpo.append("1 1 1 rg\n");
        cuerpo.append("BT /F2 10 Tf 48 ").append(y + 6).append(" Td (TOTAL) Tj ET\n");
        cuerpo.append("BT /F2 10 Tf 330 ").append(y + 6).append(" Td (")
                .append(escapePdf(consumoTotal.toPlainString())).append(") Tj ET\n");
        cuerpo.append("BT /F2 10 Tf 450 ").append(y + 6).append(" Td (")
                .append(escapePdf(costoTotal.toPlainString())).append(") Tj ET\n");
        cuerpo.append("0 0 0 rg\n");

        // Pie / sello
        cuerpo.append("0.85 0.85 0.85 RG\n");
        cuerpo.append("40 90 m 555 90 l S\n");
        cuerpo.append("0 0 0 RG\n");
        cuerpo.append("BT /F2 9 Tf 40 70 Td (LUXURY CORPORATE - Documento oficial) Tj ET\n");
        cuerpo.append("BT /F1 8 Tf 40 55 Td (Este documento fue generado automaticamente por el sistema. ");
        cuerpo.append("Uso interno.) Tj ET\n");
        cuerpo.append("BT /F1 8 Tf 40 42 Td (Reporte: ").append(escapePdf(periodo));
        cuerpo.append("  |  Emitido: ").append(escapePdf(fechaGeneracion)).append(") Tj ET\n");

        String stream = cuerpo.toString();

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

    private String recortar(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
