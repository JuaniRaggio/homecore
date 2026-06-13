# Plan de Implementación - HomeCore Mobile (Sprint Final)

> Reescrito el **2026-06-12** a partir del estado REAL del código en `homecore-mobile/`.
> El plan anterior asumía Fragments/XML y un proyecto desde cero; la app ya existe,
> está construida con **Jetpack Compose** y tiene aproximadamente el 60% de los RF
> obligatorios resueltos (varios solo contra datos mock).

## Información del Proyecto

- **Entrega**: domingo **14 de junio** (quedan ~2 días) - Defensa: jueves 25 de junio
- **Stack real**: Kotlin 2.0.20 - Jetpack Compose (BOM 2024.12.01) + Material 3 - MVVM con StateFlow - Retrofit 2.11 - DataStore - AGP 8.7.3
- **API**: remota, `https://hci.it.itba.edu.ar/api/` (header `X-API-Key` + JWT `Bearer`)
- **Arquitectura**: repos detrás de interfaces con implementaciones `Mock*` / `Remote*`, seleccionadas en `di/AppModule.kt` (`USE_MOCK`). Ver `homecore-mobile/docs/DECISIONES_ARQUITECTURA.md`
- **Equipo**: 4 personas (RF17-RF19 Hogares son OPCIONALES; quedan fuera del alcance del sprint)

---

## 1. Estado real por requisito (verificado 2026-06-12)

### Requisitos Funcionales obligatorios

| RF | Descripción | Estado | Evidencia / Falta |
|----|-------------|--------|-------------------|
| RF1 | Registrar cuenta | HECHO (validar contra API real) | `RegisterScreen.kt`, `POST /users/register` |
| RF2 | Verificar cuenta | **FALTA** | `VerifyScreen.kt` vacío. Endpoints: `POST /users/verify-account`, `POST /users/send-verification` |
| RF3 | Recuperar contraseña | **FALTA** | `RecoverScreen.kt` vacío. Endpoints: `forgot-password` + `reset-password` |
| RF4 | Cambiar contraseña | **FALTA** | Sin UI ni endpoint en `AuthApi`. Endpoint: `POST /users/change-password` |
| RF5 | Iniciar sesión | HECHO | `LoginScreen.kt`, JWT en DataStore, 401 global vía `SessionEvents` |
| RF6 | Cerrar sesión | HECHO | `UsuarioScreen.kt` con AlertDialog de confirmación (feedback E2 aplicado) |
| RF7 | Gestionar dispositivos | PARCIAL | Crear: hecho (`DeviceSheets.kt`). **Falta editar y eliminar** (`PUT/DELETE /devices/{id}`) |
| RF8 | Consultar dispositivos | HECHO | `DevicesScreen.kt`: búsqueda + agrupación por habitación |
| RF9 | Controlar dispositivos | PARCIAL | Toggle on/off y acciones simples. **Falta detalle con controles por tipo** (brillo, temperatura, modo, volumen, nivel de persiana) |
| RF11 | Consultar rutinas | HECHO | `RoutinesScreen.kt` |
| RF12 | Ejecutar rutinas | HECHO | `PATCH /routines/{id}/execute` + feedback visual |
| RF14 | Gestionar habitaciones | PARCIAL | Crear: hecho (sheet). **Falta editar y eliminar** |
| RF15 | Consultar habitaciones | PARCIAL | Solo como agrupador en Dispositivos. Falta vista/detalle de habitación |
| RF16 | Vincular dispositivos a habitaciones | PARCIAL | Solo al crear el dispositivo. **Falta vincular/desvincular existentes** (`POST /rooms/{roomId}/devices/{deviceId}`, `DELETE /rooms/devices/{deviceId}`) |
| RF20 | Enviar notificaciones | **FALTA** | Campana en `HouseHeader` es solo visual. Ver estrategia en sección 4.5 |

### Requisitos Funcionales opcionales

| RF | Estado | Nota |
|----|--------|------|
| RF13 Historial | CASI (datos fake) | `UsuarioScreen` ya tiene la card; conectar `GET /devices/logs/limit/{l}/offset/{o}` es barato |
| RF22 Consumo | CASI (estimación local) | Ya hay card de consumo; documentar como estimación |
| RF17-19 Hogares, RF21, RF23 | FUERA DE ALCANCE | Opcionales; no entran en 2 días |

