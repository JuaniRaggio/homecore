// ============================================================
// HomeCore -- TP Gestión de Casas Inteligentes (Segunda Entrega)
// ============================================================

#set document(
  title: "HomeCore - TP 2 Implementación Web",
  author: "Grupo 15",
)

#set page(
  paper: "a4",
  margin: (top: 2.5cm, bottom: 2.5cm, left: 2cm, right: 2cm),
  numbering: "1",
  number-align: bottom + right,
  header: context {
    if counter(page).get().first() > 1 [
      #set text(size: 9pt, fill: gray)
      #grid(
        columns: (1fr, 1fr, 1fr),
        align: (left, center, right),
        [HomeCore], [Segunda Entrega -- Grupo 15], [#datetime.today().display("[day]/[month]/[year]")],
      )
      #line(length: 100%, stroke: 0.5pt + gray)
    ]
  },
  footer: context [
    #set text(size: 9pt, fill: gray)
    #line(length: 100%, stroke: 0.5pt + gray)
    #v(0.2em)
    #align(center)[
      Página #counter(page).display() de #counter(page).final().first()
    ]
  ],
)

#set text(
  font: "New Computer Modern",
  size: 11pt,
  lang: "es",
  hyphenate: true,
)

#set par(
  justify: true,
  leading: 0.65em,
  first-line-indent: 0em,
  spacing: 1.2em,
)

#set heading(numbering: "1.1")
#show heading.where(level: 1): set text(size: 16pt, weight: "bold")
#show heading.where(level: 2): set text(size: 14pt, weight: "bold")
#show heading.where(level: 3): set text(size: 12pt, weight: "bold")

#show heading: it => {
  v(0.5em)
  it
  v(0.3em)
}

#set list(indent: 1em, marker: ("--", "-", "."))
#set enum(indent: 1em, numbering: "1.a.")

#show link: underline

// ====================================
// PORTADA
// ====================================

#page(numbering: none, header: none, footer: none)[
  #v(3fr)
  #align(center)[
    #text(size: 32pt, weight: "bold")[HomeCore - HCI]
    #v(0.5em)
    #text(size: 18pt)[Gestión de Casas Inteligentes]
    #v(0.3em)
    #text(size: 14pt, fill: gray)[Segunda Entrega -- Grupo 15]
    #v(2em)
    #line(length: 60%, stroke: 1pt + gray)
    #v(2em)
    #text(size: 12pt)[
      *Integrantes*
    ]
    #v(0.5em)
    #table(
      columns: (auto, auto),
      align: (left, center),
      stroke: 0.5pt,
      inset: 10pt,
      fill: (x, y) => if y == 0 { gray.lighten(80%) },
      table.header([*Nombre*], [*Legajo*]),
      [Matias Bernasconi], [64188],
      [Juan Ignacio Garcia Vautrin Raggio], [63319],
      [Victoria Helena Park], [64498],
      [Maria Del Pilar Resek], [65528],
    )
    #v(2em)
    #text(size: 11pt, fill: gray)[
      #datetime.today().display("[day]/[month]/[year]")
    ]
  ]
  #v(3fr)
]

// ====================================
// ÍNDICE
// ====================================

#page(numbering: none, header: none, footer: none)[
  #outline(
    title: [Índice],
    indent: 1.5em,
    depth: 3,
  )
]

#counter(page).update(1)

// ====================================
// 1. INTRODUCCIÓN
// ====================================

= Introducción

Esta segunda entrega consiste en la implementación funcional del sitio web de HomeCore, la aplicación de gestión de casas inteligentes diseñada en la primera entrega. El objetivo fue trasladar los prototipos de alta fidelidad a una aplicación web real, conectada a la API provista por la cátedra, manteniendo las decisiones de diseño y usabilidad establecidas previamente.

La implementación se realizó con Vue.js 3 (Composition API), Pinia para el manejo de estado, Vue Router para la navegación, y Socket.io para notificaciones en tiempo real. Se priorizó la fidelidad al prototipo original, adaptando únicamente lo necesario para resolver limitaciones técnicas o mejorar la experiencia a partir del feedback recibido en la primera entrega.

// ====================================
// 2. STACK TECNOLÓGICO
// ====================================

