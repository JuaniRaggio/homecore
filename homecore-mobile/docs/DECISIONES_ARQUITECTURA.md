# Decisiones de Arquitectura — HomeCore Mobile

> Documento de trazabilidad de las decisiones técnicas tomadas durante la
> migración de la app web a Android (tercera entrega). Cada decisión incluye su
> **justificación** y, cuando corresponde, la referencia al contenido de la
> materia (clases 20–22) o al feedback de la segunda entrega.

**Última actualización:** 2026-06-15
**Stack:** Kotlin · Jetpack Compose · Material 3 · MVVM · Coroutines/StateFlow · Retrofit/OkHttp · Socket.IO

---

## 1. Objetivo y estado

La UI y los ViewModels se construyeron **desacoplados del origen de datos**
(dependen de **interfaces** de repositorio), de modo que el backend real se
integró **sin tocar la UI ni los ViewModels**. Hoy la app corre **siempre contra
la API HCI real** (`https://hci.it.itba.edu.ar/api/`); la capa de prototipo/mock
que se usó al inicio fue eliminada.

El desacople surgió de una necesidad concreta: durante la integración aparecían
errores de `Invalid token` que bloqueaban el desarrollo de la interfaz. Separar
la UI de la red permitió avanzar con el diseño en paralelo y volvió trivial el
swap al backend real.

---

## 2. Arquitectura general: MVVM por capas

Se adoptó **MVVM estricto** con separación de responsabilidades en capas, tal
como exige el proyecto y como se vio en la materia (clase 22):

```
┌─────────────┐     ┌──────────────┐     ┌──────────────────────┐
│  UI         │ ──▶ │  ViewModel   │ ──▶ │ Repository (interfaz)│
│  (Compose)  │ ◀── │  (StateFlow) │ ◀── │          │           │
└─────────────┘     └──────────────┘     └──────────┴───────────┘
   observa            expone estado              Remote*Repository
   estado             llama al repo            (Retrofit / API HCI)
```

**Reglas que se respetan:**

- La UI **solo observa estado** y emite eventos (clicks). No llama APIs ni
  contiene lógica de negocio.
- El ViewModel **no conoce Retrofit**: depende de una **interfaz** de
  repositorio, no de una implementación.
- El repositorio encapsula el origen de datos (la API HCI vía Retrofit).

**Por qué:** es la arquitectura que pide el enunciado, facilita el testing y
evita el acoplamiento temprano. En la clase 20 se remarcó que las funciones de
composición deben **describir UI y emitir `Unit`**, no manejar datos ni efectos
de red; este diseño lo garantiza estructuralmente.

---

## 3. Inyección de dependencias: interfaces + service locator

### Decisión

Cada repositorio es una **interfaz** implementada por una clase `Remote*`
(Retrofit contra la API HCI). El cableado se hace en **un único punto**,
`di/AppModule.kt`, un `object` service-locator con instancias `lazy`:

```kotlin
object AppModule {
    val devicesRepository: DevicesRepository by lazy { RemoteDevicesRepository() }
    val routinesRepository: RoutinesRepository by lazy { RemoteRoutinesRepository() }
    val homesRepository: HomesRepository by lazy { RemoteHomesRepository() }
    fun authRepository(context: Context): AuthRepository = RemoteAuthRepository(context.applicationContext)
}
```

> **Histórico:** al inicio cada interfaz tenía además una implementación
> `Mock*Repository` (datos en memoria que reproducían los mockups) seleccionada
> con un flag `USE_MOCK`. Una vez validada la integración real, la capa mock y el
> flag se eliminaron; quedaron solo las `Remote*`. Las **interfaces se mantienen**:
> la UI y los ViewModels siguen dependiendo de la abstracción, no de Retrofit.

### Por qué

- **Inversión de dependencias:** la UI/VM dependen de la interfaz, no de la
  implementación. Esto permitió el swap mock → real sin tocarlas, y habilita
  inyectar fakes en tests.
- **Service Locator en vez de un framework de DI (Hilt/Koin):** para un proyecto
  académico, Hilt sumaría plugins, procesadores de anotaciones y complejidad de
  build. Un `object` con instancias `lazy` cumple el mismo objetivo (un único
  lugar de cableado) sin esa infraestructura.

---

## 4. Inyección en ViewModels sin romper `viewModel()`

### Problema

Compose instancia ViewModels con `viewModel()`, que usa **reflexión sobre el
constructor sin argumentos**. Si el VM recibe el repositorio por constructor
(`class VM(repo: Repository)`), `viewModel()` falla porque no encuentra un
constructor vacío.

