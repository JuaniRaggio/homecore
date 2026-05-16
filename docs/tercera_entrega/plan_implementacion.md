# Plan de Implementación - HomeCore Mobile

Plan detallado para la implementación de la aplicación móvil Android de HomeCore.

## Información del Proyecto

- **Entrega**: Tercera Entrega - Aplicación Móvil
- **Tecnología**: Android Studio + Kotlin
- **API Mínimo**: Android 10 (API 29)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Referencia**: Aplicación web existente en `homecore-web/`

## Requisitos a Cumplir

### Requisitos Funcionales Obligatorios (RF1-RF20)

#### Autenticación
- [x] RF1: Registro de cuenta
- [x] RF2: Verificación de cuenta
- [x] RF3: Inicio de sesión
- [x] RF4: Cierre de sesión

#### Dispositivos
- [x] RF5: Consultar dispositivos
- [x] RF6: Consultar detalle de dispositivo
- [x] RF7: Controlar dispositivo

#### Rutinas
- [x] RF8: Consultar rutinas
- [x] RF9: Consultar detalle de rutina
- [x] RF10: Ejecutar rutina

#### Habitaciones
- [x] RF11: Consultar habitaciones
- [x] RF12: Gestionar habitaciones (crear/editar/eliminar)
- [x] RF13: Vincular/desvincular dispositivos a habitaciones

#### Hogares (Opcional)
- [ ] RF14: Consultar hogares
- [ ] RF15: Gestionar hogares (crear/editar/eliminar)
- [ ] RF16: Vincular/desvincular habitaciones a hogares

#### Notificaciones
- [x] RF17: Notificaciones push
- [x] RF18: Notificaciones en tiempo real (WebSocket)

### Requisitos No Funcionales (RNF1-RNF6)

- [x] RNF1: Internacionalización (español e inglés)
- [x] RNF2: Barra de aplicación contextual (App Bar)
- [x] RNF3: Personalización (tema oscuro por defecto)
- [x] RNF4: Adaptabilidad a tipo de dispositivo (teléfonos y tabletas)
- [x] RNF5: Adaptabilidad a orientación (vertical y horizontal)
- [x] RNF6: Compatibilidad con Android 10+

## Fases de Implementación

### FASE 0: Configuración Inicial (1-2 días)

**Objetivo**: Preparar el proyecto base con todas las configuraciones necesarias.

#### 0.1. Crear proyecto en Android Studio
- [ ] Crear proyecto "Empty Views Activity"
- [ ] Package name: `com.itba.homecore`
- [ ] Ubicación: `homecore-mobile/`
- [ ] Language: Kotlin
- [ ] Minimum SDK: API 29
- [ ] Build configuration: Kotlin DSL

#### 0.2. Configurar sistema de diseño
- [ ] Crear `res/values/colors.xml` basado en `design-tokens.md`
- [ ] Crear `res/values/dimens.xml` para espaciados y tamaños
- [ ] Crear `res/values/themes.xml` con tema oscuro
- [ ] Crear `res/values/strings.xml` (inglés por defecto)
- [ ] Crear `res/values-es/strings.xml` (español)

#### 0.3. Configurar dependencias
Agregar en `app/build.gradle.kts`:

```kotlin
dependencies {
    // Retrofit para API REST
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines para asincronía
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Material Design 3
    implementation("com.google.android.material:material:1.11.0")

    // DataStore para preferencias
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Socket.IO para WebSocket
    implementation("io.socket:socket.io-client:2.1.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
```

#### 0.4. Configurar permisos
Agregar en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

#### 0.5. Crear estructura de paquetes
```
com.itba.homecore/
├── data/
│   ├── api/
│   ├── model/
│   ├── repository/
│   └── local/
├── ui/
│   ├── auth/
│   ├── devices/
│   ├── rooms/
│   ├── routines/
│   ├── homes/
│   └── common/
├── viewmodel/
└── utils/
```

---

