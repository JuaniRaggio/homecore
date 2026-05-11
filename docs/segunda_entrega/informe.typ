// ============================================================
// HomeCore -- TP Gestión de Casas Inteligentes (Segunda Entrega)
// ============================================================

#let nota(contenido) = {
  block(
    fill: rgb("#E3F2FD"),
    stroke: rgb("#1976D2") + 1pt,
    inset: 10pt,
    radius: 4pt,
    width: 100%,
  )[
    #text(weight: "bold", fill: rgb("#1976D2"))[Nota:] #contenido
  ]
}

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

La implementación se realizó con Vue.js 3, la API provista, Pinia para el manejo de estado, Vue Router para la navegación, y Socket.io para notificaciones en tiempo real. Se priorizó la fidelidad al prototipo original, adaptando únicamente lo necesario para resolver limitaciones técnicas o mejorar la experiencia a partir del feedback recibido en la primera entrega.

= Modelos de Persona (Contexto)

Para fundamentar las decisiones de diseño, se utilizaron los siguientes modelos de usuario:

- *Valentina "La Power User" (27 años):* Nivel tecnológico avanzado. Busca automatizaciones complejas, control granular de dispositivos y análisis de datos.
- *Carolina "La Gestora del Hogar" (42 años):* Nivel tecnológico intermedio. Su prioridad es la seguridad familiar y la eficiencia en la gestión del hogar.
- *Marta "La Usuaria Tradicional" (63 años):* Nivel tecnológico básico. Prefiere interfaces simples, flujos lineales y botones grandes con etiquetas claras.


== Requisitos funcionales

=== RF1-6 — Gestión de cuenta y autenticación

El sistema implementa un flujo completo de autenticación que incluye registro, verificación, inicio de sesión y gestión de contraseñas. El usuario puede:

- *Registrar cuenta (RF1):* Crear una cuenta nueva ingresando nombre, correo electrónico y contraseña. Al registrarse exitosamente, el usuario es redirigido al flujo de verificación.
- *Verificar cuenta (RF2):* El sistema envía un correo de verificación para validar la dirección de correo electrónico.
- *Iniciar sesión (RF5):* Autenticarse ingresando correo electrónico y contraseña. La sesión se mantiene activa durante la navegación, y el sistema maneja automáticamente la expiración de tokens JWT redirigiendo al usuario al login con un mensaje explicativo.
- *Cerrar sesión (RF6):* Finalizar la sesión desde el menú de perfil en la barra superior.
- *Recuperar contraseña (RF3):* Ingresar el correo para recibir un código de verificación y posteriormente establecer una nueva contraseña.
- *Cambiar contraseña (RF4):* Modificar la contraseña desde la sección de configuración, ingresando la contraseña actual y la nueva.

#figure(
  grid(columns: 3, gutter: 12pt,
    image("hci_before_and_after/login_account/registrarse.png", width: 100%),
    image("hci_before_and_after/login_account/verificacion.png", width: 100%),
    image("hci_before_and_after/login_account/iniciarsesion.png", width: 100%),
    image("hci_before_and_after/login_account/logout.png", width: 100%),
    image("hci_before_and_after/login_account/recuperarcuenta.png", width: 100%),
    image("hci_before_and_after/login_account/cambiocontra.png", width: 100%),
  ),
  caption: [Flujos de autenticación: registro, verificación, inicio de sesión, cierre de sesión, recuperación de contraseña y cambio de contraseña],
)

=== RF7-9 — Gestión y control de dispositivos

==== Gestión de dispositivos (RF7)
El usuario puede crear dispositivos asignándoles un nombre y tipo, renombrarlos, editarlos y eliminarlos. Toda eliminación requiere confirmación explícita para evitar acciones accidentales.

==== Consulta de dispositivos (RF8)
El sistema presenta un listado de todos los dispositivos del hogar mostrando nombre, tipo, estado actual y habitación asignada. Además, existe una vista de detalle por dispositivo que expone su estado completo y sus controles específicos, actualizada en tiempo real.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/dispositivos/gestion2.png", width: 100%),
    image("hci_before_and_after/dispositivos/VistaDispositivos.jpg", width: 100%),
  ),
  caption: [Gestión de dispositivos (izq.) y vista de listado de dispositivos (der.)],
)

==== Control de dispositivos (RF9)
Se implementaron controles específicos para los 10 tipos de dispositivos soportados por la API:

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Tipo*], [*Acciones disponibles*]),
  [Lampara], [Encender/apagar, brillo (0–100%), color],
  [Puerta], [Abrir/cerrar, bloquear/desbloquear],
  [Alarma], [Armar (modo ausente/en casa), desarmar, cambiar código],
  [Canilla], [Abrir/cerrar],
  [Persiana], [Subir/bajar, posición exacta (0–100%) con indicador visual],
  [Aire Acondicionado], [Encender/apagar, temperatura (18–38°C), modo, velocidad del ventilador],
  [Parlante], [Play/pausa/reanudar, siguiente/anterior, volumen, género, lista de reproducción],
  [Aspiradora], [Iniciar/pausar, volver a la base, modo (aspirar/trapear), ubicación],
  [Heladera], [Temperatura heladera (2–8°C) y freezer (−20 a −8°C), modo de operación],
  [Horno], [Encender/apagar, temperatura (90–230°C), fuente de calor, grill, convección],
)

==== Estados combinados de alarmas

El sistema implementa una lógica de estados agregados para las alarmas a nivel de hogar. Cuando un hogar contiene múltiples alarmas, el estado general de seguridad se calcula combinando los estados individuales de cada alarma:

