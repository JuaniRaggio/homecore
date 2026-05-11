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
 #figure(image("hci_before_and_after/login_account/registrarse.png", width: 80%), caption: [Pantalla de registro])
 #figure(image("hci_before_and_after/login_account/verificacion.png", width: 80%), caption: [Pantalla de verificación de email])
 #figure(image("hci_before_and_after/login_account/recuperarcuenta.png", width: 80%), caption: [Pantalla de recuperación de constraseña])
 #figure(image("hci_before_and_after/login_account/cambiocontra.png", width: 80%), caption: [Pantalla de cambio de contraseña])
 #figure(image("hci_before_and_after/login_account/iniciarsesion.png", width: 80%), caption: [Pantalla de login])

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

#figure(image("hci_before_and_after/consumo/consumoactual.png", width: 100%), caption: [Vista de consumo energético])

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
  [8], [Diseño responsivo. Adaptación a distintos tamaños de pantalla.], [Cumplido],
)

== Tecnologías utilizadas (RNF1)

El frontend de HomeCore fue desarrollado con Vue.js, utilizando la Composition API y una arquitectura basada en componentes para lograr una estructura modular. La navegación entre vistas se implementó mediante Vue Router, mientras que el manejo del estado global se realizó con Pinia. 
El proyecto emplea Vite como herramienta de construcción y desarrollo, permitiendo tiempos de compilación y recarga rápida. La comunicación en tiempo real con el servidor se resolvió mediante Socket.IO, y las solicitudes HTTP al backend se realizaron utilizando la Fetch API. 
Para la visualización de datos en la vista ‘Consumo’  se integró Chart.js junto con vue-chartjs. Finalmente, la interfaz utiliza Font Awesome para los íconos y la librería p-limit para controlar la concurrencia en la carga paralela de datos. 

== Compatibilidad con navegadores (RNF2)

HomeCore es compatible con las últimas versiones de los navegadores Chromium (Google Chrome, Microsoft Edge), Firefox y Safari.

== Adaptación a resoluciones de pantalla (RNF3)

La interfaz fue diseñada y optimizada principalmente para su uso en pantallas de escritorio y laptops, priorizando una experiencia clara y estable en resoluciones de monitor estándar. El layout general, compuesto por una barra lateral de navegación y un área principal de contenido, mantiene un comportamiento adecuado para el entorno de uso previsto.
Si bien la aplicación puede presentar algunas limitaciones de adaptación en resoluciones más reducidas, como tablets o dispositivos móviles, estos casos no formaron parte del foco principal de desarrollo en la presente entrega.

== Separación de estructura y presentación (RNF4)

La aplicación respeta la separación entre la estructura del contenido y su presentación visual. Cada componente de Vue define su marcado HTML de forma independiente a los estilos que lo afectan, los cuales se declaran en bloques de CSS dedicados dentro del mismo componente o en hojas de estilo globales separadas. 

*Nota sobre RNF4:* Se utilizó un sistema de CSS en capas: variables globales (design tokens), archivos de estilos compartidos por categoría (botones, formularios, tarjetas, tablas, controles) y estilos scoped en cada componente Vue para reglas específicas de la vista. Esta decisión se detalla en la sección de decisiones de diseño.

== Separación de estructura y comportamiento (RNF5)

La aplicación mantiene una clara separación entre el marcado HTML y la lógica de comportamiento. En el modelo de componentes de Vue 3, cada unidad de la interfaz divide explícitamente su template (estructura), sus estilos (presentación) y su script (comportamiento) en bloques diferenciados. La lógica de negocio compleja —como la comunicación con el backend, el manejo del estado global y las operaciones sobre dispositivos o rutinas— se extrae de los componentes hacia capas independientes: servicios, stores y composables. 
Esto evita la mezcla de responsabilidades dentro de los componentes, facilitando su mantenimiento y escalabilidad. Por ejemplo, el componente `DeviceCard` se encarga únicamente de renderizar la información del dispositivo y manejar eventos de interacción, mientras que la lógica de actualización del estado global y las llamadas a la API se delegan a Pinia y a servicios específicos.

== Validación de HTML (RNF6)

La aplicación fue validada utilizando el W3C Markup Validation Service, asegurando que el HTML generado cumple con los estándares web (validator.w3.org). El análisis arrojó un resultado sin errores ni advertencias, confirmado por la respuesta del validador:

{"version":"26.5.9","messages":[]}

El arreglo messages vacío indica que el documento HTML cumple plenamente con el estándar, sin ninguna observación por parte del validador.

== Validación de CSS (RNF7)

