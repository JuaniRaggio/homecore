# Guía de Referencia Rápida - HomeCore Mobile

Guía rápida con snippets y patrones comunes para desarrollo.

## Índice
1. [Configuración de recursos](#1-configuración-de-recursos)
2. [Data classes](#2-data-classes)
3. [API con Retrofit](#3-api-con-retrofit)
4. [Repositorios](#4-repositorios)
5. [ViewModels](#5-viewmodels)
6. [Fragments](#6-fragments)
7. [Layouts XML](#7-layouts-xml)
8. [Navigation](#8-navigation)
9. [Internacionalización](#9-internacionalización)
10. [Patrones comunes](#10-patrones-comunes)

---

## 1. Configuración de Recursos

### colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Fondos -->
    <color name="bg_main">#0f0f14</color>
    <color name="bg_card">#1a1a24</color>
    <color name="bg_card_alt">#252532</color>

    <!-- Acentos -->
    <color name="accent">#818cf8</color>
    <color name="success">#34d399</color>
    <color name="danger">#f87171</color>
    <color name="warning">#fbbf24</color>

    <!-- Texto -->
    <color name="text_primary">#f1f5f9</color>
    <color name="text_secondary">#b0bdd0</color>
    <color name="text_muted">#8494a7</color>

    <!-- Dispositivos -->
    <color name="device_light">#f5a623</color>
    <color name="device_door">#6c8ebf</color>
    <color name="device_alarm">#e05252</color>
    <!-- ... resto según design-tokens.md -->
</resources>
```

### dimens.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Espaciado -->
    <dimen name="space_2xs">4dp</dimen>
    <dimen name="space_xs">6dp</dimen>
    <dimen name="space_sm">8dp</dimen>
    <dimen name="space_md">10dp</dimen>
    <dimen name="space_base">12dp</dimen>
    <dimen name="space_lg">14dp</dimen>
    <dimen name="space_xl">16dp</dimen>
    <dimen name="space_2xl">18dp</dimen>
    <dimen name="space_3xl">20dp</dimen>
    <dimen name="space_4xl">24dp</dimen>

    <!-- Border radius -->
    <dimen name="radius_xs">4dp</dimen>
    <dimen name="radius_sm">6dp</dimen>
    <dimen name="radius_md">8dp</dimen>
    <dimen name="radius_lg">10dp</dimen>
    <dimen name="radius_xl">12dp</dimen>

    <!-- Tipografía -->
    <dimen name="text_xs">11sp</dimen>
    <dimen name="text_sm">12sp</dimen>
    <dimen name="text_base">14sp</dimen>
    <dimen name="text_md">15sp</dimen>
    <dimen name="text_lg">16sp</dimen>
    <dimen name="text_xl">18sp</dimen>
    <dimen name="text_2xl">20sp</dimen>
</resources>
```

### strings.xml (inglés)
```xml
<resources>
    <string name="app_name">HomeCore</string>

    <!-- Auth -->
    <string name="login_title">Log in</string>
    <string name="email">Email</string>
    <string name="password">Password</string>
    <string name="login_button">Log in</string>
    <string name="register_link">Don\'t have an account? Sign up</string>

    <!-- Devices -->
    <string name="devices_title">Devices</string>
    <string name="device_light">Light</string>
    <string name="device_door">Door</string>
    <string name="status_on">On</string>
    <string name="status_off">Off</string>

    <!-- Actions -->
    <string name="action_turn_on">Turn on</string>
    <string name="action_turn_off">Turn off</string>
    <string name="action_open">Open</string>
    <string name="action_close">Close</string>

    <!-- Common -->
    <string name="loading">Loading…</string>
    <string name="error_generic">An error occurred. Please try again.</string>
    <string name="retry">Retry</string>
    <string name="save">Save</string>
    <string name="cancel">Cancel</string>
    <string name="delete">Delete</string>
    <string name="edit">Edit</string>
</resources>
```

### values-es/strings.xml (español)
```xml
<resources>
    <string name="app_name">HomeCore</string>

    <!-- Auth -->
    <string name="login_title">Iniciar sesión</string>
    <string name="email">Email</string>
    <string name="password">Contraseña</string>
    <string name="login_button">Iniciar sesión</string>
    <string name="register_link">¿No tienes cuenta? Regístrate</string>

    <!-- Devices -->
    <string name="devices_title">Dispositivos</string>
    <string name="device_light">Luz</string>
    <string name="device_door">Puerta</string>
    <string name="status_on">Encendido</string>
    <string name="status_off">Apagado</string>

    <!-- Actions -->
    <string name="action_turn_on">Encender</string>
    <string name="action_turn_off">Apagar</string>
    <string name="action_open">Abrir</string>
    <string name="action_close">Cerrar</string>

    <!-- Common -->
    <string name="loading">Cargando…</string>
    <string name="error_generic">Ocurrió un error. Intenta nuevamente.</string>
    <string name="retry">Reintentar</string>
    <string name="save">Guardar</string>
    <string name="cancel">Cancelar</string>
    <string name="delete">Eliminar</string>
    <string name="edit">Editar</string>
</resources>
```

---

## 2. Data Classes

### User.kt
```kotlin
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String
)
```

### Device.kt
```kotlin
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val state: DeviceState,
    val room: Room? = null,
    val meta: Map<String, Any>? = null
)

data class DeviceState(
    val status: String,
    val brightness: Int? = null,
    val temperature: Int? = null,
    val volume: Int? = null,
    val mode: String? = null
)

enum class DeviceType(val displayName: String, val colorRes: Int, val iconRes: Int) {
    LIGHT("Light", R.color.device_light, R.drawable.ic_lightbulb),
    DOOR("Door", R.color.device_door, R.drawable.ic_door),
    ALARM("Alarm", R.color.device_alarm, R.drawable.ic_alarm),
    WATER("Water", R.color.device_water, R.drawable.ic_water),
    CURTAIN("Curtain", R.color.device_curtain, R.drawable.ic_blinds),
    AC("AC", R.color.device_ac, R.drawable.ic_ac),
    SPEAKER("Speaker", R.color.device_speaker, R.drawable.ic_volume),
    VACUUM("Vacuum", R.color.device_vacuum, R.drawable.ic_vacuum),
    FRIDGE("Fridge", R.color.device_fridge, R.drawable.ic_kitchen),
    OVEN("Oven", R.color.device_oven, R.drawable.ic_oven),
    LOCK("Lock", R.color.device_lock, R.drawable.ic_lock);

    companion object {
        fun fromString(type: String): DeviceType {
            return values().find { it.name.equals(type, ignoreCase = true) } ?: LIGHT
        }
    }
}
```

### ApiResponse.kt
```kotlin
data class ApiResponse<T>(
    val result: T? = null,
    val error: String? = null
)

// Extension function para convertir a Result
fun <T> ApiResponse<T>.toResult(): Result<T> {
    return if (result != null) {
        Result.success(result)
    } else {
        Result.failure(Exception(error ?: "Unknown error"))
    }
}
```

---

## 3. API con Retrofit

### ApiClient.kt
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

    val authApi: AuthApi = retrofit.create()
    val devicesApi: DevicesApi = retrofit.create()
    val roomsApi: RoomsApi = retrofit.create()
    val routinesApi: RoutinesApi = retrofit.create()
}
```

### AuthInterceptor.kt
```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken() // implementar según storage

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
```

### DevicesApi.kt
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

---

## 4. Repositorios

### DevicesRepository.kt
```kotlin
class DevicesRepository(
    private val api: DevicesApi = ApiClient.devicesApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getDevices(): Result<List<Device>> = withContext(ioDispatcher) {
        try {
            api.getDevices().toResult()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDevice(id: String): Result<Device> = withContext(ioDispatcher) {
        try {
            api.getDevice(id).toResult()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeAction(
        deviceId: String,
        action: String,
        params: Map<String, Any>? = null
    ): Result<List<Device>> = withContext(ioDispatcher) {
        try {
            api.executeAction(deviceId, action, params).toResult()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 5. ViewModels

### DevicesViewModel.kt
```kotlin
class DevicesViewModel(
    private val repository: DevicesRepository = DevicesRepository()
) : ViewModel() {

    // Estado de UI
    private val _uiState = MutableLiveData<UiState<List<Device>>>()
    val uiState: LiveData<UiState<List<Device>>> = _uiState

    // Estado de acción
    private val _actionState = MutableLiveData<ActionState>()
    val actionState: LiveData<ActionState> = _actionState

    init {
        loadDevices()
    }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getDevices()
                .onSuccess { devices ->
                    _uiState.value = UiState.Success(devices)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun executeAction(deviceId: String, action: String, params: Map<String, Any>? = null) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading

            repository.executeAction(deviceId, action, params)
                .onSuccess { updatedDevices ->
                    _actionState.value = ActionState.Success
                    _uiState.value = UiState.Success(updatedDevices)
                }
                .onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Action failed")
                }
        }
    }
}

// Estados de UI
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// Estados de acciones
sealed class ActionState {
    object Idle : ActionState()
    object Loading : ActionState()
    object Success : ActionState()
    data class Error(val message: String) : ActionState()
}
```

---

## 6. Fragments

### DevicesListFragment.kt
```kotlin
class DevicesListFragment : Fragment() {
    private var _binding: FragmentDevicesListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DevicesViewModel by viewModels()
    private lateinit var adapter: DevicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevicesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = DevicesAdapter { device ->
            // Navigate to detail
            findNavController().navigate(
                DevicesListFragmentDirections.actionToDeviceDetail(device.id)
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DevicesListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadDevices()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.errorLayout.visibility = View.GONE

                    if (state.data.isEmpty()) {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.emptyLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.data)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.errorText.text = state.message
                }
                else -> {}
            }
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadDevices()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## 7. Layouts XML

### fragment_devices_list.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg_main">

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/swipeRefresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <!-- RecyclerView -->
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:padding="@dimen/space_base"
                android:clipToPadding="false" />

            <!-- Loading -->
            <ProgressBar
                android:id="@+id/progressBar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:visibility="gone" />

            <!-- Empty state -->
            <LinearLayout
                android:id="@+id/emptyLayout"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:orientation="vertical"
                android:visibility="gone"
                android:gravity="center">

                <ImageView
                    android:layout_width="64dp"
                    android:layout_height="64dp"
                    android:src="@drawable/ic_devices_empty"
                    android:tint="@color/text_muted" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="@dimen/space_xl"
                    android:text="@string/devices_empty"
                    android:textColor="@color/text_secondary"
                    android:textSize="@dimen/text_lg" />
            </LinearLayout>

            <!-- Error state -->
            <LinearLayout
                android:id="@+id/errorLayout"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:orientation="vertical"
                android:visibility="gone"
                android:gravity="center">

                <TextView
                    android:id="@+id/errorText"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/error_generic"
                    android:textColor="@color/danger"
                    android:textSize="@dimen/text_md" />

                <Button
                    android:id="@+id/retryButton"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="@dimen/space_xl"
                    android:text="@string/retry"
                    android:backgroundTint="@color/accent" />
            </LinearLayout>
        </FrameLayout>
    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### item_device.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/space_sm"
    app:cardBackgroundColor="@color/bg_card"
    app:cardCornerRadius="@dimen/radius_md"
    app:cardElevation="0dp"
    android:clickable="true"
    android:focusable="true">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="@dimen/space_xl">

        <!-- Icon -->
        <ImageView
            android:id="@+id/deviceIcon"
            android:layout_width="40dp"
            android:layout_height="40dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            tools:src="@drawable/ic_lightbulb"
            tools:tint="@color/device_light" />

        <!-- Name -->
        <TextView
            android:id="@+id/deviceName"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="@dimen/space_xl"
            android:layout_marginEnd="@dimen/space_xl"
            android:textColor="@color/text_primary"
            android:textSize="@dimen/text_lg"
            android:textStyle="bold"
            app:layout_constraintStart_toEndOf="@id/deviceIcon"
            app:layout_constraintEnd_toStartOf="@id/deviceStatus"
            app:layout_constraintTop_toTopOf="parent"
            tools:text="Living Room Light" />

        <!-- Type -->
        <TextView
            android:id="@+id/deviceType"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="@dimen/space_xl"
            android:layout_marginTop="@dimen/space_xs"
            android:textColor="@color/text_secondary"
            android:textSize="@dimen/text_sm"
            app:layout_constraintStart_toEndOf="@id/deviceIcon"
            app:layout_constraintTop_toBottomOf="@id/deviceName"
            app:layout_constraintEnd_toStartOf="@id/deviceStatus"
            tools:text="Light • Living Room" />

        <!-- Status -->
        <TextView
            android:id="@+id/deviceStatus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:paddingHorizontal="@dimen/space_md"
            android:paddingVertical="@dimen/space_xs"
            android:background="@drawable/bg_status"
            android:textColor="@color/text_on_accent"
            android:textSize="@dimen/text_sm"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            tools:text="On" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

---

## 8. Navigation

### nav_graph.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/devicesListFragment">

    <fragment
        android:id="@+id/devicesListFragment"
        android:name="com.itba.homecore.ui.devices.DevicesListFragment"
        android:label="@string/devices_title">

        <action
            android:id="@+id/action_to_device_detail"
            app:destination="@id/deviceDetailFragment"
            app:enterAnim="@anim/slide_in_right"
            app:exitAnim="@anim/slide_out_left"
            app:popEnterAnim="@anim/slide_in_left"
            app:popExitAnim="@anim/slide_out_right" />
    </fragment>

    <fragment
        android:id="@+id/deviceDetailFragment"
        android:name="com.itba.homecore.ui.devices.DeviceDetailFragment"
        android:label="{deviceName}">

        <argument
            android:name="deviceId"
            app:argType="string" />
    </fragment>
</navigation>
```

### Navegar con SafeArgs
```kotlin
// En DevicesListFragment
findNavController().navigate(
    DevicesListFragmentDirections.actionToDeviceDetail(deviceId = device.id)
)

// En DeviceDetailFragment
private val args: DeviceDetailFragmentArgs by navArgs()

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val deviceId = args.deviceId
    viewModel.loadDevice(deviceId)
}
```

---

## 9. Internacionalización

### Obtener string traducido en código
```kotlin
// En Fragment/Activity
val text = getString(R.string.devices_title)

// Con parámetros
val text = getString(R.string.device_count, count)

// En strings.xml:
// <string name="device_count">%d devices</string>
// <string name="device_count">%d dispositivos</string> (es)
```

### Plurales
```xml
<!-- values/strings.xml -->
<plurals name="device_count">
    <item quantity="one">%d device</item>
    <item quantity="other">%d devices</item>
</plurals>

<!-- values-es/strings.xml -->
<plurals name="device_count">
    <item quantity="one">%d dispositivo</item>
    <item quantity="other">%d dispositivos</item>
</plurals>
```

```kotlin
// En código
val text = resources.getQuantityString(R.plurals.device_count, count, count)
```

---

## 10. Patrones Comunes

### Mostrar Toast
```kotlin
Toast.makeText(context, R.string.success_message, Toast.LENGTH_SHORT).show()
```

### Mostrar Snackbar
```kotlin
Snackbar.make(binding.root, R.string.success_message, Snackbar.LENGTH_SHORT).show()

// Con acción
Snackbar.make(binding.root, R.string.device_deleted, Snackbar.LENGTH_LONG)
    .setAction(R.string.undo) {
        // Undo action
    }
    .show()
```

### Diálogo de confirmación
```kotlin
MaterialAlertDialogBuilder(requireContext())
    .setTitle(R.string.delete_device_title)
    .setMessage(R.string.delete_device_message)
    .setPositiveButton(R.string.delete) { _, _ ->
        viewModel.deleteDevice(deviceId)
    }
    .setNegativeButton(R.string.cancel, null)
    .show()
```

### Solicitar permisos (Android 13+)
```kotlin
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Permission granted
    } else {
        // Permission denied
    }
}

fun requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Already granted
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Show rationale
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
```

### Extension functions útiles
```kotlin
// View extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// String extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
```

---

## Comandos Útiles

### Gradle
```bash
# Build APK
./gradlew assembleDebug

# Instalar en dispositivo conectado
./gradlew installDebug

# Limpiar build
./gradlew clean

# Generar APK release
./gradlew assembleRelease
```

### ADB
```bash
# Listar dispositivos conectados
adb devices

# Instalar APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Ver logs
adb logcat

# Ver logs filtrados
adb logcat | grep "HomeCore"

# Limpiar logs
adb logcat -c

# Desinstalar app
adb uninstall com.itba.homecore
```

### Emulador
```bash
# Listar emuladores
emulator -list-avds

# Iniciar emulador
emulator -avd Pixel_8_API_36

# Iniciar en modo headless (sin ventana)
emulator -avd Pixel_8_API_36 -no-window
```

---

## Troubleshooting

### "Unresolved reference: R"
```bash
./gradlew clean
Build > Clean Project
Build > Rebuild Project
```

### "Invoke-customs are only supported starting with Android O (--min-api 26)"
En `build.gradle.kts`:
```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```

### "Failed to resolve: com.android.support:..."
Asegurarse de usar AndroidX, no support library. En `gradle.properties`:
```properties
android.useAndroidX=true
android.enableJetifier=true
```

### "Manifest merger failed"
Ver errores en `app/build/outputs/logs/manifest-merger-debug-report.txt`