### Requisitos No Funcionales obligatorios

| RNF | Estado | Falta |
|-----|--------|-------|
| RNF1 i18n es/en | PARCIAL | `values-en/` tiene 29/73 strings; además hay literales hardcodeados ("Cargando…", "Sin habitación", "Maria Fernandez", etc.) |
| RNF2 App Bar contextual | PARCIAL | `HouseHeader` existe pero el nombre del hogar está hardcodeado ("Casa de Verano") y el título no cambia por pantalla |
| RNF3 Tema oscuro | HECHO | `darkColorScheme` fijo con design tokens, sin dynamic color |
| RNF4 Teléfono y tablet | **FALTA** | Sin `WindowSizeClass`; mismo layout siempre |
| RNF5 Orientación | **FALTA** | Sin layouts por orientación; estado local con `remember` se pierde al rotar |
| RNF6 Android 10+ | HECHO | `minSdk = 29` |

---

## 2. Riesgos críticos (atacar PRIMERO)

1. **`USE_MOCK = true`**: hoy la app NO habla con el backend. La entrega exige RF reales.
   Apagar el flag temprano y validar cada flujo; cada hora que pase con mock es deuda oculta.
2. **Envelope `{ result: ... }`**: la API envuelve los éxitos en `result` (la web lo
   desenvuelve en `client.js`); las interfaces Retrofit de mobile declaran tipos pelados
   (`List<Device>`). Riesgo alto de que TODO falle al reconectar. Solución: envolver con
   `ApiResponse<T>` en `AuthApi`/`DevicesApi`/`RoomsApi`/`RoutinesApi` y desempaquetar en los `Remote*`.
3. **Contrato de register**: la web envía `{ name, email, password }`; el `AuthRepository`
   de mobile recibe también `lastName`. Verificar qué espera la API (probablemente `name` + `surname`).
4. **`setDeviceFavorite` optimista**: no hay endpoint claro de favorito; definir la llamada
   real (probablemente `PUT /devices/{id}` con `meta`) o degradar a favorito local persistido.
5. **Rotación**: al implementar RNF5, el estado local de pantallas (búsqueda, favoritos en
   memoria) se resetea. Migrar a `rememberSaveable`/ViewModel a medida que se toca cada pantalla.

---

## 3. Plan por días (4 personas)

### Viernes 13 (hoy a la noche / temprano sábado) - P0: backend real + auth completo

| Tarea | Detalle | Dueño sugerido |
|-------|---------|----------------|
| Reconexión backend | `USE_MOCK = false`; arreglar envelope `result` (riesgo 2); smoke test de login, devices, rooms, routines contra API real | Persona A (data layer) |
| Endpoints faltantes | Agregar a `AuthApi`: verify-account, send-verification, forgot-password, reset-password, change-password, profile. Agregar `PUT/DELETE /devices/{id}`, `PUT/DELETE /rooms/{id}`, vincular/desvincular | Persona A |
| RF2 Verificar cuenta | `VerifyScreen`: campo de código + reenviar. Integrarla al flujo de registro Y al login con cuenta no verificada (feedback E2: "permitir verificar fuera del flujo") | Persona B (auth UI) |
| RF3 Recuperar contraseña | `RecoverScreen` en 2 pasos: email (forgot) y código + nueva contraseña (reset). Link "Olvidaste tu contraseña" ya existe en Login | Persona B |
| RF4 Cambiar contraseña | Diálogo/pantalla desde "Gestionar contraseña" en Usuario (hoy es TODO) | Persona B |
| RF9 Detalle de dispositivo | `DeviceDetailScreen` (hoy vacío) con controles por tipo: lámpara (toggle + slider de brillo), AC (temperatura + modo), parlante (play/stop + volumen), persiana (subir/bajar + nivel), alarma (armar ausente/en casa + desarmar), horno, heladera, canilla (incluir control de cantidad: error marcado en E2), aspiradora, cerradura, puerta | Persona C (devices) |

