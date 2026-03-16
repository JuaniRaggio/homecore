#set document(
  title: "HomeCore - Usuarios Representativos y Modelos de Persona",
  author: "Juan Ignacio Raggio",
)

#set page(
  paper: "a4",
  margin: (
    top: 2.5cm,
    bottom: 2.5cm,
    left: 2cm,
    right: 2cm,
  ),
  numbering: "1",
  number-align: bottom + right,

  header: [
    #set text(size: 9pt, fill: gray)
    #grid(
      columns: (1fr, 1fr, 1fr),
      align: (left, center, right),
      [HomeCore], [Usuarios Representativos], [#datetime.today().display("[day]/[month]/[year]")],
    )
    #line(length: 100%, stroke: 0.5pt + gray)
  ],

  footer: context [
    #set text(size: 9pt, fill: gray)
    #line(length: 100%, stroke: 0.5pt + gray)
    #v(0.2em)
    #align(center)[
      Pagina #counter(page).display() / #counter(page).final().first()
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

#align(center)[
  #v(1em)
  #text(size: 24pt, weight: "bold")[HomeCore]
  #v(0.5em)
  #text(size: 18pt)[Usuarios Representativos y Modelos de Persona]
  #v(0.5em)
  #text(size: 12pt, fill: gray)[
    Gestion de Casas Inteligentes \
    #datetime.today().display("[day]/[month]/[year]")
  ]
  #v(1em)
]

#line(length: 100%, stroke: 1pt)
#v(1em)


= Usuarios Representativos

== Definicion de atributos

Se seleccionaron los siguientes atributos por ser factores que influyen directamente en como un usuario interactua con una aplicacion de domotica:

#table(
  columns: 3, // (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  table.header(
    [*Atributo*], [*Valores posibles*], [*Justificacion*],
  ),
  [Edad],
  [12-17 / 18-30 / 31-50 / 51-70],
  [Se segmenta en 4 rangos porque cada uno presenta diferencias significativas en adopcion tecnologica segun estudios de Pew Research (2023): 18-30 son nativos digitales, 31-50 adoptaron tecnologia en edad adulta, 51-70 requieren mayor curva de aprendizaje. 12-17 lo definimos ya que es una edad de aprendizaje en la que las personas comienzan a tener cada vez mas responsabilidades, este rango representa "hijos menores"],

  [Comodidad tecnologica],
  [Novice: usa solo funciones basicas del celular (llamadas, WhatsApp, camara). \
   Average: usa apps variadas con soltura, configura dispositivos, resuelve problemas simples sin ayuda. \
   Power: experiencia en aplicaciones de domotica, dandole uso a funciones avanzadas de las mismas],
  [Se evita la escala "alto/medio/bajo" por ser ambigua. Cada nivel describe comportamientos observables concretos.],

  [Composicion del hogar],
  [Vive solo / Pareja sin hijos / Familia con hijos menores / Familia con hijos mayores],
  [Determina la cantidad de usuarios simultaneos del sistema, la necesidad de restricciones de acceso a dispositivos, y la complejidad de las rutinas.],

  [Tipo de vivienda],
  [Departamento (hasta 3 ambientes) / Casa (4+ ambientes)],
  [Influye en la cantidad de dispositivos, habitaciones, y la complejidad de la navegacion dentro de la app.],

  [Frecuencia de uso de apps de hogar],
  [Nunca uso una / Uso alguna (termostato, luces) / Usa un ecosistema completo (Google Home, Alexa, HomeKit)],
  [Determina el modelo mental previo del usuario respecto a domotica y sus expectativas de la interfaz.],

  [Ingreso mensual del hogar],
  [2-4 salarios minimos / 4-8 salarios minimos / 8+ salarios minimos],
  [Un hogar inteligente requiere inversion en dispositivos. Este atributo determina la cantidad y variedad de dispositivos que el usuario podria tener.],
)

#v(1em)

== Combinaciones representativas

Se seleccionaron las siguientes combinaciones de atributos para cubrir el espectro de usuarios relevantes del sistema:

#table(
  columns: (auto, auto, auto, auto, auto, auto, auto),
  align: center,
  stroke: 0.5pt,
  inset: 6pt,
  table.header(
    [*UR*], [*Edad*], [*Tec.*], [*Hogar*], [*Vivienda*], [*Exp. domotica*], [*Ingreso*],
  ),
  [UR1], [18-30], [Nivel 3], [Solo], [Depto], [Ecosistema completo], [4-8 SM],
  [UR2], [31-50], [Nivel 2], [Familia c/ hijos menores], [Casa], [Alguna app], [8+ SM],
  [UR3], [51-70], [Nivel 1], [Pareja sin hijos], [Depto], [Nunca], [4-8 SM],
)

*Justificacion de la seleccion:*

-- *UR1* representa al usuario con mayor comodidad tecnologica, que buscara eficiencia, atajos y automatizaciones avanzadas. Vive solo, por lo que no necesita restricciones de acceso.

-- *UR2* representa al usuario mas frecuente del mercado de domotica: familia con capacidad economica, que busca comodidad y seguridad. Tiene hijos menores, lo que introduce la necesidad de restricciones y control parental sobre dispositivos.

-- *UR3* representa al usuario con mayor friccion potencial. No tiene experiencia previa con domotica, usa el celular para funciones basicas, y necesita una interfaz extremadamente clara. Su inclusion es critica para validar que la app sea accesible.

#pagebreak()

= Modelos de Persona

#block(
  fill: rgb("#FFF3E0"),
  stroke: rgb("#F57C00") + 1pt,
  inset: 10pt,
  radius: 4pt,
  width: 100%,
)[
  #text(weight: "bold", fill: rgb("#F57C00"))[Nota sobre fotografias:]
  Las imagenes de cada persona deben ser ilustraciones o avatares generados. No se utilizan fotografias de personas reales ni famosas, ya que esto sesga la percepcion de personalidad del lector hacia las caracteristicas conocidas de esa persona.
]

#v(1em)

== Persona 1: Santiago (basada en UR1)

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Nombre*], [Santiago],
  [*Edad*], [27 anios],
  [*Ocupacion*], [Desarrollador de software en una empresa de tecnologia],
  [*Comodidad tecnologica*], [Nivel 3: configura su propia red con VLANs, usa Home Assistant, automatiza tareas con scripts. Flasheo el firmware de sus luces inteligentes para integrarlas con su servidor local.],
  [*Composicion del hogar*], [Vive solo en un departamento de 2 ambientes en Palermo, CABA],
  [*Dispositivos inteligentes*], [8 dispositivos: 4 lamparas Philips Hue, 1 cerradura inteligente, 1 parlante con asistente, 1 sensor de temperatura, 1 enchufe inteligente],
  [*Experiencia con apps de domotica*], [Usa Google Home y Home Assistant diariamente. Esta familiarizado con el concepto de rutinas, escenas y automatizaciones basadas en horario o sensores.],
  [*Ingreso mensual*], [Aproximadamente 5 salarios minimos],
)

#v(0.5em)

=== Motivaciones

-- Centralizar el control de todos sus dispositivos en una sola aplicacion en lugar de tener una app por fabricante.
-- Crear rutinas complejas que combinen multiples dispositivos (por ejemplo, al llegar a casa: encender luces, desbloquear puerta, activar parlante).
-- Monitorear el consumo electrico de cada dispositivo para optimizar el gasto.

=== Escenarios de uso

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Escenario 1*], [Santiago sale de su departamento a las 8:30 para ir a la oficina. Desde la app activa la rutina "Salir de casa" que apaga todas las luces, traba la cerradura y baja las persianas. Quiere que esto se ejecute con un solo toque.],
  [*Escenario 2*], [A las 23:00, Santiago quiere preparar el ambiente para dormir. Ejecuta la rutina "Noche" que baja el brillo de la lampara del dormitorio al 10%, apaga el resto de las luces, y traba la puerta. Si la puerta ya estaba trabada, no quiere ver un error.],
  [*Escenario 3*], [Santiago recibe una notificacion de consumo inusualmente alto en un enchufe inteligente. Abre la app, consulta el historial de consumo de ese dispositivo, identifica que se dejo encendida una estufa, y la apaga remotamente.],
)

=== Comportamientos esperados

-- Va a buscar atajos y formas rapidas de operar (rutinas en el dashboard, acceso directo a favoritos).
-- Va a intentar personalizar todo: nombres de dispositivos, colores de lamparas, horarios de rutinas.
-- Se va a frustrar si la interfaz le impone pasos innecesarios para tareas simples (por ejemplo, confirmar cada vez que apaga una luz).
-- Va a explorar todas las secciones de la app en los primeros minutos de uso.

