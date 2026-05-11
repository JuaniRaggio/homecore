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

= Modelos de Persona (Contexto)

Para fundamentar las decisiones de diseño, se utilizaron los siguientes modelos de usuario:

-- *Valentina "La Power User" (27 años):* Nivel tecnológico avanzado. Busca automatizaciones complejas, control granular de dispositivos y análisis de datos.
-- *Carolina "La Gestora del Hogar" (42 años):* Nivel tecnológico intermedio. Su prioridad es la seguridad familiar y la eficiencia en la gestión del hogar.
-- *Marta "La Usuaria Tradicional" (63 años):* Nivel tecnológico básico. Prefiere interfaces simples, flujos lineales y botones grandes con etiquetas claras.


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

-- *Registrar cuenta (RF1):* El sistema permite crear una cuenta nueva ingresando nombre, correo electrónico y contraseña. Al registrarse exitosamente, el usuario es redirigido al flujo de verificación. Se implementaron validaciones en el lado del cliente para asegurar la integridad de los datos.
-- *Verificar cuenta (RF2):* Tras el registro, el sistema envía un correo de verificación al usuario para validar su dirección. El usuario debe ingresar un código de 4 dígitos en una vista dedicada para activar su cuenta.
-- *Recuperar contraseña (RF3):* El sistema implementa un flujo de recuperación de contraseña en tres pasos: ingreso de correo para recibir un código, validación del código y establecimiento de la nueva contraseña.
-- *Cambiar contraseña (RF4):* Un usuario autenticado puede actualizar su contraseña desde la sección de configuración, requiriendo la contraseña actual para validar la identidad y la nueva para el cambio.
-- *Iniciar sesión (RF5):* El sistema permite la autenticación mediante correo y contraseña. La sesión se mantiene mediante un token (JWT) que se adjunta automáticamente a las solicitudes. Se implementó un interceptor para manejar la expiración del token (error 401), redirigiendo al usuario al login con un mensaje explicativo para mantener la consistencia del estado.
-- *Cerrar sesión (RF6):* El usuario puede finalizar su sesión desde el menú de perfil. Esto elimina el token local, desconecta el WebSocket y redirige a la pantalla de inicio.

// TODO: Agregar capturas de login, registro, verificación y recuperación
// #figure(image("assets/auth-login.png", width: 80%), caption: [Pantalla de login])

== Gestión de dispositivos (RF7--RF9)

-- *Gestionar dispositivos (RF7):* El usuario puede crear dispositivos asignándoles un nombre y tipo, renombrarlos, editarlos y eliminarlos. Toda eliminación requiere confirmación explícita para evitar acciones accidentales.
-- *Consultar dispositivos (RF8):* El sistema presenta un listado de todos los dispositivos del hogar mostrando nombre, tipo, estado actual y habitación asignada. Además, existe una vista de detalle por dispositivo que expone su estado completo y sus controles específicos, actualizada en tiempo real mediante WebSocket.
-- *Controlar dispositivos (RF9):* Se implementaron controles específicos para los 11 tipos de dispositivos soportados por la API, incluyendo lámparas (brillo, color), puertas (abrir/cerrar/bloquear), alarmas (modos ausente/en casa), aire acondicionado (temperatura, modos, ventilador), parlantes (volumen, género, reproducción), aspiradoras (modos, ubicación), heladeras y hornos.

// TODO: Agregar capturas de dispositivos, detalle, edición
// #figure(image("assets/devices-list.png", width: 100%), caption: [Vista de dispositivos])

== Rutinas (RF10--RF12)

-- *Gestionar rutinas (RF10):* El usuario puede crear rutinas definiendo un nombre, los días de la semana, la hora de activación y una secuencia de acciones sobre distintos dispositivos. Las rutinas pueden ser editadas y eliminadas en cualquier momento.
-- *Consultar rutinas (RF11):* El sistema presenta un listado de todas las rutinas del hogar en formato de tarjetas, mostrando nombre, días configurados y un resumen de las acciones vinculadas.
-- *Ejecutar rutinas (RF12):* Desde la lista o el detalle de una rutina, el usuario puede ejecutarla manualmente. El sistema dispara en el backend todas las acciones definidas de forma coordinada.

