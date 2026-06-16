# Decisiones de Arquitectura — HomeCore Mobile

> Documento de trazabilidad de las decisiones técnicas tomadas durante la
> migración de la app web a Android (tercera entrega). Cada decisión incluye su
> **justificación** y, cuando corresponde, la referencia al contenido de la
> materia (clases 20–22) o al feedback de la segunda entrega.

**Última actualización:** 2026-06-11
**Stack:** Kotlin · Jetpack Compose · Material 3 · MVVM · Coroutines/StateFlow

---

## 1. Objetivo de esta etapa

Construir toda la estructura base de la app (pantallas, navegación, estados,
componentes reutilizables) **sin depender del backend**, usando datos de
prototipo, de modo que más adelante se pueda reconectar la API real **sin tocar
la UI ni los ViewModels**.

Esta decisión surgió de una necesidad concreta: durante la integración real
aparecían errores de `Invalid token` que bloqueaban el desarrollo de la
interfaz. Desacoplar la UI de la red permite avanzar con el diseño en paralelo a
la resolución de problemas de backend.

---

## 2. Arquitectura general: MVVM por capas

Se adoptó **MVVM estricto** con separación de responsabilidades en capas, tal
como exige el proyecto y como se vio en la materia (clase 22):

```
┌─────────────┐     ┌──────────────┐     ┌──────────────────────┐
│  UI         │ ──▶ │  ViewModel   │ ──▶ │ Repository (interfaz)│
│  (Compose)  │ ◀── │  (StateFlow) │ ◀── │                      │
└─────────────┘     └──────────────┘     └──────────┬───────────┘
   observa            expone estado         ┌────────┴────────┐
   estado             llama al repo         │                 │
                                       Mock*Repository   Remote*Repository
                                       (datos locales)   (Retrofit / API HCI)
```

**Reglas que se respetan:**

- La UI **solo observa estado** y emite eventos (clicks). No llama APIs ni
  contiene lógica de negocio.
- El ViewModel **no conoce Retrofit**: depende de una **interfaz** de
  repositorio, no de una implementación.
- El repositorio encapsula el origen de datos (mock o red).

**Por qué:** es la arquitectura que pide el enunciado, facilita el testing y
evita el acoplamiento temprano. En la clase 20 se remarcó que las funciones de
composición deben **describir UI y emitir `Unit`**, no manejar datos ni efectos
de red; este diseño lo garantiza estructuralmente.

---

## 3. Estrategia mock-first con interfaces intercambiables

> **Actualización (2026-06-13): la capa mock fue eliminada.** Una vez validada la
> integración con el backend real, se borraron `data/mock/MockData.kt` y las
> implementaciones `Mock*Repository`, y `AppModule` quedó proveyendo directamente
> las implementaciones `Remote*` (sin flag `USE_MOCK`). Las interfaces de
> repositorio se mantienen (la UI y los ViewModels siguen dependiendo de la
> abstracción). El resto de esta sección queda como registro histórico de por qué
> se usó mock-first durante el desarrollo.

### Decisión

Cada repositorio se definió como **interfaz** con **dos implementaciones**:

| Interfaz             | Mock (prototipo)          | Real (backend)              |
|----------------------|---------------------------|-----------------------------|
| `DevicesRepository`  | `MockDevicesRepository`   | `RemoteDevicesRepository`   |
| `RoutinesRepository` | `MockRoutinesRepository`  | `RemoteRoutinesRepository`  |
| `AuthRepository`     | `MockAuthRepository`      | `RemoteAuthRepository`      |

La selección se hace en **un único punto**, `di/AppModule.kt`:

```kotlin
object AppModule {
    const val USE_MOCK = true   // ⬅️ cambiar a false para usar el backend real

    val devicesRepository: DevicesRepository by lazy {
        if (USE_MOCK) MockDevicesRepository() else RemoteDevicesRepository()
    }
    // ...
}
```

### Por qué

- **Swap trivial:** reconectar el backend es cambiar `USE_MOCK = false`. Ni la
  UI ni los ViewModels se modifican, porque dependen de la abstracción
  (principio de inversión de dependencias).
- **Service Locator en vez de un framework de DI (Hilt/Koin):** para un proyecto
  académico, agregar Hilt sumaría plugins, procesadores de anotaciones y
  complejidad de build. Un `object` con un flag cumple el mismo objetivo
  (un solo lugar de decisión) sin costo de infraestructura.
- **Las implementaciones `Remote*` ya están listas:** son la lógica Retrofit que
  ya existía, solo movida detrás de la interfaz. No se descartó trabajo.

### Datos mock

`data/mock/MockData.kt` mantiene **listas mutables en memoria** que reproducen
los mockups (rutinas "Buenos días" / "Buenas noches" / "Riego automático";
dispositivos favoritos "Lámpara principal" / "Puerta principal"). Son mutables
para que las acciones (encender, marcar favorito, ejecutar) **se reflejen en
vivo** durante la sesión. Cada operación incluye un `delay()` corto para simular
latencia y poder validar los **estados de carga** de la UI.

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

> Nota: en la etapa mock (`USE_MOCK = true`) esta capa no se ejercita porque no
> hay red, pero queda lista para cuando se reconecte el backend.

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

## 8. Pantallas migradas (el feed)

Se construyeron las tres pantallas del feed siguiendo los mockups:

| Pantalla        | Contenido                                                        |
|-----------------|-----------------------------------------------------------------|
| `DashboardScreen` | Rutinas favoritas + dispositivos favoritos (grilla de 2 columnas) |
| `RoutinesScreen`  | Búsqueda, alta de rutina, cards con switch / estrella / "Ejecutar ahora" |
| `ProfileScreen`   | Perfil, consumo, historial y cerrar sesión                      |

**Componentes reutilizables** (`ui/components/CommonComponents.kt`):

- `HouseHeader` — encabezado con el nombre del hogar y **slot opcional** para la
  campana de notificaciones.
- `PanelCard` — tarjeta con título, acción "Ver todas" y un **slot de contenido**
  (`content: @Composable ColumnScope.() -> Unit`).

**Por qué slots:** la clase 20 presentó la **API de slots** de Compose como el
patrón para maximizar reutilización dejando "espacios vacíos" que el llamador
rellena. `HouseHeader` y `PanelCard` lo aplican: el mismo encabezado sirve para
Inicio (con campana) y para Usuario/Rutinas (sin campana) sin duplicar código.

La estructura visual usa `Column` / `Row` / `Box` anidados y `Scaffold` con sus
slots (`topBar`, `bottomBar`), tal como se vio en la misma clase.

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

No se modificó la lista de dependencias: Retrofit / OkHttp / Gson se mantienen
porque se necesitan para las implementaciones `Remote*` al reconectar el backend.

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

## 12. Internacionalización

Todos los textos visibles se definen en `res/values/strings.xml` (`R.string.*`),
no como literales en el código. Esto cumple el RNF1 (español/inglés) y deja
preparada la app para agregar `values-en/strings.xml`.

---

## 13. Mapa de decisiones ↔ contenidos de la materia

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

---

## 14. Pendientes conocidos (TODO)

- `RemoteDevicesRepository.setDeviceFavorite` es optimista: la API HCI no expone
  un endpoint claro de favorito de dispositivo; al reconectar habrá que definir
  la llamada real (probablemente `PUT /devices/{id}` con metadata).
- Navegación desde "Ver todas" del feed hacia las pantallas completas.
- Alta de rutina y de dispositivo (botones "+ Nueva …" hoy son placeholders).
- Historial real desde el endpoint de logs (hoy se deriva de los dispositivos en
  modo prototipo).
