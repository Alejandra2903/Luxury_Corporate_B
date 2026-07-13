package com.example.luxury.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.luxury.api.dto.ConsumoApiResponse;
import com.example.luxury.api.dto.UsuarioApiResponse;
import com.example.luxury.dominios.alerta.dto.AlertaResponse;
import com.example.luxury.dominios.auditoria.dto.AuditoriaResponse;
import com.example.luxury.dominios.common.enums.EstadoAlerta;
import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.consumo.dto.ConsumoResponse;
import com.example.luxury.dominios.eventoacceso.dto.EventoAccesoResponse;
import com.example.luxury.dominios.finanzas.dto.MonedaResponse;
import com.example.luxury.dominios.finanzas.dto.TipoCambioResponse;
import com.example.luxury.dominios.recurso.dto.TipoRecursoResponse;
import com.example.luxury.dominios.reporte.dto.ReporteMensualResponse;
import com.example.luxury.dominios.sede.dto.SedeResponse;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.tarifa.dto.TarifaResponse;
import com.example.luxury.dominios.umbral.dto.UmbralResponse;

final class ApiMapper {

    private ApiMapper() {
    }

    static Map<String, Object> usuario(Usuario usuario) {
        String roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .sorted()
                .reduce((actual, siguiente) -> actual + ", " + siguiente)
                .orElse(NombreRol.OPERADOR.name());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", usuario.getId());
        data.put("nombres", usuario.getNombres());
        data.put("apellidos", usuario.getApellidos());
        data.put("nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos());
        data.put("sedeId", null);
        data.put("sedeNombre", null);
        data.put("tipoDocumento", usuario.getTipoDocumento());
        data.put("numeroDocumento", usuario.getNumeroDocumento());
        data.put("telefono", usuario.getTelefono());
        data.put("correo", usuario.getCorreo());
        data.put("activo", usuario.isActivo());
        data.put("estado", usuario.isActivo() ? "ACTIVO" : "INACTIVO");
        data.put("roles", roles);
        data.put("fechaRegistro", usuario.getFechaRegistro());
        data.put("fechaActualizacion", usuario.getFechaActualizacion());
        return data;
    }

    static Map<String, Object> usuario(UsuarioResponse usuario) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", usuario.getId());
        data.put("nombres", usuario.getNombres());
        data.put("apellidos", usuario.getApellidos());
        data.put("nombreCompleto", usuario.getNombreCompleto());
        data.put("sedeId", null);
        data.put("sedeNombre", null);
        data.put("tipoDocumento", usuario.getTipoDocumento());
        data.put("numeroDocumento", usuario.getNumeroDocumento());
        data.put("telefono", usuario.getTelefono());
        data.put("correo", usuario.getCorreo());
        data.put("activo", usuario.isActivo());
        data.put("estado", usuario.getEstado());
        data.put("roles", usuario.getRoles());
        data.put("fechaRegistro", usuario.getFechaRegistro());
        data.put("fechaActualizacion", usuario.getFechaActualizacion());
        return data;
    }

    static UsuarioApiResponse usuarioDto(UsuarioResponse usuario) {
        return new UsuarioApiResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getNombreCompleto(),
                null,
                null,
                usuario.getTipoDocumento() == null ? null : usuario.getTipoDocumento().name(),
                usuario.getNumeroDocumento(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.isActivo(),
                usuario.getEstado(),
                usuario.getRoles(),
                usuario.getFechaRegistro(),
                usuario.getFechaActualizacion());
    }

    static UsuarioApiResponse usuarioDto(Usuario usuario) {
        String roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .sorted()
                .reduce((actual, siguiente) -> actual + ", " + siguiente)
                .orElse(NombreRol.OPERADOR.name());
        return new UsuarioApiResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getNombres() + " " + usuario.getApellidos(),
                null,
                null,
                usuario.getTipoDocumento() == null ? null : usuario.getTipoDocumento().name(),
                usuario.getNumeroDocumento(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.isActivo(),
                usuario.isActivo() ? "ACTIVO" : "INACTIVO",
                roles,
                usuario.getFechaRegistro(),
                usuario.getFechaActualizacion());
    }