- *Desarmada:* Todas las alarmas del hogar están desactivadas. El hogar se visualiza con un indicador de seguridad en rojo, señalizando ausencia de protección.
- *Armada:* Todas las alarmas del hogar están activadas (ya sea en modo ausente o en casa). El hogar muestra un indicador verde, confirmando protección completa.
- *Parcialmente armada:* Algunas alarmas están activadas mientras otras permanecen desactivadas. El hogar se identifica con un indicador amarillo, alertando sobre una cobertura de seguridad incompleta.

#figure(
  image("hci_before_and_after/MultiplesAlarmasEstados.jpg", width: 100%),
  caption: [Se observan los 4 posibles estados de las casas],
)

Esta agregación de estados permite al usuario identificar de un vistazo el nivel de protección de cada propiedad sin necesidad de revisar individualmente el estado de cada alarma. La representación visual mediante colores (rojo, verde, amarillo) sigue convenciones estándar de sistemas de seguridad, facilitando el reconocimiento inmediato del estado de protección.

#pagebreak()

=== RF10-12, 23 — Gestión completa de rutinas

El sistema permite gestionar rutinas automatizadas que ejecutan múltiples acciones sobre dispositivos del hogar.

==== Creación y edición de rutinas (RF10)
El usuario puede crear rutinas mediante un asistente de 4 pasos donde define:
1. Nombre de la rutina
2. Selección de dispositivos a controlar
3. Configuración de acciones específicas para cada dispositivo
4. Resumen y confirmación

Las rutinas pueden editarse y eliminarse en cualquier momento. La creación incluye la configuración de los días de la semana y hora de ejecución automática (RF23), sin necesidad de intervención manual posterior.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/rutinas/crear-rutina1.png", width: 100%),
    image("hci_before_and_after/rutinas/crear-rutina2.png", width: 100%),
    image("hci_before_and_after/rutinas/crear-rutina3.png", width: 100%),
    image("hci_before_and_after/rutinas/crear-rutina4.png", width: 100%),
  ),
  caption: [Asistente de creación de rutinas (4 pasos) con configuración de horarios y días],
)

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/rutinas/editar-rutina.png", width: 100%),
    image("hci_before_and_after/rutinas/editar-rutina2.png", width: 100%),
  ),
  caption: [Edición de rutinas existentes],
)

#pagebreak()

==== Consulta y ejecución de rutinas (RF11-12)
El sistema presenta un listado de todas las rutinas del hogar en formato de tarjetas, mostrando nombre, días configurados y un resumen de las acciones que ejecuta. Desde la lista o el detalle de una rutina, el usuario puede ejecutarla manualmente con un solo clic en "ejecutar ahora", disparando todas las acciones definidas sobre los dispositivos correspondientes.

#figure(
  image("hci_before_and_after/rutinas/gestion.png", width: 100%),
  caption: [Vista de listado y gestión de rutinas],
)


=== RF13 — Consultar acciones realizadas
El sistema presenta un registro paginado de todas las acciones ejecutadas sobre los dispositivos del hogar en la vista "Historial", incluyendo el nombre del dispositivo, la acción realizada y la marca temporal de cada evento.

#figure(
  image("hci_before_and_after/historial/historia.png", width: 100%)
)

=== RF14-16 — Gestión de habitaciones y vinculación de dispositivos

==== Gestión de habitaciones (RF14-15)
El usuario puede crear habitaciones dentro de un hogar, renombrarlas y eliminarlas. Toda eliminación requiere confirmación explícita. El sistema lista las habitaciones del hogar seleccionado, y al ingresar al detalle de una habitación, se visualizan los dispositivos que contiene con acceso directo al control de cada uno.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/habitaciones/image.png", width: 100%),
    image("hci_before_and_after/habitaciones/habitaciones.png", width: 100%),
  ),
  caption: [Gestión de habitaciones (izq.) y vista de listado con dispositivos asociados (der.)],
)

#pagebreak()

==== Vinculación de dispositivos a habitaciones (RF16)
El usuario puede asignar o mover un dispositivo a una habitación distinta dentro del mismo hogar. Todos los dispositivos deben pertenecer a alguna habitación; no se permiten dispositivos sin asignar. Esta funcionalidad mantiene la jerarquía organizacional hogar $->$ habitación $->$ dispositivo.

#figure(
  image("hci_before_and_after/habitaciones/vincular.png", width: 100%),
  caption: [Vinculación de dispositivos a habitaciones],
)

=== RF17-19 — Gestión y consulta de hogares

El sistema permite gestionar múltiples hogares de forma completa:

==== Gestión de hogares (RF17)
El usuario puede crear hogares con un nombre identificatorio, editarlos y eliminarlos. Adicionalmente, se implementó la posibilidad de compartir un hogar con otros usuarios mediante su correo electrónico, otorgándoles acceso a sus dispositivos y habitaciones.

Las habitaciones se crean y gestionan dentro del contexto de un hogar específico (RF19), manteniendo en todo momento la jerarquía hogar $->$ habitación $->$ dispositivo tanto en la navegación como en la lógica del sistema.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/AAAAAA.jpg", width: 100%),
    image("hci_before_and_after/hogar/gestionOver.png", width: 100%),
  ),
  caption: [Gestión de hogares: desde el Inicio (izq.) y desde Overview (der.)],
)

==== Consulta de hogares (RF18)
El sistema presenta un panel general con todos los hogares a los que tiene acceso el usuario, tanto propios como compartidos, junto con un resumen del estado de sus dispositivos. Desde este panel se navega al detalle de cada hogar.