La validación de estilos correspondiente al requerimiento RNF7 se realizó utilizando el servicio W3C CSS Validation Service, configurado con el perfil CSS Level 3. Cada hoja de estilos del proyecto fue analizada de manera individual mediante carga multipart. La aplicación organiza sus estilos en doce archivos especializados, entre ellos variables.css, reset.css, utilities.css, layout.css y forms.css.
Como resultado general, los doce archivos CSS del proyecto superaron la validación sin presentar errores.
Las advertencias restantes detectadas por el validador se relacionan con limitaciones conocidas de la herramienta frente a características modernas de CSS. Por un lado, se generaron advertencias sobre expresiones var(--nombre) debido a que las CSS Custom Properties son dinámicas y su valor solo puede resolverse en tiempo de ejecución en el navegador. Por otro lado, la importación de Google Fonts mediante `@import` produjo advertencias porque el validador no analiza recursos externos cargados desde URLs.

== Wave (RNF)

La validación de accesibilidad correspondiente al requerimiento RNF se realizó utilizando herramientas basadas en axe-core y criterios WCAG 2.x nivel AA. Inicialmente se detectaron problemas relacionados con la ausencia del elemento semántico `<main>`, contenido fuera de regiones accesibles y contrastes insuficientes en algunos componentes de interfaz.
Las correcciones aplicadas incluyeron la incorporación de landmarks semánticos en todas las vistas de autenticación y el ajuste de colores para garantizar relaciones de contraste adecuadas según las pautas de accesibilidad.
Como resultado final, las cuatro páginas públicas del sistema (/login, /registro, /verificar y /recuperar) superaron exitosamente la validación de accesibilidad, obteniendo un estado de “0 violations found”, sin errores ni advertencias reportadas por las herramientas de análisis.


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

== Navegación con sidebar fija y breadcrumbs

*Decisión:* Se mantuvo la estructura de sidebar con 6 secciones principales, consistente con el prototipo. La barra superior muestra la ubicación actual (hogar / sección) como breadcrumb. Se eliminó la funcionalidad de colapsar la sidebar (reducirla de 220px a 60px) que estaba presente en una versión intermedia.

