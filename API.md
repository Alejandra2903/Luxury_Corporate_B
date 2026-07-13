# Luxury Corporate — API REST

Documento academico rapido para probar el backend Spring Boot.
Swagger UI interactivo disponible en:

```
http://localhost:8080/swagger-ui.html
```

Base URL:

```
http://localhost:8080/api
```

Todas las respuestas son JSON. Todos los endpoints (excepto `/api/auth/**`) requieren `Authorization: Bearer <token>`.

---

## Autenticacion

### POST `/api/auth/login`
Publico. Autentica y devuelve JWT.

Request:
```json
{ "identificador": "admin@luxury.com", "contrasena": "admin123" }
```

Response 200:
```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "usuario": { "id": 1, "nombres": "Administrador", "roles": "ADMIN", ... },
  "expiraEnSegundos": 86400
}
```

Errores:
- 400 con `fields` si validaciones fallan.
- 401 si credenciales invalidas.

### POST `/api/auth/registro`
Publico. Crea un usuario con rol OPERADOR.

Request:
```json
{
  "nombres": "Nuevo",
  "apellidos": "Usuario",
  "tipoDocumento": "DNI",
  "numeroDocumento": "12345678",
  "telefono": "987654321",
  "correo": "nuevo@luxury.com",
  "contrasena": "usuario123"
}
```

---

## Usuarios (solo ADMIN)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET    | `/api/usuarios` | Lista todos |
| GET    | `/api/usuarios/{id}` | Detalle |
| POST   | `/api/usuarios` | Crear |
| PUT    | `/api/usuarios` | Actualizar (id en body) |
| PATCH  | `/api/usuarios` | Cambiar estado activo |

Campos validados en POST/PUT: `nombres`, `apellidos`, `tipoDocumento` (DNI|CE|PASAPORTE), `numeroDocumento` (8-20 digitos), `telefono` (9 digitos), `correo` (email), `contrasena` (min 6), `roles`.

---

## Recursos

### Sedes — ADMIN, GERENTE, OPERADOR, ANALISTA
| Metodo | Ruta |
|--------|------|
| GET    | `/api/sedes` |
| POST   | `/api/sedes` |
| DELETE | `/api/sedes/{id}` (baja logica) |

### Tipos de recurso — ADMIN, GERENTE, OPERADOR, ANALISTA
| Metodo | Ruta |
|--------|------|
| GET    | `/api/tipos-recurso` |
| POST   | `/api/tipos-recurso` |

### Consumos — ADMIN, GERENTE, OPERADOR, ANALISTA
| Metodo | Ruta |
|--------|------|
| GET    | `/api/consumos` |
| GET    | `/api/consumos/paginado?page=0&size=20&sort=id&direction=desc` |
| GET    | `/api/consumos/{id}` |
| GET    | `/api/consumos/sede/{idSede}` |
| GET    | `/api/consumos/periodo/{YYYY-MM}` |
| POST   | `/api/consumos` |

POST body:
```json
{
  "sedeId": 1,
  "tipoRecursoId": 1,
  "periodo": "2026-05",
  "cantidad": 123.45
}
```

`cantidad` debe ser mayor a cero. El backend calcula el costo con la tarifa vigente y aplica reglas de umbral (genera alertas si supera limites).

---

## Dashboard — ADMIN, GERENTE, AUDITOR, ANALISTA

| Metodo | Ruta |
|--------|------|
| GET    | `/api/dashboard/resumen` |
| GET    | `/api/dashboard/consumo-por-sede` |
| GET    | `/api/dashboard/costos-por-mes` |

`/resumen` devuelve KPIs con periodo mas reciente calculado dinamicamente.

---

## Reglas de negocio — ADMIN, GERENTE

### Tarifas
| Metodo | Ruta |
|--------|------|
| GET    | `/api/tarifas` |
| GET    | `/api/tarifas/vigente?sedeId=1&tipoRecursoId=1` |
| POST   | `/api/tarifas` |
| PUT    | `/api/tarifas` |
| DELETE | `/api/tarifas/{id}` |

### Umbrales
| Metodo | Ruta |
|--------|------|
| GET    | `/api/umbrales` |
| POST   | `/api/umbrales` |
| PUT    | `/api/umbrales` |
| DELETE | `/api/umbrales/{id}` |

### Alertas
| Metodo | Ruta |
|--------|------|
| GET    | `/api/alertas` |
| GET    | `/api/alertas/sede/{id}` |
| POST   | `/api/alertas` |
| DELETE | `/api/alertas/{id}` |
| PATCH  | `/api/alertas/{id}/atender` |

---

## Finanzas — ADMIN, GERENTE