    static ConsumoApiResponse consumoDto(ConsumoResponse consumo) {
        BigDecimal costo = consumo.getCostos().stream()
                .filter(item -> "PEN".equalsIgnoreCase(item.getMoneda()))
                .map(item -> item.getMonto())
                .findFirst()
                .orElse(BigDecimal.ZERO);
        return new ConsumoApiResponse(
                consumo.getId(),
                consumo.getSedeId(),
                consumo.getSede(),
                consumo.getTipoRecursoId(),
                codigoRecurso(consumo.getTipoRecurso()),
                consumo.getTipoRecurso(),
                unidad(consumo.getUnidadMedida()),
                consumo.getPeriodo(),
                consumo.getCreadoEn(),
                consumo.getCantidadConsumida(),
                costo,
                "PEN",
                "REGISTRADO",
                "Registrado desde backend");
    }

    static Map<String, Object> sede(SedeResponse sede) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", sede.getId());
        data.put("nombre", sede.getNombre());
        data.put("codigo", "SED-" + sede.getId());
        data.put("direccion", sede.getDireccion());
        data.put("ciudad", sede.getCiudad());
        data.put("activa", EstadoRegistro.ACTIVO.equals(sede.getEstado()));
        data.put("responsable", "Administrador Luxury");
        return data;
    }

    static Map<String, Object> tipoRecurso(TipoRecursoResponse tipo) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", tipo.getId());
        data.put("codigo", codigoRecurso(tipo.getNombre()));
        data.put("nombre", tipo.getNombre());
        data.put("unidad", unidad(tipo.getUnidadMedida()));
        data.put("activo", true);
        return data;
    }

    static Map<String, Object> consumo(ConsumoResponse consumo) {
        BigDecimal costo = consumo.getCostos().stream()
                .filter(item -> "PEN".equalsIgnoreCase(item.getMoneda()))
                .map(item -> item.getMonto())
                .findFirst()
                .orElse(BigDecimal.ZERO);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", consumo.getId());
        data.put("sedeId", consumo.getSedeId());
        data.put("sedeNombre", consumo.getSede());
        data.put("tipoRecursoId", consumo.getTipoRecursoId());
        data.put("tipoRecursoCodigo", codigoRecurso(consumo.getTipoRecurso()));
        data.put("tipoRecursoNombre", consumo.getTipoRecurso());
        data.put("unidad", unidad(consumo.getUnidadMedida()));
        data.put("periodo", consumo.getPeriodo());
        data.put("fechaRegistro", consumo.getCreadoEn());
        data.put("cantidad", consumo.getCantidadConsumida());
        data.put("costo", costo);
        data.put("moneda", "PEN");
        data.put("estado", "REGISTRADO");
        data.put("observacion", "Registrado desde backend");
        return data;
    }

    static Map<String, Object> tarifa(TarifaResponse tarifa) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", tarifa.getId());
        data.put("sedeId", tarifa.getSedeId());
        data.put("sedeNombre", tarifa.getSede());
        data.put("tipoRecursoId", tarifa.getTipoRecursoId());
        data.put("tipoRecursoCodigo", codigoRecurso(tarifa.getTipoRecurso()));
        data.put("tipoRecursoNombre", tarifa.getTipoRecurso());
        data.put("monedaId", 1L);
        data.put("monedaCodigo", "PEN");
        data.put("costoUnitario", tarifa.getPrecioUnitarioPen());
        data.put("fechaInicio", tarifa.getFechaInicio());
        data.put("fechaFin", tarifa.getFechaFin());
        data.put("vigente", EstadoRegistro.ACTIVO.equals(tarifa.getEstado()));
        return data;
    }

    static Map<String, Object> umbral(UmbralResponse umbral) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", umbral.getId());
        data.put("sedeId", umbral.getSedeId());
        data.put("sedeNombre", umbral.getSede());
        data.put("tipoRecursoId", umbral.getTipoRecursoId());
        data.put("tipoRecursoCodigo", codigoRecurso(umbral.getTipoRecurso()));
        data.put("tipoRecursoNombre", umbral.getTipoRecurso());
        data.put("unidad", unidadPorNombre(umbral.getTipoRecurso()));
        data.put("minimo", BigDecimal.ZERO);
        data.put("maximo", umbral.getLimiteConsumo() == null ? umbral.getLimitePresupuestoPen() : umbral.getLimiteConsumo());
        data.put("periodo", "MENSUAL");
        data.put("activo", EstadoRegistro.ACTIVO.equals(umbral.getEstado()));
        return data;
    }

    static Map<String, Object> alerta(AlertaResponse alerta) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", alerta.getId());
        data.put("sedeId", 0L);
        data.put("sedeNombre", alerta.getSede());
        data.put("tipoRecursoId", 0L);
        data.put("tipoRecursoCodigo", codigoRecurso(alerta.getTipoRecurso()));
        data.put("severidad", alerta.getNivel() == null ? "MEDIA" : alerta.getNivel().name());
        data.put("mensaje", alerta.getMensaje());
        data.put("fechaGeneracion", alerta.getFechaGeneracion());
        data.put("atendida", EstadoAlerta.ATENDIDA.equals(alerta.getEstado()));
        data.put("atendidaPor", null);
        return data;
    }

    static Map<String, Object> moneda(MonedaResponse moneda) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", moneda.getId());
        data.put("codigo", moneda.getCodigo());
        data.put("nombre", moneda.getNombre());
        data.put("simbolo", simbolo(moneda.getCodigo()));
        data.put("activa", true);
        return data;
    }

    static Map<String, Object> tipoCambio(TipoCambioResponse cambio, List<MonedaResponse> monedas) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", cambio.getId());
        data.put("monedaOrigenId", idMoneda(cambio.getMonedaOrigen(), monedas));
        data.put("monedaOrigenCodigo", cambio.getMonedaOrigen());
        data.put("monedaDestinoId", idMoneda(cambio.getMonedaDestino(), monedas));
        data.put("monedaDestinoCodigo", cambio.getMonedaDestino());
        data.put("tasa", cambio.getValor());
        data.put("fechaVigencia", cambio.getFecha());
        data.put("fuente", "Backend Luxury");
        data.put("activo", EstadoRegistro.ACTIVO.equals(cambio.getEstado()));
        return data;
    }

    static Map<String, Object> auditoria(AuditoriaResponse auditoria) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", auditoria.getId());
        data.put("usuarioId", auditoria.getUsuarioId());
        data.put("usuarioNombre", auditoria.getUsuario());
        data.put("usuarioRol", "ADMIN");
        data.put("sedeId", null);
        data.put("modulo", modulo(auditoria.getModulo()));
        data.put("accion", accion(auditoria.getAccion()));
        data.put("descripcion", auditoria.getDescripcion());
        data.put("entidad", auditoria.getTablaAfectada());
        data.put("entidadId", auditoria.getIdRegistroAfectado());
        data.put("ipOrigen", "127.0.0.1");
        data.put("fechaEvento", auditoria.getFecha());
        data.put("resultado", "EXITOSO");
        return data;
    }

    static Map<String, Object> eventoAcceso(EventoAccesoResponse evento) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", evento.getId());
        data.put("usuarioId", evento.getUsuarioId());
        data.put("usuarioNombre", evento.getEmailIntentado());
        data.put("identificador", evento.getEmailIntentado());
        data.put("rol", "ADMIN");
        data.put("sedeId", null);
        data.put("tipo", evento.getTipoEvento());
        data.put("ipOrigen", evento.getIp());
        data.put("userAgent", "Backend Luxury");
        data.put("rutaSolicitada", "/auth/login");
        data.put("fechaEvento", evento.getFecha());
        data.put("exitoso", evento.getTipoEvento() != null && evento.getTipoEvento().name().contains("EXITOSO"));
        data.put("detalle", evento.getDescripcion());
        return data;
    }

    static Map<String, Object> reporteMensual(String periodo, List<ReporteMensualResponse> rows, long alertas) {
        BigDecimal costoTotal = rows.stream().map(ReporteMensualResponse::getCostoPen).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal energia = totalPorRecurso(rows, "Luz");
        BigDecimal agua = totalPorRecurso(rows, "Agua");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("periodo", periodo);
        data.put("fechaGeneracion", LocalDateTime.now());
        data.put("moneda", "PEN");
        data.put("costoTotal", costoTotal);
        data.put("variacionCostoPorcentaje", BigDecimal.ZERO);
        data.put("tendencia", "ESTABLE");
        data.put("sedesEvaluadas", rows.stream().map(ReporteMensualResponse::getSede).distinct().count());
        data.put("alertasDetectadas", alertas);
        data.put("cumplimientoPromedioPorcentaje", 91.4);
        data.put("recursos", List.of(recursoReporte("ENERGIA", "Energia", energia, "kWh", costoTotal), recursoReporte("AGUA", "Agua", agua, "m3", costoTotal)));
        data.put("sedes", rows.stream().map(row -> sedeReporte(row, costoTotal, alertas)).toList());
        return data;
    }

    static String codigoRecurso(String nombre) {
        if (nombre == null) {
            return "ENERGIA";
        }
        String value = nombre.trim().toUpperCase();
        if (value.contains("AGUA")) {
            return "AGUA";
        }
        if (value.contains("GAS")) {
            return "GAS";
        }
        if (value.contains("INTERNET") || value.contains("DATOS")) {
            return "INTERNET";
        }
        return "ENERGIA";
    }

    static String unidadPorNombre(String nombre) {
        return "AGUA".equals(codigoRecurso(nombre)) || "GAS".equals(codigoRecurso(nombre)) ? "m3" : "kWh";
    }

    private static String unidad(String unidad) {
        if (unidad == null || unidad.isBlank()) {
            return "kWh";
        }
        return unidad;
    }

    private static String simbolo(String codigo) {
        if ("USD".equalsIgnoreCase(codigo)) {
            return "$";
        }
        if ("EUR".equalsIgnoreCase(codigo)) {
            return "€";
        }
        return "S/";
    }

    private static Long idMoneda(String codigo, List<MonedaResponse> monedas) {
        return monedas.stream()
                .filter(moneda -> moneda.getCodigo().equalsIgnoreCase(codigo))
                .map(MonedaResponse::getId)
                .findFirst()
                .orElse(0L);
    }

    private static String modulo(String modulo) {
        if (modulo == null) {
            return "ADMIN";
        }
        if (modulo.equalsIgnoreCase("CONSUMOS")) {
            return "RECURSOS";
        }
        if (modulo.equalsIgnoreCase("TARIFAS")) {
            return "FINANZAS";
        }
        return modulo.toUpperCase();
    }

    private static String accion(String accion) {
        if (accion == null) {
            return "CONSULTA";
        }
        if (accion.equalsIgnoreCase("CREAR")) {
            return "CREACION";
        }
        if (accion.equalsIgnoreCase("ACTUALIZAR")) {
            return "ACTUALIZACION";
        }
        return accion.toUpperCase();
    }

    private static BigDecimal totalPorRecurso(List<ReporteMensualResponse> rows, String recurso) {
        return rows.stream()
                .filter(row -> row.getTipoRecurso().equalsIgnoreCase(recurso))
                .map(ReporteMensualResponse::getTotalConsumido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Map<String, Object> recursoReporte(String codigo, String nombre, BigDecimal consumo, String unidad, BigDecimal costoTotal) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("codigo", codigo);
        data.put("nombre", nombre);
        data.put("consumo", consumo);
        data.put("unidad", unidad);
        data.put("costo", costoTotal);
        data.put("participacionPorcentaje", 50);
        data.put("variacionPorcentaje", 0);
        return data;
    }

    private static Map<String, Object> sedeReporte(ReporteMensualResponse row, BigDecimal costoTotal, long alertas) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sedeId", 0L);
        data.put("sedeNombre", row.getSede());
        data.put("ciudad", null);
        data.put("costoTotal", row.getCostoPen());
        data.put("consumoEnergiaKwh", "Luz".equalsIgnoreCase(row.getTipoRecurso()) ? row.getTotalConsumido() : BigDecimal.ZERO);
        data.put("consumoAguaM3", "Agua".equalsIgnoreCase(row.getTipoRecurso()) ? row.getTotalConsumido() : BigDecimal.ZERO);
        data.put("alertas", alertas);
        data.put("cumplimientoPorcentaje", 91.4);
        return data;
    }
}
