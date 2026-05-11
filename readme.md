# Problemas Resueltos (Registro Historico)

## [RESUELTO - 2026] Deadlock en carga de dispositivos en Overview

### Sintoma
- Los dispositivos no se mostraban en la vista Overview (OverviewView.vue)
- Las tarjetas de casas mostraban "0 dispositivos", "0 activos", "0W consumo" aunque las casas tuvieran habitaciones y dispositivos configurados
- Las rutinas globales tampoco cargaban dispositivos para seleccionar
- Los logs mostraban que las habitaciones se agrupaban correctamente por casa, pero nunca aparecian logs de carga de dispositivos individuales

### Causa raiz
**Deadlock con `pLimit` en `useOverviewData.js`**

El codigo usaba un unico limite de concurrencia (`pLimit(MAX_CONCURRENT)` con valor 3) para controlar llamadas paralelas a la API en DOS niveles:

1. **Nivel externo:** Procesar casas en paralelo
2. **Nivel interno:** Procesar habitaciones de cada casa en paralelo

Ambos niveles usaban el MISMO `limit`, causando un deadlock:
- Las 3 slots del `limit` se ocupaban procesando casas
- Cada casa intentaba procesar sus habitaciones usando el mismo `limit`
- Como no habia slots disponibles (todas ocupadas esperando que las casas terminen), las habitaciones nunca se procesaban
- Las casas quedaban esperando que terminen las habitaciones, pero estas nunca empezaban
- **Resultado:** Deadlock - nada se ejecutaba, los dispositivos nunca se cargaban

### Solucion implementada
Crear dos limites de concurrencia separados en `useOverviewData.js`:

```javascript
const MAX_CONCURRENT_HOMES = 3  // Procesar 3 casas en paralelo
const MAX_CONCURRENT_ROOMS = 5  // Cada casa puede procesar 5 habitaciones en paralelo

const limitHomes = pLimit(MAX_CONCURRENT_HOMES)
const limitRooms = pLimit(MAX_CONCURRENT_ROOMS)
```

Esto permite que:
- Multiples casas se procesen simultaneamente sin bloquearse entre si
- Cada casa puede procesar sus habitaciones en paralelo sin interferir con el limite de casas
- No hay competencia por slots entre diferentes niveles de paralelizacion

### Archivos modificados
- `src/composables/useOverviewData.js`: Separacion de limites de concurrencia

### Leccion aprendida
Cuando se usa `pLimit` con multiples niveles de paralelizacion anidados (Promise.all dentro de Promise.all), SIEMPRE usar limites separados para cada nivel. De lo contrario, el nivel externo consume todos los slots y el nivel interno nunca se ejecuta.

---

## [RESUELTO - 2026] Estado de alarmas no persiste entre vistas

### Sintoma
- Al activar/desactivar una alarma en `DeviceDetailView`, el cambio se ve reflejado inmediatamente
- Al salir de la vista y volver a entrar, la alarma aparece en su estado anterior (desactivada)
- El mismo problema afectaba potencialmente a otros dispositivos con acciones de encendido/apagado

### Causa raiz
**Falta de sincronizacion entre estado local y store global**

Cuando se ejecutaba una accion sobre un dispositivo (ej: `armAway`, `disarm`, `togglePower`), el codigo solo actualizaba el estado local de `DeviceDetailView`:

```javascript
onSuccess() {
  device.value.isOn = true  // Solo actualiza la vista actual
}
```

El problema era que el store de dispositivos (`useDevicesStore`) NO se actualizaba, entonces:

1. Si el dispositivo estaba en el store (cargado previamente en otra vista), el store mantenia el estado viejo
2. Al salir y volver a la vista, `onMounted` recargaba el dispositivo, y si venia desde el store o cache, mostraba el estado desactualizado
3. Otros componentes que mostraban el mismo dispositivo (ej: Overview) tampoco veian el cambio

### Solucion implementada
Actualizar tambien el store global despues de cada accion exitosa usando el metodo `applyDeviceEvent` que ya existia para eventos de websocket:

```javascript
onSuccess() {
  device.value.isOn = true  // Actualiza vista local
  // Actualiza el store tambien para que persista entre vistas
  devicesStore.applyDeviceEvent({
    id: device.value.id,
    data: { status: 'on' }
  })
}
```

