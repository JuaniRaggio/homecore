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