= Stack tecnológico

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Tecnología*], [*Justificación*]),
  [Vue.js 3 + Composition API], [Requerido por la cátedra (RNF1). Se eligió Composition API sobre Options API por su mayor composabilidad y reutilización de lógica entre componentes.],
  [Pinia], [Store manager oficial de Vue 3. Reemplaza a Vuex con una API más simple y tipado nativo.],
  [Vue Router], [Manejo de rutas con lazy loading. Permite navegación SPA sin recargas.],
  [Vite], [Bundler con HMR instantáneo, mejorando la velocidad de desarrollo.],
  [Socket.io Client], [Conexión en tiempo real para recibir eventos del backend y generar notificaciones.],
  [Chart.js + vue-chartjs], [Gráficos de consumo energético (donut y barras) con renderizado reactivo.],
  [Font Awesome], [Iconografía consistente en toda la aplicación.],
)

// ====================================
// 3. REQUISITOS FUNCIONALES
// ====================================

= Requisitos funcionales implementados

== Autenticación (RF1--RF6)

Se implementaron los flujos completos de registro, verificación por email, recuperación de contraseña, cambio de contraseña, login y logout.

-- *Registro (RF1):* Formulario con nombre, email y contraseña con validación client-side. Al registrarse, se redirige a la pantalla de verificación.
-- *Verificación de email (RF2):* Vista dedicada donde el usuario ingresa el código de 4 dígitos recibido por email. Se implementó reenvío de código.
-- *Recuperación de contraseña (RF3):* Flujo de 3 pasos: ingreso de email, código de verificación y nueva contraseña.
-- *Cambio de contraseña (RF4):* Disponible en la vista de Configuración. Requiere contraseña actual y confirmación de la nueva.
-- *Login/Logout (RF5, RF6):* Login con email y contraseña, persistencia de sesión con token en localStorage. Logout desconecta el WebSocket y limpia el estado.

// TODO: Agregar capturas de login, registro, verificación y recuperación
// #figure(image("assets/auth-login.png", width: 80%), caption: [Pantalla de login])

== Gestión de dispositivos (RF7--RF9)

-- *CRUD de dispositivos (RF7):* Se pueden agregar dispositivos desde la vista de Dispositivos, seleccionando tipo y nombre. La edición se realiza en una vista dedicada (`EditDeviceView`) donde se puede modificar nombre, tipo y habitación asignada. La eliminación requiere confirmación mediante modal.
-- *Consulta de dispositivos (RF8):* La vista de Dispositivos muestra todos los dispositivos del hogar en formato de tarjetas (`DeviceCard`) con estado on/off, tipo e indicador de favorito. Se puede filtrar por tipo o habitación.
-- *Control de dispositivos (RF9):* Cada tipo de dispositivo tiene controles específicos en su vista de detalle: sliders para luces (brillo, color), botones para puertas (abrir/cerrar/bloquear), sliders para cortinas (apertura), controles de temperatura para AC, etc.

// TODO: Agregar capturas de dispositivos, detalle, edición
// #figure(image("assets/devices-list.png", width: 100%), caption: [Vista de dispositivos])

== Rutinas (RF10--RF12)

-- *Gestión de rutinas (RF10):* Creación mediante un wizard de 4 pasos: nombre, selección de dispositivos, configuración de acciones por dispositivo y resumen. La edición y eliminación se realizan desde la vista de rutinas.
-- *Consulta de rutinas (RF11):* Vista con tarjetas que muestran nombre, cantidad de acciones y estado (activa/inactiva).
-- *Ejecución de rutinas (RF12):* Botón de ejecución directa en cada tarjeta de rutina. Al ejecutar, se aplican todas las acciones configuradas sobre los dispositivos.

// TODO: Agregar capturas de rutinas, wizard
// #figure(image("assets/routines.png", width: 100%), caption: [Vista de rutinas])

== Historial (RF13)

Vista de historial con tabla que muestra todas las acciones ejecutadas sobre los dispositivos. Se puede filtrar por tipo de acción y por dispositivo. Los registros se obtienen de la API y se muestran ordenados cronológicamente con paginación.

// TODO: Agregar captura de historial
// #figure(image("assets/history.png", width: 100%), caption: [Vista de historial])

== Habitaciones (RF14--RF16)

-- *Gestión de habitaciones (RF14):* Creación y edición mediante modales. Eliminación con confirmación que advierte que los dispositivos vinculados también serán eliminados.
-- *Consulta de habitaciones (RF15):* Vista en grilla de tarjetas, cada una mostrando el nombre de la habitación y los dispositivos vinculados con sus controles de toggle.
-- *Vinculación de dispositivos (RF16):* Cada tarjeta de habitación incluye un selector para vincular dispositivos disponibles (sin habitación asignada). La desvinculación se realiza con un botón por dispositivo, con confirmación.

