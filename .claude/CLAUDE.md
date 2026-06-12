# Contexto del Proyecto HomeCore

Este es un proyecto académico de HCI (Human-Computer Interaction) que consiste en un sistema de gestión de casas inteligentes con múltiples aplicaciones.

---

## Estado Actual del Proyecto Mobile (verificado 2026-06-12)

La app Android está construida con **Jetpack Compose + Material 3** (UI 100% declarativa: NO hay XML de layouts ni Fragments). Las decisiones técnicas y sus justificaciones están documentadas en `homecore-mobile/docs/DECISIONES_ARQUITECTURA.md` (leerlo antes de tocar arquitectura).

Puntos clave del estado actual:

- **Arquitectura**: MVVM por capas. Compose observa `StateFlow` del ViewModel; el ViewModel depende de **interfaces** de repositorio; cada interfaz tiene implementación `Mock*` (datos de prototipo en memoria, `data/mock/MockData.kt`) y `Remote*` (Retrofit contra la API HCI).
- **Selección mock/real**: `di/AppModule.kt` con `USE_MOCK = true`. **Hoy la app corre contra datos mock.** Reconectar el backend es poner `false` (y validar: ver riesgos en el plan).
- **Navegación**: manual con `enum AppScreen` en `MainActivity.kt` + bottom navigation de 4 tabs en `MainScreen.kt` (Inicio, Dispositivos, Rutinas, Usuario). Navigation Compose está como dependencia pero NO se usa (`navigation/NavGraph.kt` y `Screen.kt` están vacíos).
- **Implementado**: login y registro (RF1, RF5), logout con confirmación (RF6), feed de Inicio (rutinas y dispositivos favoritos), lista de dispositivos con búsqueda y agrupación por habitación (RF8), control on/off y acciones simples (RF9 parcial), alta de dispositivos y habitaciones vía bottom sheets (`DeviceSheets.kt`), lista y ejecución de rutinas (RF11, RF12), pantalla Usuario con consumo estimado e historial derivado, manejo global de 401 (`SessionEvents`), sesión persistida en DataStore.
- **Faltante (RF obligatorios)**: verificar cuenta (RF2), recuperar contraseña (RF3), cambiar contraseña (RF4), editar/eliminar dispositivos (RF7 parcial), detalle de dispositivo con controles por tipo (RF9 completo), editar/eliminar habitaciones y su consulta como sección (RF14, RF15), vincular/desvincular dispositivos a habitaciones (RF16), notificaciones (RF20).
- **Faltante (RNF)**: adaptabilidad a tablet (RNF4) y a orientación (RNF5) sin implementar (no hay WindowSizeClass); i18n incompleta (RNF1: `values/` tiene 73 strings, `values-en/` solo 29); app bar contextual parcial (RNF2: `HouseHeader` existe pero el nombre del hogar está hardcodeado).
- **Build**: AGP 8.7.3, Kotlin 2.0.20, Gradle 8.10.2, Compose BOM 2024.12.01, minSdk 29, targetSdk 35.