### Decisión

Constructor **primario con la dependencia** + **constructor secundario sin
argumentos** que la resuelve desde `AppModule`:

```kotlin
class DevicesViewModel(
    private val repository: DevicesRepository
) : ViewModel() {
    constructor() : this(AppModule.devicesRepository)   // ← lo usa viewModel()
}
```

### Por qué

Es lo mejor de ambos mundos:
- `viewModel()` encuentra el constructor sin args y funciona como siempre.
- En **tests** se puede inyectar un repositorio falso por el constructor
  primario, sin levantar Compose ni la red.

Se evitó así crear `ViewModelProvider.Factory` manuales (más código repetitivo)
para cada pantalla.

---

## 5. Manejo de estado: `sealed class` + `StateFlow`

### Decisión

Cada pantalla con datos asíncronos expone un **`StateFlow` de una `sealed
class`** con los estados posibles:

```kotlin
sealed class DevicesUiState {
    object Loading : DevicesUiState()
    data class Success(val rooms: List<Room>, val devices: List<Device>) : DevicesUiState()
    data class Error(val message: String) : DevicesUiState()
}
```

La UI hace un `when (state)` exhaustivo.

### Por qué

- Tal como se mostró en la clase 22, modelar el estado con una interfaz/sealed de
  `Loading / Success / Error` **elimina estados inválidos** (no se puede estar
  "cargando" y "con error" a la vez) y obliga al `when` a contemplar todos los
  casos.
- `StateFlow` (vía `collectAsStateWithLifecycle`) respeta el ciclo de vida y es
  la forma recomendada de exponer estado a Compose.
- Las corrutinas se lanzan con `viewModelScope.launch`; **no se usa
  `LaunchedEffect` para llamar al repositorio**, error que la clase 20 marcó
  explícitamente como mala práctica (los side-effects de composición son para
  interacción de UI, no para acceso a datos).

---

## 6. Capa de red y el bug de "Invalid token"

### Diagnóstico

Al abrir la app, se entraba directo al feed con un **token viejo persistido** en
DataStore (de una sesión previa cuyo JWT ya había vencido). El backend rechazaba
la primera llamada con HTTP 401 + "Invalid token", pero **nada reaccionaba**: el
cliente seguía creyéndose logueado.

La app web ya tenía resuelto esto (en su `client.js`, un 401 borra el token y
redirige a login). Mobile no tenía esa red de seguridad.

### Decisión

Se replicó el mismo mecanismo, idiomático a Android:

1. `data/api/SessionEvents.kt` — un `SharedFlow` global donde la capa de red
   emite cuando ocurre un 401.
2. `ApiClient` — el interceptor OkHttp detecta `response.code == 401`, limpia el
   token en memoria y emite el evento.
3. `AuthViewModel` — observa ese flow; ante un 401 hace `logout()`, pone
   `isLoggedIn = false` (la app vuelve a Login) y muestra el mensaje
   *"Tu sesión expiró. Iniciá sesión nuevamente."*

Además se marcó el token con `@Volatile` para visibilidad correcta entre el hilo
de auth y el de OkHttp.

### Por qué

- Un token vencido es un caso esperable, no un crash: la app debe **recuperarse
  sola** y guiar al usuario.
- El mensaje es **específico**, atendiendo el feedback de la segunda entrega que
  criticaba mensajes genéricos como "Ocurrió un error inesperado".

---

## 7. Decisiones del modelo de datos

- **`Device.metadata.favorite`:** se agregó al modelo porque el JSON real del
  backend trae `"metadata": { "favorite": true }` y porque el feed de favoritos
  lo necesita. Antes los favoritos se guardaban en un `Set` local de la pantalla
  que arrancaba **vacío**, por lo que nunca aparecían. Ahora el favorito es una
  propiedad del dato (`Device.isFavorite()`), consistente entre Inicio y
  Dispositivos.
- **`Routine` + `RoutineMetadata`:** modelan `favorite`, `active`, `time`, `days`
  y `description`, que son los campos que muestran los mockups (horario y días de
  la rutina, estrella de favorito, switch de activa/inactiva).
- **Extensiones de dominio** (`isOn()`, `isFavorite()`, `category()`, `time()`,
  `days()`): centralizan la interpretación del estado para **no duplicar lógica**
  en cada pantalla, atendiendo el feedback de la segunda entrega sobre funciones
  duplicadas (`getDeviceInfo`, `formatTime`, etc.).