// TODO: Agregar capturas de habitaciones
// #figure(image("assets/rooms.png", width: 100%), caption: [Vista de habitaciones])

== Hogares (RF17--RF19)

-- *Gestión de hogares (RF17):* Creación de propiedades con nombre y dirección desde una vista dedicada.
-- *Consulta de hogares (RF18):* Vista Overview que muestra un resumen del hogar seleccionado con dispositivos favoritos, consumo total y dispositivos activos.
-- *Vinculación de habitaciones (RF19):* Las habitaciones se crean dentro del contexto de un hogar específico. El selector de hogar en el sidebar permite navegar entre propiedades.

// TODO: Agregar capturas de overview y nueva propiedad
// #figure(image("assets/overview.png", width: 100%), caption: [Vista Overview])

== Notificaciones -- opcional (RF20)

Se implementó un sistema de notificaciones en tiempo real mediante Socket.io. El backend emite eventos cuando se producen cambios en dispositivos o en la configuración del hogar. El frontend recibe estos eventos, los almacena en un store dedicado y los muestra en un dropdown accesible desde la campana de notificaciones en la barra superior.

Los eventos manejados son:

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Evento*], [*Descripción*]),
  [`deviceCreated`], [Un dispositivo fue agregado al hogar.],
  [`deviceUpdated`], [Un dispositivo fue modificado (nombre, tipo, habitación).],
  [`deviceDeleted`], [Un dispositivo fue eliminado.],
  [`deviceEvent`], [Un dispositivo cambió de estado (encendido/apagado, brillo, etc.). Se aplica el cambio en el store local sin refetch.],
  [`homeShared`], [Se compartió un hogar con el usuario actual.],
  [`homeUnshared`], [Se revocó el acceso a un hogar.],
)

Cada notificación muestra título, mensaje descriptivo y fecha. Se pueden marcar como leídas de forma individual o masiva. El badge en la campana muestra la cantidad de no leídas.

*Decisión de diseño:* Se separó el sistema de notificaciones persistentes (dropdown) del sistema de toasts efímeros (feedback de acciones). Los toasts confirman acciones del usuario ("Dispositivo guardado"), mientras que las notificaciones informan sobre eventos externos. Esta separación evita que el usuario confunda feedback propio con información del sistema (Nielsen \#1: visibilidad del estado).

// TODO: Agregar captura de notificaciones
// #figure(image("assets/notifications.png", width: 60%), caption: [Dropdown de notificaciones])

== Consumo energético -- opcional (RF22)

Vista dedicada que muestra el consumo actual del hogar con tres métricas resumidas (consumo en tiempo real, proyección diaria, dispositivos activos), dos gráficos (donut por tipo de dispositivo y barras por dispositivo individual) y una tabla de detalle ordenada por consumo.

Los colores de los gráficos se asignan por tipo de dispositivo de forma consistente, con una paleta de fallback para tipos no previstos.

// TODO: Agregar captura de consumo
// #figure(image("assets/consumption.png", width: 100%), caption: [Vista de consumo energético])

// ====================================
// 4. REQUISITOS NO FUNCIONALES
// ====================================

= Requisitos no funcionales

#table(
  columns: (auto, 1fr, auto),
  align: (center, left, center),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*RNF*], [*Descripción*], [*Estado*]),
  [1], [Uso de Vue.js y su ecosistema (Pinia, Vue Router, Vite).], [Cumplido],
  [2], [Compatible con Chromium 140+ y Firefox 140+.], [Cumplido],
  [3], [Resolución de 1280px a 1920px de ancho.], [Cumplido],
  [4], [Separación de estructura y presentación (HTML/CSS). Sin estilos inline en el HTML.], [Cumplido],
  [5], [Separación de estructura y comportamiento (HTML/JS). Sin handlers inline.], [Cumplido],
)

*Nota sobre RNF4:* Se utilizó un sistema de CSS en capas: variables globales (design tokens), archivos de estilos compartidos por categoría (botones, formularios, tarjetas, tablas, controles) y estilos scoped en cada componente Vue para reglas específicas de la vista. Esta decisión se detalla en la sección de decisiones de diseño.

// ====================================
// 5. CAPTURAS DE PANTALLA
// ====================================

= Capturas de pantalla

// En esta sección se comparan las pantallas del prototipo de alta fidelidad
// (primera entrega) con la implementación final.