El plan vigente y priorizado está en `docs/tercera_entrega/plan_implementacion.md`.

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
// ❌ Hardcoded en el composable
Box(Modifier.background(Color(0xFF818CF8)))
```

**Bien:**
```kotlin
// ✅ Tokens del tema (ui/theme/Color.kt expuestos vía MaterialTheme)
Box(Modifier.background(MaterialTheme.colorScheme.primary))
```

### Sincronización web ↔ mobile

Los colores, espaciados, y tipografías deben estar sincronizados:

- **Web**: `homecore-web/src/assets/styles/variables.css`
- **Mobile**: `homecore-mobile/app/src/main/java/com/itba/homecore/ui/theme/Color.kt` (+ `Theme.kt`, `Type.kt`)
- **Referencia**: `design-tokens.md` (fuente de verdad)

Nota: quedan colores hardcodeados sueltos en algunas pantallas (estrella de favorito `0xFFFFD43B`, fondos de avatar, etc.); al tocarlas, moverlos a `Color.kt`.

Si cambias un color en web, actualiza `design-tokens.md` y propaga a mobile.

---

## Modularización del Código

### Principios generales

1. **DRY (Don't Repeat Yourself)**: No duplicar lógica
2. **Separación de responsabilidades**: Cada archivo/función tiene un propósito claro
3. **Arquitectura por capas**: Data → Repository → ViewModel → UI

### Android (Mobile) - Jetpack Compose

Usar arquitectura **MVVM** estricta. La app es 100% **Jetpack Compose + Material 3**. Estructura real del paquete:

```
com.itba.homecore/
├── data/
│   ├── api/           # Interfaces Retrofit + ApiClient (OkHttp, interceptores) + SessionEvents (bus de 401)
│   ├── model/         # Data classes (Device, Routine, Room, User) + extensiones de dominio
│   ├── mock/          # MockData: datos de prototipo mutables en memoria
│   ├── repository/    # Interfaces + implementaciones Mock* y Remote*
│   └── local/         # SessionManager (DataStore Preferences: token y datos de usuario)
├── di/                # AppModule: service locator, flag USE_MOCK elige Mock* o Remote*
├── viewmodel/         # ViewModels con StateFlow + sealed UiState (Loading/Success/Error)
├── ui/
│   ├── screens/       # Pantallas Compose (auth/, main/, devices/, routines/, rooms/, homes/)
│   ├── components/    # CommonComponents: HcButton, HcTextField, HouseHeader, PanelCard (API de slots)
│   └── theme/         # Color.kt, Theme.kt (darkColorScheme fijo, sin dynamic color), Type.kt
├── navigation/        # NavGraph.kt y Screen.kt (HOY VACIOS: la navegación es manual en MainActivity)
└── MainActivity.kt    # Estado de navegación (enum AppScreen) + arranque de MainScreen
```

**Reglas:**
- ❌ **NO** hacer llamadas de API directamente desde composables NI desde ViewModels
- ✅ Composable → ViewModel → Repository (interfaz) → API
- ❌ **NO** hacer lógica de negocio en composables
- ✅ Los composables solo observan StateFlow (`collectAsStateWithLifecycle`) y emiten eventos (clicks)
- ❌ **NO** llamar al repositorio desde `LaunchedEffect`: los datos se cargan en el ViewModel con `viewModelScope.launch` (mala práctica marcada en la clase 20)
- ✅ Los ViewModels dependen de las INTERFACES de repositorio. Patrón: constructor primario con la dependencia + constructor secundario sin argumentos que resuelve desde `AppModule` (así `viewModel()` funciona y los tests pueden inyectar fakes)
- ❌ **NO** hardcodear strings, colores, o dimens
- ✅ Usar recursos: `stringResource(R.string.x)`, `MaterialTheme.colorScheme` / `ui/theme/Color.kt`
- ✅ **Comentarios de código: TODOS en inglés.** Mensajes visibles al usuario: SIEMPRE en español (vía recursos o, en la capa de datos, literales en español). Nunca mezclar idiomas en comentarios
- ✅ Estado de UI que debe sobrevivir la rotación: `rememberSaveable` o subirlo al ViewModel (nunca `remember` pelado para datos que importan)
- ✅ Lógica compartida entre pantallas (íconos por categoría, formato de horarios, etc.) va en un solo lugar, no duplicada por pantalla (feedback E2)
- Ver justificaciones completas en `homecore-mobile/docs/DECISIONES_ARQUITECTURA.md`

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

Todos los textos deben estar en archivos de recursos. **Convención real del proyecto** (inversa a la típica): el idioma por defecto es ESPAÑOL.

```xml
<!-- res/values/strings.xml (ESPAÑOL, default) -->
<string name="devices_title">Dispositivos</string>

<!-- res/values-en/strings.xml (inglés) -->
<string name="devices_title">Devices</string>
```

Al agregar un string nuevo, agregarlo SIEMPRE en ambos archivos. Estado al 2026-06-12: `values/` tiene 73 strings y `values-en/` solo 29; hay que completar la traducción antes de entregar (RNF1).

**En código (Compose):**
```kotlin
// ✅ Correcto
Text(text = stringResource(R.string.devices_title))

