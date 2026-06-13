# HomeCore — Web

Web client for HomeCore, a smart-home management system. Single-page application
that lets users control devices, manage rooms/homes, run routines, and view
electricity consumption. It is part of the
[HomeCore monorepo](../readme.md); shared design decisions live in
[`design-tokens.md`](../design-tokens.md).

## Tech stack

| Concern      | Library                  | Version  |
|--------------|--------------------------|----------|
| Framework    | Vue (Composition API)    | ^3.5.34  |
| Router       | Vue Router               | ^5.0.6   |
| State        | Pinia                    | ^3.0.4   |
| Build / dev  | Vite                     | ^8.0.10  |
| Vite plugin  | @vitejs/plugin-vue       | ^6.0.0   |
| Charts       | Chart.js / vue-chartjs   | ^4.5.1 / ^5.3.3 |
| Real-time    | socket.io-client         | ^4.8.3   |
| Concurrency  | p-limit                  | ^7.3.0   |
| HTTP         | native `fetch`           | —        |

## Prerequisites

- **Node.js 18+** (Vite 8 requires a modern Node LTS).
- **npm 9+** (bundled with Node.js).
- Access to the remote course API (configured via environment variables).

## Install

```bash
npm install
```

## Environment setup

The API client reads configuration from Vite env variables (see
`src/services/api/client.js`). Create a `.env.local` file in this directory:

```env
VITE_API_BASE_URL=https://hci.it.itba.edu.ar/api
VITE_API_KEY=your_api_key_here
VITE_WS_URL=https://hci.it.itba.edu.ar
```

| Variable             | Description                                                         |
|----------------------|---------------------------------------------------------------------|
| `VITE_API_BASE_URL`  | Base URL of the shared HCI API. Requests are built from this.       |
| `VITE_API_KEY`       | API key sent as the `X-API-Key` header on every request.            |
| `VITE_WS_URL`        | WebSocket endpoint for real-time notifications.                     |

The JWT auth token is stored in `localStorage` (`auth_token`) after login and
attached as `Authorization: Bearer <token>`. On a `401` response the client
clears the token and redirects to `/login`.

## Commands

| Command           | Description                                          |
|-------------------|------------------------------------------------------|
| `npm run dev`     | Start the Vite dev server with hot reload.           |
| `npm run build`   | Build the production bundle into `dist/`.            |
| `npm run preview` | Serve the production build locally for preview.      |

The dev server runs on `http://localhost:5173` by default.

## Project structure

```
src/
├── assets/         # Global styles and static resources
├── components/     # Reusable Vue components
│   ├── common/     # Generic UI (buttons, modals, inputs)
│   └── devices/    # Device-specific controls
├── composables/    # Reusable Composition API logic (hooks)
├── config/         # Device types, action definitions
├── router/         # Vue Router configuration
├── services/       # Service layer
│   └── api/        # HTTP client and API calls (client.js)
├── stores/         # Pinia stores (global state)
├── utils/          # Utility helpers
├── views/          # Top-level pages
├── App.vue         # Root component
└── main.js         # Application entry point
```

## Conventions

- Components fetch data through composables/Pinia stores, never via direct
  `fetch` calls in the template layer.
- Use `<script setup>` with the Composition API.
- Do not hardcode colors; use the CSS variables derived from the shared design
  tokens.
