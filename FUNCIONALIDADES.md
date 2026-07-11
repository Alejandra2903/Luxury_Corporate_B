# Funcionalidades Del Sistema Luxury

## 1. Planteamiento Del Problema

En la actualidad, muchas organizaciones con varias sedes corporativas no cuentan con una herramienta centralizada para controlar sus consumos operativos. Los registros de luz, agua y gas suelen manejarse en hojas de calculo, documentos sueltos o reportes manuales, lo que genera desorden, duplicidad de informacion y poca trazabilidad.

Esta falta de control dificulta saber que sede consume mas, que recurso genera mayor gasto, que periodo tuvo incrementos anormales y que usuario registro determinada informacion. Ademas, cuando los costos se analizan en soles, dolares o euros, se vuelve necesario conservar el tipo de cambio usado en cada calculo para evitar que datos historicos cambien de manera incorrecta.

Otro problema importante es la ausencia de alertas automaticas. Si una sede supera un limite de consumo o presupuesto, el personal responsable puede enterarse tarde, cuando el gasto ya se produjo. Esto reduce la capacidad de reaccion y complica la toma de decisiones administrativas.

Luxury propone solucionar estos problemas mediante una plataforma web centralizada que registra consumos, calcula costos, controla umbrales, genera alertas, conserva auditoria y ofrece reportes para apoyar la gestion operativa y financiera.

## 2. Descripcion Y Alcance Del Sistema

Luxury es una aplicacion web MVC para la gestion de consumos operativos de sedes corporativas. El sistema permite registrar sedes, recursos medibles, tarifas, monedas, tipos de cambio, consumos, umbrales y alertas. Tambien ofrece dashboard, reportes, auditoria y control de acceso por roles.

La plataforma trabaja sobre PostgreSQL y utiliza JPA/Hibernate para crear y relacionar las tablas. La interfaz se implementa con Thymeleaf y formularios web, siguiendo el patron:

```text
Controller -> Service -> Repository -> Base de datos
```

El sistema no contempla frontend independiente, microservicios ni consumo de APIs externas. Los tipos de cambio se registran manualmente en la base de datos y los reportes se muestran desde vistas web.

## 3. Actores Del Sistema

### Administrador

Usuario con mayor nivel de acceso. Puede gestionar usuarios, revisar auditoria, administrar catalogos, consultar dashboard, registrar consumos y revisar alertas.

### Gerente

Usuario orientado a la consulta ejecutiva. Puede revisar dashboard, consumos, alertas y reportes para tomar decisiones.

### Analista

Usuario operativo. Puede registrar consumos, consultar informacion relacionada y revisar alertas.

### Auditor

Usuario enfocado en trazabilidad. Puede revisar auditorias, eventos de acceso y reportes.

## 4. Estructura De Modulos Y Requerimientos Funcionales

### Modulo 1: Seguridad Y Autenticacion

- RF1: El sistema debe permitir iniciar sesion mediante correo y contraseña.
- RF2: El sistema debe validar credenciales usando Spring Security.
- RF3: El sistema debe proteger las rutas internas para que solo usuarios autenticados puedan acceder.
- RF4: El sistema debe permitir registrar usuarios desde un formulario web.
- RF5: El sistema debe asignar roles a los usuarios para controlar permisos.
- RF6: El sistema debe cerrar sesion mediante logout.

### Modulo 2: Usuarios, Roles Y Permisos

- RF7: El sistema debe almacenar usuarios con nombre, correo, contraseña, estado y fecha de creacion.
- RF8: El sistema debe gestionar roles como ADMIN, GERENTE, ANALISTA y AUDITOR.
- RF9: El sistema debe gestionar permisos como CONSUMO_CREAR, REPORTE_VER, ALERTA_VER y AUDITORIA_VER.
- RF10: El sistema debe relacionar usuarios con roles mediante una tabla intermedia.
- RF11: El sistema debe relacionar roles con permisos mediante una tabla intermedia.
- RF12: El Administrador debe poder listar usuarios registrados.
- RF13: El Administrador debe poder ver el detalle de un usuario.

### Modulo 3: Sedes Corporativas

- RF14: El sistema debe permitir registrar sedes corporativas.
- RF15: El sistema debe guardar nombre, ciudad, direccion y estado de cada sede.
- RF16: El sistema debe listar las sedes registradas.
- RF17: El sistema debe permitir editar los datos de una sede.
- RF18: El sistema debe permitir desactivar una sede cambiando su estado a INACTIVO.
- RF19: El sistema debe usar las sedes como base para registrar consumos, tarifas y umbrales.

### Modulo 4: Tipos De Recurso