// ❌ Incorrecto
Text(text = "Dispositivos")
```

### Web (Vue)

Actualmente no está implementado, pero si lo agregan:
- Usar Vue I18n
- Archivos `locales/en.json` y `locales/es.json`

---

## Configuración de la API

### URL base

**No hay backend local.** Web y mobile usan la misma API remota hosteada por la cátedra:

**Mobile:**
```kotlin
// data/api/ApiClient.kt (valor real en el código)
private const val BASE_URL = "https://hci.it.itba.edu.ar/api/"
```

**Web:**
```env
# .env.local
VITE_API_BASE_URL=https://hci.it.itba.edu.ar/api
VITE_API_KEY=<api key del grupo>
```

### Autenticación

Toda request lleva el header `X-API-Key` (constante `API_KEY` en `ApiClient.kt`). Las requests autenticadas agregan además **JWT**:
```
Authorization: Bearer <token>
```

- Mobile: token guardado en `DataStore` (`data/local/SessionManager.kt`); el interceptor de OkHttp agrega ambos headers y ante un 401 limpia el token y emite `SessionEvents.unauthorized` (la app vuelve a Login automáticamente)
- Web: guardado en `localStorage`

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

Actualizado el 2026-06-12 a un **plan de sprint final** basado en el estado real del código (qué está hecho, qué falta por RF/RNF, riesgos de la reconexión al backend y cierre de entrega).

⚠️ La guía `docs/tercera_entrega/guia_referencia_rapida.md` quedó desactualizada: asume Fragments/XML/LiveData. Sirven sus snippets de Retrofit y los design tokens; ignorar lo demás.

---

## Equipo

**Grupo 15 - 4 personas:**
- Matias Bernasconi (64188)
- Juan Ignacio Garcia Vautrin Raggio (63319)
- Victoria Helena Park (64498)
- Maria Del Pilar Resek (65528)

**Implicaciones de ser grupo de 4:**
- RF17-RF19 (Hogares) son **OPCIONALES** según enunciado (dice "opcional grupo de 3 integrantes")
- Como son 4, pueden implementarlos para completitud del sistema
- El enunciado no los hace obligatorios para grupos de 4, es decisión del equipo

---

## Aprendizajes de la Segunda Entrega (Web)

Correcciones y feedback del profesor que **deben aplicarse en la tercera entrega (mobile)**:

### ❌ Errores que NO deben repetirse:

#### Informe:
1. **Ortografía y redacción**
   - Errores detectados: decidió (sin tilde), código (sin tilde), creación, ejecución, aplicación, a través, energético
   - Frases mejorables: "cumpliendo con la ayudar a los usuarios"
   - ✅ **Solución**: Revisar TODO con corrector ortográfico antes de entregar

2. **Instructivo de instalación**
   - ❌ En 2da entrega fue archivo separado
   - ✅ En 3ra entrega: incluirlo como sección del informe

3. **Diseño gráfico incompleto**
   - ❌ Solo mencionaron paleta de colores
   - ✅ Debe incluir:
     - Paleta de colores completa (design-tokens.md)
     - Tipografía (tipos, jerarquías, tamaños en sp)
     - Iconografía (Material Icons, mapeo por tipo de dispositivo)
     - Formato de elementos (botones FAB/contained/outlined, TextInputLayout, Cards, border-radius)

4. **Capturas de pantalla**
   - ❌ Datos no representativos (palabras inexistentes en "Figura 20")
   - ✅ Usar datos reales o realistas en todas las capturas

5. **Versiones no especificadas**
   - ❌ Solo dijeron "Chrome, Firefox, Safari" sin versiones
   - ✅ Mobile: especificar API Levels testeados (API 29, API 33, API 36, etc.)

#### Justificaciones:
1. **Evitar justificaciones subjetivas**
   - ❌ "Se ve mejor", "es más bonito", "me gusta más"
   - ✅ Trazabilidad: Problema de usabilidad identificado → Solución implementada

2. **Justificaciones con modelos de persona**
   - ❌ Usar características genéricas ("interacción intuitiva para Marta" aplica a todos)
   - ✅ Usar características ESPECÍFICAS del modelo que justifican la decisión

3. **No forzar justificaciones**
   - ❌ Forzar referencias a diseño gráfico/temas/modelos cuando no aplica realmente
   - ✅ Solo justificar cuando existe relación directa y válida

4. **Correcciones de entregas anteriores**
   - ❌ No contemplaron TODAS las correcciones de 1ra entrega
   - ✅ En 3ra entrega: contemplar y explicitar TODAS las correcciones de 2da entrega

#### Código:
1. **Funciones duplicadas**
   - ❌ Web tenía: `getDeviceInfo`, `describeLogAction`, `formatTime`, `curtainUp` duplicadas
   - ❌ Constantes duplicadas: `DAY_OPTIONS`, `DAY_LEVELS`
   - ✅ Mobile: Refactorizar desde el inicio, usar helpers/utils centralizados

2. **Comentarios inadecuados**
   - ❌ Comentarios obvios: `// Loading state for edit home modal`
   - ❌ Comentarios en inglés mezclados con español (el problema fue la MEZCLA de idiomas)
   - ❌ Comentarios desactualizados: `<!-- Vista principal "Inicio" - contiene todo lo que estaba en page-content del HTML original -->`
   - ✅ Mobile (convención del equipo, 2026-06-12): comentarios TODOS en inglés, consistentes, y solo cuando agregan valor (lógica compleja, decisiones no obvias). Los mensajes visibles al usuario SIEMPRE en español

