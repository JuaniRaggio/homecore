# HomeCore

HomeCore is a smart-home management system that lets users control and monitor
home devices (lights, doors, alarms, appliances, and more) from a unified,
accessible interface. It is an academic project built for the
**Human-Computer Interaction (HCI)** course at **ITBA**, with a strong focus on
usability, accessibility, and consistent design across platforms.

The system is a **monorepo** containing two client applications — a web app and
an Android app — that talk to a **shared remote API** provided by the course.
Both clients implement the same product on different platforms, sharing a common
design system.

## Monorepo structure

```
HomeCore/
├── homecore-web/        # Web client (Vue 3 + Vite). See homecore-web/README.md
├── homecore-mobile/     # Android client (Kotlin + Jetpack Compose). See homecore-mobile/README.md
├── docs/                # Deliverable reports (primer / segunda / tercera entrega)
├── assets/              # Shared resources
├── design-tokens.md     # Shared design system (colors, spacing, radius, typography)
├── LICENSE              # Apache License 2.0
└── readme.md            # This file
```

## Shared backend API

Both clients consume the same remote API hosted by the course; no local backend
needs to be run.

- **Base URL:** `https://hci.it.itba.edu.ar/api/`
- **Auth:** JWT bearer token plus an API key sent per request.
- **Real-time:** WebSocket notifications for device/routine updates.

## Design system

The single source of truth for visual decisions lives in
[`design-tokens.md`](./design-tokens.md). It defines the color palette
(dark theme by default), per-device-type colors, spacing scale, border radii,
and typography. Both the web and mobile apps derive their styles from these
tokens so the product looks and behaves consistently across platforms.

## Tech stack at a glance

| Area        | Web (`homecore-web`)            | Mobile (`homecore-mobile`)              |
|-------------|--------------------------------|-----------------------------------------|
| Language    | JavaScript                     | Kotlin 2.0.20                           |
| UI          | Vue 3.5 (Composition API)      | Jetpack Compose + Material 3            |
| Build tool  | Vite 8                         | Gradle (AGP 8.7.3)                      |
| State       | Pinia 3                        | MVVM + StateFlow                        |
| Routing/Nav | Vue Router 5                   | Navigation Compose                      |
| Networking  | fetch                          | Retrofit + OkHttp + Gson                |
| Real-time   | Socket.IO client 4.8           | (HTTP polling / repositories)           |
| Charts      | Chart.js 4.5                   | —                                       |
| Persistence | localStorage                   | DataStore Preferences                   |

## Per-app documentation

- **Web app:** [`homecore-web/README.md`](./homecore-web/README.md) — setup,
  environment variables, dev/build commands, and `src/` structure.
- **Android app:** [`homecore-mobile/README.md`](./homecore-mobile/README.md) —
  requirements, build/run, the mock-vs-real backend switch, and architecture.

## About

University project for the **HCI** course at **ITBA** (Group 15):

- Matias Bernasconi (64188)
- Juan Ignacio Garcia Vautrin Raggio (63319)
- Victoria Helena Park (64498)
- Maria Del Pilar Resek (65528)

## License

Apache License 2.0 — see [`LICENSE`](./LICENSE).