=== RF20 — Enviar notificaciones
El sistema notifica al usuario en tiempo real cuando un dispositivo cambia de estado, mostrando un mensaje emergente en pantalla.

=== RF21 — Restringir acceso a dispositivos, rutinas, habitaciones y hogares
El sistema garantiza que cada usuario solo pueda visualizar y operar sobre los hogares, habitaciones, dispositivos y rutinas a los que tiene acceso autorizado, el resto de dispositivos pueden accionarse mediante una contraseña.

#figure(
  image("hci_before_and_after/Editar-dispositivo/alarma-control.png", width: 100%)
)

=== RF22 — Consultar consumo eléctrico
El sistema presenta gráficos de consumo eléctrico de los dispositivos del hogar, permitiendo al usuario visualizar el uso energético a lo largo del tiempo.

#figure(
  image("hci_before_and_after/consumo/consumoactual.png", width: 100%),
  caption: [Visualización de consumo eléctrico por dispositivo],
)

== Requisitos no funcionales

=== RNF1 — Tecnologías utilizadas
El frontend de HomeCore fue desarrollado con Vue.js, utilizando la Composition API y una arquitectura basada en componentes para lograr una estructura modular. La navegación entre vistas se implementó mediante Vue Router, mientras que el manejo del estado global se realizó con Pinia.

El proyecto emplea Vite como herramienta de construcción y desarrollo, permitiendo tiempos de compilación y recarga rápida. La comunicación en tiempo real con el servidor se resolvió mediante Socket.IO, y las solicitudes HTTP al backend se realizaron utilizando la Fetch API.

Para la visualización de datos en la vista “Consumo” se integró Chart.js junto con vue-chartjs. Finalmente, la interfaz utiliza Font Awesome para los íconos y la librería p-limit para controlar la concurrencia en la carga paralela de datos.

=== RNF2 — Compatibilidad con Navegadores
La aplicación es compatible con los navegadores: Google Chrome, Mozilla Firefox, Microsoft Edge y Safari.

=== RNF3 — Adaptación a resoluciones de pantalla
La interfaz fue diseñada y optimizada principalmente para su uso en pantallas de escritorio y laptops, priorizando una experiencia clara y estable en resoluciones de monitor estándar. El layout general, compuesto por una barra lateral de navegación y un área principal de contenido, mantiene un comportamiento adecuado para el entorno de uso previsto.

Si bien la aplicación puede presentar algunas limitaciones de adaptación en resoluciones más reducidas, como tablets o dispositivos móviles, estos casos no formaron parte del foco principal de desarrollo en la presente entrega.

=== RNF4 — Separación de estructura y presentación
La aplicación respeta la separación entre la estructura del contenido y su presentación visual. Cada componente de Vue define su marcado HTML de forma independiente a los estilos que lo afectan, los cuales se declaran en bloques de CSS dedicados dentro del mismo componente o en hojas de estilo globales separadas.

=== RNF5 — Separación de estructura y comportamiento
La aplicación mantiene una clara separación entre el marcado HTML y la lógica de comportamiento. En el modelo de componentes de Vue 3, cada unidad de la interfaz divide explícitamente su template (estructura), sus estilos (presentación) y su script (comportamiento) en bloques diferenciados.

La lógica de negocio compleja —como la comunicación con el backend, el manejo del estado global y las operaciones sobre dispositivos o rutinas— se extrae de los componentes hacia capas independientes: servicios, stores y composables.

=== RNF6 — Validación de HTML
El HTML generado por la aplicación fue sometido al validador oficial del W3C (validator.w3.org). El análisis arrojó un resultado sin errores ni advertencias, confirmado por la respuesta del validador:

```json
{"version":"26.5.9","messages":[]}
```

El arreglo `messages` vacío indica que el documento HTML cumple plenamente con el estándar, sin ninguna observación por parte del validador.

=== RNF7 — Validación de CSS
La validación de estilos correspondiente al requerimiento RNF7 se realizó utilizando el servicio W3C CSS Validation Service, configurado con el perfil CSS Level 3. Cada hoja de estilos del proyecto fue analizada de manera individual mediante carga multipart. La aplicación organiza sus estilos en doce archivos especializados, entre ellos `variables.css`, `reset.css`, `utilities.css`, `layout.css` y `forms.css`.

Como resultado general, los doce archivos CSS del proyecto superaron la validación sin presentar errores.

Las advertencias restantes detectadas por el validador se relacionan con limitaciones conocidas de la herramienta frente a características modernas de CSS. Por un lado, se generaron advertencias sobre expresiones `var(--nombre)` debido a que las CSS Custom Properties son dinámicas y su valor solo puede resolverse en tiempo de ejecución en el navegador. Por otro lado, la importación de Google Fonts mediante `@import` produjo advertencias porque el validador no analiza recursos externos cargados desde URLs.

=== RNF9 — Wave
La validación de accesibilidad correspondiente al requerimiento RNF se realizó utilizando herramientas basadas en axe-core y criterios WCAG 2.x nivel AA. Inicialmente se detectaron problemas relacionados con la ausencia del elemento semántico `<main>`, contenido fuera de regiones accesibles y contrastes insuficientes en algunos componentes de interfaz.

Las correcciones aplicadas incluyeron la incorporación de landmarks semánticos en todas las vistas de autenticación y el ajuste de colores para garantizar relaciones de contraste adecuadas según las pautas de accesibilidad.

