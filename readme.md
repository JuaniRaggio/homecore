# HomeCore - Sistema de Gestión de Casas Inteligentes

HomeCore es una aplicación web moderna para la gestión y control de dispositivos domésticos inteligentes. Permite a los usuarios controlar luces, puertas, alarmas, electrodomésticos y más desde una interfaz unificada e intuitiva.

## Características Principales

- **Gestión de Dispositivos**: Control de 11 tipos de dispositivos (luces, puertas, alarmas, cortinas, aire acondicionado, parlantes, aspiradoras, heladeras, hornos, canillas y cerraduras)
- **Rutinas Automatizadas**: Creación y ejecución de rutinas programadas con múltiples acciones
- **Multi-hogar**: Soporte para múltiples hogares con compartición entre usuarios
- **Notificaciones en Tiempo Real**: Actualizaciones instantáneas mediante WebSocket
- **Visualización de Consumo**: Gráficos de consumo eléctrico por dispositivo
- **Historial de Acciones**: Registro completo de todas las acciones ejecutadas
- **Estados Agregados**: Visualización del estado de seguridad combinado (alarmas)
- **Interfaz Responsive**: Diseño adaptado para diferentes resoluciones

## Tecnologías

### Frontend
- **Vue.js 3.5** - Framework progresivo con Composition API
- **Vue Router 4.5** - Enrutamiento SPA
- **Pinia 3.0** - Gestión de estado global
- **Vite 8.0** - Build tool y dev server
- **Chart.js 4.5** - Visualización de datos
- **Socket.IO Client 4.8** - Comunicación en tiempo real

### Backend (API Provista)
- API REST para gestión de dispositivos, hogares, habitaciones y rutinas
- WebSocket para notificaciones en tiempo real
- Autenticación JWT

## Requisitos Previos

- **Node.js**: versión 18 o superior
- **npm**: versión 9 o superior (incluido con Node.js)
- Acceso a la API del backend (configurado en variables de entorno)

## Instalación

1. Clonar el repositorio:
```bash
git clone git@github.com:JuaniRaggio/homecore.git
cd homecore/homecore-web
```

2. Instalar dependencias:
```bash
npm install
```

3. Configurar variables de entorno:
```bash
cp .env.example .env
```

Editar `.env` con la configuración de la API:
```env
VITE_API_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080
```

## Ejecución

### Modo Desarrollo

Inicia el servidor de desarrollo con hot-reload:
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

### Build de Producción

Genera la versión optimizada para producción:
```bash
npm run build
```

Los archivos compilados se generan en el directorio `dist/`

### Preview de Producción

Previsualizar el build de producción localmente:
```bash
npm run preview
```

## Estructura del Proyecto

```
homecore-web/
├── public/              # Archivos estáticos
├── src/
│   ├── assets/         # Recursos (estilos, imágenes)
│   │   └── styles/     # CSS global modular
│   ├── components/     # Componentes Vue reutilizables
│   │   ├── common/     # Componentes comunes (botones, modals)
│   │   └── devices/    # Componentes específicos de dispositivos
│   ├── composables/    # Lógica reutilizable (hooks)
│   ├── config/         # Configuración (tipos de dispositivos, acciones)
│   ├── router/         # Configuración de rutas
│   ├── services/       # Servicios (API, WebSocket)
│   ├── stores/         # Stores de Pinia (estado global)
│   ├── utils/          # Funciones utilitarias
│   ├── views/          # Vistas principales (páginas)
│   ├── App.vue         # Componente raíz
│   └── main.js         # Punto de entrada
├── .env                # Variables de entorno (no versionado)
├── .env.example        # Plantilla de variables de entorno
├── index.html          # HTML principal
├── package.json        # Dependencias y scripts
└── vite.config.js      # Configuración de Vite
```

## Scripts Disponibles

- `npm run dev` - Inicia el servidor de desarrollo
- `npm run build` - Genera el build de producción
- `npm run preview` - Previsualiza el build de producción

## Convenciones de Código

### CSS
- **Variables CSS**: Definidas en `src/assets/styles/variables.css`
- **Estilos Globales**: Archivos modulares por categoría (buttons, forms, controls, etc.)
- **Estilos Scoped**: Específicos de cada componente Vue

### Vue
- **Composition API**: Preferida sobre Options API
- **Nomenclatura**: PascalCase para componentes, camelCase para composables
- **Organización**: Un componente por archivo, estructura clara de `<template>`, `<script setup>`, `<style>`

### Estado
- **Pinia Stores**: Para estado global compartido
- **Reactive/Ref**: Para estado local de componentes
- **Composables**: Para lógica reutilizable entre componentes

## Características Implementadas

### Autenticación
- Registro de cuenta con verificación por email
- Inicio y cierre de sesión
- Recuperación y cambio de contraseña
- Manejo automático de expiración de tokens JWT

### Gestión de Hogares
- Crear, editar y eliminar hogares
- Compartir hogares con otros usuarios
- Vista general con métricas (dispositivos activos, consumo)

### Gestión de Habitaciones
- Crear, renombrar y eliminar habitaciones
- Vincular habitaciones a hogares
- Vista de detalle con dispositivos asociados

### Gestión de Dispositivos
- Crear, editar y eliminar dispositivos
- 11 tipos de dispositivos con controles específicos
- Favoritos para acceso rápido
- Historial de acciones por dispositivo
- Estados combinados (alarmas)

### Rutinas
- Crear rutinas con múltiples acciones
- Programar ejecución por días y hora
- Ejecutar manualmente
- Editar y eliminar rutinas
- Rutinas globales y específicas por hogar

### Consumo Eléctrico
- Gráficos de consumo por dispositivo
- Visualización histórica
- Comparación entre dispositivos

### Notificaciones
- Toast temporales (confirmación de acciones)
- Notificaciones persistentes (eventos del sistema)
- Confirmaciones para acciones destructivas

## Documentación Adicional

- **[TROUBLESHOOTING.md](./TROUBLESHOOTING.md)**: Registro histórico de problemas resueltos y soluciones técnicas
- **[docs/segunda_entrega/informe.typ](./docs/segunda_entrega/informe.typ)**: Informe completo de la implementación

## Equipo de Desarrollo

Grupo 15 - HCI (Interacción Humano-Computadora)
- Matias Bernasconi (64188)
- Juan Ignacio Garcia Vautrin Raggio (63319)
- Victoria Helena Park (64498)
- Maria Del Pilar Resek (65528)

## Licencia

Apache License 2.0
