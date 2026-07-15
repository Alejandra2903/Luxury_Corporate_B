# Plan de testeo de integración — Luxury Corporate

## 0. Contexto del proyecto

# Validación de módulos

Para cada módulo se verificó el funcionamiento de la interfaz web (Thymeleaf), el procesamiento de formularios y los servicios REST correspondientes.

### 1. Autenticación

Se comprobó el correcto funcionamiento del inicio de sesión y registro de usuarios tanto desde la interfaz web como mediante la API.

**Endpoints probados:**

* `GET /auth/login`
* `GET /auth/registro`
* `POST /auth/login`
* `POST /api/auth/login`
* `POST /api/auth/registro`

También se verificó que el sistema rechace credenciales incorrectas devolviendo un error **401 Unauthorized**. Queda pendiente validar manualmente el cierre de sesión (`/logout`) y la eliminación de la cookie de autenticación.

### 2. Usuarios

Se validó el listado, creación, edición y consulta de usuarios, comprobando además que únicamente los administradores tengan acceso a estas funciones.

**Endpoints probados:**

* `GET /usuarios`
* `GET /usuarios/nuevo`
* `POST /usuarios`
* `GET /usuarios/{id}/editar`
* `GET /api/usuarios`
* `POST /api/usuarios`

Como mejora futura sería recomendable agregar pruebas para el cambio de estado de los usuarios mediante `PATCH /api/usuarios`.

### 3. Sedes

Se comprobó el registro y consulta de sedes tanto desde la interfaz como mediante la API.

**Endpoints probados:**

* `GET /sedes`
* `GET /sedes/nueva`
* `POST /sedes`
* `GET /api/sedes`

Queda pendiente validar la edición (`POST /sedes/{id}/editar`) y la eliminación lógica (`POST /sedes/{id}/eliminar`).

### 4. Tipos de recurso

Se verificó el registro y consulta de los tipos de recurso.

**Endpoints probados:**

* `GET /tipos-recurso`
* `GET /api/tipos-recurso`
* `POST /api/tipos-recurso`

Durante las pruebas se detectó que la vista `GET /tipos-recurso` genera un error **500** debido a que faltan las plantillas HTML correspondientes.

### 5. Consumos

Se comprobó el registro de consumos y el cálculo automático del costo generado.

**Endpoints probados:**

* `GET /consumos`
* `GET /consumos/registrar`
* `POST /consumos`
* `GET /api/consumos`
* `POST /api/consumos`

Se recomienda agregar una prueba que verifique la creación automática de alertas cuando un consumo supera el umbral permitido.

### 6. Tarifas

Se validó la consulta de tarifas vigentes y el acceso restringido según el rol del usuario.

**Endpoints probados:**

* `GET /tarifas`
* `GET /api/tarifas`
* `GET /api/tarifas/vigente`

### 7. Umbrales

Se verificó el registro de umbrales y el almacenamiento correcto de los límites definidos.

**Endpoints probados:**

* `GET /umbrales`
* `POST /api/umbrales`

### 8. Alertas

Se comprobó el registro y visualización de alertas.

**Endpoints probados:**

* `GET /alertas`
* `GET /api/alertas`
* `POST /api/alertas`

Como mejora pendiente se recomienda probar el endpoint `PATCH /api/alertas/{id}/atender`.

### 9. Finanzas

Se validó el funcionamiento de los módulos de monedas y tipos de cambio.

**Endpoints probados:**

* `GET /monedas`
* `GET /api/monedas`
* `GET /tipos-cambio`
* `POST /api/tipos-cambio`

También se verificó que los usuarios sin permisos reciban un error **403 Forbidden**.

### 10. Auditoría y eventos de acceso

Se comprobó que únicamente los usuarios autorizados puedan consultar los registros de auditoría.

**Endpoints probados:**

* `GET /auditorias`
* `GET /eventos-acceso`
* `GET /api/auditorias`
* `GET /api/eventos-acceso`

### 11. Dashboard

Se verificó que el panel principal muestre correctamente la información estadística del sistema.

**Endpoints probados:**

* `GET /dashboard`
* `GET /api/dashboard/resumen`
* `GET /api/dashboard/consumo-por-sede`
* `GET /api/dashboard/costos-por-mes`

### 12. Reportes

Se comprobó la generación de reportes tanto en PDF como en formato JSON.

**Endpoints probados:**

* `GET /reportes/mensual`
* `GET /api/reportes/mensual`
* `GET /api/reportes/mensual/pdf`

### 13. Monitoreo de sesión

Se verificó el registro de eventos y el acceso restringido al historial de sesiones.

**Endpoints probados:**

* `POST /api/sessions/events`
* `GET /api/session-monitoring/eventos`

