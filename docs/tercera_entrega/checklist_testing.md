# Checklist de Testing Manual - HomeCore Mobile

Lista completa de pruebas a realizar antes de la entrega.

## Configuración de Pruebas

### Dispositivos/Emuladores a probar
- [ ] Emulador Pixel 6 - API 29 (Android 10) - Mínimo requerido
- [ ] Emulador Pixel 8 - API 36 (Android 16) - Más reciente
- [ ] Tablet - Pixel Tablet - API 36
- [ ] Dispositivo físico (si disponible)

### Orientaciones
- [ ] Vertical (Portrait)
- [ ] Horizontal (Landscape)

### Idiomas
- [ ] Español
- [ ] Inglés

---

## 1. Autenticación

### RF1: Registro
- [ ] Abrir app sin cuenta
- [ ] Navegar a pantalla de registro
- [ ] Intentar registrar con email inválido → debe mostrar error
- [ ] Intentar registrar con contraseñas que no coinciden → debe mostrar error
- [ ] Intentar registrar con campos vacíos → debe mostrar error
- [ ] Registrar con datos válidos → debe navegar a pantalla de verificación
- [ ] Verificar que se muestra mensaje de éxito/confirmación
- [ ] Verificar que el loading state se muestra durante la petición

**Casos especiales**:
- [ ] Registrar con email ya existente → debe mostrar error del servidor

### RF2: Verificación
- [ ] Después de registro, debe mostrar pantalla de verificación
- [ ] Campo de código debe aceptar el formato correcto
- [ ] Botón "Verificar" debe estar deshabilitado si campo vacío
- [ ] Ingresar código incorrecto → debe mostrar error
- [ ] Ingresar código correcto → debe verificar y navegar a home
- [ ] Botón "Reenviar código" debe funcionar (con cooldown)

### RF3: Inicio de sesión
- [ ] Abrir app con cuenta ya verificada
- [ ] Debe mostrar pantalla de login
- [ ] Intentar login con email vacío → debe mostrar error
- [ ] Intentar login con contraseña vacía → debe mostrar error
- [ ] Intentar login con credenciales incorrectas → debe mostrar error del servidor
- [ ] Login exitoso → debe navegar a pantalla principal (dispositivos)
- [ ] Token debe guardarse correctamente
- [ ] Al cerrar y reabrir app → debe seguir logueado (sesión persistente)

### RF4: Cierre de sesión
- [ ] Navegar a menú/configuración
- [ ] Click en "Cerrar sesión"
- [ ] Debe mostrar confirmación
- [ ] Confirmar → debe cerrar sesión y navegar a login
- [ ] Token debe borrarse
- [ ] Al reabrir app → debe pedir login nuevamente

---

## 2. Dispositivos

### RF5: Consultar dispositivos
- [ ] Iniciar sesión
- [ ] Pantalla principal debe mostrar lista de dispositivos
- [ ] Debe mostrar:
  - Nombre del dispositivo
  - Tipo de dispositivo (con icono correcto)
  - Estado actual (encendido/apagado/etc.)
  - Color según tipo (de design tokens)
- [ ] Pull-to-refresh debe actualizar la lista
- [ ] Si no hay dispositivos → debe mostrar empty state
- [ ] Loading state mientras carga
- [ ] Si falla la carga → debe mostrar mensaje de error y botón "Reintentar"

**Agrupación**:
- [ ] Si hay habitaciones, dispositivos deben estar agrupados por habitación
- [ ] Dispositivos sin habitación deben estar en sección "Sin asignar"

**Tipos a verificar** (si hay en el backend):
- [ ] Light (luz) → icono lightbulb, color `#f5a623`
- [ ] Door (puerta) → icono door_front, color `#6c8ebf`
- [ ] Alarm (alarma) → icono alarm, color `#e05252`
- [ ] Water (canilla) → icono water_drop, color `#4fc3f7`
- [ ] Curtain (cortina) → icono blinds, color `#81c784`
- [ ] AC (aire) → icono ac_unit, color `#ba68c8`
- [ ] Speaker (parlante) → icono volume_up, color `#ff8a65`
- [ ] Vacuum (aspiradora) → icono clean_hands, color `#90a4ae`
- [ ] Fridge (heladera) → icono kitchen, color `#4dd0e1`
- [ ] Oven (horno) → icono oven_gen, color `#ff7043`
- [ ] Lock (cerradura) → icono lock, color `#2196f3`

### RF6: Consultar detalle de dispositivo
- [ ] Click en un dispositivo de la lista
- [ ] Debe navegar a pantalla de detalle
- [ ] Debe mostrar:
  - Nombre completo
  - Tipo
  - Estado actual
  - Habitación (si está asignado)
  - Controles según tipo
- [ ] Botón de retroceso debe volver a la lista