Esto garantiza que:
- El estado se persiste en el store global
- Otras vistas que muestren el mismo dispositivo ven el cambio
- Al volver a la vista, el estado se mantiene correcto (si el dispositivo esta en el store)

### Archivos modificados
- `src/views/DeviceDetailView.vue`: Actualizacion del store en `handleArmAway`, `handleArmHome`, `handleDisarm`, y `togglePower`

### Nota importante
Esta solucion asume que:
1. La API esta persistiendo correctamente el cambio de estado en el backend
2. El dispositivo ya esta cargado en el store (si no lo esta, `applyDeviceEvent` no tiene efecto)

Si el problema persiste despues de este fix, significa que **la API no esta guardando el estado** y hay que investigar el backend.

### Leccion aprendida
Cuando se usa un store global (Pinia, Vuex, etc) para gestionar estado de entidades, SIEMPRE actualizar el store despues de mutaciones locales exitosas. No basta con actualizar el estado local de la vista - otros componentes y navegacion entre vistas dependen del store como fuente de verdad.

---

## [RESUELTO - 2026] Desalineacion masiva de acciones entre frontend y backend (404 Not Found)

### Problema general
Al migrar de acciones de toggle genericas a acciones especificas por tipo de dispositivo, varios dispositivos comenzaron a fallar con error 404 "action not found". El problema afecto a:
- **Alarmas**: Frontend usaba `armHome`, backend tenia `armStay`
- **Cortinas**: Frontend usaba `open`/`close`, backend tenia `up`/`down`

Este fue un problema sistemico causado por asumir nombres de acciones sin verificar contra los deviceTypes reales del backend.

---

## [RESUELTO - 2026] Acciones de alarma no existen en la API (404 Not Found)

### Sintoma
- Al intentar activar/desactivar una alarma, aparecia un error 404
- En consola: `Error: action with name 'armHome' for device with id 'xxx' not found`
- Las acciones `armAway`, `armHome`, `disarm` fallaban con 404
- Otros dispositivos (luces, puertas, etc.) funcionaban correctamente

### Causa raiz
**Desalineacion entre nombres de acciones en frontend y backend**

El frontend esperaba que las alarmas tuvieran estas acciones:
- `armAway` (Activar modo regular)
- **`armHome`** (Activar modo casa) <- **Problema**
- `disarm` (Desactivar)

Pero el `deviceType` de alarma en el backend tenia:
- `armAway` ✓
- **`armStay`** <- **Nombre diferente**
- `disarm` ✓

La API devolvia 404 porque el frontend intentaba ejecutar `armHome`, pero esa accion no existia en el `deviceType` - se llamaba `armStay`.

### Solucion implementada
Actualizar el frontend para usar `armStay` en vez de `armHome`:

```javascript
// Antes
await cmd.execute(device.value.id, 'armHome', { ... })

// Despues
await cmd.execute(device.value.id, 'armStay', { ... })
```

### Archivos modificados
- `src/views/DeviceDetailView.vue`: Cambio de `armHome` a `armStay` en handler
- `src/config/routine-actions.js`: Actualizacion de nombre de accion para rutinas

### Leccion aprendida
Al crear `deviceTypes` personalizados en el backend con acciones especificas, SIEMPRE verificar que los nombres de las acciones coincidan exactamente entre frontend y backend. La API devuelve 404 si la accion no existe en el `deviceType`, aunque el dispositivo este configurado correctamente.

**Como verificar acciones disponibles:**
1. Obtener el `typeId` del dispositivo: `GET /devices/{id}`
2. Consultar las acciones del tipo: `GET /devicetypes/{typeId}`
3. Verificar que `actions[].name` coincida con lo que el frontend intenta ejecutar

---

## [RESUELTO - 2026] Acciones de cortinas no existen en la API (404 Not Found)

### Sintoma
- Al intentar abrir/cerrar cortinas, aparecia error 404
- En consola: `Error: action with name 'open' for device with id 'xxx' not found`
- Las acciones `open` y `close` fallaban con 404

### Causa raiz
**Mismo problema que alarmas: nombres de acciones no coinciden**

El frontend esperaba:
- `open` (Abrir)
- `close` (Cerrar)
- `setLevel` (Posicion) ✓