3. **Llamadas redundantes a API**
   - ❌ Web llamaba `/send-verification` cuando `/register` ya lo hace automáticamente
   - ❌ Llamaba `/mailconfig` innecesariamente al registrar
   - ❌ Llamaba `/state` cuando `/devices/{id}` ya incluye el estado
   - ✅ Mobile: Estudiar bien la API antes de implementar, evitar llamadas duplicadas

#### Usabilidad:
1. **Validaciones de formularios**
   - ❌ No mostraban errores en conjunto
   - ❌ No destacaban visualmente los campos con error
   - ❌ No indicaban campos requeridos con asterisco (*)
   - ❌ Mensajes genéricos: "Por favor complete los campos" sin especificar cuál
   - ✅ Mobile:
     - Indicar campos requeridos con asterisco
     - Highlight visual en campos con error (color rojo, borde)
     - Mostrar mensaje específico por cada campo
     - Resumen de errores si son múltiples

2. **Iconografía incorrecta**
   - ❌ Cruz (X) para borrar (debería ser trash/delete)
   - ❌ Estrella para favoritos (debería ser corazón - aunque en Android estrella es aceptable para "destacado")
   - ❌ Íconos de alarma y puerta poco representativos
   - ✅ Mobile: Usar Material Icons correctos y reconocibles

3. **Uso incorrecto de colores**
   - ❌ Rojo para estados normales de dispositivos (rojo = peligro/error)
   - ✅ Mobile: Seguir design-tokens.md estrictamente, rojo solo para errores/destructivo

4. **Confirmaciones faltantes**
   - ❌ No pedían confirmación antes de cerrar sesión
   - ❌ Confirmación de eliminación sin mostrar nombre de la entidad
   - ✅ Mobile:
     - Confirmar todas las acciones destructivas (eliminar, cerrar sesión)
     - Mostrar contexto en confirmaciones (ej: "¿Eliminar habitación 'Sala'?")
     - Usar AlertDialog con mensaje claro

5. **Mensajes de error genéricos**
   - ❌ "Ocurrió un error inesperado"
   - ❌ "Error de inicio de sesión" (sin especificar qué falló)
   - ❌ Al cambiar color de lámpara: "Color: #37629a" (no comprensible)
   - ✅ Mobile: Mensajes específicos según el error:
     - "Email o contraseña incorrectos"
     - "La cuenta no está verificada. Revisa tu email."
     - "Este email ya está registrado"
     - "No se pudo conectar. Verifica tu conexión a internet."