### RF7: Controlar dispositivo
Probar para cada tipo de dispositivo disponible:

**Dispositivos on/off** (light, ac, speaker):
- [ ] Toggle switch debe reflejar estado actual
- [ ] Click en toggle → debe cambiar estado
- [ ] Debe mostrar loading durante la acción
- [ ] Estado debe actualizarse en UI al completar
- [ ] Si falla → debe mostrar error y revertir estado

**Dispositivos con acciones específicas**:

**Door (puerta)**:
- [ ] Botón "Abrir" debe abrir la puerta
- [ ] Botón "Cerrar" debe cerrar la puerta
- [ ] Estados: "Abierta" / "Cerrada"

**Alarm (alarma)**:
- [ ] Botón "Activar" debe activar alarma
- [ ] Botón "Desactivar" debe desactivar alarma
- [ ] Estados: "Activada" / "Desactivada"

**Curtain (cortina)**:
- [ ] Botón "Subir" debe subir cortina
- [ ] Botón "Bajar" debe bajar cortina
- [ ] Estados: "Abierta" / "Cerrada"

**Vacuum (aspiradora)**:
- [ ] Botón "Iniciar" debe iniciar limpieza
- [ ] Botón "Detener" debe detener/enviar a dock
- [ ] Estados: "Activa" / "Inactiva"

**Dispositivos con slider** (brightness, volume, temperature):
- [ ] Slider debe mostrar valor actual
- [ ] Mover slider debe actualizar valor
- [ ] Valor debe enviarse al backend
- [ ] Verificar rango mínimo y máximo

**Speaker**:
- [ ] Play/Stop
- [ ] Volumen (slider)
- [ ] Estados: "Reproduciendo" / "Detenido"

**Fridge/Oven**:
- [ ] Control de temperatura (slider)
- [ ] Freezer temperature (fridge)

---

## 3. Rutinas

### RF8: Consultar rutinas
- [ ] Navegar a sección "Rutinas" desde menú/bottom nav
- [ ] Debe mostrar lista de rutinas
- [ ] Cada rutina debe mostrar:
  - Nombre
  - Cantidad de acciones
  - Programación (si tiene)
- [ ] Botón "Ejecutar" visible en cada item
- [ ] Pull-to-refresh funciona
- [ ] Empty state si no hay rutinas
- [ ] Loading state

### RF9: Consultar detalle de rutina
- [ ] Click en una rutina
- [ ] Debe navegar a pantalla de detalle
- [ ] Debe mostrar:
  - Nombre de la rutina
  - Lista de acciones (dispositivo + acción + parámetros)
  - Días de ejecución programada (si aplica)
  - Hora de ejecución (si aplica)
- [ ] Botón grande "Ejecutar rutina"

### RF10: Ejecutar rutina
- [ ] Click en botón "Ejecutar" desde lista o detalle
- [ ] Debe mostrar loading/progress
- [ ] Al completar:
  - Mensaje de éxito "Rutina ejecutada correctamente"
  - O mensaje de error si falla
- [ ] Verificar que las acciones se ejecutaron (revisar estados de dispositivos)

---

## 4. Habitaciones

### RF11: Consultar habitaciones
- [ ] Navegar a sección "Habitaciones"
- [ ] Debe mostrar lista de habitaciones
- [ ] Cada habitación debe mostrar:
  - Nombre
  - Cantidad de dispositivos
  - Hogar asociado (si aplica)
- [ ] FAB "Agregar habitación" visible
- [ ] Click en habitación → navegar a detalle
- [ ] Empty state si no hay habitaciones

### RF12: Gestionar habitaciones

**Crear**:
- [ ] Click en FAB "+"
- [ ] Debe mostrar formulario de creación
- [ ] Campo nombre requerido
- [ ] Dropdown para seleccionar hogar (opcional)
- [ ] Botón "Guardar" → crear y volver a lista
- [ ] Nueva habitación debe aparecer en la lista

**Editar**:
- [ ] Desde detalle de habitación, botón "Editar"
- [ ] Formulario con datos actuales pre-cargados
- [ ] Modificar nombre
- [ ] Guardar → actualizar y volver a detalle

**Eliminar**:
- [ ] Desde detalle de habitación, botón "Eliminar"
- [ ] Debe mostrar diálogo de confirmación
- [ ] Confirmar → eliminar y volver a lista
- [ ] Habitación desaparece de la lista

### RF13: Vincular/desvincular dispositivos

**Vincular**:
- [ ] Desde detalle de habitación, botón "Agregar dispositivo"
- [ ] Debe mostrar lista de dispositivos disponibles (sin habitación)
- [ ] Seleccionar dispositivo(s)
- [ ] Botón "Vincular"
- [ ] Dispositivo(s) debe(n) aparecer en la habitación