### Sábado 13/14 - P0 restante + RNF obligatorios

| Tarea | Detalle | Dueño |
|-------|---------|-------|
| RF7 completar | Editar (renombrar) y eliminar dispositivo desde el detalle, con confirmación que muestre el NOMBRE ("¿Eliminar 'Lámpara del living'?", feedback E2) | Persona C |
| RF14/RF15/RF16 | Gestión de habitaciones: detalle de habitación (desde el header del grupo en Dispositivos), editar/eliminar ahí (feedback E2: acciones en el detalle, no en la tarjeta), vincular/desvincular dispositivos existentes | Persona C |
| RF20 Notificaciones | Mínimo viable: `NotificationManager` + canal + permiso `POST_NOTIFICATIONS` (API 33+). Disparar notificación local ante eventos reales (rutina ejecutada, dispositivo crítico cambiado). Si hay tiempo: polling liviano de `/devices` para detectar cambios externos. Documentar la decisión en el informe | Persona A |
| RNF4/RNF5 | `WindowSizeClass` (agregar `material3-window-size-class` o `material3-adaptive`): grilla de dispositivos con columnas según ancho, master-detail (lista + detalle) en tablet/landscape para Dispositivos. IMPORTANTE: el enunciado exige cambios SUSTANCIALES (organización/jerarquía/comportamiento), no mera redistribución. Pasar estado a `rememberSaveable` | Persona D (RNF) |
| RNF1 completar | Traducir los 44 strings faltantes a `values-en/`; extraer literales hardcodeados a recursos; auditar 100% español o 100% inglés por locale (feedback E2: lenguaje mezclado) | Persona D |
| RNF2 completar | Nombre de hogar dinámico (de la API o quitar el dropdown si no hay multi-hogar) y título contextual por pantalla | Persona D |
| Usuario real | `GET /users/profile` para nombre/avatar (eliminar "Maria Fernandez"); historial real con logs (RF13) | Persona A |

**Freeze de features: sábado a la noche.** Después de eso solo bugs, capturas e informe.

### Domingo 14 - Cierre y entrega

1. **Testing** según `checklist_testing.md`: API 29 y API 35/36, teléfono y tablet, vertical y horizontal, español e inglés. Sin conexión: mensaje claro con reintentar.
2. **Pulido de calidad** (solo lo que entre): deduplicar `iconForCategory` (está en `InicioScreen` y `DevicesScreen`) y la SearchBar; mover colores hardcodeados (`0xFFFFD43B`, etc.) a `Color.kt`; validaciones de formularios con asterisco en requeridos, error por campo y highlight (feedback E2); empty states con CTA.
3. **APK**: `./gradlew clean assembleDebug`; instalar en dispositivo/emulador limpio y probar el flujo completo de punta a punta.
4. **Capturas**: todas las pantallas x (teléfono, tablet) x (vertical, horizontal) x (es, en) con datos REALISTAS (feedback E2: nada de palabras inventadas).
5. **Informe** (ver sección 5) + instructivo INTEGRADO al informe.
6. **ZIP**: fuente sin `build/` + APK. Repo privado compartido con los docentes.

---

## 4. Notas técnicas por tarea

### 4.1 Reconexión del backend (P0, bloqueante de todo lo demás)
- `di/AppModule.kt`: `USE_MOCK = false`.
- Verificar con logcat (el logging interceptor ya imprime los bodies) si las respuestas vienen como `{ "result": ... }`. Si es así, cambiar las firmas Retrofit a `ApiResponse<T>` y desempaquetar en los `Remote*` (el patrón `Result<T>` de los repos no cambia, la UI no se entera).
- Mantener los `Mock*` funcionando: son útiles para capturas con datos prolijos y para demo offline en la defensa.

### 4.2 Auth completo (RF2-RF4)
- Reusar `HcTextField`/`HcButton` y el patrón de `AuthUiState` existente.
- Login con cuenta no verificada: la API va a devolver error; mostrar mensaje específico + botón "Reenviar código" que navegue a `VerifyScreen` (corrige error marcado en E2).
- Mensajes de error específicos por caso (nunca "Ocurrió un error inesperado").