Como resultado final, las cuatro páginas públicas del sistema (`/login`, `/registro`, `/verificar` y `/recuperar`) superaron exitosamente la validación de accesibilidad, obteniendo un estado de “0 violations found”, sin errores ni advertencias reportadas por las herramientas de análisis.


// ====================================
// 5. SISTEMA DE NOTIFICACIONES
// ====================================

= Sistema de notificaciones

El sistema de notificaciones responde al feedback de la primera entrega, donde se identificó la necesidad de mejorar la visibilidad del estado del sistema y proporcionar retroalimentación clara al usuario. Se implementaron tipos de notificaciones diferenciadas según su propósito y urgencia.

== Notificaciones de éxito (_toasts_)

Las notificaciones temporales confirman acciones del usuario inmediatamente, proporcionando retroalimentación visual no intrusiva que refuerza la *Visibilidad del estado del sistema* _(Heurística de Nielsen #1)_.

#figure(
  grid(columns: 3, gutter: 12pt,
    image("hci_before_and_after/Notificaciones/Creacion-de-casa.png", width: 100%),
    image("hci_before_and_after/Notificaciones/rutina creada.png", width: 100%),
    image("hci_before_and_after/Notificaciones/habitacion-eliminada.png", width: 100%),
  ),
  caption: [Notificaciones de éxito: Confirmación de creación de casa, rutina creada, y habitación eliminada. Estas notificaciones temporales aparecen durante 3 segundos y se desvanecen automáticamente.],
)

El color verde y el mensaje conciso eliminan la incertidumbre sin requerir acciones adicionales del usuario. Este tipo de retroalimentación inmediata es fundamental para cualquier interfaz, ya que confirma que la acción se registró correctamente y el sistema respondió como se esperaba.

== Notificaciones de error

Los mensajes de error proporcionan información clara sobre qué salió mal y cómo proceder, cumpliendo con la *ayudar a los usuarios a reconocer, diagnosticar y recuperarse de errores*.

#figure(
  image("hci_before_and_after/Notificaciones/codigo-seguridad-incorrecto.png", width: 60%),
  caption: [Notificación de error: Código de seguridad incorrecto. El mensaje en rojo indica claramente el problema y permite al usuario intentar nuevamente.],
)

El mensaje claro evita frustración y guía al usuario hacia la recuperación del error, proporcionando información específica sobre qué falló sin exponer detalles sensibles del sistema.

== Confirmaciones preventivas

Los diálogos de confirmación implementan la *Prevención de errores*, solicitando confirmación antes de acciones destructivas.

#figure(
  image("hci_before_and_after/Notificaciones/eliminar-habitacion.png", width: 60%),
  caption: [Modal de confirmación: Eliminar habitación. El diálogo explica las consecuencias de la acción (eliminación de dispositivos asociados) y requiere confirmación explícita antes de proceder.],
)

El diálogo explica claramente las consecuencias de la acción ("y todos sus dispositivos serán eliminados") y ofrece la opción de cancelar, previniendo errores irreversibles y dando tiempo al usuario para reconsiderar acciones destructivas.

== Notificaciones de eventos externos

Las notificaciones persistentes (accesibles desde el menú superior) informan sobre eventos del sistema que ocurren independientemente de las acciones del usuario.

#figure(
  image("hci_before_and_after/Notificaciones/puerta-abierta.png", width: 60%),
  caption: [Notificación de evento: Puerta del frente abierta. Este tipo de notificación informa sobre cambios de estado de dispositivos monitoreados, especialmente útil para eventos de seguridad.],
)

Este sistema de notificaciones diferenciadas cubre todos los escenarios de comunicación sistema-usuario, asegurando que cada tipo de mensaje tenga el tratamiento visual y temporal adecuado según su importancia y naturaleza.

// ====================================
// 6. DECISIONES DE DISEÑO E IMPLEMENTACIÓN
// ====================================

= Decisiones de diseño e implementación

Esta sección detalla las decisiones adoptadas durante el desarrollo, fundamentadas en principios de usabilidad, requisitos técnicos y el feedback recibido en la primera entrega.

- Se eliminó la vista isométrica siguiendo la sugerencia del equipo docente, ya que se identificó que no aportaba utilidad práctica y representaba una característica meramente estética que no justificaba su complejidad técnica.

- Se separó el sistema de notificaciones persistentes (menú desplegable) del sistema de avisos temporales o _toasts_ (feedback de acciones). Los _toasts_ confirman acciones inmediatas del usuario ("Dispositivo guardado"), mientras que las notificaciones informan sobre eventos externos. Esta distinción mejora la visibilidad del estado del sistema al diferenciar el origen de la información.

== Decisiones de diseño

=== Asistente de rutinas en pasos

*Decisión:* La creación de rutinas se organizó en un asistente de 4 pasos: nombre, selección de dispositivos, configuración de acciones y resumen.

*Justificación:* En la etapa de prototipado, se observó que la configuración de rutinas podía resultar compleja. El asistente fragmenta la tarea en etapas simples con una progresión clara, reduciendo la carga cognitiva. El paso final de resumen permite verificar la configuración antes de confirmarla, funcionando como un mecanismo de prevención de errores.

*Beneficia especialmente a:* Marta (interfaces simples, flujos lineales) y Carolina (eficiencia en la gestión sin complejidad innecesaria).

=== Confirmación de acciones irreversibles

*Decisión:* Las acciones de eliminación requieren una confirmación explícita mediante un cuadro de diálogo.

*Justificación:* Esta medida busca prevenir errores accidentales. En el caso de las habitaciones, se informa adicionalmente sobre el impacto en los dispositivos vinculados para que el usuario tome una decisión informada.