// TODO: Agregar capturas de rutinas, wizard
// #figure(image("assets/routines.png", width: 100%), caption: [Vista de rutinas])

== Historial (RF13)

El sistema presenta un registro paginado de todas las acciones ejecutadas sobre los dispositivos del hogar en la vista “Historial”, incluyendo el nombre del dispositivo, la acción realizada y la marca temporal de cada evento. Los registros se obtienen de la API y se presentan ordenados cronológicamente para facilitar la auditoría.

// TODO: Agregar captura de historial
// #figure(image("assets/history.png", width: 100%), caption: [Vista de historial])

== Habitaciones (RF14--RF16)

-- *Gestionar habitaciones (RF14):* El usuario puede crear habitaciones dentro de un hogar, renombrarlas y eliminarlas. Toda eliminación requiere confirmación explícita para asegurar la consistencia del sistema.
-- *Consultar habitaciones (RF15):* El sistema lista las habitaciones del hogar seleccionado. Al ingresar al detalle de una habitación, se visualizan los dispositivos que contiene y se puede acceder directamente al control de cada uno.
-- *Vincular dispositivos a habitaciones (RF16):* El usuario puede asignar o mover un dispositivo a una habitación distinta dentro del mismo hogar. Se garantiza que cada dispositivo esté correctamente ubicado dentro de la jerarquía espacial del hogar.

// TODO: Agregar capturas de habitaciones
// #figure(image("assets/rooms.png", width: 100%), caption: [Vista de habitaciones])

== Hogares (RF17--RF19)

-- *Gestionar hogares (RF17):* El sistema permite crear hogares con un nombre identificatorio y dirección, editarlos y eliminarlos. Adicionalmente, se implementó la posibilidad de compartir un hogar con otros usuarios mediante su correo electrónico.
-- *Consultar hogares (RF18):* El sistema presenta un panel general con todos los hogares a los que tiene acceso el usuario (propios y compartidos), junto con un resumen de métricas clave y dispositivos favoritos.
-- *Vincular habitaciones a hogares (RF19):* Las habitaciones se crean y gestionan dentro del contexto de un hogar específico, manteniendo la jerarquía estructural y de navegación en todo momento.

// TODO: Agregar capturas de overview y nueva propiedad
// #figure(image("assets/overview.png", width: 100%), caption: [Vista Overview])

== Notificaciones -- opcional (RF20)

Se implementó un sistema de notificaciones en tiempo real utilizando Socket.io. El frontend recibe eventos del servidor sobre cambios en los dispositivos o en la configuración del hogar, los almacena y los presenta en un menú desplegable accesible desde la barra superior. El sistema notifica al usuario cuando un dispositivo cambia de estado, permitiendo un monitoreo continuo.

Los eventos procesados son:

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
  [`deviceEvent`], [Un dispositivo cambió de estado (encendido/apagado, brillo, etc.). El cambio se refleja inmediatamente en la interfaz.],
  [`homeShared`], [Se compartió un hogar con el usuario.],
  [`homeUnshared`], [Se revocó el acceso a un hogar.],
)

Cada notificación incluye un título, mensaje descriptivo y marca temporal. Los usuarios pueden gestionarlas marcándolas como leídas de forma individual o masiva.

== Restricción de acceso -- opcional (RF21)

El sistema garantiza que cada usuario solo pueda visualizar y operar sobre los hogares, habitaciones, dispositivos y rutinas a los que tiene acceso autorizado. Para dispositivos que requieren mayor seguridad, como alarmas o cerraduras, se implementó el uso de códigos de seguridad para autorizar acciones críticas.

== Consumo energético -- opcional (RF22)

El sistema presenta gráficos de consumo eléctrico de los dispositivos del hogar, permitiendo al usuario visualizar el uso energético de forma agregada y detallada por dispositivo. Se incluyen visualizaciones dinámicas (gráficos de torta y barras) para facilitar la interpretación de los datos de consumo en tiempo real.

== Planificar ejecución de rutinas -- opcional (RF23)