### 4.3 Detalle de dispositivo (RF9)
- Un solo `DeviceDetailScreen` que hace `when (device.category())` y muestra la sección de controles del tipo. Acciones vía `PATCH /devices/{id}/{action}` con params en array (ej: `setBrightness` con `[80]`).
- Actualización optimista con rollback si falla (patrón ya validado en la web, feedback E1 sobre incertidumbre).
- Slider de color de lámpara: representación visual del color, NUNCA el hex (feedback E2).

### 4.4 Habitaciones (RF14-16)
- No hace falta una quinta tab: el prototipo de E1 agrupa dispositivos por habitación dentro de Dispositivos. Detalle de habitación accesible tocando el encabezado del grupo; ahí van editar/eliminar/vincular.
- Eliminar habitación: confirmación que explique qué pasa con sus dispositivos.

### 4.5 Notificaciones (RF20)
- La web usa Socket.IO; integrarlo en Android en 2 días es riesgo alto. Estrategia mínima defendible: notificaciones locales del sistema ante eventos que la app conoce (rutina ejecutada, alarma armada/desarmada) + (si hay tiempo) polling para cambios externos.
- No olvidar: canal de notificaciones, permiso runtime en API 33+, y degradación silenciosa en API 29-32.

### 4.6 Adaptabilidad (RNF4/RNF5) - clave para el informe
- Cambios sustanciales exigidos, sugerencia concreta:
  - Teléfono vertical: lista, navegación por pantallas (como hoy).
  - Teléfono horizontal: grilla de más columnas + bottom sheet para el detalle (la devolución de E1 criticó el desaprovechamiento del landscape en teléfono).
  - Tablet: master-detail permanente (lista a la izquierda, detalle a la derecha) y navegación lateral (NavigationRail) en vez de bottom bar.
- Cada una de estas decisiones se justifica en el informe con el cambio de jerarquía/comportamiento, no solo de tamaño.

---

## 5. Informe (máximo 30 páginas, instructivo INCLUIDO)

Estructura sugerida:

1. Introducción breve.
2. RF y RNF implementados (descripción breve y precisa, uno por uno).
3. Capturas de TODAS las vistas: teléfono/tablet x vertical/horizontal x es/en, con descriptores y comparación contra los prototipos de E1.
4. Decisiones de usabilidad. Para CADA decisión: problema identificado, solución, principio de la materia aplicado y persona beneficiada (características ESPECÍFICAS de Valentina/Carolina/Marta, no genéricas).
5. Diseño gráfico completo: paleta (design-tokens.md), tipografía con tamaños en sp, iconografía Material por tipo de dispositivo, formato de componentes.
6. **Tabla de correcciones de E2 contempladas** (explícita, una fila por corrección: qué marcó la cátedra y cómo se resolvió en mobile). La devolución de E2 penalizó no explicitar esto.
7. Instructivo de instalación (dispositivo físico, API levels testeados CON número, sin pasos de Android Studio).
8. Conclusión no enumerativa.

Revisión final obligatoria con corrector ortográfico (errores de tildes penalizados en E1 y E2).

Material de trazabilidad ya disponible: `homecore-mobile/docs/DECISIONES_ARQUITECTURA.md` mapea decisiones técnicas con clases de la materia y feedback de E2; reusarlo para la sección de justificaciones.

---

## 6. Checklist de entrega final

- [ ] `USE_MOCK = false` en el commit final (verificarlo DOS veces)
- [ ] Sin API keys nuevas hardcodeadas fuera de `ApiClient` (la key del grupo ya está ahí; evaluar moverla a `local.properties` si hay tiempo)
- [ ] APK debug generado e instalado en limpio, flujo completo probado
- [ ] ZIP sin `build/`, `.gradle/`, `.idea/`, `local.properties`
- [ ] Repo privado con los docentes como colaboradores
- [ ] Informe en PDF, menos de 30 páginas, pasado por corrector
- [ ] Capturas con datos realistas en los 8 contextos (2 dispositivos x 2 orientaciones x 2 idiomas)
- [ ] Checklist de testing (`checklist_testing.md`) ejecutado en API 29 y API 35/36
