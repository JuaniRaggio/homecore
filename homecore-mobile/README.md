# HomeCore — Mobile (Android)

Android client for HomeCore, a smart-home management system. It mirrors the web
app's product (devices, rooms, routines, consumption) using a native Jetpack
Compose UI. Part of the [HomeCore monorepo](../readme.md); shared visual
decisions live in [`design-tokens.md`](../design-tokens.md).

## Tech stack

| Concern        | Library / tool                | Version    |
|----------------|-------------------------------|------------|
| Language       | Kotlin                        | 2.0.20     |
| Build          | Android Gradle Plugin (AGP)   | 8.7.3      |
| UI             | Jetpack Compose (BOM)         | 2024.12.01 |
| Design         | Material 3                    | —          |
| Navigation     | Navigation Compose            | 2.8.5      |
| Architecture   | MVVM + StateFlow              | —          |
| Lifecycle      | lifecycle-viewmodel-compose   | 2.8.7      |
| Networking     | Retrofit / OkHttp / Gson      | 2.11.0 / 4.12.0 / 2.11.0 |
| Concurrency    | kotlinx-coroutines-android    | 1.8.1      |
| Persistence    | DataStore Preferences         | 1.1.1      |

## Requirements

- **Android 10 (API 29)** or higher. `minSdk = 29`, `targetSdk = 35`,
  `compileSdk = 35`.
- **Android Studio** (recent stable, compatible with AGP 8.7.3 / Kotlin 2.0.20).
- **JDK 11** (the project compiles against Java/Kotlin target 11).

No local backend is required: the app uses the remote course API at
`https://hci.it.itba.edu.ar/api/`.

## Build & run

Open the `homecore-mobile/` project in Android Studio and run it on an emulator
or a connected device, or build from the command line:

```bash
./gradlew assembleDebug
```

The debug APK is generated at:

```
app/build/outputs/apk/debug/app-debug.apk
```

> A full end-user installation guide (sideloading the APK on a physical device)
> exists separately in the project deliverables; this README stays focused on
> developers.

## Mock vs. real backend switch

The app decides between prototype (in-memory) data and the real HCI API in a
**single place**: `com.itba.homecore.di.AppModule`.

```kotlin
object AppModule {
    const val USE_MOCK = false   // true = prototype data, false = real HCI API
    // ...
}
```

`AppModule` is a lightweight service locator. Each repository is an **interface**
with two implementations — `Mock*Repository` (local data) and
`Remote*Repository` (Retrofit against the API). Flipping `USE_MOCK` swaps every
data source without touching the UI or the ViewModels, since they depend on the
interfaces, not the concrete classes.

## Architecture overview

Strict **MVVM by layers**: the Compose UI only observes state and emits events;
ViewModels expose a `StateFlow` of a sealed `UiState` (`Loading` / `Success` /
`Error`) and call repository interfaces; repositories encapsulate the data
source (mock or network).

```
app/src/main/java/com/itba/homecore/
├── data/
│   ├── api/          # Retrofit service, ApiClient, session events (401 handling)
│   ├── model/        # Data models (Device, Room, Routine, ...)
│   ├── mock/         # MockData — in-memory prototype data
│   ├── local/        # DataStore-backed session storage
│   └── repository/   # Repository interfaces + Mock*/Remote* implementations
├── di/               # AppModule (service locator + USE_MOCK switch)
├── navigation/       # Navigation Compose graph
├── viewmodel/        # ViewModels (StateFlow, viewModelScope)
└── ui/
    ├── screens/      # Feature screens (auth, devices, rooms, routines, homes, main)
    ├── components/   # Reusable composables (slots: HouseHeader, PanelCard, ...)
    └── theme/        # Material 3 dark color scheme + typography
```

For the rationale behind these decisions (mock-first strategy, ViewModel
injection, state modeling, the 401/"Invalid token" recovery, build tweaks for
AGP 8.x, theming, i18n), see
[`docs/DECISIONES_ARQUITECTURA.md`](./docs/DECISIONES_ARQUITECTURA.md).