- **`door` (Puerta) vs `lock` (Cerradura) son tipos distintos de la API.** El
  catálogo `/devicetypes` define **11 tipos** e incluye ambos (verificado contra la
  API real). La **puerta** se abre/cierra **y** trae el lock integrado (`state.status`
  opened/closed + `state.lock` locked/unlocked; acciones `open`/`close`/`lock`/`unlock`).
  La **cerradura** es un dispositivo independiente que solo se traba/destraba
  (`lock`/`unlock`). Por eso existen `DeviceCategory.DOOR` y `DeviceCategory.LOCK`
  separados, con `DoorControls` (4 acciones) y `LockControls` (2). Nota: la web está
  más incompleta acá — su `resolveTypeKey` no tiene patrón para `lock`, así que un
  dispositivo cerradura cae a manejo genérico; mobile lo modela completo.

---

## 8. Pantallas y navegación

**Navegación:** manual. `MainActivity` maneja el flujo de auth (enum `AppScreen`:
login / register / verify / recover) y, una vez logueado, monta `MainScreen` con
una **bottom navigation de 5 tabs**. En pantallas anchas (tablet / teléfono
horizontal) el bottom bar se reemplaza por un `NavigationRail` y varias pantallas
adoptan layouts multi-panel (ver sección 15, RNF4/RNF5). `navigation/`
(Navigation Compose) quedó sin uso y vacío.

| Tab (label)        | Pantalla            | Contenido                                                            |
|--------------------|---------------------|----------------------------------------------------------------------|
| Inicio             | `DashboardScreen`   | Rutinas favoritas + dispositivos favoritos                           |
| Habitaciones       | `DevicesScreen`     | Dispositivos **agrupados por habitación** + alta de dispositivo/habitación (FAB) |
| Rutinas            | `RoutinesScreen` / `RoutineEditorScreen` | Búsqueda, lista, crear/editar/ejecutar rutinas (con acciones parametrizadas) |
| Actividad          | `ActivityScreen`    | Consumo eléctrico estimado + historial real de acciones              |
| Usuario            | `ProfileScreen`     | Perfil, idioma, tema, cambiar contraseña, cerrar sesión              |

- El tab "Dispositivos" se renombró a **"Habitaciones"** (ícono `MeetingRoom`):
  la vista muestra los dispositivos separados por habitación y aloja el botón para
  crear habitaciones, así que el nombre es más representativo y hace ese botón más
  fácil de encontrar.
- **Hogares no es un tab**: se gestionan desde el **dropdown del `HouseHeader`**
  (seleccionar hogar + "Nueva propiedad"). La app siempre opera dentro de un hogar
  (se auto-selecciona el primero al entrar).
- **Configuración inline**: idioma/tema/contraseña viven directo en Usuario (se
  quitó la rueda de configuración que abría un sheet).
- **Las rutinas se atan a un hogar (decisión de usabilidad, no limitación técnica).**
  Cada rutina se crea dentro del hogar activo (`metadata.homeId`) y aparece solo en
  el Inicio/lista de ese hogar. La API soporta rutinas *cross-home* y, de hecho, sus
  **acciones pueden afectar dispositivos de cualquier hogar** (el picker muestra
  todos), así que la flexibilidad de efecto ya existe. Aun así, **mobile no permite
  crear rutinas cross-home a propósito**: no hay una vista que represente con
  claridad una rutina que afecta a múltiples casas (¿en qué Inicio aparece?, ¿cómo
  se comunica su alcance?). Atarla a un hogar mantiene la navegación y la jerarquía
  de información claras. La infraestructura `crossHome` queda en el modelo y el
  filtro solo para **interoperar** con rutinas cross-home creadas desde la web.

**Componentes reutilizables** (`ui/components/`, un archivo por componente):
`HouseHeader` (con **slot opcional** de campana de notificaciones), `PanelCard`
(título + acción + **slot de contenido**), `UniformGrid` (grilla donde todas las
cards comparten la altura de la más alta, vía `SubcomposeLayout`), `AddFab`,
`HcButtons`, `HcInputs`, `StatusComponents`, `SheetHeader`.

**Por qué slots:** la clase 20 presentó la **API de slots** de Compose para
maximizar reutilización. `HouseHeader`/`PanelCard` lo aplican: el mismo encabezado
sirve con y sin campana sin duplicar código. La estructura usa `Column`/`Row`/`Box`
y `Scaffold` (`bottomBar`), como se vio en esa clase.

---