Al crear o editar una rutina, el usuario puede configurar los días de la semana y la hora exacta en que debe ejecutarse automáticamente. Esto permite la automatización total de tareas frecuentes sin necesidad de intervención manual periódica.

= Decisiones de diseño e implementación

// ====================================
// 4. REQUISITOS NO FUNCIONALES
// ====================================

= Requisitos no funcionales

-- *Tecnologías utilizadas (RNF1):* El frontend fue desarrollado con Vue.js 3 utilizando Composition API, Pinia para el estado y Vue Router. Se emplea Vite como herramienta de construcción y Socket.io para comunicación en tiempo real. Para visualización se integró Chart.js.
-- *Compatibilidad con Navegadores (RNF2):* La aplicación es compatible con Google Chrome, Mozilla Firefox, Microsoft Edge y Safari en sus versiones más recientes.
-- *Adaptación a resoluciones de pantalla (RNF3):* La interfaz fue optimizada para pantallas de escritorio y laptops (1280px a 1920px), asegurando un layout estable y legible en el entorno de uso principal.
-- *Separación de estructura y presentación (RNF4):* Se respeta la separación entre HTML y CSS, utilizando una arquitectura de estilos modular y scoped para evitar estilos inline y asegurar la mantenibilidad.
-- *Separación de estructura y comportamiento (RNF5):* La lógica de negocio y comportamiento se extrae de los componentes hacia servicios y stores independientes, manteniendo los templates enfocados en la estructura de la interfaz.
-- *Validación de HTML (RNF6):* El marcado generado cumple con los estándares del W3C, habiendo sido validado sin errores ni advertencias.
-- *Diseño responsivo (RNF8):* Aunque el foco principal fue el escritorio, se implementaron adaptaciones para resoluciones móviles, permitiendo una navegación fluida en distintos tamaños de pantalla.

// ====================================
// 5. CAPTURAS DE PANTALLA
// ====================================

= Capturas de pantalla

En esta sección se presenta la comparación entre el prototipo de alta fidelidad (primera entrega) y la implementación funcional definitiva, destacando la evolución estética y técnica del sistema.

== Resumen Global (Global Overview)
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/global overview/goold.png", width: 100%),
    image("hci_before_and_after/global overview/gonew.png", width: 100%),
  ),
  caption: [Global Overview: prototipo (izq.) vs. implementación (der.)],
)

== Resumen del Hogar (Home Overview)
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/overview/oold.png", width: 100%),
    image("hci_before_and_after/overview/onew.png", width: 100%),
  ),
  caption: [Home Overview: prototipo (izq.) vs. implementación (der.)],
)

== Habitaciones
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/habitaciones/haold.png", width: 100%),
    image("hci_before_and_after/habitaciones/hanew.png", width: 100%),
  ),
  caption: [Gestión de habitaciones: prototipo (izq.) vs. implementación (der.)],
)

== Historial de acciones
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/historial/hold.png", width: 100%),
    image("hci_before_and_after/historial/hnew.png", width: 100%),
  ),
  caption: [Historial: prototipo (izq.) vs. implementación (der.)],
)

== Consumo energético
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/consumo/cold.png", width: 100%),
    image("hci_before_and_after/consumo/cnew.png", width: 100%),
  ),
  caption: [Métricas de consumo: prototipo (izq.) vs. implementación (der.)],
)

== Configuración de usuario
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/config/coold.png", width: 100%),
    image("hci_before_and_after/config/conew.png", width: 100%),
  ),
  caption: [Ajustes de perfil: prototipo (izq.) vs. implementación (der.)],
)

== Evolución de controles de dispositivos (Cortinas)
#figure(
  grid(columns: 2, gutter: 12pt,
    image("hci_before_and_after/boton de la cortina/bold.png", width: 100%),
    image("hci_before_and_after/boton de la cortina/bnew.png", width: 100%),
  ),
  caption: [Control de cortinas: El interruptor binario del prototipo evolucionó hacia un panel de acciones contextuales más intuitivo para estados intermedios.],
)

== Notificaciones

// TODO: dropdown, toasts

