# Contexto del Proyecto HomeCore

Este es un proyecto académico de HCI (Human-Computer Interaction) que consiste en un sistema de gestión de casas inteligentes con múltiples aplicaciones.

## Estructura del Monorepo

```
HomeCore/
├── homecore-web/          # Aplicación web (Vue 3 + Vite)
├── homecore-mobile/       # Aplicación móvil (Android + Kotlin)
├── docs/                  # Documentación y entregas
├── assets/                # Recursos compartidos
├── design-tokens.md       # Sistema de diseño compartido
└── .claude/              # Contexto de Claude
```

---

## Reglas de .gitignore

### Estructura de múltiples .gitignore

Este monorepo usa **3 archivos .gitignore** organizados por contexto:

1. **Raíz (`.gitignore`)**: Solo ignores GLOBALES
   - Archivos del OS (.DS_Store, Thumbs.db)
   - IDEs genéricos (.vscode/, .idea/)
   - Logs globales (*.log)
   - Variables de entorno (.env)

2. **`homecore-web/.gitignore`**: Específico de Node.js/Vue/Vite
   - node_modules/
   - dist/
   - .vercel/
   - *.local

3. **`homecore-mobile/.gitignore`**: Específico de Android/Gradle
   - *.apk, *.aab
   - .gradle/
   - local.properties
   - *.keystore, *.jks
   - google-services.json

### ⚠️ NO hacer

- ❌ NO duplicar ignores entre archivos
- ❌ NO poner rutas específicas en la raíz (ej: `homecore-mobile/build/`)
- ❌ NO agregar `node_modules/` en la raíz (va en homecore-web)

### ✅ Hacer

- ✅ Agregar ignores en el archivo más específico posible
- ✅ Si es global (OS, IDE) → raíz
- ✅ Si es de Node → homecore-web/.gitignore
- ✅ Si es de Android → homecore-mobile/.gitignore

---

## Sistema de Diseño (Design Tokens)

Existe un archivo **`design-tokens.md`** en la raíz que define:

- **Paleta de colores**: Tema oscuro con fondos `#0f0f14`, acento `#818cf8`
- **Colores por tipo de dispositivo**: 11 tipos con colores específicos
- **Tipografía**: Escalas de fuentes adaptadas (web px → mobile sp)
- **Espaciado**: Sistema de 4dp a 28dp
- **Border radius**: De 4dp a 20dp
- **Iconografía**: Mapeo Font Awesome (web) → Material Icons (mobile)

### ⚠️ NO hardcodear valores

**Mal:**
```kotlin
// ❌ Hardcoded
view.setBackgroundColor(Color.parseColor("#818cf8"))
```

**Bien:**
```kotlin
// ✅ Usando recursos
view.setBackgroundColor(ContextCompat.getColor(context, R.color.accent))
```

```xml
<!-- res/values/colors.xml -->
<color name="accent">#818cf8</color>
```

### Sincronización web ↔ mobile

Los colores, espaciados, y tipografías deben estar sincronizados:

- **Web**: `homecore-web/src/assets/styles/variables.css`
- **Mobile**: `homecore-mobile/app/src/main/res/values/{colors,dimens}.xml`
- **Referencia**: `design-tokens.md` (fuente de verdad)

Si cambias un color en web, actualiza `design-tokens.md` y propaga a mobile.

---

## Modularización del Código

### Principios generales