**Desvincular**:
- [ ] Desde lista de dispositivos de la habitación
- [ ] Swipe o menú contextual → "Desvincular"
- [ ] Confirmación
- [ ] Dispositivo desaparece de la habitación

---

## 5. Hogares (Opcional - RF14-RF16)

Si implementaron gestión de hogares:

### RF14: Consultar hogares
- [ ] Lista de hogares
- [ ] Click en hogar → detalle

### RF15: Gestionar hogares
- [ ] Crear hogar
- [ ] Editar hogar
- [ ] Eliminar hogar

### RF16: Vincular habitaciones a hogares
- [ ] Vincular habitación a hogar
- [ ] Desvincular habitación de hogar

---

## 6. Notificaciones

### RF17-RF18: Notificaciones

**Push notifications**:
- [ ] Solicitud de permiso de notificaciones (Android 13+)
- [ ] Recibir notificación cuando app está en background
- [ ] Click en notificación → abrir app en pantalla relevante
- [ ] Notificación debe tener título, mensaje e icono

**Notificaciones en tiempo real (WebSocket)**:
- [ ] Conectar WebSocket al iniciar sesión
- [ ] Cuando otro cliente cambia estado de dispositivo → actualizar en tiempo real
- [ ] Banner/snackbar mostrando el cambio
- [ ] Lista de dispositivos se actualiza automáticamente
- [ ] Desconectar WebSocket al cerrar sesión

**Probar**:
- [ ] Abrir app en dispositivo/emulador 1
- [ ] Abrir app web en navegador
- [ ] Cambiar estado de dispositivo en web → debe reflejarse en mobile en tiempo real
- [ ] Viceversa: cambiar en mobile → debe verse en web

---

## 7. Requisitos No Funcionales

### RNF1: Internacionalización

**Español**:
- [ ] Cambiar idioma del dispositivo a español
- [ ] Abrir app
- [ ] Verificar que TODOS los textos están en español:
  - Títulos de pantallas
  - Labels de formularios
  - Botones
  - Mensajes de error
  - Estados de dispositivos
  - Nombres de acciones
  - Menús
  - Diálogos de confirmación
  - Empty states

**Inglés**:
- [ ] Cambiar idioma del dispositivo a inglés
- [ ] Abrir app
- [ ] Verificar traducción completa
- [ ] No debe haber textos hardcodeados en español

**Cambio dinámico** (si implementaron):
- [ ] Cambiar idioma desde Settings
- [ ] App debe actualizarse sin reiniciar

### RNF2: App Bar contextual

Verificar en cada pantalla:
- [ ] Lista de dispositivos: "Dispositivos" / "Devices"
- [ ] Detalle de dispositivo: Nombre del dispositivo
- [ ] Lista de habitaciones: "Habitaciones" / "Rooms"
- [ ] Detalle de habitación: Nombre de la habitación
- [ ] Lista de rutinas: "Rutinas" / "Routines"
- [ ] Detalle de rutina: Nombre de la rutina
- [ ] Login: "Iniciar sesión" / "Log in"
- [ ] Registro: "Registro" / "Register"

**Funcionalidad**:
- [ ] Botón de retroceso (back arrow) presente donde corresponda
- [ ] Menú overflow (3 puntos) donde haya acciones adicionales
- [ ] Search/filter icons donde aplique

### RNF3: Personalización (tema oscuro)

- [ ] App usa tema oscuro por defecto
- [ ] Verificar paleta de colores de `design-tokens.md`:
  - Fondo principal: `#0f0f14`
  - Cards: `#1a1a24`
  - Acento: `#818cf8`
  - Textos: `#f1f5f9`
- [ ] Contraste adecuado en todos los textos
- [ ] Botones y controles visibles

**Opcional**:
- [ ] Si implementaron tema claro, cambiar desde Settings
- [ ] Verificar que tema claro también se ve bien

### RNF4: Adaptabilidad a tipo de dispositivo

**Teléfono (< 600dp)**:
- [ ] Layouts con lista en pantalla completa
- [ ] Bottom Navigation (si usaron)
- [ ] Pantallas de detalle en pantalla completa
- [ ] Texto legible
- [ ] Botones accesibles

**Tablet (>= 600dp)**:
- [ ] Layouts adaptados (más espacio utilizado)
- [ ] Master-detail pattern (lista + detalle en mismo screen)
- [ ] Navigation Rail (si implementaron)
- [ ] Más columnas en grids donde aplique
- [ ] Márgenes laterales amplios