## 9. Cerrar sesión con confirmación

### Decisión

El botón "Cerrar sesión" en Usuario es un `Button` visible (rojo, con ícono de
logout) que abre un `AlertDialog` de confirmación antes de ejecutar la acción.

### Por qué

El feedback de la segunda entrega marcó explícitamente:
> *"No pedían confirmación antes de cerrar sesión"* y *"Confirmar todas las
> acciones destructivas"*.

Cerrar sesión es destructivo (se pierde el estado de la sesión), por lo que se
agregó el diálogo con mensaje claro y opción de cancelar.

---

## 10. Decisiones de build (Gradle)

El proyecto se bajó de **AGP 9.x a AGP 8.7.3** (Kotlin 2.0.20, Gradle 8.10.2).
Ese cambio destapó configuración que en 9.x se manejaba automáticamente. Se
ajustó:

| Cambio                                          | Motivo                                                                 |
|-------------------------------------------------|------------------------------------------------------------------------|
| Aplicar `kotlin.compose` en `app/build.gradle.kts` | En Kotlin 2.0 + AGP 8.x el plugin del compilador de Compose es **obligatorio**. |
| `android.useAndroidX=true` en `gradle.properties` | Requerido por las dependencias AndroidX/Compose.                       |
| `kotlinOptions { jvmTarget = "11" }`            | Alinear el target de Kotlin con el de Java (estaban en 21 vs 11).      |

> Estos ajustes son específicos de AGP 8.x. En AGP 9.x **no** se aplica
> `kotlin-android` explícito ni se usa `kotlinOptions {}` (la extensión se
> registra sola). Verificar siempre la versión de AGP antes de tocar plugins.

Dependencias de red: Retrofit / OkHttp / Gson para las implementaciones `Remote*`
contra la API HCI, y `socket.io-client` para el tiempo real (sección 16; se
excluye su `org.json` para usar el de la plataforma).

---

## 11. Material Design 3 y tema

### Decisión

`ui/theme/Theme.kt` usa un **`darkColorScheme` fijo** con los colores del sistema
de diseño (acento `#818cf8`, fondo `#0f0f14`, etc.). **No se usa dynamic color.**

### Por qué

- El dynamic color (Material You) toma colores del wallpaper del usuario y
  generaría **inconsistencias visuales** respecto al sistema de diseño durante el
  desarrollo. El enunciado pide **tema oscuro por defecto** (RNF3) y coherencia
  con la identidad visual.
- Los colores, espaciados y tipografías se toman de recursos / tokens, **sin
  hardcodear**, según la regla del proyecto y el feedback de la segunda entrega.

---

## 12. Internacionalización (RNF1)

Todos los textos visibles salen de recursos (`R.string.*`), no de literales en el
código. Hay **paridad completa** entre `res/values/strings.xml` (español, default)
y `res/values-en/strings.xml` (inglés) — ~248 claves cada uno. El idioma puede
**cambiarse desde Usuario** (se aplica con `AppCompatDelegate.setApplicationLocales`
y se persiste); `MainActivity` extiende `AppCompatActivity` para que el locale por
app funcione con Compose.

**Excepción documentada:** la capa de datos (repositorios, `SocketManager`,
`RoutineScheduler`) emite mensajes al usuario como **literales en español**
(snackbars/notificaciones/excepciones). Es el único lugar con texto en español
embebido, por no tener `Context`/`stringResource`. Los **identificadores y
comentarios son siempre en inglés**.

---

## 13. Unificación de strings de la API

Los strings que viajan a la API se centralizaron en **una única fuente** cada uno,
para que no haya literales sueltos que deriven del backend:

- **`DeviceAction` (enum, `data/model`)**: los 34 nombres de acción canónicos
  (`turnOn`, `setMode`, `setBrightness`, …) tal como los espera la API en
  `PATCH /devices/{id}/{action}`. Todo control y todo paso de rutina referencia
  `DeviceAction.X.api`, nunca un literal.
- **`DeviceValues` (`data/model`)**: valores de estado/parámetro (`DeviceStatus`,
  `AcMode`, `SpeakerGenre`, `OvenHeat`, …). Verificados **en vivo** contra la API.
  Hallazgo importante: la API es **inconsistente** — los modos son inglés
  (`cool`/`heat`/`fan`, `party`/`vacation`) pero el género es español-ish
  (`clasica`, `latina`); el status de alarma es camelCase (`armedAway`). Por eso
  no se puede inferir por idioma; se confirmó tocando los endpoints.