*Justificación:* La sidebar colapsable presentaba un problema técnico: al reducir su ancho, el área de contenido (`page-content`) no ajustaba su `margin-left` correspondiente, dejando un espacio vacío entre la sidebar y el contenido. Más allá del bug, el collapse agregaba complejidad sin beneficio claro: en el rango obligatorio de 1280px a 1920px, la sidebar de 220px no compite con el espacio del contenido. Para pantallas angostas (menores a 768px), se adoptó un patrón de overlay mobile en lugar de collapse, como se detalla en la sección de diseño responsivo. La barra superior muestra `Casa Martinez / Dispositivos` para que el usuario sepa en todo momento dónde se encuentra, sirviendo como "salida de emergencia" (Nielsen \#3).

== CSS manual vs. frameworks de componentes (Vuetify)

*Decisión:* Se implementaron todos los estilos de la aplicación con CSS puro organizado en módulos (variables, botones, formularios, tarjetas, tablas, controles, layout) y estilos scoped por componente, sin utilizar frameworks de UI como Vuetify, Quasar o PrimeVue.

*Justificación:* Esta decisión se fundamenta en criterios técnicos y pedagógicos:

-- *Comprensión del modelo de caja y del flujo CSS:* Escribir las reglas de layout, flexbox, grid y media queries a mano obliga a entender cómo funciona el posicionamiento, el sizing y la respuesta a cambios de viewport. Un framework como Vuetify abstrae estos mecanismos detrás de props (`cols`, `sm`, `md`) y clases utilitarias, lo cual resuelve el problema pero no enseña el fundamento. Al implementar el responsive manualmente, cada decisión (cuándo apilar columnas, cuándo ocultar un elemento, cómo manejar overflow) es explícita y trazable en el código.

-- *Control sobre el sistema de diseño:* HomeCore utiliza un sistema de design tokens propio (variables CSS para colores, tipografía, espaciado, radios) que define la identidad visual de la aplicación. Vuetify impone su propio sistema de diseño (Material Design) con tokens, componentes y convenciones que habría que sobrescribir extensivamente para lograr la estética definida en el prototipo. Esto introduce una capa de complejidad: se estaría trabajando _contra_ el framework en lugar de _con_ él.

-- *Tamaño del bundle:* Vuetify agrega entre 200KB y 500KB al bundle comprimido, dependiendo de la configuración de tree-shaking. La aplicación actual, con CSS modular propio, tiene un CSS total de ~25KB gzip. Esta diferencia es significativa para la performance de carga inicial, especialmente relevante en el contexto de una aplicación de domotica donde los usuarios pueden acceder desde dispositivos con conectividad limitada.

-- *Separación de responsabilidades (RNF4):* Al no depender de clases utilitarias mezcladas en el template (`<v-col cols="12" sm="6" md="4">`), la separación entre estructura HTML y presentación CSS se mantiene clara. Las reglas de layout viven en archivos CSS, no dispersas en atributos del template.

-- *Responsabilidad del sizing en el componente correcto:* Al escribir CSS manual, se forzó una decisión arquitectónica importante: las grillas de layout (`items-grid`, `homes-grid`, `bottom-grid`) son las responsables de definir el tamaño de las celdas mediante `minmax()`, mientras que las tarjetas hijo (`DeviceCard`, `HomeCard`, `RoutineCard`) se adaptan al espacio disponible sin imponer anchos mínimos propios. En una versión intermedia, `DeviceCard` tenía `min-width: 240px` que hacía que las tarjetas desbordaran su contenedor cuando la grilla asignaba columnas más angostas. La corrección (cambiar a `min-width: 0`) ilustra el principio: el padre define el espacio, el hijo lo ocupa.

== Diseño responsivo (RNF8)

*Decisión:* Se implementó diseño responsivo con un breakpoint principal en 768px. Por debajo de ese ancho, la sidebar se oculta y el contenido ocupa el ancho completo. Se incorporó un botón de menú hamburguesa en la barra superior para acceder a la sidebar como overlay con backdrop semitransparente.

*Justificación:* Si bien el RNF3 obligatorio solo exige soporte para resoluciones de 1280px a 1920px, se optó por implementar el RNF8 opcional (diseño responsivo) por las siguientes razones:

-- *Perfil de usuario:* En la primera entrega se definieron modelos de persona que incluyen usuarios que gestionan su hogar desde dispositivos móviles (por ejemplo, verificar el estado de los dispositivos fuera de casa). Una aplicación de domotica que solo funciona en escritorio limita su utilidad al contexto del hogar, lo cual contradice uno de sus principales beneficios: el control remoto.

-- *Principio de flexibilidad y eficiencia (Nielsen \#7):* La adaptación a distintos tamaños de pantalla permite que tanto usuarios novatos (que acceden desde el celular) como expertos (que usan un monitor de escritorio) tengan una experiencia funcional sin degradación.

Las adaptaciones específicas por componente fueron:

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Componente*], [*Adaptación en mobile (<=768px)*]),
  [Sidebar], [Se oculta con `translateX(-100%)`. Aparece como overlay al tocar la hamburguesa. Un backdrop semitransparente permite cerrarla tocando fuera. Se cierra automáticamente al navegar.],
  [TopBar], [Se muestra el botón hamburguesa. Se oculta el nombre de usuario (queda solo el avatar) para liberar espacio horizontal.],
  [Layout general], [El `page-content` elimina el margen izquierdo de la sidebar y usa márgenes simétricos de 16px.],
  [HomeView], [La grilla inferior (favoritos + rutinas) pasa de 2 columnas a 1. La sección de casa apila el panel de habitaciones y la isometría verticalmente.],
  [OverviewView], [La grilla de propiedades pasa de auto-fill a 1 columna. Se reduce el tamaño del título de bienvenida.],
  [DevicesView], [Los filtros de tipo y habitación se apilan verticalmente.],
  [Wizard (rutinas)], [Los labels del stepper se ocultan y quedan solo los números. Las filas de acciones apilan sus elementos verticalmente. El stepper tiene `overflow: hidden` para que nunca desborde su contenedor independientemente del ancho.],
  [Vistas existentes], [RoomsView, ConsumptionView, HistoryView, SettingsView, RoomDetailView ya eran responsivas por usar grillas con `auto-fill` o layouts naturalmente verticales.],
)

*Implementación técnica:* El estado de la sidebar mobile se maneja con un composable singleton (`useSidebar.js`) que expone un `ref` reactivo compartido entre la TopBar (que lo toglea) y la SideBar (que reacciona). Este patrón es consistente con otros composables de la aplicación (`useModal`, `useConfirmAction`) y evita acoplar los componentes mediante props o eventos.

// ====================================
// 7. DIFERENCIAS CON EL PROTOTIPO
// ====================================

= Diferencias respecto al prototipo

Durante la implementación se realizaron ajustes respecto al prototipo de la primera entrega. Los cambios más significativos son:

== Cambios en el Overview del hogar

*Decisión:* Se reemplazó el mapa visual de la casa por tarjetas de resumen con métricas clave y una vista más simple del hogar.

*Justificación:* El prototipo original incluía un plano isométrico más avanzado de la casa con habitaciones y dispositivos ubicados espacialmente. Sin embargo, la API provista no ofrece datos de ubicación ni dimensiones de los dispositivos, lo que imposibilitaba generar un mapa visual preciso. En su lugar, se priorizó mostrar tarjetas de resumen que destacan los dispositivos favoritos, los dispositivos activos y rutinas recientes, manteniendo la función principal del Overview del hogar como punto de entrada rápido a la información más relevante.