El backend tenia:
- **`up`** (Subir) <- Nombre diferente
- **`down`** (Bajar) <- Nombre diferente
- `setLevel` ✓

### Solucion implementada
Actualizar el frontend para usar `up`/`down` en vez de `open`/`close`:

```javascript
// device-types.js - STATUS_MAP
curtain: {
  on: 'Abierta', off: 'Cerrada',
  actionOn: 'up',    // antes: 'open'
  actionOff: 'down', // antes: 'close'
  verbOn: 'subir',   verbOff: 'bajar'
}

// routine-actions.js - ACTIONS_MAP
curtain: [
  { actionName: 'up',   label: 'Subir', params: [] },  // antes: 'open'
  { actionName: 'down', label: 'Bajar', params: [] },  // antes: 'close'
  { actionName: 'setLevel', label: 'Posicion', params: [...] },
]
```

### Archivos modificados
- `src/config/device-types.js`: Cambio de acciones en STATUS_MAP
- `src/config/routine-actions.js`: Cambio de acciones para rutinas

---

## Leccion aprendida general: Verificacion de acciones

**Problema raiz comun:** El frontend asumia nombres de acciones estandar sin verificar contra los deviceTypes reales del backend.

**Proceso correcto para agregar soporte a un nuevo tipo de dispositivo:**

1. **Backend primero:**
   ```bash
   POST /devicetypes
   {
     "name": "tipo",
     "actions": [
       { "name": "accionReal", "params": [...] }
     ]
   }
   ```

2. **Verificar acciones disponibles:**
   ```bash
   GET /devicetypes/{typeId}
   # Anotar los nombres EXACTOS de las acciones
   ```

3. **Frontend - actualizar configuraciones:**
   - `device-types.js` - `STATUS_MAP` con acciones correctas
   - `routine-actions.js` - `ACTIONS_MAP` con acciones correctas

4. **Frontend - actualizar interpretacion de estados:**
   - Si el deviceType usa estados personalizados (ej: `armedStay`, no solo `on`/`off`)
   - Agregar esos estados a:
     - `device-helpers.js` - `normalizeDevice()`
     - `DeviceDetailView.vue` - `loadDeviceState()`
     - `stores/devices.js` - `applyDeviceEvent()`

5. **Probar:**
   ```javascript
   // En consola del navegador, despues de ejecutar accion:
   fetch(`/devices/${id}/state`, {...})
     .then(r => r.json())
     .then(s => console.log('Estado:', s.status))
   // Verificar que el estado se guardo correctamente
   ```

**Checklist de archivos a revisar al agregar un tipo de dispositivo:**
- [ ] `device-types.js` - STATUS_MAP con acciones correctas
- [ ] `routine-actions.js` - ACTIONS_MAP con acciones correctas
- [ ] `device-helpers.js` - Estados personalizados en normalizeDevice
- [ ] `DeviceDetailView.vue` - Estados personalizados en loadDeviceState
- [ ] `stores/devices.js` - Estados personalizados en applyDeviceEvent

---

## [RESUELTO - 2026] Estados de alarma no reconocidos al recargar

### Sintoma (Parte 2 del problema de alarmas)
- Al activar una alarma, funcionaba correctamente y se veia activada
- Al salir de la vista y volver, la alarma aparecia desactivada
- La API SI estaba guardando el estado (`armedStay`, `armedAway`)
- Pero el frontend no lo reconocia al recargar

### Causa raiz
**Estados especificos de alarma no incluidos en la logica de interpretacion**

El frontend tenia tres lugares donde interpretaba `state.status` para determinar si un dispositivo esta "ON":

1. `normalizeDevice()` en `device-helpers.js`
2. `loadDeviceState()` en `DeviceDetailView.vue`
3. `applyDeviceEvent()` en `stores/devices.js`

Todos usaban la misma condicion:
```javascript
isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing'
```

Pero las alarmas usan estados especificos:
- `'armedStay'` (activada modo casa)
- `'armedAway'` (activada modo regular)
- `'disarmed'` (desactivada)

Como `'armedStay'` y `'armedAway'` no estaban en la lista, el frontend los interpretaba como "OFF", aunque la API los guardaba correctamente.

### Solucion implementada
Agregar los estados de alarma a las tres condiciones:

```javascript
isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing'
    || state.status === 'armedStay' || state.status === 'armedAway'  // <- Agregado
```