### FASE 1: Capa de Datos (3-4 días)

**Objetivo**: Implementar toda la comunicación con el backend.

#### 1.1. Modelos de datos
Crear data classes en `data/model/`:

- [ ] `User.kt` (id, email, firstName, lastName)
- [ ] `Device.kt` (id, name, type, state, room, meta)
- [ ] `DeviceType.kt` (enum: LIGHT, DOOR, ALARM, etc.)
- [ ] `Room.kt` (id, name, home, devices)
- [ ] `Home.kt` (id, name, meta, rooms)
- [ ] `Routine.kt` (id, name, actions, meta)
- [ ] `RoutineAction.kt` (device, actionName, params)
- [ ] `ApiResponse.kt` (result, error)

**Referencia**: Ver modelos en web (`homecore-web/src/stores/*.js`)

#### 1.2. API Interfaces (Retrofit)
Crear interfaces en `data/api/`:

- [ ] `AuthApi.kt`
  ```kotlin
  interface AuthApi {
      @POST("user")
      suspend fun register(@Body request: RegisterRequest): ApiResponse<User>

      @POST("user/login")
      suspend fun login(@Body credentials: LoginRequest): ApiResponse<LoginResponse>

      @POST("user/verify/{code}")
      suspend fun verify(@Path("code") code: String): ApiResponse<Unit>

      @POST("user/logout")
      suspend fun logout(): ApiResponse<Unit>
  }
  ```

- [ ] `DevicesApi.kt`
  ```kotlin
  interface DevicesApi {
      @GET("devices")
      suspend fun getDevices(): ApiResponse<List<Device>>

      @GET("devices/{id}")
      suspend fun getDevice(@Path("id") id: String): ApiResponse<Device>

      @PUT("devices/{id}/{action}")
      suspend fun executeAction(
          @Path("id") id: String,
          @Path("action") action: String,
          @Body params: Map<String, Any>? = null
      ): ApiResponse<List<Device>>
  }
  ```

- [ ] `RoomsApi.kt`
  ```kotlin
  interface RoomsApi {
      @GET("rooms")
      suspend fun getRooms(): ApiResponse<List<Room>>

      @GET("rooms/{id}")
      suspend fun getRoom(@Path("id") id: String): ApiResponse<Room>

      @POST("rooms")
      suspend fun createRoom(@Body request: CreateRoomRequest): ApiResponse<Room>

      @PUT("rooms/{id}")
      suspend fun updateRoom(@Path("id") id: String, @Body request: UpdateRoomRequest): ApiResponse<Room>

      @DELETE("rooms/{id}")
      suspend fun deleteRoom(@Path("id") id: String): ApiResponse<Unit>
  }
  ```

- [ ] `RoutinesApi.kt`
  ```kotlin
  interface RoutinesApi {
      @GET("routines")
      suspend fun getRoutines(): ApiResponse<List<Routine>>

      @GET("routines/{id}")
      suspend fun getRoutine(@Path("id") id: String): ApiResponse<Routine>

      @PUT("routines/{id}/execute")
      suspend fun executeRoutine(@Path("id") id: String): ApiResponse<Unit>
  }
  ```

- [ ] `HomesApi.kt` (opcional)

**Referencia**: Ver `homecore-web/src/services/api/*.js`

#### 1.3. Configuración de Retrofit
Crear `data/api/ApiClient.kt`:

- [ ] Configurar base URL desde BuildConfig
- [ ] Agregar interceptor para JWT (Authorization header)
- [ ] Agregar logging interceptor (solo debug)
- [ ] Configurar timeouts
- [ ] Manejo de errores globales

```kotlin
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/" // Emulador

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val devicesApi: DevicesApi = retrofit.create(DevicesApi::class.java)
    val roomsApi: RoomsApi = retrofit.create(RoomsApi::class.java)
    val routinesApi: RoutinesApi = retrofit.create(RoutinesApi::class.java)
}
```