*Beneficia especialmente a:* Marta (prevención de errores por confusión con la interfaz) y Carolina (protección de configuraciones de seguridad críticas).

=== Vista Overview como punto de acceso rápido

*Decisión:* Se diseñó la vista Overview para ofrecer un resumen del hogar con dispositivos favoritos, consumo y estado de actividad.

*Justificación:* Esta vista centraliza la información más consultada, permitiendo al usuario realizar acciones frecuentes sin necesidad de navegar por las distintas secciones. Esto favorece el reconocimiento sobre el recuerdo al presentar el estado general de forma inmediata.

*Beneficia especialmente a:* Carolina (monitoreo rápido del estado de seguridad del hogar) y Valentina (acceso directo a dispositivos y rutinas frecuentes sin navegación).

=== Respuesta inmediata en el control de dispositivos

*Decisión:* Los controles aplican el cambio visual instantáneamente mientras procesan la petición en segundo plano. En caso de error, el estado se revierte y se informa al usuario.

*Justificación:* La falta de respuesta inmediata puede generar incertidumbre sobre si la acción fue registrada. Esta técnica de "actualización optimista" mejora la percepción de control y visibilidad del estado, mientras que el manejo de errores asegura la consistencia entre la interfaz y el estado real del sistema.

*Beneficia especialmente a:* Todos los usuarios. Marta obtiene confirmación visual inmediata reduciendo incertidumbre, Valentina percibe mayor control directo, y Carolina confía en que las acciones de seguridad se ejecutaron correctamente.

=== Navegación y ubicación

*Decisión:* Se mantuvo la estructura de barra lateral consistente con el diseño original. La barra superior incluye _breadcrumbs_ para indicar la ubicación actual.

*Justificación:* Informar claramente al usuario sobre su ubicación dentro de la jerarquía de la aplicación facilita la navegación y proporciona libertad de movimiento con salidas de emergencia claras.

*Beneficia especialmente a:* Marta (orientación clara en la jerarquía de hogares/habitaciones/dispositivos) y Carolina (navegación eficiente entre múltiples hogares y secciones de seguridad).

=== Diseño responsivo (RNF8)

_Este requisito no funcional fue implementado parcialmente ya que no fue
testeado de forma exhaustiva, pero en su mayoria responde correctamente a
tamaños de pantalla mobile_

*Decisión:* Se implementó diseño responsivo con un breakpoint principal en 768px. Por debajo de ese ancho, la sidebar se oculta y el contenido ocupa el ancho completo. Se incorporó un botón de menú en la barra superior para acceder a la sidebar como overlay con backdrop semitransparente.

*Justificación:* Si bien el RNF3 obligatorio solo exige soporte para resoluciones de 1280px a 1920px, se optó por implementar el RNF8 opcional (diseño responsivo) por las siguientes razones:

- *Perfil de usuario:* En la primera entrega se definieron modelos de persona que incluyen usuarios que gestionan su hogar desde dispositivos móviles (por ejemplo, verificar el estado de los dispositivos fuera de casa). Una aplicación de domotica que solo funciona en escritorio limita su utilidad al contexto del hogar, lo cual contradice uno de sus principales beneficios: el control remoto.

- *Principio de flexibilidad y eficiencia:* La adaptación a distintos tamaños de pantalla permite que tanto usuarios novatos como expertos tengan una experiencia funcional sin degradación.

*Beneficia especialmente a:* Carolina (acceso remoto desde dispositivos móviles para verificar seguridad) y Valentina (control desde cualquier dispositivo cuando está fuera de casa).

#nota[
  Se tiene un detalle en el overview que en el breakpoint principal, se
  muestra el boton de menu de la sidebar, pero siendo que nuestra
  implementacion se centra en el desarrollo web y la aplicacion va a
  requerir una implementacion mobile aparte, decidimos que sea una correcion
  para el futuro. Es posible que haya otros posibles puntos en los que la
  aplicacion no se adapte perfectamente al tamaño mobile.
]

#pagebreak()

== Decisiones de implementación

Esta subsección documenta las decisiones técnicas de arquitectura y patrones de programación relevantes, sin profundizar en detalles exhaustivos de código.

=== Sistema de design tokens y paleta de colores

*Decisión:* Todos los colores, tamaños, espacos y radios se centralizaron en variables CSS (`variables.css`), eliminando valores hardcoded en los componentes.

*Justificación:* Esto garantiza consistencia visual de forma técnica: un cambio de color se propaga a toda la aplicación. Durante la implementación se detectaron valores hardcoded (por ejemplo, `#d32f2f` en vez de `var(--danger)`) lo que generaba inconsistencias y distintos tonos de colores a traves de toda la aplicacion.

La paleta de colores definida en la primera entrega se mantuvo fielmente en la implementación, garantizando coherencia visual entre el prototipo y la aplicación final.

#let swatch(color, nombre, hex) = {
  box(
    width: 100%,
    inset: 0pt,
    stack(
      dir: ttb,
      rect(fill: rgb(color), width: 100%, height: 32pt, radius: (top-left: 4pt, top-right: 4pt)),
      rect(
        fill: rgb("#1a1a24"),
        stroke: 0.5pt + rgb("#3a3a4a"),
        width: 100%,
        radius: (bottom-left: 4pt, bottom-right: 4pt),
        inset: 6pt,
      )[
        #set text(size: 8pt)
        #text(fill: rgb("#f1f5f9"), weight: "bold")[#nombre] \
        #text(fill: rgb("#8494a7"))[#hex]
      ],
    )
  )
}