// TODO: Para cada vista, agregar:
// 1. Captura del prototipo (de la primera entrega)
// 2. Captura de la implementación
// Ejemplo:
//
// == Login
// #figure(
//   grid(columns: 2, gutter: 12pt,
//     image("assets/proto-login.png", width: 100%),
//     image("assets/impl-login.png", width: 100%),
//   ),
//   caption: [Login: prototipo (izq.) vs. implementación (der.)],
// )

== Autenticación

// TODO: login, registro, verificación, recuperación

== Overview

// TODO: vista principal con resumen del hogar

== Dispositivos

// TODO: lista, detalle, edición

== Habitaciones

// TODO: vista de habitaciones con dispositivos vinculados

== Rutinas

// TODO: lista, wizard de creación

== Historial

// TODO: tabla con filtros

== Consumo

// TODO: gráficos y tabla

== Configuración

// TODO: perfil y cambio de contraseña

== Notificaciones

// TODO: dropdown, toasts

// ====================================
// 6. DECISIONES DE DISEÑO E IMPLEMENTACIÓN
// ====================================

= Decisiones de diseño e implementación

Esta sección documenta las decisiones tomadas durante la implementación que complementan o adaptan lo planteado en la primera entrega. Cada decisión se justifica con los principios de HCI establecidos previamente.

== Arquitectura CSS: módulos globales vs. estilos scoped

*Decisión:* Se organizó el CSS en una capa de archivos globales por categoría (`variables.css`, `buttons.css`, `forms.css`, `cards.css`, `tables.css`, `controls.css`, `modals.css`) complementados con estilos scoped en cada componente Vue para reglas específicas de la vista.

*Justificación:* La alternativa idiomática de Vue sería encapsular cada patrón visual en un componente base (por ejemplo, `<BaseCard>`, `<DataTable>`). Se optó por CSS global modular por las siguientes razones:

-- Los patrones compartidos (tarjetas, botones, formularios) son puramente visuales y no encapsulan comportamiento ni estado. Crear componentes wrapper sin lógica agrega una capa de abstracción que no aporta valor funcional.
-- Las clases CSS permiten composición libre (`class="card card--xl chart-card"`) sin necesidad de props para cada variante, lo cual resulta en menos código y mayor flexibilidad.
-- La separación estructura/presentación (RNF4) se mantiene de forma más clara: el HTML define la semántica, el CSS global define la apariencia compartida, y el scoped define excepciones.

Los componentes base se reservaron para casos donde sí encapsulan comportamiento: `ToggleSwitch` (estado reactivo), `ToastContainer` (auto-dismiss), `DeviceCard` (interacción con el store).

== Sistema de design tokens

*Decisión:* Todos los colores, tamaños, espacios y radios se centralizaron en variables CSS (`variables.css`), eliminando valores hardcoded en los componentes.

