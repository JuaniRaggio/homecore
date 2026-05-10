# Problemas Resueltos (Registro Historico)

## [RESUELTO - 2025] Deadlock en carga de dispositivos en Overview

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

## [RESUELTO - 2025] Estado de alarmas no persiste entre vistas

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

## [RESUELTO - 2025] Acciones de alarma no existen en la API (404 Not Found)

### Sintoma
- Al intentar activar/desactivar una alarma, aparecia un error 404
- En consola: `Error: action with name 'armHome' for device with id 'xxx' not found`
- Las acciones `armAway`, `armHome`, `disarm` fallaban con 404
- Otros dispositivos (luces, puertas, etc.) funcionaban correctamente

### Causa raiz
**Desalineacion entre nombres de acciones en frontend y backend**

El frontend esperaba que las alarmas tuvieran estas acciones:
- `armAway` (Activar modo regular)
- **`armHome`** (Activar modo casa) ← **Problema**
- `disarm` (Desactivar)

Pero el `deviceType` de alarma en el backend tenia:
- `armAway` ✓
- **`armStay`** ← **Nombre diferente**
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

# Problemas de vista para solucionar

## Responsiveness

Cuando achico la vista, los breadcrumbs colisionan con la parte de 
notificaciones y el perfil del usuario, directamente se superponen, 
no es que se comporta de forma correcta y responsive. **Es importantisimo 
que solucionemos este problema ya que tiene que ser responsive la pagina 
y tiene que funcionar en multiples tamaños de pantalla**


# Problemas de requisitos funcionales

_Work in progress by Maru_
- [~] Registrar cuenta no funciona realmente ya que no verificamos como 
      corresponde asique se puede considerar que esta mal

> [!NOTE]
> Esto incluye los siguientes requisitos funcionales:
> - Registrar cuenta
> - Verificar cuenta
> - Recuperar Contraseña
> - Cambiar contraseña


- [X] Iniciar sesion

- [ ] Cerrar sesion

- [X] Gestionar Dispositivos

- [X] Consultar dispositivos

- [X] Controlar dispositivos - *solcuionado*

> [!NOTE]
> Actualmente _Juani_ esta trabajando en esto para solucionar los problemas
> que surgieron al agregar controles para poder modificar tmp, intensidad,
> etc. a los distintos tipos de dispositivos

- [ ] Gestionar rutinas - _Pendiente a testear_

- [ ] Consultar rutinas - _Pendiente a testear_

- [ ] Ejecutar rutinas - _Pendiente a testear_

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