- **`DeviceCategory` (enum)**: los 11 tipos (`typeName` + `patterns` de alias para
  tolerar nombres en inglés/español que devuelve la API).

**Por qué:** si uno de estos strings está mal, se rompe el control del dispositivo
contra el backend. Centralizarlos hace que un cambio de la API se ajuste en **un
solo lugar** y elimina el riesgo de drift entre pantallas.

---

## 14. Controles por tipo y rutinas con acciones parametrizadas

- **Controles por dispositivo** (`ui/screens/devices/controls/`): un composable
  por tipo (`LightControls`, `AcControls`, …) que comparten **primitives**
  (`ControlSlider`, `SegmentedSelector`/`LabeledSelector`, `ColorSwatchRow`,
  `ControlTextField`). Cada tipo arma su layout pero el estilo es único.
- **Rutinas con parámetros** (`RoutineActionCatalog`): define, por tipo, la
  **totalidad** de acciones con sus parámetros (`Num`, `Choice`, `Code`,
  `ColorPick`, `Rooms`). El picker de rutinas es de 3 etapas (dispositivo →
  acción → parámetros) y **reusa los mismos primitives** para recolectar el valor.
  Así una rutina puede hacer todo lo que el detalle del dispositivo (no solo
  on/off): temperatura, modo, brillo, color, ubicación de la aspiradora, etc.
- **Consumo eléctrico (RF22):** `ActivityScreen` suma el `DeviceType.powerUsage`
  (del catálogo `/devicetypes`) de los dispositivos encendidos, igual que la web.

---

## 15. Adaptabilidad a dispositivo y orientación (RNF4 / RNF5)

El breakpoint vive en un único lugar (`ui/util/WindowSize.kt`): `isWideScreen()`
(`screenWidthDp >= WIDE_BREAKPOINT_DP = 600`) es la fuente de verdad que consultan
todas las pantallas, y `Modifier.readableWidth()` limita el ancho de un bloque de
contenido en pantallas anchas. Así "ancho" significa lo mismo en toda la app
(tablet en cualquier orientación y teléfono en horizontal) y no se repite el
cálculo por pantalla.

La adaptación **no es una mera redistribución/redimensionamiento**: cada pantalla
cambia su estructura de información y su jerarquía visual cuando hay espacio:

- **Navegación**: bottom `NavigationBar` (compacto) -> `NavigationRail` lateral
  (ancho). Cambia el patrón de navegación, no su tamaño.
- **Habitaciones**: lista de una columna (compacto) -> **master-detail** (ancho):
  la lista de habitaciones/dispositivos queda a la izquierda y el detalle del
  dispositivo seleccionado se abre en el panel derecho, sin navegación a pantalla
  completa. Mantiene el contexto de la lista mientras se opera un dispositivo.
- **Rutinas**: el editor reemplaza la pantalla (compacto) -> vive en el panel
  derecho junto a la lista (ancho), mismo patrón master-detail.
- **Inicio**: dos paneles apilados (compacto) -> dos secciones en paralelo
  (Rutinas | Dispositivos) que se ven de un vistazo sin scrollear; la grilla de
  dispositivos baja a 2 columnas porque cada panel ocupa media pantalla.
- **Actividad**: consumo sobre el historial (compacto) -> el consumo se vuelve una
  columna lateral fija junto al historial, de modo que el resumen no desaparece al
  crecer la lista.
- **Auth y Usuario**: contenido de columna única limitado con `readableWidth()` y
  centrado, para no estirar formularios a un ancho ilegible en tablet.

Los paneles que no aplican (sin selección) muestran un placeholder
(`select_device_hint`, `select_routine_hint`). El estado de UI que debe sobrevivir
la rotación usa `rememberSaveable` (incluida la selección del master-detail).

---

## 16. Tiempo real (WebSocket) y notificaciones (RF20)

- **WebSocket (`data/api/SocketManager.kt`, Socket.IO):** conecta al loguear con
  `{ token, apiKey }` en el handshake (transporte polling) y escucha
  `deviceEvent` / `deviceCreated` / `deviceDeleted` / `deviceUpdated` y
  `homeShared` / `homeUnshared`. Ante un evento **refresca el estado en vivo**
  (`DeviceSyncEvents` → `DevicesViewModel`) y **notifica los cambios externos**.
  Como el server reenvía al propio emisor, se **suprime el eco propio** (por
  `deviceId`, marcado en `runAction`/create/delete) y se **dedupea** por device.
  El payload solo trae `deviceId`, así que el nombre se resuelve con
  `DeviceRegistry` (mapa id→nombre que mantiene `DevicesViewModel`).