#v(0.5em)
#text(size: 10pt, weight: "bold")[Fondos]
#grid(
  columns: (1fr, 1fr, 1fr),
  gutter: 8pt,
  swatch("#0f0f14", "Primary", "#0f0f14"),
  swatch("#1a1a24", "Secondary", "#1a1a24"),
  swatch("#252532", "Tertiary", "#252532"),
)

#v(0.3em)
#text(size: 10pt, weight: "bold")[Acentos]
#grid(
  columns: (1fr, 1fr, 1fr),
  gutter: 8pt,
  swatch("#818cf8", "Indigo", "#818cf8"),
  swatch("#a5b4fc", "Indigo Hover", "#a5b4fc"),
  swatch("#fbbf24", "Ámbar", "#fbbf24"),
)

#v(0.3em)
#text(size: 10pt, weight: "bold")[Semánticos]
#grid(
  columns: (1fr, 1fr, 1fr),
  gutter: 8pt,
  swatch("#34d399", "Success", "#34d399"),
  swatch("#f87171", "Danger", "#f87171"),
  swatch("#3a3a4a", "Border", "#3a3a4a"),
)

#v(0.3em)
#text(size: 10pt, weight: "bold")[Texto]
#grid(
  columns: (1fr, 1fr, 1fr),
  gutter: 8pt,
  swatch("#f1f5f9", "Primary", "#f1f5f9"),
  swatch("#b0bdd0", "Secondary", "#b0bdd0"),
  swatch("#8494a7", "Muted", "#8494a7"),
)

=== Arquitectura CSS: módulos globales y estilos encapsulados

*Decisión:* Se implementó un esquema híbrido que combina una base de CSS global organizado por categorías (`variables.css`, `buttons.css`, `forms.css`, etc.) con estilos específicos encapsulados mediante el atributo `scoped` en cada componente Vue.

*Justificación:* Esta arquitectura se seleccionó para resolver los desafíos de escalabilidad y mantenibilidad, basándose en los siguientes pilares técnicos:

- *Sistema de Design Tokens:* El uso de variables CSS centralizadas actúa como una "única fuente de verdad" para la identidad visual. Esto asegura que cualquier cambio en la paleta de colores, tipografía o espaciado se propague instantáneamente a toda la aplicación, garantizando una consistencia visual absoluta.
- *Aislamiento de efectos colaterales:* El uso de `<style scoped>` garantiza que las reglas CSS de un componente no "filtren" hacia el resto de la interfaz. Esto elimina los conflictos en el espacio de nombres global, permitiendo realizar ajustes locales de layout con la seguridad de no romper otras secciones de la aplicación.
- *Separación de Responsabilidades:* Se distingue claramente entre la *identidad visual compartida* (estilos globales) y el *posicionamiento específico* (estilos locales). Mientras que los archivos globales definen qué es un botón o una tarjeta, el componente decide cómo se integra ese elemento en su propia estructura.
- *Optimización y Performance:* Al evitar frameworks de UI pesados, el tamaño del bundle se mantiene mínimo. Vue se encarga de inyectar y retirar los estilos _scoped_ dinámicamente según el ciclo de vida de los componentes, optimizando el consumo de recursos en el navegador.
- *Flexibilidad sin abstracciones innecesarias:* En lugar de crear componentes "wrapper" sin lógica propia (ej. un `<BaseButton>` que solo aplica una clase), se optó por composición de clases CSS. Esto reduce la profundidad del árbol de componentes y simplifica el desarrollo sin sacrificar la estandarización.

Los componentes de UI se reservaron exclusivamente para casos que requieren lógica de estado o comportamiento interactivo, como `ToggleSwitch` o `DeviceCard`.

=== Patrones de implementación técnica

*CSS manual vs. frameworks:* Se optó por CSS puro organizado en módulos sin utilizar frameworks como Vuetify, priorizando control sobre el sistema de diseño, menor tamaño de bundle (~25KB vs 200-500KB) y separación clara de responsabilidades.

*Diseño responsivo:* El estado de la sidebar mobile se gestiona mediante un composable singleton (`useSidebar.js`) que expone un `ref` reactivo compartido, siguiendo el mismo patrón de otros composables de la aplicación.

*Manejo de expiración de token JWT:* Se implementó un interceptor global que detecta respuestas HTTP 401, limpia el token expirado y redirige automáticamente al login con mensaje explicativo.

=== Patrones de optimización

*Actualización optimista con rollback:* Los controles de dispositivos aplican cambios visuales inmediatamente y envían la petición en paralelo. Si falla, el estado se revierte automáticamente.

*Consolidación de código:* Se unificaron vistas duplicadas de rutinas globales y específicas, reduciendo ~700 líneas de código mediante detección del tipo por metadata.

*Prevención de memory leaks:* Se implementaron mecanismos de limpieza explícitos para intervalos y estructuras de datos que persisten entre navegaciones.

*Manejo de errores:* Todos los formularios utilizan bloques `try/catch/finally` para garantizar que el estado de carga se restablece incluso si las peticiones fallan.

*Deduplicación de notificaciones:* Sistema de ventana deslizante de 2 segundos para filtrar eventos redundantes del WebSocket.

#pagebreak()

// ====================================
// 7. DIFERENCIAS CON EL PROTOTIPO
// ====================================


= Diferencias respecto al prototipo

Durante la implementación se realizaron ajustes respecto al diseño original para adaptarlo a las capacidades técnicas de la API y mejorar la usabilidad.

== Ajustes en la vista de Hogar