#### 1.4. Repositorios
Crear repositorios en `data/repository/`:

- [ ] `AuthRepository.kt` (login, register, logout, getToken, saveToken)
- [ ] `DevicesRepository.kt` (getDevices, getDevice, executeAction)
- [ ] `RoomsRepository.kt` (CRUD completo)
- [ ] `RoutinesRepository.kt` (get, execute)
- [ ] `HomesRepository.kt` (opcional)

**Patrón**: Los repositorios exponen `Flow<Resource<T>>` o `suspend` functions.

```kotlin
class DevicesRepository(
    private val api: DevicesApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getDevices(): Result<List<Device>> = withContext(ioDispatcher) {
        try {
            val response = api.getDevices()
            if (response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### 1.5. Almacenamiento local
Crear en `data/local/`:

- [ ] `PreferencesManager.kt` (DataStore para token JWT, idioma, tema)
- [ ] `TokenManager.kt` (guardar/obtener/borrar token)

```kotlin
class PreferencesManager(private val context: Context) {
    private val dataStore = context.createDataStore("settings")

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    val token: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
}
```

---

### FASE 2: Autenticación (2-3 días)

**Objetivo**: Implementar flujo completo de autenticación (RF1-RF4).

#### 2.1. ViewModels
- [ ] `AuthViewModel.kt`
  - States: Idle, Loading, Success, Error
  - Functions: login(), register(), verify(), logout()
  - LiveData para estado de autenticación

#### 2.2. Pantallas de autenticación
Crear en `ui/auth/`:

- [ ] `LoginFragment.kt` (RF3)
  - Email y contraseña
  - Botón "Iniciar sesión"
  - Link a registro
  - Link a recuperar contraseña
  - Validación de campos
  - Mostrar errores del servidor

- [ ] `RegisterFragment.kt` (RF1)
  - Nombre, apellido, email, contraseña
  - Confirmar contraseña
  - Validación de campos
  - Al registrarse exitosamente → pantalla de verificación

- [ ] `VerifyFragment.kt` (RF2)
  - Campo para código de verificación
  - Botón "Verificar"
  - Reenviar código
  - Al verificar → login automático

- [ ] `RecoverPasswordFragment.kt` (opcional)

#### 2.3. Layouts
Crear XMLs en `res/layout/`:

- [ ] `fragment_login.xml`
- [ ] `fragment_register.xml`
- [ ] `fragment_verify.xml`

**Diseño**: Usar colores y espaciados de `design-tokens.md`

#### 2.4. Navigation Graph
- [ ] Crear `res/navigation/auth_nav_graph.xml`
- [ ] Definir transiciones entre login, register, verify

#### 2.5. Interceptor de autenticación
- [ ] Crear `AuthInterceptor.kt` que agregue `Authorization: Bearer <token>` a todas las requests
- [ ] Si token es null o expirado → redirigir a login

---

### FASE 3: Pantalla Principal y Dispositivos (4-5 días)

**Objetivo**: Implementar listado y control de dispositivos (RF5-RF7).

#### 3.1. ViewModels
- [ ] `DevicesViewModel.kt`
  - getDevices()
  - getDevice(id)
  - executeAction(deviceId, action, params)
  - LiveData<List<Device>>
  - LiveData<Device> para detalle

#### 3.2. Pantallas
Crear en `ui/devices/`:

- [ ] `DevicesListFragment.kt` (RF5)
  - RecyclerView con lista de dispositivos
  - Agrupado por habitación (si tiene)
  - Filtros por tipo de dispositivo
  - Pull-to-refresh
  - Click en item → navegar a detalle

- [ ] `DeviceDetailFragment.kt` (RF6, RF7)
  - Nombre, tipo, estado
  - Controles según tipo de dispositivo:
    - Toggle para on/off (light, ac, speaker, etc.)
    - Slider para brightness, volume, temperature
    - Botones para acciones específicas (door: open/close, alarm: arm/disarm)
  - Habitación asociada
  - Último estado conocido
  - Indicador de carga durante acción

#### 3.3. Adapters
- [ ] `DevicesAdapter.kt` (RecyclerView)
  - ViewHolder por tipo de dispositivo
  - Click listener
  - Mostrar icono según tipo (Material Icons)
  - Color según tipo (de `colors.xml`)

#### 3.4. Componentes de control
Crear custom views en `ui/common/`:

- [ ] `DeviceControlSwitch.kt` (toggle on/off)
- [ ] `DeviceControlSlider.kt` (brightness, volume, temp)
- [ ] `DeviceControlButtons.kt` (acciones específicas)

**Mapeo de tipos**:
Ver `homecore-web/src/config/device-types.js` para mapeo de:
- Iconos por tipo
- Colores por tipo
- Estados y acciones por tipo

#### 3.5. Layouts
- [ ] `fragment_devices_list.xml`
- [ ] `fragment_device_detail.xml`
- [ ] `item_device.xml` (para RecyclerView)

---

### FASE 4: Habitaciones (2-3 días)

**Objetivo**: Implementar gestión de habitaciones (RF11-RF13).

#### 4.1. ViewModels
- [ ] `RoomsViewModel.kt`
  - getRooms()
  - getRoom(id)
  - createRoom(name, homeId)
  - updateRoom(id, name)
  - deleteRoom(id)
  - addDeviceToRoom(roomId, deviceId)
  - removeDeviceFromRoom(roomId, deviceId)

#### 4.2. Pantallas
Crear en `ui/rooms/`:

- [ ] `RoomsListFragment.kt` (RF11)
  - RecyclerView con lista de habitaciones
  - Mostrar cantidad de dispositivos por habitación
  - FAB para crear nueva habitación
  - Click en item → detalle

- [ ] `RoomDetailFragment.kt` (RF11, RF13)
  - Nombre de la habitación
  - Lista de dispositivos vinculados
  - Botón "Agregar dispositivo"
  - Botón "Editar habitación"
  - Botón "Eliminar habitación" (con confirmación)

- [ ] `EditRoomFragment.kt` (RF12)
  - Campo para nombre
  - Dropdown para seleccionar hogar (opcional)
  - Botones guardar/cancelar

- [ ] `LinkDeviceToRoomFragment.kt` (RF13)
  - Lista de dispositivos disponibles
  - Checkbox para seleccionar
  - Botón "Vincular"

#### 4.3. Adapters
- [ ] `RoomsAdapter.kt`
- [ ] `RoomDevicesAdapter.kt`

#### 4.4. Layouts
- [ ] `fragment_rooms_list.xml`
- [ ] `fragment_room_detail.xml`
- [ ] `fragment_edit_room.xml`
- [ ] `item_room.xml`

---

### FASE 5: Rutinas (2-3 días)

**Objetivo**: Implementar consulta y ejecución de rutinas (RF8-RF10).

#### 5.1. ViewModels
- [ ] `RoutinesViewModel.kt`
  - getRoutines()
  - getRoutine(id)
  - executeRoutine(id)
  - LiveData<List<Routine>>
  - LiveData<Boolean> para estado de ejecución

#### 5.2. Pantallas
Crear en `ui/routines/`:

- [ ] `RoutinesListFragment.kt` (RF8)
  - RecyclerView con lista de rutinas
  - Mostrar nombre, cantidad de acciones
  - Botón "Ejecutar" por cada rutina
  - Click en item → detalle

- [ ] `RoutineDetailFragment.kt` (RF9, RF10)
  - Nombre de la rutina
  - Lista de acciones (dispositivo + acción)
  - Programación (días y hora, si aplica)
  - Botón grande "Ejecutar rutina"
  - Feedback visual de ejecución (loading + resultado)

#### 5.3. Adapters
- [ ] `RoutinesAdapter.kt`
- [ ] `RoutineActionsAdapter.kt`

#### 5.4. Layouts
- [ ] `fragment_routines_list.xml`
- [ ] `fragment_routine_detail.xml`
- [ ] `item_routine.xml`
- [ ] `item_routine_action.xml`

---

### FASE 6: Notificaciones (2 días)

**Objetivo**: Implementar notificaciones push y en tiempo real (RF17-RF18).

#### 6.1. WebSocket (Socket.IO)
Crear en `data/api/`:

- [ ] `WebSocketManager.kt`
  ```kotlin
  class WebSocketManager(private val token: String) {
      private val socket: Socket = IO.socket("ws://10.0.2.2:8080")

      fun connect() {
          socket.io().opts.extraHeaders = mapOf(
              "Authorization" to listOf("Bearer $token")
          )
          socket.connect()
      }

      fun onDeviceUpdate(callback: (Device) -> Unit) {
          socket.on("deviceUpdate") { args ->
              val device = Gson().fromJson(args[0].toString(), Device::class.java)
              callback(device)
          }
      }

      fun disconnect() {
          socket.disconnect()
      }
  }
  ```

- [ ] Integrar en `MainActivity` o `Application` class
- [ ] Actualizar LiveData cuando llega evento

#### 6.2. Notificaciones Push
- [ ] Configurar Firebase Cloud Messaging (FCM)
- [ ] Crear `MyFirebaseMessagingService.kt`
- [ ] Solicitar permisos en runtime (Android 13+)
- [ ] Mostrar notificación cuando la app está en background
- [ ] Navegar a pantalla relevante al hacer tap en notificación

#### 6.3. Notificaciones in-app
- [ ] Crear `ui/common/NotificationBanner.kt` (Snackbar o custom view)
- [ ] Mostrar cuando llegan eventos del WebSocket

---

### FASE 7: Hogares (Opcional - 2 días)

**Objetivo**: Implementar gestión de hogares (RF14-RF16).

Solo si quieren implementar la funcionalidad opcional:

- [ ] `HomesViewModel.kt`
- [ ] `HomesListFragment.kt`
- [ ] `HomeDetailFragment.kt`
- [ ] `EditHomeFragment.kt`

---

### FASE 8: Navegación y UI Global (2-3 días)

**Objetivo**: Implementar estructura global de navegación (RNF2).

#### 8.1. Activity principal
- [ ] `MainActivity.kt`
  - Contiene NavHostFragment
  - BottomNavigationView o NavigationDrawer
  - Toolbar con AppBar contextual
  - Gestiona logout

#### 8.2. Navigation Component
- [ ] Crear `res/navigation/main_nav_graph.xml`
- [ ] Definir destinos:
  - devices_list (inicio)
  - device_detail
  - rooms_list
  - room_detail
  - routines_list
  - routine_detail
  - settings
- [ ] Configurar transiciones y animaciones

#### 8.3. Bottom Navigation / Drawer
- [ ] Crear menú con opciones:
  - Dispositivos
  - Habitaciones
  - Rutinas
  - Hogares (opcional)
  - Configuración
  - Cerrar sesión

#### 8.4. App Bar
- [ ] Título dinámico según pantalla
- [ ] Botón de retroceso
- [ ] Menú contextual (search, filter, etc.)

---

### FASE 9: Requisitos No Funcionales (3-4 días)

**Objetivo**: Cumplir todos los requisitos no funcionales.

#### 9.1. Internacionalización (RNF1)
- [ ] Completar `res/values/strings.xml` (inglés)
- [ ] Completar `res/values-es/strings.xml` (español)
- [ ] Permitir cambiar idioma en Settings
- [ ] Verificar que TODOS los textos usen `@string/`
- [ ] Probar en ambos idiomas

**Textos a traducir**:
- Títulos de pantallas
- Labels de formularios
- Mensajes de error
- Nombres de estados de dispositivos
- Nombres de acciones
- Confirmaciones

#### 9.2. Adaptabilidad a dispositivos (RNF4)
- [ ] Crear layouts alternativos en `res/layout-sw600dp/` para tablets
- [ ] Usar `ConstraintLayout` con porcentajes en lugar de dp fijos
- [ ] Probar en múltiples tamaños de pantalla (4", 6", 10")
- [ ] Master-detail pattern para tablets (lista + detalle en mismo screen)

#### 9.3. Adaptabilidad a orientación (RNF5)
- [ ] Crear layouts alternativos en `res/layout-land/` donde sea necesario
- [ ] Asegurar que los estados se preservan en rotation
- [ ] Usar `ViewModel` para mantener datos durante rotation
- [ ] Probar rotación en todas las pantallas

#### 9.4. Tema oscuro (RNF3)
- [ ] Definir tema oscuro en `themes.xml`
- [ ] Usar `?attr/colorPrimary` en lugar de colores hardcoded
- [ ] Opcional: permitir cambiar a tema claro en Settings
- [ ] Verificar contraste de textos

#### 9.5. Compatibilidad Android 10+ (RNF6)
- [ ] Probar en emulador con API 29
- [ ] Probar en emulador con API 36
- [ ] Verificar permisos runtime
- [ ] No usar APIs deprecated

---

### FASE 10: Pulido y Testing (3-4 días)

**Objetivo**: Asegurar calidad y preparar entrega.

#### 10.1. Manejo de errores
- [ ] Mostrar mensajes de error amigables (no stack traces)
- [ ] Pantalla de error de red con botón "Reintentar"
- [ ] Loading states en todas las pantallas
- [ ] Empty states (sin dispositivos, sin rutinas, etc.)

#### 10.2. UX
- [ ] Feedback visual para todas las acciones (loading, success, error)
- [ ] Confirmaciones para acciones destructivas (eliminar)
- [ ] Transiciones suaves entre pantallas
- [ ] Animaciones para loading (ProgressBar, Skeleton)
- [ ] Pull-to-refresh en listas

#### 10.3. Optimizaciones
- [ ] Caching de respuestas API (opcional)
- [ ] Imágenes optimizadas
- [ ] Reducir llamadas redundantes a la API
- [ ] Cancelar coroutines cuando Fragment se destruye

#### 10.4. Testing manual
- [ ] Crear checklist de pruebas (archivo separado)
- [ ] Probar todos los flujos:
  - Registro → Verificación → Login
  - Ver dispositivos → Ver detalle → Controlar
  - Ver habitaciones → Crear → Editar → Eliminar
  - Ver rutinas → Ver detalle → Ejecutar
- [ ] Probar en múltiples dispositivos/emuladores
- [ ] Probar en español e inglés
- [ ] Probar rotación de pantalla
- [ ] Probar sin conexión a internet

#### 10.5. Generación de APK
- [ ] Build → Build Bundle(s) / APK(s) → Build APK(s)
- [ ] Probar APK en dispositivo físico
- [ ] Verificar que funciona sin Android Studio

---

### FASE 11: Documentación y Entrega (2 días)

**Objetivo**: Preparar informe y repositorio para entrega.

#### 11.1. README del proyecto móvil
- [ ] Crear `homecore-mobile/README.md`
- [ ] Instrucciones de compilación
- [ ] Configuración de la API URL
- [ ] Estructura del proyecto
- [ ] Tecnologías utilizadas

#### 11.2. Capturas de pantalla
Crear carpeta `docs/tercera_entrega/screenshots/`:

- [ ] Login (español e inglés)
- [ ] Registro
- [ ] Verificación
- [ ] Lista de dispositivos
- [ ] Detalle de dispositivo (varios tipos)
- [ ] Control de dispositivo
- [ ] Lista de habitaciones
- [ ] Detalle de habitación
- [ ] Lista de rutinas
- [ ] Detalle de rutina
- [ ] Configuración
- [ ] Notificación
- [ ] Orientación horizontal
- [ ] Tablet (si implementan)

**Para cada pantalla**:
- Captura en vertical
- Captura en horizontal
- Captura en español
- Captura en inglés

#### 11.3. Video demostración
- [ ] Grabar video de 2-3 minutos mostrando todas las funcionalidades
- [ ] Mostrar flujo completo: registro → login → uso

#### 11.4. Informe
Crear `docs/tercera_entrega/informe.md`:

- [ ] Introducción
- [ ] Decisiones de diseño
- [ ] Arquitectura (diagrama)
- [ ] Tecnologías utilizadas
- [ ] Requisitos cumplidos (tabla con checkmarks)
- [ ] Dificultades encontradas
- [ ] Mejoras futuras
- [ ] Conclusiones

#### 11.5. Repositorio GitHub
- [ ] Actualizar `.gitignore` para Android
- [ ] Commit final con mensaje descriptivo
- [ ] Tag de la versión: `v3.0-mobile`
- [ ] Push al repositorio privado
- [ ] Agregar colaboradores/profesores

---

## Distribución de Tareas (Sugerencia para 4 personas)

### Persona 1: Backend & Data Layer
- FASE 1: Capa de datos completa
- FASE 6: WebSocket y notificaciones
- Configuración inicial (Retrofit, repos, etc.)

### Persona 2: Autenticación & Navegación
- FASE 2: Autenticación completa
- FASE 8: Navegación global y UI
- Layouts base y temas

### Persona 3: Dispositivos & Habitaciones
- FASE 3: Dispositivos
- FASE 4: Habitaciones
- Componentes de control custom

### Persona 4: Rutinas & Requisitos No Funcionales
- FASE 5: Rutinas
- FASE 9: RNFs (i18n, adaptabilidad)
- FASE 10: Testing y pulido

**Coordinación**:
- FASE 0 hacerla juntos (1-2 horas)
- Reuniones diarias de 15 min para sincronizar
- Usar branches y PRs para evitar conflictos

---

## Cronograma Estimado

Asumiendo 15-20 horas/semana por persona:

| Semana | Fases | Entregables |
|--------|-------|-------------|
| 1 | 0, 1, 2 | Proyecto configurado, autenticación funcionando |
| 2 | 3, 4 | Dispositivos y habitaciones |
| 3 | 5, 6, 8 | Rutinas, notificaciones, navegación |
| 4 | 9, 10, 11 | RNFs, testing, informe, entrega |

---

## Checklist de Entrega

- [ ] APK funcional generado
- [ ] Repositorio GitHub actualizado
- [ ] Informe completo (max 30 páginas)
- [ ] Screenshots de todas las vistas
- [ ] Video demostración (opcional pero recomendado)
- [ ] README con instrucciones de compilación
- [ ] Código documentado
- [ ] Todos los requisitos funcionales obligatorios cumplidos
- [ ] Todos los requisitos no funcionales cumplidos

---

## Referencias

### Documentación
- [Android Developers](https://developer.android.com/)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Retrofit](https://square.github.io/retrofit/)
- [Material Design 3](https://m3.material.io/)

### Código de referencia
- Aplicación web: `homecore-web/`
- Design tokens: `design-tokens.md`
- Enunciado: `docs/tercera_entrega/enunciado_tercer_entrega.pdf`

---

## Notas Importantes

1. **No hardcodear valores**: Usar recursos (strings, colors, dimens)
2. **Async con Coroutines**: Nunca hacer llamadas de red en el main thread
3. **LiveData/StateFlow**: Para comunicación entre ViewModel y UI
4. **Repository pattern**: No llamar a la API directamente desde ViewModels
5. **Manejo de errores**: Try-catch en repos, mostrar mensajes amigables
6. **Permisos runtime**: Solicitar permisos necesarios (notificaciones, etc.)
7. **Nullability**: Kotlin null-safety correctamente
8. **Testing**: Aunque no es requerido, escribir algunos tests ayuda