=== Caracteristicas deseadas

-- Ejecucion de rutinas desde el dashboard con un solo toque.
-- Vista de consumo electrico detallada por dispositivo.
-- Control granular de cada dispositivo (brillo, color, caudal, posicion).
-- Historial de acciones para poder rastrear que paso y cuando.

#pagebreak()

== Persona 2: Carolina (basada en UR2)

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Nombre*], [Carolina],
  [*Edad*], [42 anios],
  [*Ocupacion*], [Contadora publica en un estudio contable. Trabaja de lunes a viernes de 9 a 18.],
  [*Comodidad tecnologica*], [Nivel 2: usa el celular con soltura para apps de banco, delivery, redes sociales. Configuro sola el router de su casa y las camaras de seguridad. No programa ni modifica configuraciones avanzadas.],
  [*Composicion del hogar*], [Vive con su pareja y dos hijos de 8 y 12 anios en una casa de 5 ambientes en Martinez, GBA norte.],
  [*Dispositivos inteligentes*], [12 dispositivos: 6 lamparas inteligentes, 2 persianas motorizadas, 1 alarma con 4 zonas, 1 cerradura inteligente, 1 grifo de jardin automatizado, 1 termostato],
  [*Experiencia con apps de domotica*], [Usa la app del fabricante de la alarma y la app de las lamparas por separado. Le resulta incomodo tener que alternar entre apps.],
  [*Ingreso mensual del hogar*], [Aproximadamente 10 salarios minimos (entre ambos adultos)],
)

#v(0.5em)

=== Motivaciones

-- Tener control sobre la seguridad del hogar desde cualquier lugar, especialmente la alarma y la cerradura, porque los chicos llegan solos del colegio.
-- Simplificar las tareas diarias: que las persianas se abran solas a la maniana, que las luces se prendan al atardecer.
-- Poder restringir lo que sus hijos pueden controlar desde la app (no quiere que desactiven la alarma o abran la puerta de calle).

=== Escenarios de uso

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Escenario 1*], [Carolina esta en la oficina y su hijo de 12 anios llega del colegio. Ella recibe una notificacion de que la puerta principal se abrio. Entra a la app para verificar que la alarma del perimetro siga activa y que solo se desactivo la zona de la puerta.],
  [*Escenario 2*], [Un sabado a la maniana, Carolina quiere regar el jardin antes de que haga calor. Desde la cama abre la app, activa el grifo automatizado al 60% de caudal por 15 minutos. Quiere poder hacer esto sin levantarse ni tener que navegar por muchas pantallas.],
  [*Escenario 3*], [Carolina crea una rutina "Vacaciones" que simula presencia en la casa: enciende y apaga luces en horarios aleatorios, baja persianas de noche y las sube de dia, y mantiene la alarma armada en todas las zonas. Necesita que el proceso de creacion sea guiado paso a paso.],
)

=== Comportamientos esperados

-- Va a usar la app principalmente para verificar estados (la alarma esta activa? la puerta esta trabada?) mas que para configurar cosas nuevas.
-- Va a preferir flujos guiados (wizard) para crear rutinas, en lugar de tener que recordar opciones.
-- Se va a preocupar por la seguridad: quiere confirmaciones antes de desactivar la alarma o destrabar la puerta.
-- No va a explorar toda la app de entrada; va a ir descubriendo funcionalidades a medida que las necesite.

=== Caracteristicas deseadas

-- Notificaciones claras cuando se activan dispositivos de seguridad (alarma, puerta).
-- Wizard paso a paso para crear rutinas (no un formulario unico con todos los campos).
-- Dashboard con estado rapido de los dispositivos criticos (alarma, puerta, cerradura).
-- Posibilidad de restringir acceso a ciertos dispositivos para perfiles de usuario limitados.

#pagebreak()