*Decisión:* Se priorizó el uso de tarjetas de resumen y métricas clave sobre la representación gráfica del plano de la casa.

*Justificación:* El diseño original contemplaba un plano isométrico con la ubicación espacial de los dispositivos. Dado que la API no proporciona datos de posicionamiento, se optó por un enfoque basado en tarjetas de información. Esto asegura que el usuario reciba datos precisos y accionables sobre sus dispositivos favoritos y el consumo energético, manteniendo la claridad informativa para usuarios como *Carolina*, que buscan monitorear su hogar rápidamente.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/overview/oold.png", width: 100%),
    image("hci_before_and_after/overview/onew.png", width: 100%),
  ),
  caption: [Home Overview: prototipo isométrico (izq.) vs. implementación final (der.)],
)

== Simplificación de roles de usuario

*Decisión:* Se implementó un modelo de usuario con acceso completo, posponiendo la gestión de perfiles con permisos restringidos.

*Justificación:* Se priorizó la robustez de las funcionalidades principales de gestión de dispositivos y rutinas. El modelo actual simplifica la interacción para el usuario principal sin comprometer la capacidad de control del sistema.

== Evolución de los controles de dispositivos

*Decisión:* Se reemplazaron algunos selectores binarios (on/off) por paneles de acciones contextuales para dispositivos con múltiples estados.

*Justificación:* Dispositivos como cortinas o sistemas de audio poseen estados intermedios que no se representan adecuadamente con un interruptor simple. El uso de botones para acciones específicas (subir/bajar, reproducir/pausar) mejora la correspondencia con el mundo real y hace la interacción más intuitiva para perfiles como *Marta*. El diseño actual refleja el modelo de interacción familiar para sistemas multimedia y de automatización, reforzando la coherencia entre el sistema y el mundo real.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/boton de la cortina/bold.png", width: 100%),
    image("hci_before_and_after/boton de la cortina/bnew.png", width: 100%),
  ),
  caption: [Control de cortinas: El interruptor binario del prototipo evolucionó hacia un panel de acciones contextuales.],
)

== Introducción de la vista de Resumen Global (Overview)

*Decisión:* Se incorporó una pantalla de bienvenida que ofrece una visión general de todas las propiedades y dispositivos críticos antes de profundizar en un hogar específico.

*Justificación:* Esta vista permite al usuario identificar rápidamente estados de alerta o ejecutar rutinas frecuentes sin necesidad de navegar por cada propiedad individualmente. Esto reduce el esfuerzo necesario para obtener una visión general del sistema, potenciando el reconocimiento sobre el recuerdo y la eficiencia de uso.

// ====================================
// 8. FEEDBACK DE LA PRIMERA ENTREGA
// ====================================

= Feedback de la primera entrega

== Contraste de colores y legibilidad

*Feedback:* "El contraste entre ciertos elementos de texto y sus fondos debería mejorarse para garantizar mejor legibilidad."

*Resolución:* Se mantuvo y mejoró el contraste de colores en toda la aplicación para asegurar una legibilidad óptima. La paleta de colores definida en la primera entrega ya cumplía con los estándares WCAG AA, pero durante la implementación se validó sistemáticamente que todos los pares texto-fondo mantuvieran ratios de contraste adecuados. Se prestó especial atención a elementos críticos como botones de acción, etiquetas de estado de dispositivos y mensajes de error, asegurando que la información importante sea claramente legible en todas las condiciones de uso. Esta decisión refuerza el principio de accesibilidad y garantiza que usuarios con diferentes capacidades visuales puedan interactuar efectivamente con el sistema, cumpliendo con el compromiso de diseño inclusivo establecido desde la primera entrega.

== Vista isométrica del hogar

*Feedback:* "La vista isométrica 3D del hogar no aporta utilidad práctica y representa complejidad técnica innecesaria."

*Resolución:* Siguiendo la sugerencia de la cátedra, se eliminó completamente la vista isométrica 3D que formaba parte del diseño original. Esta decisión se fundamentó en que la representación gráfica del plano de la casa, aunque visualmente atractiva, no proporcionaba información funcional que justificara su complejidad de implementación y mantenimiento. En su lugar, se priorizó una presentación más directa y eficiente de la información mediante tarjetas de resumen, métricas clave y listas de dispositivos favoritos.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/global overview/goold.png", width: 100%),
    image("hci_before_and_after/global overview/gonew.png", width: 100%),
  ),
  caption: [Resumen Global: prototipo con vista isométrica (izq.) vs. implementación sin vista isométrica (der.). La nueva versión prioriza métricas y favoritos en lugar de la representación gráfica del plano.],
)

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/overview/oold.png", width: 100%),
    image("hci_before_and_after/overview/onew.png", width: 100%),
  ),
  caption: [Vista del Hogar: prototipo con vista isométrica (izq.) vs. implementación con tarjetas de dispositivos y estado (der.). El cambio mejora la velocidad de carga y el acceso directo a la información relevante.],
)

Este enfoque mejora la velocidad de carga de la vista principal, reduce la complejidad del código y mantiene el foco en la funcionalidad real del sistema: el control y monitoreo de dispositivos. La eliminación de este elemento decorativo refuerza el principio de diseño minimalista, donde cada componente de la interfaz debe cumplir un propósito claro en la experiencia del usuario.

== Tamaño de gráficos de consumo energético

*Feedback:* "Los gráficos de consumo energético ocupan demasiado espacio visual y deberían reducirse para mejorar la densidad de información."