| Metodo | Ruta |
|--------|------|
| GET    | `/api/monedas` |
| POST   | `/api/monedas` |
| GET    | `/api/tipos-cambio` |
| POST   | `/api/tipos-cambio` |
| PUT    | `/api/tipos-cambio` |

Codigo de moneda debe ser 3 letras mayusculas (PEN, USD, EUR).

---

## Auditoria — ADMIN, AUDITOR

| Metodo | Ruta |
|--------|------|
| GET    | `/api/auditorias` |
| GET    | `/api/auditorias/paginado?page=0&size=20` |
| GET    | `/api/auditorias/usuario/{id}` |
| GET    | `/api/auditorias/modulo/{modulo}` |
| GET    | `/api/eventos-acceso` |

---

## Reportes — ADMIN, GERENTE, AUDITOR, ANALISTA

| Metodo | Ruta |
|--------|------|
| GET    | `/api/reportes/mensual?periodo=YYYY-MM` |
| GET    | `/api/reportes/sede/{id}` |
| GET    | `/api/reportes/mensual/pdf?periodo=YYYY-MM` |

El PDF se genera sin librerias externas: incluye titulo institucional, KPIs, tabla con sedes y recursos, totales y pie con sello.

---

## Monitoreo de sesiones

### POST `/api/sessions/events`
Cualquier usuario autenticado. Registra un evento de telemetria en la BD.

Request:
```json
{
  "sesionId": "sess-1",
  "usuarioId": 1,
  "tipo": "REGISTRO_CONSUMO",
  "ruta": "/dashboard",
  "descripcion": "...",
  "metadata": { "foo": "bar" }
}
```

### GET (solo ADMIN)
| Metodo | Ruta |
|--------|------|
| GET    | `/api/session-monitoring/eventos` |
| GET    | `/api/session-monitoring/eventos/usuario/{id}` |
| GET    | `/api/session-monitoring/eventos/tipo/{tipo}` |
| GET    | `/api/session-monitoring/eventos/sesion/{sesionId}` |

Los eventos se persisten en tabla `monitoreo_sesion_eventos` (sobreviven a reinicios).

---

## Roles y matriz de permisos

| Modulo             | ADMIN | GERENTE | ANALISTA | AUDITOR | OPERADOR |
|--------------------|:-----:|:-------:|:--------:|:-------:|:--------:|
| Usuarios           |  ✔    |         |          |         |          |
| Dashboard          |  ✔    |   ✔     |    ✔     |   ✔     |          |
| Sedes / TiposRec.  |  ✔    |   ✔     |    ✔     |         |   ✔      |
| Consumos           |  ✔    |   ✔     |    ✔     |         |   ✔      |
| Tarifas/Umbr/Alrt  |  ✔    |   ✔     |          |         |          |
| Finanzas           |  ✔    |   ✔     |          |         |          |
| Auditoria/Eventos  |  ✔    |         |          |   ✔     |          |
| Reportes           |  ✔    |   ✔     |    ✔     |   ✔     |          |
| Session monitoring |  ✔    |         |          |         |          |

Acceso denegado (autenticado pero sin rol) devuelve `403`.
Sin token o token invalido devuelve `401` con JSON.

---

## Formato de errores

Todos los errores devuelven JSON con esta forma:
```json
{
  "timestamp": "2026-07-11T02:19:38.031069",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Datos invalidos.",
  "fields": {
    "correo": "El correo no es valido.",
    "telefono": "El telefono debe tener 9 digitos."
  }
}
```

`fields` aparece solo cuando falla validacion bean-validation.

Codigos comunes:
- **400** Datos invalidos, JSON mal formado, IllegalArgumentException.
- **401** Sin token, token invalido o credenciales incorrectas.
- **403** Autenticado pero sin rol requerido.
- **404** Recurso no encontrado.
- **409** Violacion de integridad (duplicado, foreign key).
- **500** Error interno.

---

## DTOs tipados

Endpoints devuelven records tipados (paquete `com.example.luxury.api.dto`):
- `UsuarioApiResponse` — GET/POST/PUT/PATCH `/api/usuarios/**`
- `ConsumoApiResponse` — GET/POST `/api/consumos/**`
- `DashboardResumenApiResponse` — GET `/api/dashboard/resumen`
- `SessionEventResponse` — POST/GET `/api/sessions/**`, `/api/session-monitoring/**`

El resto de endpoints devuelve JSON con campos estables (documentados por Swagger).

---

## Levantar el backend

```bash
# Requisitos: Java 21, PostgreSQL con base `luxury`.
cd "Luxury Corporate B"
DB_PASSWORD=<tu-clave> mvn spring-boot:run
```

Puerto por defecto: 8080. Usuario admin sembrado: `admin@luxury.com` / `admin123`.

Tests:
```bash
mvn test    # 23 tests con H2, sin tocar PostgreSQL
```