== Persona 3: Roberto (basada en UR3)

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Nombre*], [Roberto],
  [*Edad*], [63 anios],
  [*Ocupacion*], [Jubilado, ex-empleado administrativo de una empresa de logistica.],
  [*Comodidad tecnologica*], [Nivel 1: usa el celular para WhatsApp, llamadas y la camara. Su hijo le configuro el home banking y lo usa con un tutorial impreso al lado. Cuando algo no funciona, llama a su hijo o va a un servicio tecnico.],
  [*Composicion del hogar*], [Vive con su esposa en un departamento de 3 ambientes en Belgrano, CABA. Su hijo vive a 15 minutos y es quien le instalo los dispositivos inteligentes.],
  [*Dispositivos inteligentes*], [5 dispositivos: 3 lamparas inteligentes, 1 persiana motorizada en el living, 1 cerradura inteligente. Todos fueron instalados y configurados por su hijo.],
  [*Experiencia con apps de domotica*], [Nunca uso una. El concepto de "rutina" o "automatizacion" no le es familiar. Entiende "prender" y "apagar" pero no "configurar una escena".],
  [*Ingreso mensual*], [Aproximadamente 4 salarios minimos (jubilacion mas rentas)],
)

#v(0.5em)

=== Motivaciones

-- No tener que levantarse para apagar luces o bajar persianas. A su edad, la comodidad fisica es una motivacion central.
-- Sentirse seguro: poder verificar que la puerta esta trabada desde la cama antes de dormir.
-- No depender de su hijo para cada cosa. Quiere poder hacer las operaciones basicas solo.

=== Escenarios de uso

#table(
  columns: (auto, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  [*Escenario 1*], [Roberto se acuesta a las 22:30. Quiere apagar todas las luces y verificar que la puerta este trabada. Abre la app y necesita poder hacer esto sin recordar donde esta cada opcion. Si ve un toggle de "encendido" al lado del nombre de la luz, lo entiende. Si ve un icono abstracto sin texto, no.],
  [*Escenario 2*], [A la maniana, Roberto quiere subir la persiana del living. Abre la app y busca la persiana. Espera que el control sea similar a un control remoto fisico: un boton para subir, uno para bajar. No va a entender un slider de "posicion porcentual" sin contexto.],
  [*Escenario 3*], [Roberto toca algo por accidente y la alarma se activa (en un escenario donde tuviera alarma). Necesita un mensaje claro que le diga que paso y como revertirlo. Si ve un mensaje tecnico como "Zona perimetral activada", no va a saber que hacer.],
)

=== Comportamientos esperados

-- Va a usar la app para un conjunto reducido de acciones: prender/apagar luces, subir/bajar persianas, verificar puerta.
-- No va a crear rutinas ni explorar secciones como "consumo electrico" o "historial" por iniciativa propia.
-- Va a leer todo el texto en pantalla antes de tocar algo. Los labels claros y descriptivos son criticos.
-- Si comete un error, va a buscar un boton visible para deshacer o salir. Si no lo encuentra, va a cerrar la app y abrirla de nuevo.
-- Va a preferir botones grandes y con texto, no iconos solos.

=== Caracteristicas deseadas

-- Textos descriptivos junto a cada control (no solo iconos).
-- Botones grandes con areas de toque amplias.
-- Feedback claro despues de cada accion ("Luz del living apagada", "Puerta trabada").
-- Acceso directo a los dispositivos que usa con frecuencia sin tener que navegar por menus.
-- Lenguaje simple y coloquial, sin terminos tecnicos.

#pagebreak()

= Resumen comparativo

#table(
  columns: (auto, 1fr, 1fr, 1fr),
  align: (left, left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  table.header(
    [], [*Santiago*], [*Carolina*], [*Roberto*],
  ),
  [*Motivacion principal*],
  [Eficiencia y control total],
  [Seguridad y comodidad familiar],
  [Comodidad fisica y autonomia],

  [*Uso predominante*],
  [Rutinas, consumo, personalizacion],
  [Verificar estados, seguridad, rutinas guiadas],
  [Prender/apagar, verificar puerta],

  [*Nivel de exploracion*],
  [Explora toda la app de entrada],
  [Descubre funcionalidades progresivamente],
  [Usa solo lo que le ensenaron],

  [*Tolerancia a errores*],
  [Baja: se frustra con pasos innecesarios],
  [Media: acepta confirmaciones en acciones criticas],
  [Baja: se asusta con mensajes que no entiende],

  [*Necesidad de feedback*],
  [Minimo y no intrusivo],
  [Claro en acciones de seguridad],
  [Explicito y descriptivo en toda accion],

  [*Complejidad de rutinas*],
  [Multiples dispositivos, condiciones, horarios],
  [Moderada, prefiere wizard guiado],
  [No usa rutinas],
)