- RF20: El sistema debe permitir registrar tipos de recurso medibles.
- RF21: El sistema debe guardar nombre y unidad de medida de cada recurso.
- RF22: El sistema debe manejar recursos como Luz, Agua y Gas.
- RF23: El sistema debe permitir listar tipos de recurso.
- RF24: El sistema debe permitir eliminar tipos de recurso cuando corresponda.
- RF25: El sistema debe usar el tipo de recurso para clasificar consumos, tarifas y umbrales.

### Modulo 5: Monedas

- RF26: El sistema debe permitir registrar monedas.
- RF27: El sistema debe guardar codigo y nombre de cada moneda.
- RF28: El sistema debe cargar monedas iniciales PEN, USD y EUR.
- RF29: El sistema debe usar las monedas para calcular costos por consumo.

### Modulo 6: Tipos De Cambio

- RF30: El sistema debe permitir registrar tipos de cambio.
- RF31: El sistema debe guardar moneda origen, moneda destino, valor, fecha y estado.
- RF32: El sistema debe permitir actualizar tipos de cambio.
- RF33: El sistema debe buscar tipos de cambio vigentes al calcular costos.
- RF34: El sistema debe permitir conversiones desde PEN hacia USD y EUR.
- RF35: El sistema debe conservar el tipo de cambio usado en cada calculo historico.

### Modulo 7: Tarifas Por Recurso

- RF36: El sistema debe permitir registrar tarifas por sede y tipo de recurso.
- RF37: El sistema debe guardar el precio unitario en soles.
- RF38: El sistema debe guardar fecha de inicio y fecha de fin de la tarifa.
- RF39: El sistema debe identificar la tarifa vigente al momento de registrar un consumo.
- RF40: El sistema debe permitir listar tarifas registradas.
- RF41: El sistema debe usar la tarifa para calcular el costo base en PEN.

### Modulo 8: Umbrales

- RF42: El sistema debe permitir registrar umbrales por sede y tipo de recurso.
- RF43: El sistema debe guardar limite de consumo.
- RF44: El sistema debe guardar limite de presupuesto en PEN.
- RF45: El sistema debe manejar vigencia mediante fecha de inicio y fecha de fin.
- RF46: El sistema debe permitir desactivar umbrales.
- RF47: El sistema debe buscar el umbral vigente al registrar un consumo.

### Modulo 9: Registro De Consumos

- RF48: El sistema debe permitir registrar un consumo desde un formulario web.
- RF49: El sistema debe solicitar sede, tipo de recurso, cantidad consumida, fecha de consumo y periodo.
- RF50: El sistema debe validar que la cantidad consumida sea mayor que cero.
- RF51: El sistema debe validar que el periodo tenga formato similar a 2026-05.
- RF52: El sistema debe asociar el consumo al usuario autenticado.
- RF53: El sistema debe asociar el consumo a la tarifa vigente.
- RF54: El sistema debe guardar el consumo en la tabla consumos.
- RF55: El sistema debe redirigir al detalle del consumo despues de registrar correctamente.
- RF56: El sistema debe permitir listar consumos.
- RF57: El sistema debe permitir consultar consumos por sede.
- RF58: El sistema debe permitir consultar consumos por periodo.

### Modulo 10: Calculo Financiero De Consumos

- RF59: El sistema debe calcular el costo base en PEN multiplicando cantidad consumida por precio unitario.
- RF60: El sistema debe guardar el costo calculado en PEN.
- RF61: El sistema debe buscar el tipo de cambio PEN a USD.
- RF62: El sistema debe calcular el costo equivalente en USD.
- RF63: El sistema debe buscar el tipo de cambio PEN a EUR.
- RF64: El sistema debe calcular el costo equivalente en EUR.
- RF65: El sistema debe guardar los costos calculados en la tabla consumo_costos.
- RF66: El sistema debe guardar el tipo de cambio usado para mantener trazabilidad historica.

### Modulo 11: Alertas

- RF67: El sistema debe evaluar automaticamente si un consumo supera un umbral.
- RF68: El sistema debe generar alerta por EXCESO_CONSUMO cuando se supere el limite de consumo.
- RF69: El sistema debe generar alerta por EXCESO_PRESUPUESTO cuando se supere el limite de presupuesto.
- RF70: El sistema debe asignar niveles de alerta como BAJA, MEDIA, ALTA o CRITICA.
- RF71: El sistema debe guardar mensaje, nivel, estado y fecha de generacion de la alerta.
- RF72: El sistema debe listar alertas registradas.
- RF73: El sistema debe filtrar alertas por sede.
- RF74: El sistema debe permitir marcar una alerta como atendida.

### Modulo 12: Auditoria

- RF75: El sistema debe registrar acciones importantes realizadas por usuarios.
- RF76: El sistema debe registrar auditoria cuando se crea un consumo.
- RF77: El sistema debe guardar modulo, accion, tabla afectada, id del registro afectado, descripcion y fecha.
- RF78: El sistema debe permitir listar auditorias.
- RF79: El sistema debe permitir filtrar auditorias por usuario.
- RF80: El sistema debe permitir filtrar auditorias por modulo.