6. **Experiencia de usuario**
   - ❌ No permitir verificar cuenta fuera del flujo (si el usuario intenta login con cuenta no verificada, no hay opción para verificar desde ahí)
   - ❌ Dos códigos de verificación diferentes (el segundo invalida el primero, confuso)
   - ❌ Opciones de editar/eliminar en contexto incorrecto (habitaciones: botones en tarjeta cuando deberían estar en detalle)
   - ❌ Orden de dispositivos cambia al volver de detalle (confuso)
   - ❌ Botón "Crear cuenta" en pantalla de desembarco (debería ser link)
   - ❌ Representación hexadecimal de color no comprensible para usuarios (#37629a)
   - ✅ Mobile:
     - Flujos claros y consistentes
     - Permitir acciones relevantes desde cualquier punto
     - Mantener estado y orden consistente
     - Usar representaciones comprensibles para usuarios (ej: selector de color visual, no hex)

7. **Accesibilidad**
   - ❌ AIM Score 5.9/10
   - ❌ Errores de contraste
   - ❌ Texto alternativo redundante
   - ✅ Mobile:
     - Contrastar textos correctamente (mínimo 4.5:1 para texto normal, 3:1 para texto grande)
     - Content descriptions apropiados en ImageView/ImageButton
     - Tamaño mínimo táctil 48dp × 48dp

8. **JavaScript deshabilitado**
   - ❌ No verificaban si JS está activado
   - ✅ Mobile: No aplica (nativo)

9. **Onboarding**
   - ❌ No guiaban al usuario sobre cómo comenzar
   - ❌ Dashboard vacío si no hay rutinas/favoritos configurados
   - ✅ Mobile:
     - Mostrar hints/tips en primera ejecución
     - Empty states con CTAs claros ("Agrega tu primer dispositivo")
     - Considerar tutorial opcional

10. **Consistencia visual**
    - ❌ Tamaño y color de botones inconsistente
    - ❌ Capitalización inconsistente ("Nueva Habitación" vs "Nuevo dispositivo")
    - ❌ Página de verificación no mantenía consistencia visual con otras páginas
    - ✅ Mobile:
      - Usar Material Design 3 components consistentemente
      - Capitalización según Material Design guidelines (sentence case para la mayoría)
      - Mantener misma estructura visual en todas las pantallas

### ✅ Aspectos positivos a mantener:

1. **Informe bien estructurado**
   - Introducción contextualiza correctamente
   - Desarrollo completo
   - Conclusión no enumerativa, retoma puntos clave

2. **Implementación completa**
   - Todos los RF obligatorios cumplidos
   - Todos los RNF cumplidos
   - RF opcionales implementados (RF13, RF22, RF23)

3. **Arquitectura y código**
   - Separación de estilos correcta (aunque mejorable en consistencia de variables)
   - WebSocket funcionando
   - Validación HTML/CSS con pocos errores no atribuibles al código implementado

4. **Balance técnico**
   - Buen balance entre aspectos de diseño y técnicos en justificaciones
   - Aunque se pueden dar más detalles (ej: memory leaks)

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
https://hci.it.itba.edu.ar/api/   (API remota de la cátedra; la usan web y mobile)
```

### Autenticación
Toda request lleva `X-API-Key`. Las autenticadas agregan:
```
Authorization: Bearer <JWT_TOKEN>
```

### Endpoints (verificados contra `homecore-web/src/services/api/`, que ya funciona contra esta API)

**Auth (`/users`):**
- `POST /users/register` - Registro (la web envía name, email, password)
- `POST /users/login` - Login (devuelve JWT)
- `POST /users/logout` - Logout
- `POST /users/verify-account` - Verificar cuenta (body: `{ code }`)
- `POST /users/send-verification` - Reenviar código (body: `{ email }`). NO llamarlo después de register: register ya manda el mail (feedback E2)
- `POST /users/forgot-password` - Pedir código de recupero (body: `{ email }`)
- `POST /users/reset-password` - Resetear contraseña (body: `{ code, password }`)
- `POST /users/change-password` - Cambiar contraseña (body: `{ oldPassword, newPassword }`)
- `GET /users/profile` / `POST /users/profile` - Perfil del usuario

**Devices:**
- `GET /devices` - Listar dispositivos
- `GET /devices/{id}` - Detalle (YA incluye el estado: NO llamar a `/state` aparte, feedback E2)
- `POST /devices` - Crear dispositivo
- `PUT /devices/{id}` - Editar dispositivo (nombre, metadata)
- `DELETE /devices/{id}` - Eliminar dispositivo
- `PATCH /devices/{id}/{action}` - Ejecutar acción (body: array de params, puede ser `[]`)
- `GET /devices/logs/limit/{limit}/offset/{offset}` - Historial global (RF13)
- `GET /devices/{id}/logs/limit/{limit}/offset/{offset}` - Historial por dispositivo

**Rooms:**
- `GET /rooms` - Listar habitaciones
- `GET /rooms/{id}` - Detalle de habitación
- `POST /rooms` - Crear (la web envía `{ ...data, home: { id } }`)
- `PUT /rooms/{id}` - Editar habitación
- `DELETE /rooms/{id}` - Eliminar habitación
- `GET /rooms/{roomId}/devices` - Dispositivos de una habitación
- `POST /rooms/{roomId}/devices/{deviceId}` - Vincular dispositivo (RF16)
- `DELETE /rooms/devices/{deviceId}` - Desvincular dispositivo

**Routines:**
- `GET /routines` - Listar rutinas
- `GET /routines/{id}` - Detalle de rutina
- `POST /routines` - Crear rutina
- `PUT /routines/{id}` - Editar rutina (se usa para toggles de favorite/active vía metadata)
- `DELETE /routines/{id}` - Eliminar rutina
- `PATCH /routines/{id}/execute` - Ejecutar rutina

**Homes (RF17-19, opcionales):**
- CRUD en `/homes` (referencia: `homecore-web/src/services/api/homes.js`)

### Estructura de Response

En éxito la API envuelve el payload: `{ "result": ... }`. En error: `{ "error": { "code", "description" } }`.

⚠️ **Riesgo conocido para la reconexión del backend**: la web desenvuelve `result` en `client.js`; en mobile las interfaces Retrofit declaran tipos pelados (`List<Device>`, etc.). Al pasar `USE_MOCK = false` hay que verificar el contrato real y, si hace falta, envolver las respuestas con `ApiResponse<T>` (`data/model/ApiResponse.kt`) o un deserializador de Gson.

### WebSocket

La web usa Socket.IO (`homecore-web/src/services/socket.js`). En mobile NO está implementado; para RF20 (notificaciones) ver la estrategia elegida en `docs/tercera_entrega/plan_implementacion.md`.

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

Versiones reales en `homecore-mobile/gradle/libs.versions.toml`:

- **Language**: Kotlin 2.0.20
- **Build**: AGP 8.7.3, Gradle 8.10.2 (bajado de AGP 9.x; ver sección 10 de `DECISIONES_ARQUITECTURA.md` antes de tocar plugins)
- **Min SDK**: API 29 (Android 10) / **Target y Compile SDK**: API 35
- **UI**: Jetpack Compose (BOM 2024.12.01) + Material 3
- **Architecture**: MVVM (StateFlow, sin LiveData)
- **HTTP**: Retrofit 2.11.0 + OkHttp Logging 4.12.0 + Gson 2.11.0
- **Coroutines**: kotlinx-coroutines 1.8.1
- **Lifecycle**: lifecycle-viewmodel-compose / runtime-ktx 2.8.7
- **Navigation**: Navigation Compose 2.8.5 (declarada, AÚN NO usada)
- **DataStore**: datastore-preferences 1.1.1
- **WebSocket**: no hay (Socket.IO no está integrado en mobile)

---

## Estructura de Datos

Los modelos REALES viven en `homecore-mobile/app/src/main/java/com/itba/homecore/data/model/` (`Device.kt`, `Routine.kt`, `Room.kt`, `User.kt`, `AuthResponse.kt`, `ApiResponse.kt`). No duplicar sus definiciones acá: ante una duda, leer el archivo.

Claves a respetar:

- **`Device.metadata.favorite`** es la fuente de verdad de favoritos (viene del JSON del backend). NO volver a guardar favoritos en `Set`s locales de pantalla: eso causaba que se perdieran y que Inicio y Dispositivos quedaran inconsistentes.
- **`DeviceCategory`** (enum en `Device.kt`) cubre los 11 tipos: LAMP, DOOR, ALARM, FAUCET, BLINDS, AC, SPEAKER, VACUUM, REFRIGERATOR, OVEN, LOCK. Ojo con el mapeo de nombres respecto al enunciado (lamp = Light, faucet = Water, blinds = Curtain, refrigerator = Fridge).
- **`Routine` + `RoutineMetadata`** modelan `favorite`, `active`, `time`, `days`, `description`.
- **Extensiones de dominio** (`isOn()`, `isFavorite()`, `category()`, `time()`, `days()`) centralizan la interpretación del estado. Usarlas SIEMPRE en vez de reimplementar la lógica por pantalla (feedback E2 sobre funciones duplicadas).

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
5. App envía acción a API → `PATCH /devices/{id}/{action}`
6. Loading state mientras procesa
7. API responde con estado actualizado
8. UI actualiza el estado del dispositivo

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

### Mobile (Compose + MVVM)
```
Composable observa StateFlow del ViewModel (collectAsStateWithLifecycle)
ViewModel llama a la INTERFAZ del repositorio (viewModelScope.launch)
AppModule (USE_MOCK) resuelve MockXxxRepository o RemoteXxxRepository
Remote* llama Retrofit y devuelve Result<T>
ViewModel actualiza su StateFlow (sealed UiState)
Compose recompone con el nuevo estado
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

Cada pantalla con datos asíncronos define su propia sealed class de estados (convención ya usada en `DevicesViewModel`, `RoutinesViewModel`, `AuthViewModel`):

```kotlin
sealed class DevicesUiState {
    object Loading : DevicesUiState()
    data class Success(val rooms: List<Room>, val devices: List<Device>) : DevicesUiState()
    data class Error(val message: String) : DevicesUiState()
}
```

En el composable:
```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()
when (val s = state) {
    is DevicesUiState.Loading -> LoadingIndicator()
    is DevicesUiState.Success -> DevicesList(s.rooms, s.devices)
    is DevicesUiState.Error -> ErrorMessage(s.message, onRetry = viewModel::load)
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

### Mobile (Compose)
- **Listas**: `LazyColumn` / `LazyVerticalGrid` SIEMPRE con `key = { it.id }` para recomposición eficiente
- **Estado derivado**: `remember` / `derivedStateOf` para cálculos costosos (filtros de búsqueda, agrupaciones)
- **Rotación**: `rememberSaveable` para estado de UI local (texto de búsqueda, tab seleccionado)
- **State hoisting**: subir el estado al ViewModel cuando lo comparten varias pantallas
- **Imágenes**: vectores (`ImageVector` / Material Icons) en lugar de bitmaps
- **Coroutines**: usar `viewModelScope` (se cancela solo); no lanzar corrutinas sueltas desde la UI

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

### "La app muestra datos que no cambian / no pega a la red"
1. Revisar `di/AppModule.kt`: si `USE_MOCK = true`, la app usa datos de prototipo y NUNCA toca la red (es intencional)
2. Con `USE_MOCK = false`, mirar el logcat: el `HttpLoggingInterceptor` loguea request y response completos

### "Cannot connect to API"
1. La API es remota (`https://hci.it.itba.edu.ar/api/`): verificar conexión a internet del emulador/dispositivo
2. Verificar que el header `X-API-Key` se esté enviando (interceptor en `ApiClient.kt`)
3. Ver el body del error en logcat: la API responde `{ "error": { "code", "description" } }`

### "Token expired / Invalid token"
1. Ya está manejado: el interceptor detecta el 401, limpia el token y `SessionEvents` hace que `AuthViewModel` vuelva a Login
2. Si no redirige, verificar que `AuthViewModel` esté observando `SessionEvents.unauthorized`

### "Las respuestas vienen vacías o Gson tira error al deserializar"
1. Probable causa: la API envuelve el éxito en `{ "result": ... }` y la interfaz Retrofit declara el tipo pelado
2. Comparar con cómo lo desenvuelve la web (`homecore-web/src/services/api/client.js`) y ajustar con `ApiResponse<T>`

### "Layouts se ven mal en tablet / al rotar"
1. En Compose NO existen `layout-sw600dp/`: usar `WindowSizeClass` (o `BoxWithConstraints`) y elegir composición según el ancho
2. Estado que desaparece al rotar: cambiar `remember` por `rememberSaveable` o moverlo al ViewModel

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

### Paso 4: Conectividad

La app usa la API remota de la cátedra (`https://hci.it.itba.edu.ar/api/`): solo se necesita conexión a internet (WiFi o datos móviles). No hay que instalar ni configurar ningún backend local.

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

**Nota**: La app usa la API remota de la cátedra; el emulador solo necesita salida a internet.

## Verificación de Instalación

1. Abrir la app
2. Intentar registrar una cuenta
3. Si el registro funciona → instalación exitosa
4. Si hay errores de red → verificar conexión a internet del dispositivo

## Troubleshooting

**"Error al instalar"**
- Verificar que Android >= 10
- Verificar espacio disponible
- Desinstalar versión anterior si existe

**"No se puede conectar al servidor"**
- Verificar conexión a internet (WiFi o datos móviles)
- La API es remota (https://hci.it.itba.edu.ar); no requiere configuración local

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

**Última actualización**: 2026-06-12 (sincronizado con el estado real del código: Jetpack Compose, arquitectura mock-first con AppModule, endpoints reales de la API HCI)