== Eliminación de perfiles de usuario

*Decisión:* No se implementaron perfiles de usuario (administrador, adolescente).

*Justificación:* El prototipo incluía la posibilidad de crear perfiles con permisos diferenciados (por ejemplo, un perfil adolescente sin acceso a la configuración). Sin embargo, el requisito RF21 que contemplaba esta funcionalidad era opcional y, dado el alcance de la implementación, se priorizaron otras funcionalidades consideradas más críticas para la experiencia general. La implementación actual asume un modelo de usuario único con acceso completo, lo cual simplifica la gestión de permisos y roles sin comprometer las funcionalidades principales de la aplicación.

== Cambios en los botones de acción de dispositivos específicos 

*Decisión:* Se cambiaron los controles de dispositivos como el parlante y la cortina. Anteriormente se tenía un toggle button para encender/apagar. En la implementación se optó por un panel de acciones contextuales compuesto por múltiples botones independientes.

*Justificación:* En el prototipo, dispositivos como el parlante o la cortina tenían un toggle button que alternaba entre encendido y apagado. Sin embargo, durante la implementación se identificó que estos dispositivos tienen acciones discretas (por ejemplo, una cortina puede estar abierta, cerrada o en posición intermedia) que no se adaptan bien a un toggle binario. Haciendo que el manejo del dispositivo sea muy poco intuitivo. Por lo tanto, se diseñó un panel de acciones contextuales con botones independientes para cada acción relevante (subir, bajar para la cortina; reproducir, pausar, siguiente para el parlante), lo que mejora la correspondencia con las funcionalidades reales de los dispositivos y reduce la confusión del usuario. El diseño actual de cada control refleja el modelo de interacción familiar para dispositivos multimedia y automatización del hogar, reenforzando la coherencia entre el sistema y el mundo real (Nielsen \#2).

== Incorporación de una vista Overview como pantalla principal al ingresar a la página web

*Decisión:* Se agregó una vista Overview que se muestra al ingresar a la página web. Ésta muestra un resumen global del sistema , mostrando todas las propiedades registradas, métricas principales de cada una, dispositivos críticos y rutinas globales favoritas.

*Justificación:* En la primera entrega se definió que "el usuario puede ver estados de alerta o rutinas favoritas sin navegar casa por casa, reduciendo drásticamente la carga de trabajo". Sin embargo, el prototipo no incluía una vista específica para esto. Por ende, para cumplir con esta premisa, se decidió implementar una vista Overview que sirva como dashboard principal previo a ingresar a un hogar. Esta vista proporciona acceso rápido a la información más relevante (como dispositivos críticos y rutinas favoritas) y un resumen de cada propiedad a lo que respecta sus dispositivos. Esto logra informar al usuario para que luego tome una decisión más directa en vez de navegar por cada hogar para obtener la misma información. De esta forma, se aplica el principio de reconocimiento sobre recuerdo (Nielsen \#6) al mostrar la información clave de forma inmediata, sin requerir navegación adicional.



// ====================================
// 8. FEEDBACK DE LA PRIMERA ENTREGA
// ====================================

= Feedback de la primera entrega

== Configuración de usuario

*Feedback:* "La sección de configuración debería ser más visible. Su ubicación es poco estratégica."

*Resolución:* En la primera entrega, la sección de Configuración solo era accesible desde el fondo de la barra lateral, lo cual dificultaba su descubrimiento y acceso. A partir del feedback recibido, se decidió incorporarla también dentro del menú de usuario en la barra superior, aumentando su visibilidad y accesibilidad para todos los usuarios. Esta decisión se relaciona con la heurística de "Consistencia y estándares" (Nielsen \#4) ya que ubicar la configuración dentro del menú de usuario sigue patrones de navegación ampliamente utilizados en aplicaciones modernas, haciendo que la interfaz resulte más familiar e intuitiva. Además, también refuerza la heurística "Reconocimiento antes que recuerdo"(Nielsen \#6), porque el usuario puede identificar rápidamente dónde acceder a las configuraciones sin necesidad de recordar su ubicación específica dentro de la barra lateral.


// TODO: Completar con el feedback recibido del profesor/evaluador
// y cómo se abordó en la implementación.
// Ejemplo:
// -- *Feedback:* "La sección de configuración debería estar visible para todos
//    los perfiles, aunque con restricciones."
//    *Resolución:* Se implementó la sección de Configuración accesible desde
//    el menú de usuario en la barra superior, visible para todos los usuarios.