### Modulo 13: Eventos De Acceso

- RF81: El sistema debe registrar eventos relacionados con accesos al sistema.
- RF82: El sistema debe guardar usuario, correo intentado, tipo de evento, descripcion, fecha e IP.
- RF83: El sistema debe permitir consultar la lista de eventos de acceso.
- RF84: El sistema debe servir como apoyo para revisar intentos o movimientos de acceso.

### Modulo 14: Dashboard

- RF85: El sistema debe mostrar un resumen general al ingresar al dashboard.
- RF86: El sistema debe mostrar total de sedes.
- RF87: El sistema debe mostrar total de consumos.
- RF88: El sistema debe mostrar total de alertas.
- RF89: El sistema debe mostrar costo total en PEN, USD y EUR.
- RF90: El sistema debe mostrar consumo agrupado por sede.
- RF91: El sistema debe mostrar costos agrupados por mes y moneda.

### Modulo 15: Reportes

- RF92: El sistema debe generar reportes mensuales por periodo.
- RF93: El sistema debe permitir consultar reportes usando un periodo como 2026-05.
- RF94: El sistema debe generar reportes por sede.
- RF95: El sistema debe agrupar informacion por periodo, sede y tipo de recurso.
- RF96: El sistema debe mostrar total consumido y costos en PEN, USD y EUR.
- RF97: El sistema debe usar consultas JPQL para obtener datos resumidos.

### Modulo 16: Datos Iniciales

- RF98: El sistema debe cargar roles iniciales.
- RF99: El sistema debe cargar permisos iniciales.
- RF100: El sistema debe crear el usuario administrador inicial.
- RF101: El sistema debe cargar monedas iniciales.
- RF102: El sistema debe cargar tipos de recurso iniciales.
- RF103: El sistema debe cargar sedes iniciales.
- RF104: El sistema debe cargar tipos de cambio iniciales.
- RF105: El sistema debe cargar tarifas y umbrales de prueba.

## 5. Requerimientos No Funcionales

- RNF1: El sistema debe usar Java 21.
- RNF2: El sistema debe estar construido con Spring Boot y Maven.
- RNF3: El sistema debe usar PostgreSQL como base de datos principal.
- RNF4: El sistema debe usar Spring Data JPA e Hibernate para persistencia.
- RNF5: El sistema debe usar Spring Security para proteger rutas.
- RNF6: El sistema debe usar Thymeleaf para renderizar vistas.
- RNF7: El sistema debe usar validaciones con Jakarta Validation.
- RNF8: El sistema debe usar Lombok `@Data` para reducir codigo repetitivo.
- RNF9: El sistema debe organizarse por modulos internos.
- RNF10: El sistema debe seguir la estructura Controller, Service, Repository y Model.
- RNF11: El sistema debe evitar exponer entidades directamente en vistas cuando se usen DTOs de respuesta.
- RNF12: El sistema debe mantener una base normalizada de 16 tablas.

## 6. Funcionalidad Principal Resumida

El caso de uso central es registrar un consumo operativo. El usuario autenticado ingresa la sede, el recurso, la cantidad consumida, la fecha y el periodo. El sistema busca la tarifa vigente, calcula el costo en PEN, convierte el resultado a USD y EUR usando tipos de cambio guardados, registra los costos calculados, evalua umbrales, genera alertas si corresponde y guarda auditoria de la accion.

## 7. Tablas Que Soportan Las Funcionalidades

```text
usuarios: usuarios del sistema.
roles: roles disponibles.
permisos: permisos funcionales.
usuario_roles: relacion entre usuarios y roles.
rol_permisos: relacion entre roles y permisos.
sedes: sedes corporativas.
tipos_recurso: recursos como luz, agua y gas.
tarifas_recurso: precios por sede y recurso.
consumos: registros de consumo.
monedas: monedas del sistema.
tipos_cambio: conversiones entre monedas.
consumo_costos: costos calculados por consumo.
umbrales: limites por sede y recurso.
alertas: alertas generadas por excesos.
auditorias: registro de acciones importantes.
eventos_acceso: eventos relacionados con acceso al sistema.
```

## 8. Resumen Para Sustentacion

Luxury es un sistema web empresarial que permite gestionar sedes corporativas y controlar sus consumos operativos. Esta desarrollado con Spring Boot MVC, Thymeleaf, Spring Security, JPA/Hibernate y PostgreSQL. El sistema registra consumos de luz, agua y gas, calcula costos en varias monedas, genera alertas cuando se superan umbrales, conserva auditoria y muestra reportes ejecutivos. Su estructura esta dividida en modulos internos, lo que permite mantener el codigo ordenado y alineado con una arquitectura Controller, Service, Repository y Model.