// ====================================
// 6. DECISIONES DE DISEÑO E IMPLEMENTACIÓN
// ====================================

= Decisiones de diseño e implementación

En esta sección se detallan las decisiones adoptadas durante el desarrollo, fundamentadas en principios de usabilidad, requisitos técnicos y el feedback recibido en la primera entrega.

- Se eliminó la vista isométrica siguiendo la sugerencia del equipo docente, ya que se identificó que no aportaba utilidad práctica y representaba una característica meramente estética que no justificaba su complejidad técnica.

- Se separó el sistema de notificaciones persistentes (menú desplegable) del sistema de avisos temporales o _toasts_ (feedback de acciones). Los _toasts_ confirman acciones inmediatas del usuario ("Dispositivo guardado"), mientras que las notificaciones informan sobre eventos externos. Esta distinción mejora la visibilidad del estado del sistema al diferenciar el origen de la información.

== Arquitectura CSS: módulos globales y estilos encapsulados (_scoped_)

*Decisión:* Se implementó un esquema híbrido que combina una base de CSS global organizado por categorías (`variables.css`, `buttons.css`, `forms.css`, etc.) con estilos específicos encapsulados mediante el atributo `scoped` en cada componente Vue.

*Justificación:* Esta arquitectura se seleccionó para resolver los desafíos de escalabilidad y mantenibilidad, basándose en los siguientes pilares técnicos:

-- *Sistema de Design Tokens:* El uso de variables CSS centralizadas actúa como una "única fuente de verdad" para la identidad visual. Esto asegura que cualquier cambio en la paleta de colores, tipografía o espaciado se propague instantáneamente a toda la aplicación, garantizando una consistencia visual absoluta.
-- *Aislamiento de efectos colaterales:* El uso de `<style scoped>` garantiza que las reglas CSS de un componente no "filtren" hacia el resto de la interfaz. Esto elimina los conflictos en el espacio de nombres global, permitiendo realizar ajustes locales de layout con la seguridad de no romper otras secciones de la aplicación.
-- *Separación de Responsabilidades:* Se distingue claramente entre la *identidad visual compartida* (estilos globales) y el *posicionamiento específico* (estilos locales). Mientras que los archivos globales definen qué es un botón o una tarjeta, el componente decide cómo se integra ese elemento en su propia estructura.
-- *Optimización y Performance:* Al evitar frameworks de UI pesados, el tamaño del bundle se mantiene mínimo. Vue se encarga de inyectar y retirar los estilos _scoped_ dinámicamente según el ciclo de vida de los componentes, optimizando el consumo de recursos en el navegador.
-- *Flexibilidad sin abstracciones innecesarias:* En lugar de crear componentes "wrapper" sin lógica propia (ej. un `<BaseButton>` que solo aplica una clase), se optó por composición de clases CSS. Esto reduce la profundidad del árbol de componentes y simplifica el desarrollo sin sacrificar la estandarización.

Los componentes de UI se reservaron exclusivamente para casos que requieren lógica de estado o comportamiento interactivo, como `ToggleSwitch` o `DeviceCard`.

== Sistema de design tokens

*Decisión:* Todos los colores, tamaños, espacios y radios se centralizaron en variables CSS (`variables.css`), eliminando valores hardcoded en los componentes.

*Justificación:* Esto garantiza consistencia visual de forma técnica: un cambio de color se propaga a toda la aplicación. Durante la implementación se detectaron valores hardcoded (por ejemplo, `#d32f2f` en vez de `var(--danger)`) que fueron corregidos en una pasada de auditoría. Se agregaron las variables `--warning`, `--danger-bg` y `--success-bg` que no estaban previstas en el prototipo original pero resultaron necesarias para estados intermedios.

== Controles específicos por tipo de dispositivo

*Decisión:* Cada tipo de dispositivo cuenta con un componente de controles dedicado (`LightControls`, `DoorControls`, `CurtainControls`, `AlarmControls`, `WaterControls`) que se renderiza condicionalmente en la vista de detalle.