**Probar en**:
- [ ] Pixel 4 (5.7" - 1080x2280)
- [ ] Pixel 8 (6.2" - 1080x2400)
- [ ] Pixel Tablet (10.95" - 2560x1600)

### RNF5: Adaptabilidad a orientación

Para CADA pantalla principal:
- [ ] Login
- [ ] Registro
- [ ] Lista de dispositivos
- [ ] Detalle de dispositivo
- [ ] Lista de habitaciones
- [ ] Detalle de habitación
- [ ] Lista de rutinas
- [ ] Detalle de rutina

**Vertical**:
- [ ] Layout optimizado para vertical
- [ ] Todo el contenido visible (scroll si es necesario)
- [ ] Campos de formulario stack verticalmente

**Horizontal**:
- [ ] Layout adaptado (puede ser diferente)
- [ ] Aprovecha espacio horizontal
- [ ] Campos de formulario pueden estar en columnas
- [ ] Botones accesibles

**Rotación**:
- [ ] Rotar de vertical a horizontal → estado se preserva
- [ ] Campos de texto mantienen contenido
- [ ] Loading states no se reinician
- [ ] Scroll position se mantiene (aproximadamente)

### RNF6: Compatibilidad Android 10+

- [ ] Probar en emulador API 29 (Android 10)
- [ ] Probar en emulador API 34 (Android 14)
- [ ] Probar en emulador API 36 (Android 16)
- [ ] No debe haber crashes por APIs no disponibles
- [ ] Permisos de runtime solicitados correctamente
- [ ] Comportamiento consistente en todas las versiones

---

## 8. Casos Especiales y Edge Cases

### Sin conexión a internet
- [ ] Desactivar WiFi y datos móviles
- [ ] Abrir app (ya logueado)
- [ ] Intentar refrescar lista → debe mostrar error de red
- [ ] Botón "Reintentar" presente
- [ ] Reactivar conexión → "Reintentar" debe funcionar

### Errores del servidor
- [ ] Apagar servidor backend
- [ ] Intentar operación → debe mostrar error amigable (no stack trace)
- [ ] Mensaje del tipo "No se pudo conectar al servidor. Verifica tu conexión."

### Token expirado
- [ ] Login
- [ ] Esperar expiración del token (o forzarlo)
- [ ] Intentar operación → debe detectar token expirado y redirigir a login

### Campos vacíos en formularios
- [ ] Intentar guardar formulario con campos vacíos → debe mostrar error
- [ ] Campos requeridos deben estar marcados

### Concurrencia
- [ ] Ejecutar varias acciones rápidamente (ej: toggle de 3 dispositivos seguidos)
- [ ] No debe haber race conditions
- [ ] Estados deben actualizarse correctamente

### Estados loading
Verificar que TODAS las operaciones asíncronas muestran loading:
- [ ] Login
- [ ] Registro
- [ ] Cargar dispositivos
- [ ] Cargar detalle de dispositivo
- [ ] Controlar dispositivo
- [ ] Crear/editar/eliminar habitación
- [ ] Ejecutar rutina

### Empty states
Verificar mensajes cuando no hay datos:
- [ ] Sin dispositivos: "No hay dispositivos. Agrega uno desde la aplicación web."
- [ ] Sin habitaciones: "No hay habitaciones. Crea una tocando el botón +"
- [ ] Sin rutinas: "No hay rutinas. Crea una desde la aplicación web."

---

## 9. Accesibilidad (Opcional pero recomendado)

- [ ] Tamaños de fuente respetar configuración del sistema
- [ ] Content descriptions en imágenes e iconos
- [ ] Contraste de colores adecuado (WCAG AA)
- [ ] Botones de al menos 48dp de tamaño táctil
- [ ] Navegación con TalkBack funciona

---

## 10. Performance

- [ ] App inicia en menos de 3 segundos
- [ ] Transiciones entre pantallas suaves (60fps)
- [ ] Scroll suave en listas largas
- [ ] No hay memory leaks (verificar en Android Studio Profiler)
- [ ] Tamaño del APK razonable (< 20MB)

---

## Checklist Final Pre-Entrega

- [ ] Todos los requisitos funcionales obligatorios funcionan
- [ ] Todos los requisitos no funcionales cumplidos
- [ ] App no crashea en ningún flujo normal
- [ ] Tested en múltiples dispositivos/tamaños
- [ ] Tested en ambos idiomas
- [ ] Tested en ambas orientaciones
- [ ] APK generado y probado en dispositivo físico
- [ ] Screenshots tomadas
- [ ] Video de demostración grabado (opcional)

---

## Bugs Encontrados

Durante el testing, documentar bugs en esta sección:

| # | Pantalla | Descripción | Severidad | Estado |
|---|----------|-------------|-----------|--------|
| 1 | | | | |
| 2 | | | | |

**Severidades**:
- **Crítico**: App crashea o funcionalidad principal no funciona
- **Alto**: Funcionalidad importante no funciona como esperado
- **Medio**: Bug molesto pero con workaround
- **Bajo**: Cosmético o muy infrecuente

---
**Fecha de testeo:** __/__