1. **DRY (Don't Repeat Yourself)**: No duplicar lógica
2. **Separación de responsabilidades**: Cada archivo/función tiene un propósito claro
3. **Arquitectura por capas**: Data → Repository → ViewModel → UI

### Android (Mobile)

Usar arquitectura **MVVM** estricta:

```
com.itba.homecore/
├── data/
│   ├── api/           # Interfaces Retrofit (solo definiciones)
│   ├── model/         # Data classes (Device, Room, etc.)
│   └── repository/    # Repositorios (lógica de datos)
├── ui/
│   ├── devices/       # Fragments y Adapters de dispositivos
│   ├── rooms/         # Fragments y Adapters de habitaciones
│   └── common/        # Componentes reutilizables
├── viewmodel/         # ViewModels (estado de UI)
└── utils/             # Funciones helper, extensions
```

**Reglas:**
- ❌ **NO** hacer llamadas de API directamente desde Fragment o ViewModel
- ✅ Fragment → ViewModel → Repository → API
- ❌ **NO** hacer lógica de negocio en Fragments
- ✅ Fragments solo observan LiveData y actualizan UI
- ❌ **NO** hardcodear strings, colores, o dimens
- ✅ Usar recursos (R.string, R.color, R.dimen)

### Vue (Web)

Seguir la estructura actual:

```
homecore-web/src/
├── components/        # Componentes reutilizables
│   ├── common/        # Genéricos (botones, modals)
│   └── devices/       # Específicos de dispositivos
├── composables/       # Lógica reutilizable (hooks)
├── stores/            # Pinia stores (estado global)
├── services/
│   └── api/          # Llamadas HTTP
└── views/            # Vistas principales (páginas)
```

**Reglas:**
- ❌ **NO** hacer fetch directo en componentes
- ✅ Componente → Composable/Store → Service API
- ❌ **NO** mezclar lógica de negocio con lógica de presentación
- ✅ Usar Composition API con `<script setup>`
- ❌ **NO** hardcodear colores
- ✅ Usar variables CSS de `variables.css`

---

## Internacionalización (i18n)

### Mobile (Android)

Todos los textos deben estar en archivos de recursos:

```xml
<!-- res/values/strings.xml (inglés) -->
<string name="devices_title">Devices</string>

<!-- res/values-es/strings.xml (español) -->
<string name="devices_title">Dispositivos</string>
```

**En código:**
```kotlin
// ✅ Correcto
textView.text = getString(R.string.devices_title)

// ❌ Incorrecto
textView.text = "Devices"
```

### Web (Vue)

Actualmente no está implementado, pero si lo agregan:
- Usar Vue I18n
- Archivos `locales/en.json` y `locales/es.json`

---

## Configuración de la API

### URL base

**Mobile:**
```kotlin
// ApiClient.kt
private const val BASE_URL = "http://10.0.2.2:8080/api/" // Emulador
// private const val BASE_URL = "http://192.168.x.x:8080/api/" // Dispositivo físico
```

**Web:**
```env
# .env.local
VITE_API_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080
```

### Autenticación

Ambas apps usan **JWT** en header:
```
Authorization: Bearer <token>
```

- Mobile: Guardar en `DataStore` o `SharedPreferences`
- Web: Guardar en `localStorage`

---

## ⚠️ IMPORTANTE: Git y GitHub

### Reglas absolutas

**NO HACER BAJO NINGUNA CIRCUNSTANCIA:**
- ❌ NO hacer `git commit`
- ❌ NO hacer `git push`
- ❌ NO hacer `git pull`
- ❌ NO hacer `git add`
- ❌ NO hacer operaciones con GitHub
- ❌ NO hacer `gh` commands

**Solo el usuario puede:**
- Hacer commits
- Push al repositorio
- Pull requests
- Merge de branches
- Cualquier operación de Git

**Puedes hacer:**
- ✅ Crear/editar/eliminar archivos
- ✅ Leer archivos
- ✅ Ver estado con `git status` (solo lectura)
- ✅ Ver diff con `git diff` (solo lectura)
- ✅ Ver log con `git log` (solo lectura)

### Formato de commits (solo referencia para el usuario)

Si el usuario te pide formato de mensaje de commit:

```
tipo: descripción breve

Cuerpo opcional con más detalles

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

**Tipos:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `chore`: Cambios de mantenimiento (deps, config)
- `docs`: Solo documentación
- `refactor`: Refactorización de código
- `test`: Agregar o modificar tests
- `style`: Cambios de formato (no afectan lógica)

---

## Testing

Antes de entregar, ejecutar checklist completo en:
`docs/tercera_entrega/checklist_testing.md`

Probar en:
- API 29 (Android 10 - mínimo)
- API 36 (Android 16 - más reciente)
- Teléfono y tablet
- Orientación vertical y horizontal
- Español e inglés

---

## Plan de Implementación

Seguir el plan detallado en:
`docs/tercera_entrega/plan_implementacion.md`

**11 fases** desde configuración inicial hasta entrega.

Usar la guía de referencia rápida:
`docs/tercera_entrega/guia_referencia_rapida.md`

---

## Equipo

**Grupo 15 - 3 personas:**
- Juan Ignacio Garcia Vautrin Raggio (63319)
- Victoria Helena Park (64498)
- Maria Del Pilar Resek (65528)

**Implicaciones de ser grupo de 3:**
- RF17-RF19 (Hogares) son **OPCIONALES** (para grupos de 3)
- Pueden implementarlos para obtener puntos extra
- Si no los implementan, deben cumplir todos los demás RF obligatorios

---

## Requisitos del Proyecto (Tercera Entrega)

### Requisitos Funcionales Obligatorios (RF)

**Autenticación:**
- **RF1**: Registrar cuenta
- **RF2**: Verificar cuenta (código enviado por email)
- **RF3**: Recuperar contraseña
- **RF4**: Cambiar contraseña
- **RF5**: Iniciar sesión (JWT)
- **RF6**: Cerrar sesión

**Dispositivos:**
- **RF7**: Gestionar dispositivos (crear/editar/eliminar)
- **RF8**: Consultar dispositivos (lista)
- **RF9**: Controlar dispositivos (ejecutar acciones)

**Rutinas:**
- **RF11**: Consultar rutinas
- **RF12**: Ejecutar rutinas

**Habitaciones:**
- **RF14**: Gestionar habitaciones (crear/editar/eliminar)
- **RF15**: Consultar habitaciones
- **RF16**: Vincular dispositivos a habitaciones

**Hogares (Opcional - para grupos de 3 integrantes):**
- **RF17**: Gestionar hogares (crear/editar/eliminar)
- **RF18**: Consultar hogares
- **RF19**: Vincular habitaciones a hogares

**Notificaciones:**
- **RF20**: Enviar notificaciones

### Requisitos Funcionales Opcionales (RF)

- **RF13**: Consultar acciones realizadas (historial)
- **RF21**: Restringir acceso a dispositivos, rutinas, habitaciones y hogares
- **RF22**: Consultar consumo eléctrico
- **RF23**: Planificar ejecución de rutinas (scheduling)

### Requisitos No Funcionales Obligatorios (RNF)

- **RNF1**: Internacionalización - Español e inglés
- **RNF2**: Barra de aplicación contextual (App Bar) - Título dinámico según pantalla
- **RNF3**: Personalización - Tema oscuro por defecto
- **RNF4**: Adaptabilidad a tipo de dispositivo - Teléfonos Y tablets
- **RNF5**: Adaptabilidad a orientación - Vertical Y horizontal
- **RNF6**: Compatibilidad - Android 10+ (API 29)

### Requisitos No Funcionales Opcionales (RNF)

- **RNF7**: Interactuar con dispositivos mediante comandos de voz
- **RNF8**: Capturar códigos QR de dispositivos

---

## Entregables de la Tercera Entrega

### 1. Informe (Máximo 30 páginas)

Debe incluir:

**RF y RNF implementados:**
- Lista de requisitos funcionales implementados con descripción breve y precisa
- Lista de requisitos no funcionales implementados con descripción breve y precisa

**Capturas de pantalla:**
- TODAS las vistas implementadas
- Comparación con versión final de prototipos (si hay diferencias)
- **Factor de forma**: Capturas en teléfonos Y tabletas
- **Orientación**: Capturas en vertical Y horizontal
- Resolución adecuada para apreciar detalles
- Ubicar capturas próximas para facilitar apreciación de cambios
- Incluir descripciones para ilustrar el flujo

**Decisiones de usabilidad:**

⚠️ **IMPORTANTE - Justificaciones requeridas:**

1. **Diferencias con prototipos:**
   - Justificar toda diferencia entre vistas implementadas y prototipos
   - NO se admiten cambios radicales salvo que estén fundamentados
   - NO justificaciones subjetivas o estéticas ("se ve mejor", "me gusta más")
   - Evidenciar trazabilidad entre problema de usabilidad y solución propuesta

2. **Adaptaciones a dispositivos:**
   - Justificar cambios estructurales para adaptar a teléfonos/tabletas
   - Justificar cambios para adaptar a orientación vertical/horizontal
   - NO adaptaciones basadas solo en redistribución o redimensionamiento
   - Evidenciar cambios sustanciales en:
     - Organización de información
     - Jerarquía visual
     - Comportamiento de la interfaz

3. **Diseño gráfico:**
   - Hacer referencia a colores utilizados
   - Hacer referencia a tipografías
   - Hacer referencia a imágenes/iconografía
   - Justificar decisiones con base en:
     - Criterios de usabilidad
     - Criterios de accesibilidad
     - Coherencia con identidad visual del producto

4. **Fundamentación teórica:**
   - Hacer referencia a contenidos de la materia
   - Aplicación práctica de conceptos (NO mera reproducción teórica)
   - Hacer referencia a Modelos de Persona definidos
   - Relacionar decisiones con necesidades y objetivos de los usuarios
   - Contemplar sugerencias del equipo docente

### 2. Aplicación Móvil (APK)

**Formato de entrega:**
- Archivo ZIP o RAR con:
  - Todos los archivos necesarios para compilar
  - APK generado y funcional
- ⚠️ **NO incluir**:
  - Carpeta `build/`
  - Frameworks no utilizados

**Repositorio GitHub:**
- Repositorio PRIVADO
- Compartir con docentes (agregar como colaboradores)
- Debe contener código fuente completo

**Instructivo de instalación:**
- Tipo de dispositivo recomendado (físico o emulador)
- Versiones de API Level compatibles
- Secuencia de pasos para instalar en dispositivo físico
- ⚠️ NO contemplar ejecución desde Android Studio
- NOTA: El instructivo puede incorporarse al informe

---

## Tipos de Dispositivos

El sistema soporta **11 tipos de dispositivos**:

### 1. Light (Luz)
- **Color**: `#f5a623` (naranja)
- **Estados**: On/Off
- **Controles**: Toggle + Brightness slider (0-100)
- **Acciones**: `turnOn`, `turnOff`, `setBrightness`

### 2. Door (Puerta)
- **Color**: `#6c8ebf` (azul)
- **Estados**: Abierta/Cerrada
- **Acciones**: `open`, `close`

### 3. Alarm (Alarma)
- **Color**: `#e05252` (rojo)
- **Estados**: Activada/Desactivada
- **Acciones**: `armAway`, `armStay`, `disarm`

### 4. Water (Canilla/Grifo)
- **Color**: `#4fc3f7` (celeste)
- **Estados**: Abierto/Cerrado
- **Acciones**: `open`, `close`

### 5. Curtain (Cortina/Persiana)
- **Color**: `#81c784` (verde)
- **Estados**: Abierta/Cerrada
- **Controles**: Botones Up/Down
- **Acciones**: `up`, `down`, `setLevel`

### 6. AC (Aire Acondicionado)
- **Color**: `#ba68c8` (púrpura)
- **Estados**: On/Off
- **Controles**: Toggle + Temperature slider + Mode selector
- **Acciones**: `turnOn`, `turnOff`, `setTemperature`, `setMode`
- **Modos**: cool, heat, fan, dry

### 7. Speaker (Parlante)
- **Color**: `#ff8a65` (naranja oscuro)
- **Estados**: Reproduciendo/Detenido
- **Controles**: Play/Stop + Volume slider
- **Acciones**: `play`, `pause`, `stop`, `setVolume`

### 8. Vacuum (Aspiradora)
- **Color**: `#90a4ae` (gris azulado)
- **Estados**: Activa/Inactiva/Docked
- **Acciones**: `start`, `pause`, `dock`

### 9. Fridge (Heladera)
- **Color**: `#4dd0e1` (cyan)
- **Controles**: Temperature sliders (fridge + freezer)
- **Acciones**: `setTemperature`, `setFreezerTemperature`

### 10. Oven (Horno)
- **Color**: `#ff7043` (naranja rojizo)
- **Estados**: On/Off
- **Controles**: Toggle + Temperature slider + Mode selector
- **Acciones**: `turnOn`, `turnOff`, `setTemperature`, `setMode`
- **Modos**: conventional, convection, grill

### 11. Lock (Cerradura)
- **Color**: `#2196f3` (azul Material)
- **Estados**: Locked/Unlocked
- **Acciones**: `lock`, `unlock`

---

## API Backend

### Base URL
```
http://10.0.2.2:8080/api/   (Emulador Android)
http://localhost:8080/api   (Web)
```

### Autenticación
Todas las requests (excepto login/register) requieren:
```
Authorization: Bearer <JWT_TOKEN>
```

### Endpoints principales

**Auth:**
- `POST /user` - Registro
- `POST /user/login` - Login
- `POST /user/verify/{code}` - Verificación
- `POST /user/logout` - Logout

**Devices:**
- `GET /devices` - Listar dispositivos
- `GET /devices/{id}` - Detalle de dispositivo
- `PUT /devices/{id}/{action}` - Ejecutar acción
- `POST /devices` - Crear dispositivo (opcional)
- `PUT /devices/{id}` - Editar dispositivo (opcional)
- `DELETE /devices/{id}` - Eliminar dispositivo (opcional)

**Rooms:**
- `GET /rooms` - Listar habitaciones
- `GET /rooms/{id}` - Detalle de habitación
- `POST /rooms` - Crear habitación
- `PUT /rooms/{id}` - Editar habitación
- `DELETE /rooms/{id}` - Eliminar habitación

**Routines:**
- `GET /routines` - Listar rutinas
- `GET /routines/{id}` - Detalle de rutina
- `PUT /routines/{id}/execute` - Ejecutar rutina

**Homes (opcional):**
- `GET /homes` - Listar hogares
- `GET /homes/{id}` - Detalle de hogar
- `POST /homes` - Crear hogar
- `PUT /homes/{id}` - Editar hogar
- `DELETE /homes/{id}` - Eliminar hogar

### WebSocket (Socket.IO)
```
URL: ws://10.0.2.2:8080
Auth: Authorization header con Bearer token

Eventos:
- deviceUpdate: Estado de dispositivo cambió
- routineExecuted: Rutina ejecutada
- notification: Notificación general
```

### Estructura de Response
```json
{
  "result": { ... },  // Datos exitosos
  "error": null       // O mensaje de error
}
```

---

## Tecnologías y Dependencias

### Web (homecore-web)
- **Framework**: Vue 3.5 (Composition API)
- **Router**: Vue Router 5.0
- **State**: Pinia 3.0
- **Build**: Vite 8.0
- **Charts**: Chart.js 4.5
- **WebSocket**: Socket.IO Client 4.8
- **HTTP**: fetch nativo

### Mobile (homecore-mobile)
- **Language**: Kotlin
- **Min SDK**: API 29 (Android 10)
- **Target SDK**: API 36 (Android 16)
- **Architecture**: MVVM
- **HTTP**: Retrofit 2.9.0 + OkHttp 4.11.0
- **Coroutines**: kotlinx-coroutines-android 1.7.3
- **Lifecycle**: lifecycle-viewmodel-ktx 2.7.0
- **Navigation**: navigation-fragment-ktx 2.7.7
- **Material**: material 1.11.0 (Material Design 3)
- **DataStore**: datastore-preferences 1.0.0
- **WebSocket**: Socket.IO Client 2.1.0

---

## Estructura de Datos

### Device (modelo)
```kotlin
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,    // enum: LIGHT, DOOR, etc.
    val state: DeviceState,
    val room: Room? = null,
    val meta: Map<String, Any>? = null
)

data class DeviceState(
    val status: String,           // "on", "off", "open", "closed", etc.
    val brightness: Int? = null,  // 0-100
    val temperature: Int? = null, // grados
    val volume: Int? = null,      // 0-100
    val mode: String? = null      // "cool", "heat", etc.
)
```

### Room (modelo)
```kotlin
data class Room(
    val id: String,
    val name: String,
    val home: Home? = null,
    val meta: Map<String, Any>? = null
)
```

### Routine (modelo)
```kotlin
data class Routine(
    val id: String,
    val name: String,
    val actions: List<RoutineAction>,
    val meta: Map<String, Any>? = null  // schedule, days, etc.
)

data class RoutineAction(
    val device: Device,
    val actionName: String,     // "turnOn", "setTemperature", etc.
    val params: Map<String, Any>? = null
)
```

---

## Flujos de Usuario Principales

### Flujo de Registro y Login
1. Usuario abre app → Pantalla de login
2. Click en "Registrarse"
3. Completa formulario (nombre, apellido, email, contraseña)
4. Submit → API genera código de verificación y lo envía por email
5. Pantalla de verificación con campo de código
6. Ingresa código → API verifica
7. Login automático → Pantalla principal (lista de dispositivos)

### Flujo de Control de Dispositivo
1. Usuario ve lista de dispositivos
2. Click en un dispositivo
3. Pantalla de detalle con controles específicos del tipo
4. Usuario interactúa (toggle, slider, botón)
5. App envía acción a API → `PUT /devices/{id}/{action}`
6. Loading state mientras procesa
7. API responde con estado actualizado
8. UI actualiza el estado del dispositivo
9. WebSocket notifica a otros clientes conectados

### Flujo de Gestión de Habitaciones
1. Usuario navega a sección "Habitaciones"
2. Ve lista de habitaciones con cantidad de dispositivos
3. Click en FAB "+" para crear nueva
4. Formulario: nombre + hogar (opcional)
5. Guardar → `POST /rooms`
6. Vuelve a lista con nueva habitación visible
7. Click en habitación → detalle con dispositivos vinculados
8. Botón "Agregar dispositivo" → lista de dispositivos sin habitación
9. Selecciona dispositivos → vincula
10. Dispositivos aparecen en la habitación

### Flujo de Ejecución de Rutina
1. Usuario navega a sección "Rutinas"
2. Ve lista de rutinas
3. Click en una rutina → detalle con acciones
4. Botón "Ejecutar rutina"
5. Loading state
6. API ejecuta cada acción secuencialmente
7. Muestra resultado (éxito/error)
8. Estados de dispositivos afectados se actualizan

---

## Patrones de Diseño Implementados

### Mobile (MVVM)
```
Fragment observa ViewModel
ViewModel expone LiveData/StateFlow
ViewModel llama Repository
Repository llama API
API retorna Result<T>
ViewModel actualiza LiveData
Fragment reacciona a cambios
```

### Web (Composition API + Pinia)
```
Component usa composable
Composable usa Pinia store
Store llama service API
Service retorna Promise
Store actualiza state
Component reactivo se actualiza
```

---

## Estados de UI

Usar sealed classes para estados:

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

En Fragment:
```kotlin
viewModel.uiState.observe(viewLifecycleOwner) { state ->
    when (state) {
        is UiState.Loading -> showLoading()
        is UiState.Success -> showData(state.data)
        is UiState.Error -> showError(state.message)
        is UiState.Idle -> {}
    }
}
```

---

## Manejo de Errores

### Network Errors
- Sin internet → "No se pudo conectar. Verifica tu conexión."
- Timeout → "La operación tardó demasiado. Intenta nuevamente."
- Server error (500) → "Error del servidor. Intenta más tarde."

### Auth Errors
- Token expirado (401) → Redirigir a login automáticamente
- Credenciales incorrectas → "Email o contraseña incorrectos"
- Email ya registrado → "Este email ya está en uso"

### Validation Errors
- Email inválido → "Ingresa un email válido"
- Contraseñas no coinciden → "Las contraseñas no coinciden"
- Campo vacío → "Este campo es requerido"

---

## Optimizaciones

### Mobile
- **Imágenes**: Usar vectores (VectorDrawable) en lugar de PNG donde sea posible
- **Layouts**: Preferir ConstraintLayout sobre LinearLayout anidados
- **RecyclerView**: Usar DiffUtil para actualizaciones eficientes
- **Coroutines**: Cancelar en onDestroy para evitar leaks
- **Caché**: Implementar Room DB para caché offline (opcional)

### Web
- **Lazy loading**: Componentes pesados con `defineAsyncComponent`
- **Debounce**: Inputs de búsqueda con `useDebounceFn`
- **WebSocket**: Reconnect automático con backoff exponencial
- **Virtual scrolling**: Para listas muy largas (opcional)

---

## Debugging

### Mobile
```bash
# Ver logs de la app
adb logcat | grep "HomeCore"

# Ver logs de errores
adb logcat *:E

# Ver estado de red
adb shell dumpsys connectivity

# Verificar almacenamiento
adb shell run-as com.itba.homecore ls -la files/datastore/
```

### Web
```javascript
// En browser console
localStorage.getItem('token')  // Ver token JWT
localStorage.clear()           // Limpiar storage
```

---

## Troubleshooting Común

### "Cannot connect to API"
1. Verificar que backend esté corriendo
2. En emulador: usar `10.0.2.2` no `localhost`
3. En dispositivo físico: usar IP de la PC en la red
4. Verificar firewall no bloquea puerto 8080

### "Token expired"
1. Verificar que API no cambió formato de JWT
2. Verificar que tiempo de expiración sea razonable
3. Implementar refresh token (opcional)

### "WebSocket not connecting"
1. Verificar URL correcta (ws:// no wss:// en desarrollo)
2. Verificar que token se esté enviando en headers
3. Ver logs del servidor para errores de conexión

### "Layouts se ven mal en tablet"
1. Crear layouts alternativos en `res/layout-sw600dp/`
2. Usar porcentajes en ConstraintLayout
3. Probar en múltiples tamaños de pantalla

---

## Capturas de Pantalla para el Informe

### Requisitos de las capturas

**Cobertura:**
- TODAS las vistas implementadas
- TODAS las variantes de diseño

**Factores de forma:**
- Capturas en **teléfono** (ej: Pixel 8, 6.2")
- Capturas en **tablet** (ej: Pixel Tablet, 10.95")

**Orientaciones:**
- Capturas en **vertical** (portrait)
- Capturas en **horizontal** (landscape)

**Calidad:**
- Resolución adecuada para apreciar detalles
- Sin bordes negros del emulador (crop si es necesario)
- Ubicar capturas relacionadas próximas para comparar

**Organización sugerida en el informe:**

```
Pantalla X - Teléfono
├── Vertical - Español
├── Vertical - Inglés
├── Horizontal - Español
└── Horizontal - Inglés

Pantalla X - Tablet
├── Vertical - Español
├── Vertical - Inglés
├── Horizontal - Español
└── Horizontal - Inglés
```

**Mínimo requerido por pantalla:**
- 1 captura en teléfono vertical
- 1 captura en teléfono horizontal
- 1 captura en tablet vertical
- 1 captura en tablet horizontal

**Total estimado**: ~80-100 capturas (considerando 10-12 pantallas principales)

### Cómo tomar capturas

**En emulador Android Studio:**
1. Abrir emulador
2. Navegar a la pantalla deseada
3. Click en ícono de cámara en la barra lateral del emulador
4. O usar `Ctrl + S` (Windows/Linux) o `Cmd + S` (Mac)
5. Se guardan en `~/Pictures/Screenshots/` por defecto

**Alternativa con ADB:**
```bash
adb exec-out screencap -p > screenshot.png
```

**Rotar emulador:**
- `Ctrl + F11` / `Ctrl + F12` para rotar
- O botón de rotación en la barra del emulador

### Descriptores para capturas

Cada captura debe tener una descripción breve que explique:
- Qué pantalla es
- Qué acción se está mostrando (si aplica)
- Diferencias respecto al prototipo (si las hay)
- Particularidades de la adaptación (teléfono/tablet, orientación)

**Ejemplo:**
> "Pantalla de lista de dispositivos en tablet horizontal. Se muestra un layout de dos columnas con la lista a la izquierda y el detalle del dispositivo seleccionado a la derecha (master-detail pattern). Esta adaptación difiere del prototipo original que mostraba solo la lista, mejorando la eficiencia al eliminar navegaciones innecesarias en pantallas grandes."

---

## Checklist Pre-Commit

Antes de hacer commit, verificar:

- [ ] No hay strings hardcodeados (usar R.string)
- [ ] No hay colores hardcodeados (usar R.color)
- [ ] No hay TODOs sin resolver en código crítico
- [ ] Imports organizados (Android Studio: Ctrl+Alt+O)
- [ ] No hay warnings del linter
- [ ] Código formateado (Ctrl+Alt+L)
- [ ] No hay archivos de configuración personal (.idea, local.properties)
- [ ] No hay secrets en código (API keys, tokens)

---

## Generación del APK y Entrega

### Generar APK de Debug

```bash
# En la raíz del proyecto mobile
cd homecore-mobile

# Limpiar builds anteriores
./gradlew clean

# Generar APK de debug
./gradlew assembleDebug

# El APK se genera en:
# homecore-mobile/app/build/outputs/apk/debug/app-debug.apk
```

### Generar APK de Release (Opcional)

Si quieren generar un APK optimizado para entrega:

1. **Crear keystore** (solo primera vez):
```bash
keytool -genkey -v -keystore homecore-release.keystore -alias homecore -keyalg RSA -keysize 2048 -validity 10000
```

2. **Configurar signing** en `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../homecore-release.keystore")
            storePassword = "password"  // Cambiar por password real
            keyAlias = "homecore"
            keyPassword = "password"    // Cambiar por password real
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

3. **Generar APK**:
```bash
./gradlew assembleRelease
```

⚠️ **IMPORTANTE**:
- NO commitear el keystore al repositorio
- NO compartir las contraseñas en el código
- Para este proyecto académico, APK debug es suficiente

### Instructivo de Instalación (Template)

Crear archivo `INSTALL.md` en `homecore-mobile/`:

```markdown
# Instructivo de Instalación - HomeCore Mobile

## Requisitos del Sistema

**Dispositivo Físico:**
- Android 10.0 o superior (API Level 29+)
- Mínimo 100 MB de espacio disponible
- Conexión a internet (WiFi o datos móviles)

**O Emulador:**
- Android Studio instalado
- Emulador configurado con:
  - API Level 29 o superior
  - Google APIs (para notificaciones)
  - x86_64 o ARM64 según arquitectura de la PC

## Instalación en Dispositivo Físico

### Paso 1: Habilitar Orígenes Desconocidos

1. Abrir **Configuración** del dispositivo
2. Ir a **Seguridad** o **Aplicaciones**
3. Buscar **Instalar aplicaciones desconocidas**
4. Seleccionar el navegador o gestor de archivos
5. Habilitar **Permitir desde esta fuente**

### Paso 2: Transferir el APK

**Opción A - Por cable USB:**
1. Conectar el dispositivo a la PC
2. Copiar `app-debug.apk` a la carpeta Downloads del dispositivo

**Opción B - Por enlace web:**
1. Subir el APK a Google Drive o similar
2. Abrir el enlace desde el navegador del dispositivo
3. Descargar el APK

### Paso 3: Instalar

1. Abrir el gestor de archivos del dispositivo
2. Navegar a **Downloads**
3. Tocar `app-debug.apk`
4. Confirmar instalación
5. Esperar a que se complete

### Paso 4: Configurar Backend

⚠️ **IMPORTANTE**: La app necesita conectarse al backend.

**Si el backend está en tu PC:**
1. Asegurarse de que el dispositivo y la PC estén en la misma red WiFi
2. Obtener IP de la PC:
   - Windows: `ipconfig` (buscar IPv4)
   - Mac/Linux: `ifconfig` o `ip addr` (buscar inet)
3. La app debe configurarse para usar `http://[IP_PC]:8080/api/`

**Nota**: Si la app tiene la URL hardcodeada a localhost, no funcionará en dispositivo físico.

### Paso 5: Primera Ejecución

1. Abrir la app **HomeCore**
2. Aceptar permisos de notificaciones (si aparece el diálogo)
3. Deberías ver la pantalla de login/registro
4. Si aparece error de red, verificar:
   - Backend está corriendo
   - Dispositivo y PC en la misma red
   - Firewall no bloquea el puerto 8080

## Instalación en Emulador

### Opción 1: Desde Android Studio

1. Abrir el proyecto en Android Studio
2. Crear o seleccionar un emulador (AVD)
   - Recomendado: Pixel 8 con API 36 y Google APIs
3. Click en **Run** (triángulo verde)
4. Esperar a que compile e instale automáticamente

### Opción 2: Instalar APK manualmente

1. Iniciar el emulador
2. Arrastrar y soltar `app-debug.apk` sobre la ventana del emulador
3. O usar ADB:
   ```bash
   adb install app-debug.apk
   ```

**Nota**: En emulador, la URL del backend debe ser `http://10.0.2.2:8080/api/` (no localhost).

## Verificación de Instalación

1. Abrir la app
2. Intentar registrar una cuenta
3. Si el registro funciona → instalación exitosa
4. Si hay errores de red → verificar configuración del backend

## Troubleshooting

**"Error al instalar"**
- Verificar que Android >= 10
- Verificar espacio disponible
- Desinstalar versión anterior si existe

**"No se puede conectar al servidor"**
- Verificar que backend está corriendo
- Verificar URL correcta (10.0.2.2 para emulador)
- Verificar firewall no bloquea puerto 8080

**"La app se cierra al abrir"**
- Verificar logs con `adb logcat`
- Revisar permisos otorgados

## Contacto

Para problemas con la instalación, contactar al equipo:
- Juan Ignacio Garcia Vautrin Raggio
- Victoria Helena Park
- Maria Del Pilar Resek
```

### Preparar ZIP de Entrega

```bash
# En la raíz del repo
cd homecore-mobile

# Crear carpeta de entrega
mkdir -p entrega-tp3

# Copiar APK
cp app/build/outputs/apk/debug/app-debug.apk entrega-tp3/

# Copiar instructivo
cp INSTALL.md entrega-tp3/

# Comprimir
zip -r homecore-mobile-tp3.zip entrega-tp3/

# O excluir build y comprimir todo el proyecto
zip -r homecore-mobile-source.zip . -x "*/build/*" "*.gradle/*" "*.idea/*" "local.properties"
```

**Contenido del ZIP final:**
- `app-debug.apk` (o `app-release.apk`)
- `INSTALL.md` (instructivo)
- Código fuente completo (sin carpetas build)

---

## Referencias Útiles

### Documentación oficial
- Android: https://developer.android.com/
- Kotlin: https://kotlinlang.org/docs/
- Material Design 3: https://m3.material.io/
- Vue 3: https://vuejs.org/
- Retrofit: https://square.github.io/retrofit/

### Archivos clave del proyecto
- Sistema de diseño: `design-tokens.md`
- Plan de implementación: `docs/tercera_entrega/plan_implementacion.md`
- Checklist de testing: `docs/tercera_entrega/checklist_testing.md`
- Guía de referencia: `docs/tercera_entrega/guia_referencia_rapida.md`
- Enunciado: `docs/tercera_entrega/enunciado_tercer_entrega.pdf`

---

**Última actualización**: 2026-05-16