*Justificación:* Los dispositivos domésticos requieren interacciones diversas: una luminaria se gestiona mediante niveles de brillo y selección de color, mientras que una puerta requiere acciones directas (abrir, cerrar o bloquear). Unificar estos controles en un componente genérico podría afectar la claridad del modelo mental del usuario al presentar opciones irrelevantes para el dispositivo en uso, rompiendo la correspondencia entre el sistema y el mundo real.

== Notificaciones: persistentes vs. temporales

*Decisión:* Se diferenciaron las notificaciones persistentes (eventos del sistema) de los avisos temporales o _toasts_ (confirmación de acciones).

*Justificación:* Mezclar ambos tipos podría generar confusión sobre la naturaleza de la información. Los _toasts_ confirman una acción inmediata ("Dispositivo guardado") y desaparecen automáticamente, minimizando la interrupción. Las notificaciones del menú informan sobre eventos externos y persisten hasta ser gestionadas por el usuario. Esta separación prioriza la visibilidad del estado del sistema sin sobrecargar la interfaz con información irrelevante, manteniendo un diseño estético y minimalista.

== Asistente de rutinas en pasos

*Decisión:* La creación de rutinas se organizó en un asistente de 4 pasos: nombre, selección de dispositivos, configuración de acciones y resumen.

*Justificación:* En la etapa de prototipado, se observó que la configuración de rutinas podía resultar compleja. El asistente fragmenta la tarea en etapas simples con una progresión clara, reduciendo la carga cognitiva. El paso final de resumen permite verificar la configuración antes de confirmarla, funcionando como un mecanismo de prevención de errores.

== Confirmación de acciones irreversibles

*Decisión:* Las acciones de eliminación requieren una confirmación explícita mediante un cuadro de diálogo.

*Justificación:* Esta medida busca prevenir errores accidentales. En el caso de las habitaciones, se informa adicionalmente sobre el impacto en los dispositivos vinculados para que el usuario tome una decisión informada.

== Vista Overview como punto de acceso rápido

*Decisión:* Se diseñó la vista Overview para ofrecer un resumen del hogar con dispositivos favoritos, consumo y estado de actividad.

*Justificación:* Esta vista centraliza la información más consultada, permitiendo al usuario realizar acciones frecuentes sin necesidad de navegar por las distintas secciones. Esto favorece el reconocimiento sobre el recuerdo al presentar el estado general de forma inmediata.

== Respuesta inmediata en el control de dispositivos

*Decisión:* Los controles aplican el cambio visual instantáneamente mientras procesan la petición en segundo plano. En caso de error, el estado se revierte y se informa al usuario.

*Justificación:* La falta de respuesta inmediata puede generar incertidumbre sobre si la acción fue registrada. Esta técnica de "actualización optimista" mejora la percepción de control y visibilidad del estado, mientras que el manejo de errores asegura la consistencia entre la interfaz y el estado real del sistema.

== Navegación y ubicación

*Decisión:* Se mantuvo la estructura de barra lateral consistente con el diseño original. La barra superior incluye _breadcrumbs_ para indicar la ubicación actual.

*Justificación:* Informar claramente al usuario sobre su ubicación dentro de la jerarquía de la aplicación facilita la navegación y proporciona libertad de movimiento con salidas de emergencia claras.

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

-- *Principio de flexibilidad y eficiencia:* La adaptación a distintos tamaños de pantalla permite que tanto usuarios novatos como expertos tengan una experiencia funcional sin degradación.

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

== Manejo de expiración de token JWT

*Decisión:* Se implementó un interceptor global en `client.js` que detecta respuestas HTTP 401, limpia el token expirado del `localStorage` y redirige automáticamente al login con mensaje explicativo.

*Justificación:* Los tokens JWT tienen fecha de expiración. Sin manejo explícito, cuando el token expira todas las requests fallan silenciosamente, dejando al usuario en un estado inconsistente donde la aplicación parece funcionar pero ninguna acción se ejecuta. El interceptor global previene errores al detectar el problema de forma centralizada y guiar al usuario hacia la solución (volver a iniciar sesión). El mensaje "Tu sesión ha expirado. Por favor, inicia sesión nuevamente" informa claramente qué ocurrió y qué debe hacer, reforzando la visibilidad del estado del sistema.