*Resolución:* Siguiendo la recomendación de la cátedra, se ajustó el tamaño de los gráficos de consumo energético para optimizar el uso del espacio disponible. Los gráficos de torta y barras se redimensionaron manteniendo su legibilidad pero permitiendo que más información sea visible sin necesidad de desplazamiento vertical excesivo.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/consumo/cold.png", width: 100%),
    image("hci_before_and_after/consumo/cnew.png", width: 100%),
  ),
  caption: [Visualización de consumo energético: prototipo con gráficos de gran tamaño (izq.) vs. implementación con gráficos optimizados (der.). La reducción de escala permite visualizar múltiples métricas simultáneamente sin comprometer la legibilidad.],
)

Esta modificación mejora la densidad de información en la pantalla y reduce la necesidad de scroll, facilitando la comparación visual entre diferentes dispositivos y períodos de tiempo. El ajuste mantiene todos los elementos críticos del gráfico (etiquetas, valores, leyendas) completamente legibles mientras aprovecha mejor el espacio disponible.

== Gráficos de consumo en el overview global

*Feedback:* "Los gráficos de consumo en la vista de resumen global no aportan valor en ese contexto y aumentan la carga visual innecesariamente."

*Resolución:* Se eliminaron los gráficos de consumo energético de la vista de overview global. Esta decisión reconoce que el propósito principal del overview es proporcionar acceso rápido a todos los hogares del usuario y presentar métricas de alto nivel, no análisis detallado de consumo. Los gráficos son información secundaria que corresponde a una sección específica dedicada al análisis de consumo.

#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/global overview/goold.png", width: 100%),
    image("hci_before_and_after/global overview/gonew.png", width: 100%),
  ),
  caption: [Resumen Global: prototipo con gráficos de consumo (izq.) vs. implementación sin gráficos (der.). La nueva versión se enfoca en shortcuts y acceso rápido a las casas, eliminando información que distraía del propósito principal de la vista.],
)

Al remover los gráficos del overview, la interfaz se vuelve más limpia y enfocada en su función principal: permitir al usuario seleccionar rápidamente el hogar con el que desea interactuar y visualizar métricas clave de un vistazo (cantidad de dispositivos, alertas, favoritos). Esta simplificación reduce el tiempo de carga inicial y mejora la jerarquía visual, guiando al usuario hacia las acciones más frecuentes sin sobrecargarlo con análisis detallados que puede consultar cuando los necesite en la sección dedicada de Consumo.

// ====================================
// 9. CONCLUSIÓN
// ====================================

= Conclusión

Esta segunda entrega representa la materialización funcional de HomeCore, transformando los prototipos de alta fidelidad de la primera entrega en una aplicación web completamente operativa. El resultado es un sistema de gestión domótica que mantiene la fidelidad visual y conceptual del diseño original, mientras resuelve los desafíos técnicos a la implementación real.

El desarrollo se fundamentó en principios de usabilidad establecidos, particularmente las heurísticas de Nielsen, que guiaron cada decisión de implementación. La arquitectura técnica adoptada Vue.js con la API provista, Pinia para estado global, y un sistema de CSS modular demostró ser efectiva para mantener la consistencia visual y la escalabilidad del código.

Los requisitos funcionales obligatorios fueron implementados en su totalidad, junto con varios opcionales que enriquecen la experiencia de usuario: notificaciones en tiempo real mediante WebSocket y visualización de consumo energético con gráficos. Esta implementación responde a las necesidades identificadas en la investigación de usuarios de la primera entrega, donde se evidenció la importancia del acceso remoto y la visibilidad del consumo.

Las decisiones de diseño documentadas en este informe reflejan un proceso iterativo desde la consolidación de código para eliminar duplicación, hasta la implementación de patrones como actualización con rollback que mejoran la percepción de control del usuario. Cada decisión técnica fue evaluada por su funcionalidad e impacto en la experiencia de uso.

El feedback recibido en la primera entrega fue incorporado, evidenciando un ciclo de diseño centrado en el usuario. La reubicación de la configuración de usuario, respondiendo a observaciones sobre su baja visibilidad, ejemplifica cómo las evaluaciones empíricas informan mejoras concretas en la interfaz.

Los desafíos enfrentados durante la implementación resultaron en soluciones que fortalecieron la arquitectura general del sistema. La documentación detallada de estos problemas y sus resoluciones (disponible en el historial de git y en el archivo de troubleshooting) constituye un recurso valioso para futuras iteraciones y mantenimiento.

En términos de cumplimiento normativo, la aplicación satisface todos los requisitos no funcionales especificados: validación HTML/CSS sin errores, separación de responsabilidades estructura/presentación/comportamiento, compatibilidad con navegadores modernos, y accesibilidad básica verificada con WAVE.

== Trabajo futuro

Si bien esta entrega cumple con los objetivos establecidos, se identifican oportunidades de mejora para futuras iteraciones:

- *Planificación automática de rutinas:* La funcionalidad de configuración horaria está implementada en el frontend, pero requiere un componente de backend (cron job o scheduler) para ejecutar rutinas automáticamente sin intervención del usuario.

- *Optimización de carga progresiva:* Aunque se implementó lazy loading de rutas, podría explorarse la carga progresiva de imágenes y datos en vistas con alta densidad de información.

- *Análisis de consumo histórico:* Expandir la vista de consumo para incluir comparativas temporales (semana actual vs. anterior, proyecciones mensuales) agregaría valor analítico.

- *Responsiveness para mobile:* Como se menciono, si bien la aplicacion deberia responder a tamaños de pantalla inferiores a los solicitados, 
  deberian realizarse testeos y correcciones para poder afirmarlo con seguridad