Y enviar los estados correctos al store:
- `armAway` - `{ status: 'armedAway' }`
- `armStay` - `{ status: 'armedStay' }`
- `disarm` - `{ status: 'disarmed' }`

### Archivos modificados
- `src/utils/device-helpers.js`: Estado de alarma en `normalizeDevice()`
- `src/views/DeviceDetailView.vue`: Estado de alarma en `loadDeviceState()` y callbacks de acciones
- `src/stores/devices.js`: Estado de alarma en `applyDeviceEvent()`

### Leccion aprendida
Cuando se usan tipos de dispositivos con estados especificos (no genericos como `'on'`/`'off'`), TODOS los lugares donde se interpreta `state.status` deben incluir esos estados. Buscar en el codigo:
- Funciones de normalizacion
- Funciones de carga de estado
- Actualizaciones del store desde eventos
- Callbacks de acciones exitosas

---

## [MEJORA UX - 2026] Experiencia de usuario mejorada para control de cortinas

### Mejoras implementadas

**1. Representación visual intuitiva**
- Reemplazo del badge horizontal con texto por una ventana vertical que muestra físicamente la posición de la cortina
- La ventana tiene un overlay que baja desde arriba, representando la cortina
- Cuando level = 0%, el overlay cubre el 100% (totalmente cerrada)
- Cuando level = 100%, el overlay cubre el 0% (totalmente abierta)
- Código de colores progresivo:
  - Rojo (#ef4444) - Cerrada (0%)
  - Rojo claro (#ff6b6b) - 20%
  - Naranja (#f97316) - 40%
  - Amarillo (#eab308) - 60%
  - Verde (#22c55e) - 80-100%

**2. Controles incrementales**
- Los botones suben/bajan la cortina de 20 en 20 puntos (no 0-100 directamente)
- Permite ajuste gradual y preciso de la posición
- Los niveles posibles son: 0%, 20%, 40%, 60%, 80%, 100%
- Textos de estado alineados con los steps: "Cerrada", "20% abierta", "40% abierta", etc.

**3. Feedback visual sin notificaciones molestas**
- Eliminación de toasts en cada click (redundante con feedback visual)
- Notificaciones solo en estados importantes:
  - Toast cuando alcanza 100%: "Cortina totalmente abierta"
  - Toast cuando alcanza 0%: "Cortina totalmente cerrada"
- El feedback principal es visual: el dibujo se actualiza, el porcentaje cambia, y el texto de estado se actualiza
- Reduce la fatiga de notificaciones cuando el usuario está ajustando la posición

**4. Consistencia y mantenibilidad**
- Centralización de lógica en funciones helper exportadas:
  - `getCurtainLevelText(level)` - Calcula el texto descriptivo
  - `getCurtainColor(level)` - Calcula el color correspondiente
- Constantes de colores con nombres descriptivos (CURTAIN_COLORS)
- Eliminación de duplicación de código en 4 lugares diferentes
- Fuente única de verdad para rangos y textos

**5. Dimensiones optimizadas por contexto**
- **DeviceCard** (vista general): Ventana 50x40px (horizontal, compacta)
- **CurtainControls** (vista detalle): Ventana 80x120px (vertical, clara)
- Botones de preset adicionales en vista de detalle: 0%, 20%, 40%, 60%, 80%, 100%

### Beneficios de UX

1. **Feedback inmediato y claro**: El usuario ve exactamente dónde está la cortina sin necesidad de interpretar un porcentaje numérico
2. **Menos ruido visual**: Notificaciones solo en momentos importantes
3. **Control preciso**: Steps de 20% permiten ajuste granular
4. **Diseño intuitivo**: La metáfora de la ventana es inmediatamente comprensible

### Archivos modificados
- `src/utils/device-helpers.js`: Funciones helper y constantes de colores
- `src/stores/devices.js`: Uso de funciones centralizadas
- `src/components/devices/DeviceCard.vue`: Representación visual compacta
- `src/components/devices/CurtainControls.vue`: Representación visual detallada
- `src/composables/useDeviceActions.js`: Lógica de incrementos de 20 y notificaciones selectivas

### Principios de diseño aplicados

1. **Progressive disclosure**: Información visual inmediata, detalles en hover/interacción
2. **Feedback inmediato**: Cambios visuales instantáneos sin latencia perceptible
3. **Mapeo natural**: La representación visual corresponde directamente al estado físico
4. **Reducción de carga cognitiva**: No requiere interpretar números o textos, es visual
5. **Notificaciones mínimas**: Solo alertar en momentos críticos

---

# Problemas de vista para solucionar

## Responsiveness - DONE (igual seguir probando)

Cuando achico la vista, los breadcrumbs colisionan con la parte de 
notificaciones y el perfil del usuario, directamente se superponen, 
no es que se comporta de forma correcta y responsive. **Es importantisimo 
que solucionemos este problema ya que tiene que ser responsive la pagina 
y tiene que funcionar en multiples tamaños de pantalla**


# Problemas de requisitos funcionales

_Work in progress by Maru_
- [X] Registrar cuenta no funciona realmente ya que no verificamos como 
      corresponde asique se puede considerar que esta mal

> [!NOTE]
> Esto incluye los siguientes requisitos funcionales:
> - Registrar cuenta
> - Verificar cuenta
> - Recuperar Contraseña
> - Cambiar contraseña

- [X] Iniciar sesion

- [X] Cerrar sesion

- [X] Gestionar Dispositivos

- [X] Consultar dispositivos

- [X] Controlar dispositivos - *solcuionado*

> [!NOTE]
> Actualmente _Juani_ esta trabajando en esto para solucionar los problemas
> que surgieron al agregar controles para poder modificar tmp, intensidad,
> etc. a los distintos tipos de dispositivos

- [X] Gestionar rutinas

- [X] Consultar rutinas

- [X] Ejecutar rutinas

- [X] Consultar acciones realizadas - Historial

- [X] Gestionar habitaciones

- [X] Consultar hogares

- [X] Vincular dispositivos a habitaciones

> [!NOTE]
> Esto va de la mano con el problema de controlar dispositivos, se esta
> trabajando en ello.
> Esta hecho pero nosotros no soportamos tener dispositivos "libres" en
> el sentido de que esten desvinculados de una habitacion, esto no es un
> "error" de implementacion sino que es lo que nosotros consideramos que
> tiene sentido

# Requisitos funcionales por la catedra

1. RF1 Registrar cuenta: Check RegisterView.vue - does it have a registration form? Does it call an API to register?
2. RF2 Verificar cuenta: Check VerifyView.vue - does it have a verification code input? Does it call an API?
3. RF3 Recuperar contrasena: Check RecoverView.vue - does it have password recovery flow? (email input, code, new password)
4. RF4 Cambiar contrasena: Search for password change functionality - is there a form/modal in SettingsView or elsewhere?
5. RF5 Iniciar sesion: Check LoginView.vue
6. RF6 Cerrar sesion: Search for logout button/functionality in the UI - check sidebar, topbar, settings, any component that renders a logout button. Check if there's a logout route or button anywhere in templates.
7. RF7 Gestionar dispositivos: Check if you can create, edit, delete devices (CreateDeviceModal, EditDeviceView, delete functionality)
8. RF8 Consultar dispositivos: Check DevicesView.vue, DeviceDetailView.vue
9. RF9 Controlar dispositivos: Check device control components (AlarmControls, SpeakerControls, VacuumControls, etc.)
10. RF10 Gestionar rutinas: Check if you can create, edit, delete routines (NewRoutineView, delete in RoutinesView)
11. RF11 Consultar rutinas: Check RoutinesView.vue, RoutineDetailModal
12. RF12 Ejecutar rutinas: Check if there's an execute button/action for routines
13. RF13 Consultar acciones realizadas: Check HistoryView.vue
14. RF14 Gestionar habitaciones: Check if you can create, edit, delete rooms
15. RF15 Consultar habitaciones: Check RoomsView.vue, RoomDetailView.vue
16. RF16 Vincular dispositivos a habitaciones: Check RoomDetailView for linking functionality
17. RF17 Gestionar hogares: Check if you can create, edit, delete homes
18. RF18 Consultar hogares: Check OverviewView or similar
19. RF19 Vincular habitaciones a hogares: Check if rooms are linked to homes when created
20. RF20 Enviar notificaciones: Check notifications store/component
21. RF22 Consultar consumo electrico: Check ConsumptionView.vue