== Actualización optimista con rollback

*Decisión:* Los controles de dispositivos (toggle on/off, favoritos) aplican el cambio visual inmediatamente y envían la petición a la API en paralelo. Si la API falla, el estado se revierte y se muestra un toast de error.

*Justificación:* En las observaciones de la primera entrega se identificó que "la falta de feedback tras activar una acción generó mucha incertidumbre". El patrón de actualización optimista elimina el delay perceptible entre la acción del usuario y la respuesta visual, mejorando la sensación de control directo y la visibilidad del estado. Sin embargo, aplicar el cambio sin validación podría generar inconsistencias si la API falla. Por eso se implementó rollback: si la petición falla, el estado visual se revierte al original y se notifica al usuario del error, manteniendo la coherencia entre el modelo y la vista. Este patrón se implementó en `toggleDevice()` y `toggleFavorite()` del store de dispositivos.

== Consolidación de vistas de rutinas globales y específicas

*Decisión:* Se unificaron las vistas de detalle y edición de rutinas en componentes únicos que manejan tanto rutinas específicas de un hogar como rutinas globales (compartidas entre hogares), detectando el tipo mediante metadata.

*Justificación:* En una versión intermedia existían cuatro vistas separadas: `RoutineDetailView`, `GlobalRoutineDetailView`, `EditRoutineView` y `GlobalRoutineEditView`. Las vistas globales y específicas compartían ~90% del código, difiriendo solo en el origen de los datos (store local vs. composable de overview) y la navegación post-guardado. Mantener cuatro archivos generaba duplicación de lógica, incrementando el riesgo de bugs por inconsistencia y dificultando el mantenimiento. La consolidación aplica el principio DRY (Don't Repeat Yourself): un computed `isGlobal` detecta el tipo de rutina y condiciona el comportamiento específico (carga de dispositivos, nombres con resolución de colisión, rutas de navegación). Esto redujo ~700 líneas de código duplicado sin comprometer la funcionalidad.

== Prevención de memory leaks en stores y WebSocket

*Decisión:* Se implementaron mecanismos de limpieza explícitos para recursos que persisten más allá del ciclo de vida del componente: `clearInterval()` para el muestreo de consumo diario y limpieza periódica del Map de deduplicación de notificaciones.

*Justificación:* JavaScript no limpia automáticamente referencias a callbacks registrados con `setInterval` ni entradas en estructuras como Map. En una versión intermedia, el store de dispositivos iniciaba un intervalo para acumular consumo cada minuto pero nunca lo detenía, causando que múltiples intervalos se acumularan si el usuario navegaba entre hogares. Similar problema ocurría con el Map de deduplicación de notificaciones (`recentDeviceNotifs`) que crecía indefinidamente. La corrección incluyó:

-- `stopDailySampling()` que ejecuta `clearInterval()` al cambiar de hogar o cerrar sesión.
-- Limpieza cada 100 notificaciones del Map, eliminando entradas más antiguas que 2 segundos (ventana de deduplicación).

Esta decisión aplica principios de gestión de recursos: el código que crea un recurso debe ser responsable de liberarlo. Memory leaks degradan el rendimiento progresivamente y pueden causar fallos en sesiones largas, violando la expectativa de estabilidad y la estética minimalista del sistema al acumular datos innecesarios.

== Manejo de errores con try/catch/finally en formularios

*Decisión:* Todos los formularios (login, registro, cambio de contraseña) envuelven las peticiones asíncronas en bloques `try/catch/finally` que garantizan restablecer el estado de carga (`loading = false`) incluso si la petición falla.

*Justificación:* En una versión intermedia sin `finally`, si la petición de login fallaba por timeout de red, el botón quedaba en estado "Iniciando sesión..." permanentemente, bloqueando al usuario sin posibilidad de reintentar. El patrón `try { acción } catch { error } finally { loading = false }` garantiza que el estado de carga siempre se restablece, permitiendo reintentos y evitando bloqueos de UI. Esto previene errores y ayuda a los usuarios a reconocer, diagnosticar y recuperarse de fallos.

== Deduplicación de notificaciones WebSocket

*Decisión:* Se implementó un sistema de ventana deslizante de 2 segundos para evitar notificaciones duplicadas cuando el backend emite múltiples eventos para la misma acción del usuario (por ejemplo, `deviceEvent` + `deviceUpdated` por el mismo cambio de estado).

*Justificación:* En la implementación inicial del WebSocket, cada acción del usuario generaba múltiples notificaciones redundantes: al encender una lámpara, el backend emitía `deviceEvent` (cambio de estado) y `deviceUpdated` (actualización del dispositivo) casi simultáneamente, generando dos toasts diciendo "Lámpara sala fue modificada". Esto sobrecargaba al usuario con información repetida, afectando la estética minimalista del sistema. La deduplicación basada en un Map con timestamp por dispositivo (`shouldNotify()`) filtra eventos redundantes dentro de la ventana de 2 segundos, mostrando solo la primera notificación. Este patrón es transparente para el usuario y no afecta la reactividad del sistema.

// ====================================
// 7. DIFERENCIAS CON EL PROTOTIPO
// ====================================

= Diferencias respecto al prototipo

Durante la implementación se realizaron ajustes respecto al diseño original para adaptarlo a las capacidades técnicas de la API y mejorar la usabilidad.

== Ajustes en la vista de Hogar

*Decisión:* Se priorizó el uso de tarjetas de resumen y métricas clave sobre la representación gráfica del plano de la casa.

*Justificación:* El diseño original contemplaba un plano isométrico con la ubicación espacial de los dispositivos. Dado que la API no proporciona datos de posicionamiento, se optó por un enfoque basado en tarjetas de información. Esto asegura que el usuario reciba datos precisos y accionables sobre sus dispositivos favoritos y el consumo energético, manteniendo la claridad informativa.

== Simplificación de roles de usuario

*Decisión:* Se implementó un modelo de usuario con acceso completo, posponiendo la gestión de perfiles con permisos restringidos.

*Justificación:* Se priorizó la robustez de las funcionalidades principales de gestión de dispositivos y rutinas. El modelo actual simplifica la interacción para el usuario principal sin comprometer la capacidad de control del sistema.

== Evolución de los controles de dispositivos

*Decisión:* Se reemplazaron algunos selectores binarios (on/off) por paneles de acciones contextuales para dispositivos con múltiples estados.

*Justificación:* Dispositivos como cortinas o sistemas de audio poseen estados intermedios que no se representan adecuadamente con un interruptor simple. El uso de botones para acciones específicas (subir/bajar, reproducir/pausar) mejora la correspondencia con el mundo real y hace la interacción más intuitiva. El diseño actual refleja el modelo de interacción familiar para sistemas multimedia y de automatización, reforzando la coherencia entre el sistema y el mundo real. Esto se evidencia en la comparación del control de cortinas presentada en la sección de capturas.

== Introducción de la vista de Resumen Global (Overview)

*Decisión:* Se incorporó una pantalla de bienvenida que ofrece una visión general de todas las propiedades y dispositivos críticos antes de profundizar en un hogar específico.

*Justificación:* Esta vista permite al usuario identificar rápidamente estados de alerta o ejecutar rutinas frecuentes sin necesidad de navegar por cada propiedad individualmente. Esto reduce el esfuerzo necesario para obtener una visión general del sistema, potenciando el reconocimiento sobre el recuerdo y la eficiencia de uso.



= Diseño gráfico y paleta de colores

La identidad visual de HomeCore se basa en una paleta de tonos oscuros diseñada para minimizar la fatiga visual, especialmente en entornos de baja luminosidad, habituales en la gestión de hogares durante la noche. Esta elección cromática también permite que los estados activos (dispositivos encendidos, alertas, favoritos) resalten de forma efectiva mediante acentos de color.

== Paleta de colores

Los colores se encuentran definidos mediante variables CSS (Design Tokens) para garantizar la consistencia en todos los componentes.

#table(
  columns: (auto, auto, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Color*], [*Variable*], [*Uso principal*]),
  [#rect(fill: rgb("#0e0d13"), width: 1em, height: 1em)], [`--bg-main`], [Fondo de la aplicación],
  [#rect(fill: rgb("#181924"), width: 1em, height: 1em)], [`--bg-sidebar`], [Barras y paneles (sidebar, topbar, tarjetas)],
  [#rect(fill: rgb("#6f78da"), width: 1em, height: 1em)], [`--accent`], [Color de acento (acciones principales)],
  [#rect(fill: rgb("#f6bd20"), width: 1em, height: 1em)], [`--amber`], [Alertas y avisos],
  [#rect(fill: rgb("#f87171"), width: 1em, height: 1em)], [`--danger`], [Acciones destructivas y errores],
  [#rect(fill: rgb("#d0d3db"), width: 1em, height: 1em)], [`--text-primary`], [Texto principal y encabezados],
)

== Justificación técnica

-- *Reducción de fatiga visual:* La elección de un esquema de colores oscuros está respaldada por principios de usabilidad que indican que, en interfaces de domótica —que suelen utilizarse por la noche—, los fondos oscuros minimizan el deslumbramiento y reducen la fatiga ocular del usuario.
-- *Jerarquía visual:* El uso del acento violeta (`--accent`) crea un contraste definido sobre el fondo oscuro, guiando la atención del usuario hacia las acciones principales (encendido de dispositivos, confirmación de rutinas) sin saturar la interfaz.
-- *Accesibilidad:* Se aseguró una relación de contraste adecuada entre el color de fondo (`--bg-main`) y los colores de texto (`--text-primary`, `--text-secondary`), cumpliendo con los estándares mínimos de legibilidad (RNF9).
-- *Semántica de colores:* Los colores semánticos (`--danger` para errores/eliminación, `--success` para estados activos) siguen las convenciones estándar de la industria, facilitando el reconocimiento inmediato de estados por parte de los usuarios.

= Instructivo de instalación

Para ejecutar la aplicación en un entorno local, siga los siguientes pasos:

1.  *Prerrequisitos:* Asegúrese de tener instalado Node.js (versión 18 o superior) y npm en su sistema.
2.  *Descarga:* Extraiga el contenido del archivo comprimido o clone el repositorio de GitHub.
3.  *Instalación de dependencias:* Abra una terminal, navegue hasta el directorio `homecore-web` y ejecute:
    `npm install`
4.  *Ejecución:* Inicie el servidor de desarrollo con el comando:
    `npm run dev`
5.  *Acceso:* Una vez iniciado el servidor, acceda a la aplicación mediante la URL indicada en la terminal (generalmente `http://localhost:5173`).

== Navegadores compatibles

La aplicación ha sido testeada y es compatible con las últimas versiones de los siguientes navegadores:
-- Google Chrome
-- Mozilla Firefox
-- Microsoft Edge
-- Safari

// ====================================
// 8. FEEDBACK DE LA PRIMERA ENTREGA
// ====================================

== Configuración de usuario

*Feedback:* "La sección de configuración debería ser más visible. Su ubicación es poco estratégica."

*Resolución:* En la primera entrega, la sección de Configuración solo era accesible desde el fondo de la barra lateral, lo cual dificultaba su descubrimiento y acceso. A partir del feedback recibido, se decidió incorporarla también dentro del menú de usuario en la barra superior, aumentando su visibilidad y accesibilidad para todos los usuarios. Esta decisión se relaciona con la consistencia y el uso de estándares ya que ubicar la configuración dentro del menú de usuario sigue patrones de navegación ampliamente utilizados. Además, también refuerza el reconocimiento antes que el recuerdo, porque el usuario puede identificar rápidamente dónde acceder a las configuraciones sin necesidad de memorizar su ubicación.


// TODO: Completar con el feedback recibido del profesor/evaluador
// y cómo se abordó en la implementación.
// Ejemplo:
// -- *Feedback:* "La sección de configuración debería estar visible para todos
//    los perfiles, aunque con restricciones."
//    *Resolución:* Se implementó la sección de Configuración accesible desde
//    el menú de usuario en la barra superior, visible para todos los usuarios.