- **Notificaciones (`NotificationEvents` → `AppNotifier`):** canal + permiso
  `POST_NOTIFICATIONS` (API 33+). Disparan: rutina programada ejecutada
  ("Se ejecutó la rutina X en Casa Y", vía `RoutineScheduler`), cambios externos
  de dispositivos y hogar compartido/descompartido. La ejecución **manual** no
  notifica (solo snackbar).
- **Limitación documentada:** `RoutineScheduler` (scheduling client-side, RF23) y
  el socket corren **mientras la app está viva**; notificaciones con la app
  cerrada requerirían `WorkManager`/foreground service (fuera del alcance).

---

## 17. Modularización y comentarios

- Los archivos grandes se dividieron **por componente**: `CommonComponents` →
  `HouseHeader`/`AddFab`/`UniformGrid`/`HcInputs`/`HcButtons`/`StatusComponents`/
  `SheetHeader`; `DeviceUi` → `DeviceCard`/`DeviceCardFooter`/`DeviceIcons`/
  `DeviceTypeOption`; `ProfileScreen` → `+ Settings…`/`ChangePasswordSheet`;
  `DeviceSheets` → `AddDeviceSheet`/`AddRoomSheet`/`SheetTextField`. Mismo paquete
  y mismas firmas: cero cambios en los call sites.
- **Comentarios:** solo cuando agregan algo más que el código (el *por qué*, no el
  *qué*); se eliminaron los que repetían la línea siguiente y los que **acoplaban
  a la web** ("mirroring the web", `client.js`, etc.) — la alineación con la API
  se documenta acá, no en comentarios dispersos.

---

## 18. Mapa de decisiones ↔ contenidos de la materia

| Decisión                                  | Clase / concepto                                  |
|-------------------------------------------|---------------------------------------------------|
| `Column`/`Row`/`Box`, `Scaffold`          | Clase 20 — componentes de diseño y árbol de UI    |
| `HouseHeader` / `PanelCard` con slots     | Clase 20 — API de slots                           |
| No llamar al repo desde `LaunchedEffect`  | Clase 20 — side-effects son para UI, no para datos |
| Modelos con (de)serialización             | Clase 21 — modelo de datos para intercambio con la API |
| Repos que devuelven `Result<T>`           | Clase 21 — manejo de respuesta/errores            |
| Cliente con interceptores (token, logging)| Clase 21 — configuración del cliente HTTP         |
| MVVM por capas, repos detrás de interfaz  | Clase 22 — arquitectura y separación de capas     |
| `sealed UiState` (Loading/Success/Error)  | Clase 22 — estados sin combinaciones inválidas    |
| `StateFlow` + `viewModelScope`            | Clase 22 — estado y corrutinas en el ViewModel    |
| `DeviceAction`/`DeviceValues` (un valor canónico por string) | Clase 21 — alineación del modelo con la API |
| Primitives de control + componentes por archivo | Clase 20 — reutilización y composición            |
| `SharedFlow` para 401 / notificaciones / refresh del socket | Clase 22 — flujos de eventos desacoplados |

---

## 19. Pendientes y mejoras (no exigidos por el enunciado)

El enunciado **no pide tests automatizados**; el "testing" que sí corresponde es
**manual** (API Levels probados, teléfono/tablet, orientación, español/inglés),
documentado en `docs/tercera_entrega/checklist_testing.md` y reflejado en las
capturas del informe. Lo de abajo son mejoras de calidad, no requisitos.

- **Tests automatizados (mejora de calidad).** Hoy solo está el template de Android
  Studio. La arquitectura está lista para testear (VMs con inyección de interfaces
  por constructor); sumarían valor tests de los ViewModels (filtros de hogar/rutina,
  `isOn`, `toggleDevice`), de las extensiones de dominio (`category()`,
  `DeviceAction.fromApi`) y del `unwrapResult`/`apiCall`.
- **Ejecución en background.** `RoutineScheduler` (scheduling, RF23) y el WebSocket
  corren solo con la app viva; scheduling/notificaciones con la app cerrada
  requerirían `WorkManager`/foreground service.
- **Alarma `changeSecurityCode` en rutinas.** Queda fuera del picker de rutinas
  (necesita código viejo + nuevo); el resto de acciones de alarma sí están.

> Nota: que las rutinas se aten a un solo hogar **no** es un pendiente sino una
> decisión de usabilidad deliberada (ver §8).