*Justificación:* Esto garantiza consistencia visual (Nielsen \#4) de forma técnica: un cambio de color se propaga a toda la aplicación. Durante la implementación se detectaron valores hardcoded (por ejemplo, `#d32f2f` en vez de `var(--danger)`) que fueron corregidos en una pasada de auditoría. Se agregaron las variables `--warning`, `--danger-bg` y `--success-bg` que no estaban previstas en el prototipo original pero resultaron necesarias para estados intermedios.

== Controles específicos por tipo de dispositivo

*Decisión:* Cada tipo de dispositivo tiene un componente de controles dedicado (`LightControls`, `DoorControls`, `CurtainControls`, `AlarmControls`, `WaterControls`) que se renderiza condicionalmente en la vista de detalle.

*Justificación:* Los dispositivos del mundo real tienen interacciones fundamentalmente distintas: una lámpara se controla con un slider de brillo y un color picker, mientras que una puerta tiene acciones discretas (abrir/cerrar/bloquear). Unificar estos controles en un componente genérico sacrificaría la correspondencia con el mundo real (Nielsen \#2) y aumentaría la carga cognitiva al presentar controles irrelevantes para cada tipo.

Los estilos de estos controles (`.control-row`, `.slider`, `.btn-control`, `.color-picker`) sí se centralizaron en `controls.css` para mantener consistencia visual entre los distintos tipos.

== Notificaciones: persistentes vs. efímeras

*Decisión:* Se implementaron dos sistemas de notificaciones independientes: un dropdown de notificaciones persistentes (eventos del sistema vía WebSocket) y toasts efímeros (feedback de acciones del usuario).

*Justificación:* Mezclar ambos tipos generaría confusión sobre el origen de la información. Los toasts confirman la acción inmediata ("Dispositivo guardado") y desaparecen en 3 segundos, reduciendo la interrupción. Las notificaciones del dropdown informan sobre eventos externos ("Se compartió un hogar contigo") y persisten hasta ser leídas, permitiendo al usuario revisarlas cuando lo considere oportuno. Esta separación aplica el principio de visibilidad del estado (Nielsen \#1) sin sobrecargar al usuario (estética minimalista, Nielsen \#8).

== Wizard de rutinas en 4 pasos

*Decisión:* La creación de rutinas sigue un wizard de 4 pasos: nombre, selección de dispositivos, configuración de acciones y resumen final.

*Justificación:* En las observaciones participativas de la primera entrega, los usuarios novatos encontraron la creación de rutinas "demasiado compleja" y sugirieron plantillas o asistencia visual. El wizard fragmenta la tarea en pasos simples con una barra de progreso visible, aplicando la reducción de carga cognitiva. El paso de resumen permite revisar antes de confirmar (prevención de errores, Nielsen \#5). La barra de progreso indica claramente en qué paso se encuentra el usuario (visibilidad del estado, Nielsen \#1).

== Confirmación de acciones destructivas

*Decisión:* Todas las acciones de eliminación (dispositivos, habitaciones, desvinculación) requieren confirmación mediante modal.

*Justificación:* Consistente con la decisión de la primera entrega de prevenir errores (Nielsen \#5). El modal de eliminación de habitaciones además advierte que los dispositivos vinculados serán eliminados, informando al usuario sobre las consecuencias antes de actuar.

== Vista Overview como punto de entrada

*Decisión:* La vista Overview muestra un resumen con dispositivos favoritos, consumo total y dispositivos activos del hogar seleccionado.

*Justificación:* En la primera entrega se definió que "el usuario puede ver estados de alerta o rutinas favoritas sin navegar casa por casa, reduciendo drásticamente la carga de trabajo". La implementación prioriza la información más consultada (dispositivos favoritos con toggle rápido, métricas de consumo) en la primera pantalla visible al entrar al hogar, aplicando reconocimiento sobre recuerdo (Nielsen \#6).

== Feedback inmediato en controles de dispositivos

*Decisión:* Los toggles y controles de dispositivos aplican el cambio visual de forma inmediata y envían la petición a la API en paralelo. Si la API falla, se revierte el estado y se muestra un toast de error.

*Justificación:* En las observaciones de la primera entrega, "la falta de feedback tras activar una acción generó mucha incertidumbre". El patrón de actualización optimista elimina el delay perceptible entre la acción y el resultado visual, mientras que la reversión en caso de error mantiene la consistencia del estado.

== Navegación con sidebar colapsable y breadcrumbs

*Decisión:* Se mantuvo la estructura de sidebar con 6 secciones principales, consistente con el prototipo. La barra superior muestra la ubicación actual (hogar / sección) como breadcrumb.

*Justificación:* Consistente con la decisión de la primera entrega de aplicar la Ley de Hick (limitar opciones) y ofrecer orientación constante. La barra superior muestra `Casa Martinez / Dispositivos` para que el usuario sepa en todo momento dónde se encuentra, sirviendo como "salida de emergencia" (Nielsen \#3).

// ====================================
// 7. DIFERENCIAS CON EL PROTOTIPO
// ====================================

= Diferencias respecto al prototipo

Durante la implementación se realizaron ajustes respecto al prototipo de la primera entrega. Los cambios más significativos son:

// TODO: Completar con las diferencias reales que hayan encontrado.
// Ejemplos:
// -- El prototipo mostraba un mapa visual de la casa en el Overview.
//    En la implementación se reemplazó por tarjetas de resumen con métricas,
//    ya que la API no provee datos espaciales para generar el plano.
//
// -- El prototipo incluía perfiles de usuario (administrador, adolescente).
//    Esta funcionalidad no se implementó ya que RF21 era opcional y se
//    priorizaron otras funcionalidades.

// ====================================
// 8. FEEDBACK DE LA PRIMERA ENTREGA
// ====================================

= Feedback de la primera entrega

// TODO: Completar con el feedback recibido del profesor/evaluador
// y cómo se abordó en la implementación.
// Ejemplo:
// -- *Feedback:* "La sección de configuración debería estar visible para todos
//    los perfiles, aunque con restricciones."
//    *Resolución:* Se implementó la sección de Configuración accesible desde
//    el menú de usuario en la barra superior, visible para todos los usuarios.
