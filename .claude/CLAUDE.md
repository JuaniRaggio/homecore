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

## Commits

- No commitear sin que lo pida el usuario
- Mensajes en español o inglés (consistente)
- Formato: `tipo: descripción`
  - `feat: agregar pantalla de dispositivos`
  - `fix: corregir error en login`
  - `chore: actualizar dependencias`

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

Grupo 15 - 3 personas:
- Juan Ignacio Garcia Vautrin Raggio (63319)
- Victoria Helena Park (64498)
- Maria Del Pilar Resek (65528)

---

## Fechas Importantes

Ver `docs/Cronograma.pdf` para fechas de entregas.

---

**Última actualización**: 2026-05-16
