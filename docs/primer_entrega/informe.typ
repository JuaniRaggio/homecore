// ============================================================
// HomeCore -- TP Gestión de Casas Inteligentes (Primera Entrega)
// ============================================================

#set document(
  title: "HomeCore - TP 1 prototipado principios de UCD",
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
        [HomeCore], [Primera Entrega -- Grupo 15], [#datetime.today().display("[day]/[month]/[year]")],
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
    #text(size: 14pt, fill: gray)[Primera Entrega -- Grupo 15]
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
      // Agregar más integrantes aqui
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
// INTRODUCCIÓN
// ====================================

= Introducción

Este trabajo práctico consiste en desarrollar una aplicación diseñada para la administración y el control remoto de dispositivos inteligentes en un hogar. Su objetivo principal es permitir al usuario gestionar el estado y funcionamiento de estos dispositivos de manera óptima, eficiente y centralizada. La aplicación consiste en dos interfaces de usuario: un sitio web (accesible desde navegadores de escritorio) y una aplicación móvil (nativa para Android).

Esta primera entrega se enfoca en el diseño de interfaces del sitio web y la aplicación móvil, aplicándose la filosofía de diseño centrado en el usuario (UCD), tal como se vio en clase.

// ====================================
// 1. USUARIOS REPRESENTATIVOS
// ====================================

= Usuarios representativos

Se determinaron las siguientes características de los potenciales usuarios de la aplicación:

- *Ingreso mensual:* Los usuarios de una aplicación domótica poseen uno o más dispositivos inteligentes, los cuales están fuera del alcance de la mayoría de la población. Es por esto que se estima que los usuarios se concentran en los hogares del decil de ingresos más alto, los cuales tienen un ingreso per cápita aproximadamente de 1.699.225 pesos ("El Ingreso Promedio Sube Pero La Brecha Persiste." 2025) según la EPH (Encuesta Permanente de Hogares) del INDEC.

- *Comodidad tecnológica:* Se requiere un nivel de conocimiento básico sobre el uso de aplicaciones tal como saber que es una notificación, usar el menú lateral para cambiar de página, etc. para aprovechar en su totalidad a la aplicación.

- *Edad:* La característica de la edad está estrechamente relacionada a la comodidad tecnológica. Es por esto que se predice que habrá un límite en la cantidad de adultos mayores que podrán usar la app, así como también lo habrá en niños todavía muy pequeños para tener la suficiente responsabilidad que conlleva usar la aplicación.

// ====================================
// 2. OBSERVACIONES PARTICIPATIVAS
// ====================================

= Observaciones participativas

Durante la etapa de descubrimiento, el enfoque fue el de observación participativa, una técnica de evaluación empírica en la que los participantes interactúan con aplicaciones ya existentes en el mercado mientras se observa y registra aquellas interacciones con el objetivo de construir soluciones de diseño que se vinculen estrechamente con las necesidades del usuario.

La recolección de datos se realizó en cortas sesiones individuales de entre 10 y 15 minutos, donde cada participante fue guiado a través de escenarios ya establecidos sobre aplicaciones reales existentes de gestión de dispositivos inteligentes. Estas sesiones fueron hechas en un formato mixto: presencial o virtual a través de videollamada (computadora o celular), lo cual logro ampliar el alcance de la muestra.

Durante cada sesión, se tomaron notas sobre las acciones realizadas así también como las expresiones ya sean de confusión o satisfacción. También se utilizo la técnica de pensamiento en voz alta, con el objetivo de capturar el modelo mental y las expectativas.

La selección de participantes fue variada, incluyendo personas con experiencia previa en el uso de dispositivos inteligentes y personas sin experiencia en el área. Esto se decidió de esta manera por la necesidad de identificar patrones en los futuros usuarios expertos así como los usuarios novatos dado que la aplicación busca ser accesible para ambos perfiles. También se trató de buscar diversidad etaria.

== Aplicaciones seleccionadas

Las aplicaciones seleccionadas y sus respectivas justificaciones son las siguientes:

- *Web 1 -- Google Home:* Google Home es una de las plataformas de gestión de hogares inteligentes más conocida y utilizada mundialmente, soportando una gran variedad de dispositivos. Su interfaz web no solo logra controlar los dispositivos y generar rutinas, lo cual nuestra aplicación aspira a hacer. Se puede decir que esta aplicación es una de las que lidera el campo de gestión de dispositivos inteligentes y está en constante desarrollo, recientemente incorporando a Gemini (inteligencia artificial) a sus servicios.

- *Web 2 -- Amazon Alexa:* Alexa es la principal aplicación competidora de Google Home, ofreciendo servicios como gestionar dispositivos inteligentes, crear rutinas y administrar grupos de dispositivos por habitación. En lo que respecta a interfaces visuales, Alexa tiene una filosofía de diseño distinta a Google Home, ofreciendo más opciones y configuraciones; lo cual enriquece el análisis de que nivel de complejidad y control agrada a los usuarios.

- *Móvil 1 -- Google Home:* La versión móvil de Google Home es la referencia más directa de la aplicación móvil a diseñar. Esta aplicación logra controlar dispositivos, crear escenas y rutinas, así como recibir notificaciones; las cuales son funciones que se desea que nuestra aplicación tenga. La versión móvil de Google Home adapta los flujos de interacción de la web a pantallas pequeñas, manteniendo aun así su diseño simple y limpio.

- *Móvil 2 -- Home Assistant:* Home Assistant es una plataforma de código abierto con una comunidad que no solo es muy activa, sino que también posee un mayor nivel técnico. Su inclusión en las aplicaciones a analizar se debe a que permite observar como usuarios sin experiencia interactúan con una interfaz más densa en información y opciones. Esto informa que elementos de organización son necesarios sin perder al usuario novato.

== Escenarios preestablecidos

Los escenarios preestablecidos para los participantes, así como la justificación, son los siguientes:

#table(
  columns: (auto, auto, 1fr, 1fr),
  align: (center, left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Aplicacion*], [*Escenario*], [*Justificación*]),
  [1],
  [Google Home\ (web y móvil)],
  [Encender una luz específica de una habitación determinada y luego apagarla.],
  [Es la acción más básica y probablemente la más frecuente. Permite entender como el usuario localiza un dispositivo dentro de la interfaz, exponiendo que tan intuitiva es.],

  [2],
  [Google Home\ (web y móvil),\ Amazon Alexa (web)],
  [Crear una rutina que apague todas las luces del hogar a las 23 hs de forma automatica.],
  [La creación de rutinas es una de las funcionalidades principales. Este escenario permite ver como el usuario busca y utiliza esta funcionalidad, identificando errores frecuentes o patrones.],

  [3],
  [Amazon Alexa (web),\ Home Assistant\ (móvil)],
  [Organizar/agrupar los dispositivos en habitaciones.],
  [La organización de dispositivos por habitaciones es un modelo mental que los usuarios traen de su realidad. Permite evaluar la intuitividad y accesibilidad de la categorización y agrupación.],

  [4],
  [Google Home\ (móvil),\ Home Assistant\ (móvil)],
  [Activar una escena que baje las luces y cierre las persianas.],
  [Permite observar como los usuarios manejan múltiples dispositivos como si fueran uno, y si identifican visualmente que escenas tienen disponibles y su estado actual.],

  [5],
  [Amazon Alexa (web),\ Home Assistant\ (móvil)],
  [Simular agregar un nuevo dispositivo al sistema y asignarle a una habitación.],
  [Permite identificar que información y asistencia visual del sistema facilita a ambos perfiles de usuario en el proceso de incorporación de dispositivos.],
)

== Conclusiones de las observaciones

Según las respuestas de cada participante (detalladas en el Anexo), se definieron varias conclusiones:

- En cuanto a la comparación entre la versión móvil y la web, los perfiles novatos prefirieron la primera ya que el uso les resultaba más natural y cómodo.

- Crear rutinas se les hizo demasiado complejo a los usuarios novatos, los cuales sugirieron que se muestren ejemplos o plantillas para crearlas.

- Se observó confusión porque muchos participantes no comprendían la diferencia entre _rutina_, _grupo_ o _escena_, y la diferenciación visual no era suficiente.

- La falta de feedback tras activar una acción generó mucha incertidumbre.

- Varios participantes concluyeron que Home Assistant es demasiado complejo para aquellos que no tienen mucha experiencia con este tipo de aplicaciones.

// ====================================
// 3. ENTREVISTAS Y ENCUESTAS
// ====================================

= Entrevistas y encuestas

Se realizaron una serie de 4 entrevistas y una encuesta mediante Google Forms. Las preguntas, así como la justificación de ellas, fueron las siguientes:

#table(
  columns: (1fr, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Pregunta*], [*Justificación*]),

  [Qué tan familiarizado estas con la tecnología?\ _(La uso solo para lo básico / Me manejo bien con apps cotidianas / Aprendo rápido tecnologías nuevas / Soy entusiasta tecnológico)_],
  [Permite clasificar a los usuarios según su nivel de conocimiento tecnológico, clave para diseñar una aplicación accesible tanto para principiantes como para avanzados.],

  [Cuántos dispositivos tecnológicos utilizas en tu hogar?\ _(Ninguno / 1--3 / 3--5 / Más de 5)_],
  [Ayuda a dimensionar el nivel de interacción tecnológica en el hogar y la complejidad del ecosistema que el usuario maneja.],

  [Qué tipo de dispositivos inteligentes utilizas en tu hogar?],
  [Permite identificar qué dispositivos son más comunes y relevantes para los usuarios.],

  [Cuáles son los que más utilizas?],
  [Ayuda a detectar los dispositivos prioritarios para el usuario, lo que permite enfocar la aplicación en lo más usado.],

  [Podrías mencionar qué aplicaciones utilizas para manejar estos dispositivos?],
  [Permite conocer herramientas actuales del usuario y detectar oportunidades de mejora frente a soluciones existentes.],

  [Utilizas más un smartphone o una página web? Por qué?],
  [Ayuda a definir la plataforma principal de uso (mobile o web) y entender las preferencias del usuario.],

  [Según tu experiencia, qué cambiarías o qué desventajas encuentras en las aplicaciones que utilizas?],
  [Permite identificar _pain points_ actuales, fundamentales para proponer una solución superadora.],

  [Controlas tus dispositivos principalmente desde dentro de casa o de forma remota?\ _(Desde dentro / De forma remota / Ambos)_],
  [Ayuda a entender el contexto de uso (presencial o remoto), clave para definir funcionalidades.],

  [Qué tan seguido revisas o ajustas la configuración de las apps que usas?\ _(Nunca / Rara vez / A veces / Frecuentemente)_],
  [Permite evaluar el nivel de interacción avanzada del usuario con la tecnología.],

  [Cuándo queres cambiar el estado de un dispositivo, qué es lo primero que buscas?\ _(Scrolleo hasta encontrarlo / Busco en el historial / Busco el nombre en el buscador)_],
  [Ayuda a entender el comportamiento del usuario dentro de una interfaz y optimizar la usabilidad.],

  [Te gustaría recibir notificaciones del sistema? De que tipo?\ _(Alertas de seguridad / Ejecución de rutinas / Consumo elevado / Estado de dispositivos / Prefiero no recibir)_],
  [Permite identificar qué tipo de información es relevante para el usuario y evitar sobrecarga de notificaciones.],

  [Qué es lo primero que haces cuando llegas a tu casa?],
  [Ayuda a detectar patrones de comportamiento y posibles automatizaciones útiles.],

  [Cuándo fue la última vez que tuviste un problema con un electrodoméstico? Qué ocurrió?],
  [Permite identificar situaciones problemáticas reales en el uso cotidiano.],

  [Cuál de estas situaciones te genera más ansiedad?\ _(Salir apurado sin revisar todo / No acordarme si prendí o apagué algo / Quedarme pensando si corte el aparato a la noche)_],
  [Ayuda a detectar puntos de fricción emocional, importantes para diseñar soluciones que generen tranquilidad.],

  [Cuál es tu rutina antes de salir de tu casa?],
  [Permite identificar hábitos repetitivos que podrían automatizarse.],

  [Hay algún electrodoméstico que revises dos o tres veces antes de irte o antes de dormir? Por qué?],
  [Permite detectar dispositivos críticos y oportunidades de mejora en control o seguridad.],

  [Hay algún electrodoméstico que NO tocarías remotamente aunque pudieras?],
  [Ayuda a identificar límites de confianza en la automatización.],

  [Discutiste alguna vez con alguien de tu casa por cómo usa los electrodomésticos?],
  [Permite detectar conflictos de uso y problemas de convivencia relacionados con la tecnología.],

  [Hay alguna tarea relacionada con electrodomésticos automatizados que odias hacer?],
  [Permite identificar tareas repetitivas o molestas que podrían simplificarse.],

  [Hay algún electrodoméstico que sabes que gasta mucho pero igual usas sin control?],
  [Permite entender la relación entre consumo, conciencia y comportamiento real del usuario.],
)

== Respuestas obtenidas

Se notó un gran desconocimiento sobre las aplicaciones de domótica por parte de los entrevistados. Buena parte solo controlaba sus dispositivos electrónicos de manera física o directamente no tenía electrodomésticos inteligentes, por lo que funciones más avanzadas como las rutinas no deberían ser el foco de la aplicación.

Otra función que no es de uso cotidiano para la mayoría de los usuarios es la de consultar el consumo eléctrico. El 60% prefiere no recibir notificaciones de consumo eléctrico elevado, mientras que la mayoría ya sabe que electrodomésticos tienen un consumo elevado. Se estima que esto es algo que los usuarios solo revisarán sí reciben una factura muy elevada, por lo que se diseñaron las interfaces pensando que sería la funcionalidad menos utilizada.

Al preguntar sobre las rutinas de las personas se notó que la mayoría tiene un dispositivo inteligente en particular que utiliza mucho, ya sea una alarma, cámaras o un instrumento de cocina. Esto llevó a agregar la posibilidad de marcar dispositivos o rutinas como favoritos, los cuales aparecen en el menú inicial de la casa.

// ====================================
// 4. MODELOS DE PERSONAS
// ====================================

= Modelos de personas

== Definición de atributos

Se seleccionaron los siguientes atributos por ser factores que influyen directamente en como un usuario interactúa con una aplicación de domótica:

#table(
  columns: 3,
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header(
    [*Atributo*], [*Valores posibles*], [*Justificación*],
  ),
  [Edad],
  [12-17 / 18-30 / 31-50 / 51-70],
  [Se segmenta en 4 rangos porque cada uno presenta diferencias significativas en adopción tecnológica: 18-30 son nativos digitales, 31-50 adoptaron tecnología en edad adulta, 51-70 requieren mayor curva de aprendizaje. 12-17 representa a "hijos menores" con responsabilidades crecientes.],

  [Comodidad tecnológica],
  [Novice: funciones básicas (WhatsApp). \
   Average: usa apps variadas, configura dispositivos. \
   Power: experto, usa funciones avanzadas y automatizaciones.],
  [Describe comportamientos observables concretos para evitar ambigüedad.],

  [Composición del hogar],
  [Solo / Pareja / Familia con hijos (menores/mayores)],
  [Determina usuarios simultáneos, restricciones de acceso y complejidad de rutinas.],

  [Tipo de vivienda],
  [Departamento / Casa],
  [Influye en la cantidad de dispositivos y la estructura de la navegación (habitaciones).],

  [Experiencia en domótica],
  [Nunca / Alguna app / Ecosistema completo],
  [Determina el modelo mental previo y las expectativas del usuario.],

  [Ingreso mensual],
  [2-4 SM / 4-8 SM / 8+ SM],
  [Determina la inversión potencial en el ecosistema de dispositivos.],
)

== Combinaciones representativas

#align(center)[#table(
  columns: (auto, auto, auto, auto, auto, auto, auto),
  align: center,
  stroke: 0.5pt,
  inset: 6pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header(
    [*UR*], [*Edad*], [*Tec.*], [*Hogar*], [*Vivienda*], [*Exp. dom.*], [*Ingreso*],
  ),
  [UR1], [18-30], [Nivel 3], [Solo], [Depto], [Ecosistema], [4-8 SM],
  [UR2], [31-50], [Nivel 2], [Familia], [Casa], [Alguna app], [8+ SM],
  [UR3], [51-70], [Nivel 1], [Pareja], [Depto], [Nunca], [4-8 SM],
)]

*Justificación de la selección:*

- *UR1 (La Entusiasta):* Usuario que busca eficiencia extrema y personalización. Validará funciones avanzadas.

- *UR2 (La Administradora):* Usuario tipico del mercado que busca seguridad familiar. Validará control parental y robustez.

- *UR3 (La Recién Llegada):* Usuario con alta fricción potencial. Validará la accesibilidad y claridad de la interfaz.

== Persona 1: Valentina "La Power User"

#grid(
  columns: (auto, 1fr),
  gutter: 20pt,
  stack(
    dir: ttb,
    image("assets/personas/santiago.jpg", width: 4cm),
    v(0.5em),
    align(center)[#text(style: "italic", size: 9pt)["Automatizar para optimizar."]]
  ),
  table(
    columns: (auto, 1fr),
    align: (left, left),
    stroke: 0.5pt,
    inset: 8pt,
    [*Atributo*], [*Descripción*],
    [*Edad*], [27 años],
    [*Comodidad*], [Nivel 3 - Power User: automatizaciones, API, scripts],
    [*Ubicación*], [Depto en Palermo, CABA],
    [*Dispositivos*], [12+ (Luces, Cerradura, AC, Hub)],
  )
)

=== Comportamientos esperados
- Prioriza atajos y comandos rápidos para acciones frecuentes.
- Explora las opciones de configuración avanzada sin dudar.
- Utiliza datos (gráficos, logs) para tomar decisiones de uso.

=== Características deseadas
- API robusta para integraciones externas.
- Control granular de dispositivos (valores precisos, no solo on/off).
- Interfaz de baja latencia con respuesta visual inmediata.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Centralización total: una sola app para todo.
    - Automatizaciones complejas y encadenadas.
    - Análisis de datos (consumo, temperatura).
  ],
  block(fill: rgb("#FFEBEE"), inset: 8pt, radius: 4pt, width: 100%)[
    *Frustraciones*
    - Apps con "paredes cerradas" (no API).
    - Interfaces lentas o con animaciones largas.
    - Falta de control granular (ej. solo on/off).
  ]
)

=== Escenarios de uso
- *Escenario 1 (Eficiencia):* Valentina sale a correr. Al alejarse 100m de su casa (geofencing), la app debe ejecutar "Modo Ausente" sin preguntarle, pero enviando una confirmación silenciosa.
- *Escenario 2 (Análisis):* Sospecha que el aire acondicionado consume de más. Entra a la sección de consumo para comparar el gasto de esta semana vs. la anterior.
- *Escenario 3 (Control):* Durante una película, quiere bajar la intensidad de las luces del living al 15% y cambiar el tono a cálido sin salir del dashboard principal.

#pagebreak()

== Persona 2: Carolina "La Gestora del Hogar"

#grid(
  columns: (auto, 1fr),
  gutter: 20pt,
  stack(
    dir: ttb,
    image("assets/personas/carolina.jpg", width: 4cm),
    v(0.5em),
    align(center)[#text(style: "italic", size: 9pt)["Necesito que la casa se cuide sola mientras no estoy."]]
  ),
  table(
    columns: (auto, 1fr),
    align: (left, left),
    stroke: 0.5pt,
    inset: 8pt,
    [*Nombre*], [Carolina],
    [*Edad*], [42 años],
    [*Ocupación*], [Contadora Pública],
    [*Ubicación*], [Casa en Martinez, GBA],
    [*Tecnologia*], [Nivel 2 (Average)],
    [*Dispositivos*], [15+ (Alarma, Camaras, Riego, Luces, Portón)],
  )
)

=== Bio
Madre de dos hijos y profesional ocupada. Para ella, la domótica no es un hobby, es una herramienta de seguridad y ahorro de tiempo. Su principal preocupación es que sus hijos lleguen bien del colegio y que la casa quede protegida cuando la familia sale. Valora la claridad y la confiabilidad por sobre la personalización extrema.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Paz mental: saber que la casa está segura.
    - Control de acceso para sus hijos y servicio.
    - Rutinas de ahorro energético (luces y riego).
  ],
  block(fill: rgb("#FFEBEE"), inset: 8pt, radius: 4pt, width: 100%)[
    *Frustraciones*
    - Notificaciones falsas o confusas de la alarma.
    - No saber sí dejo algo encendido al irse.
    - Procesos de configuración difíciles de recordar.
  ]
)

=== Escenarios de uso
- *Escenario 1 (Seguridad):* Carolina esta en una reunión y recibe un aviso: "Hijo 1 ingresó a las 16:15". Verifica en la app que la alarma se desactivó correctamente.
- *Escenario 2 (Mantenimiento):* El pronóstico anuncia lluvia. Carolina abre la app para desactivar la rutina de riego automático de esa tarde con un simple toggle.
- *Escenario 3 (Control Parental):* Configura la tablet de su hijo menor para que solo pueda controlar las luces de su cuarto, restringiendo el acceso a la alarma y la cerradura principal mediante una password.

#pagebreak()

== Persona 3: Marta "La Usuario Tradicional"

#grid(
  columns: (auto, 1fr),
  gutter: 20pt,
  stack(
    dir: ttb,
    image("assets/personas/roberto.jpg", width: 4cm),
    v(0.5em),
    align(center)[#text(style: "italic", size: 9pt)["Solo quiero apagar la luz sin tener que levantarme."]]
  ),
  table(
    columns: (auto, 1fr),
    align: (left, left),
    stroke: 0.5pt,
    inset: 8pt,
    [*Nombre*], [Marta],
    [*Edad*], [63 años],
    [*Ocupación*], [Jubilada],
    [*Ubicación*], [Departamento en Belgrano, CABA],
    [*Tecnologia*], [Nivel 1 (Novice)],
    [*Dispositivos*], [5 (Luces, Persianas, Cerradura)],
  )
)

=== Bio
Marta es una persona activa pero prefiere la simplicidad. Su hijo le instaló dispositivos inteligentes para ayudarla con su movilidad (le cuesta agacharse o subir escaleras). Usa el celular para lo justo y necesario. Si la app parece un "tablero de avión", se asusta y deja de usarla. Necesita botones grandes y lenguaje claro.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Comodidad física: controlar el entorno desde el sofá.
    - Autonomía: no llamar a su hijo por cada duda.
    - Verificación rápida de seguridad (puertas).
  ],
  block(fill: rgb("#FFEBEE"), inset: 8pt, radius: 4pt, width: 100%)[
    *Frustraciones*
    - Iconos abstractos sin etiquetas de texto.
    - Letra chica o poco contraste.
    - Miedo a borrar algo o desconfigurar el sistema.
  ]
)

=== Escenarios de uso
- *Escenario 1 (Comodidad):* Marta esta viendo la tele y el sol le molesta. Abre la app y toca un boton grande que dice "Bajar Persiana Living".
- *Escenario 2 (Seguridad Nocturna):* Ya en la cama, abre la app para asegurarse de que la puerta de entrada tiene el candado puesto. El icono verde de "Cerrado" le da tranquilidad.
- *Escenario 3 (Asistencia):* Toca una opción por error y se abre un menú desconocido. Busca un boton de "Atrás" o "Inicio" claramente identificado para volver a lo que conoce.

== Resumen comparativo de necesidades

#table(
  columns: (auto, 1fr, 1fr, 1fr),
  align: (left, left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header(
    [], [*Valentina*], [*Carolina*], [*Marta*],
  ),
  [*Prioridad*], [Velocidad y Potencia], [Seguridad y Control], [Simplicidad y Ayuda],
  [*Interfaz*], [Compacta / Muchos datos], [Limpia / Notificaciones], [Grande / Textual],
  [*Navegación*], [Atajos y Gestos], [Menús categorizados], [Flujo lineal],
  [*Feedback*], [Sutil / Técnico], [Informativo], [Afirmativo / Guiado],
  [*Error*], [Lo resuelve solo], [Busca el manual/ayuda], [Se frustra / Abandona],
)

// ====================================
// 5. EVALUACIÓN DE USABILIDAD
// ====================================

= Evaluación de usabilidad

== Evaluación predictiva

=== Metodología

La metodología utilizada fue que cada integrante realice una inspección individual del prototipo web y móvil desarrollado por el otro grupo, utilizando las heuristicas seleccionadas. Luego se hizo una puesta en común donde se consolidaron los hallazgos, eliminaron los duplicados y priorizaron los problemas según la severidad (crítico, mayor, menor, cosmético).

=== Heuristicas seleccionadas: Principios de Jacob Nielsen

Se eligieron las 10 heuristicas de Nielsen dado que es el marco más establecido para la evaluación heurística de interfaces. Solo las primeras 8 fueron seleccionadas. Se justifica la elección de cada principio en la siguiente tabla:

#table(
  columns: (auto, auto, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Heurística*], [*Justificación*]),
  [1], [Visibilidad del estado del sistema],
  [Crítico en domótica: el usuario necesita saber en todo momento si la alarma está armada, si la puerta está bloqueada, si una rutina se ejecutó. Un estado incorrecto o no visible puede generar riesgos de seguridad reales.],
  [2], [Coherencia entre sistema y mundo real],
  [La terminología debe ser comprensible para todos los perfiles. Se tiene que verificar que las metáforas visuales coinciden con el modelo mental del usuario.],
  [3], [Control y libertad del usuario],
  [Es importante que el usuario tenga control granular y que pueda lograr lo que intenta.],
  [4], [Consistencia y estándares],
  [Los patrones de interacción tienen que ser consistentes entre dispositivos, respetando las convenciones de plataforma.],
  [5], [Prevención de errores],
  [Un sistema mal adaptado a distintos usuarios puede generar errores por contexto inadecuado.],
  [6], [Reconocimiento en lugar de memoria],
  [El usuario no tendría que adivinar o recordar que hace cada función; la carga cognitiva innecesaria debe minimizarse.],
  [7], [Flexibilidad y eficiencia de uso],
  [Deberían haber caminos rápidos para usuarios frecuentes, así como personalización de la experiencia.],
  [8], [Diseño estético y minimalista],
  [Con múltiples dispositivos, la sobrecarga visual es un riesgo concreto que puede generar confusión innecesaria.],
)

=== Problemas que se incorporan como mejoras

El hallazgo más contundente --- mencionado de forma independiente por los tres evaluadores --- es que el flujo para agregar elementos (dispositivos, habitaciones y pisos) es complejo y antiintuitivo. Se trata de un problema estructural del flujo de creación que será abordado mediante una revisión completa de ese recorrido, buscando reducir la cantidad de pasos, unificar el patrón de interacción y hacer más evidente el punto de entrada.

El *tamaño reducido de los elementos interactivos y del texto* fue señalado por los tres evaluadores como una dificultad real. Se incorporará una revisión tipográfica y de tamaño de componentes en toda la interfaz, priorizando la legibilidad y el área de toque.

La *falta de consistencia visual y de interacción* fue identificada por dos evaluadores. Se observaron switches distintos para una misma acción, disposición variable de elementos equivalentes en distintas secciones, y líneas sin criterio uniforme. Se unificara el componente de switch en toda la app y se establecerá un criterio posicional consistente.

El *estado poco claro de los dispositivos* también se incorpora. El color de la persiana, el gris utilizado en rutinas y el uso de "sí/no" para representar encendido/apagado generaron confusión real. El estado de cada dispositivo debe comunicarse de forma inequívoca a través de color, icono y etiqueta de texto.

La observación sobre el *gráfico/mapa de la casa* se incorpora parcialmente. Su modo de edición resultó frustrante para todos los perfiles. Se adoptara la separación entre la vista visual de solo lectura y la edición de estructura.

=== Problemas que se descartan o se incorporan parcialmente

La observación sobre *eliminar la organización por pisos y cuartos* se descarta como solución definitiva. La organización jerárquica del hogar es una funcionalidad central que responde a las necesidades identificadas en la investigación con usuarios. El problema no es su existencia sino su implementación actual. La solución es simplificar su presentación y hacer que el home muestre el estado de los dispositivos de forma directa.

La observación sobre el *historial que registra todos los eventos sin filtro* se incorpora como mejora de mediano plazo. No representa un problema crítico que bloquee el uso de la aplicación. Se priorizaran primero los problemas estructurales.

== Evaluación empírica

=== Metodología

La metodología fue presencial, con un integrante del grupo observando y tomando notas. Al participante se le dieron instrucciones verbales sobre el escenario, dejándolo interactuar sin guía adicional salvo que se bloquee. Los datos recolectados fueron la tasa de éxito, el tiempo de completado, la cantidad de errores y otras observaciones cualitativas.

=== Escenarios seleccionados

#table(
  columns: (1fr, 1.2fr, 1.2fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Escenario*], [*Objetivo*], [*Criterio de éxito*]),
  [Controlar una lámpara desde el dashboard],
  [Validar la eficiencia de la navegación principal y la interacción con el slider de brillo.],
  [El usuario localiza la lámpara, accede al detalle y ajusta el brillo en menos de 30 segundos sin ayuda.],

  [Crear una rutina "Buenos días"],
  [Validar la usabilidad del wizard de 4 pasos.],
  [El usuario completa los 4 pasos del wizard y crea la rutina sin errores y sin retroceder más de una vez.],

  [Verificar la seguridad del hogar antes de dormir],
  [Validar sí la estructura de navegación permite encontrar dispositivos de seguridad rápidamente.],
  [El usuario encuentra y confirma el estado de alarma y puerta en menos de 1 minuto.],

  [Cambiar al perfil adolescente e intentar desarmar la alarma],
  [Validar la implementación de restricciones por rol.],
  [El usuario cambia de perfil, ve que el control está deshabilitado y comprende la restricción sin confusión.],

  [Consultar el consumo eléctrico de la última semana],
  [Validar la encontrabilidad de la sección de consumo y la legibilidad de los gráficos.],
  [El usuario navega a consumo e identifica el dispositivo de mayor consumo en menos de 45 segundos.],

  [Recuperarse de una navegación accidental],
  [Validar la capacidad de recuperación y orientación.],
  [El usuario vuelve al dashboard en menos de 2 clics sin expresar confusión.],

  [Verificar que el perfil adolescente no puede acceder a configuración],
  [Validar el guard del router y la coherencia de la restricción visual.],
  [El usuario nota que "Configuración" no aparece. Si escribe la URL manualmente, es redirigido sin error confuso.],
)

=== Análisis de los resultados

El registro completo de los resultados se encuentra en el Anexo. Las fricciones detectadas más relevantes fueron:

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Escenario*], [*Fricción detectada*], [*Acción de mejora*]),

  [(3) Verificar seguridad],
  [El participante trató de buscar los dispositivos en el gráfico del hogar y no los encontró. Luego los encontró en favoritos.],
  [Aumentamos el tamaño del grafico para que se vean mejor los detalles. Seguimos trabajando para mejorar la interacción con el mismo ya que a pesar de no ser de gran utilidad, a primera vista le genera a los usuarios un sentimiento],

  [(5) Consultar consumo],
  [Algunos participantes de edad media primero fueron a "Historial" y no a "Consumo".],
  [No consideramos que esto sea un problema de diseño sino que es una interpretacion vaga del usuario],

  [(7) Acceder a configuración],
  [Ningun participante pudo completar esta tarea. Al cambiarse al perfil adolescente, la opción desaparecía, generando frustración.],
  [La API no permite configurar tipos de usuarios por lo que tambien seria correcto darle acceso a la configuracion a todos los usuarios, en caso de querer cambiar una contraseña desde configuracion, que te pida la antigua],
)

== Evaluación participativa

=== Metodología

La evaluación participativa se llevó a cabo inmediatamente después de la empírica, por lo cual los participantes son los mismos. Fue un cuestionario semi-estructurado, registrándose las respuestas textuales del participante. Las preguntas están organizadas en 4 bloques temáticos.

Las respuestas completas se encuentran en el Anexo.

=== Bloque 1: Navegación y estructura

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },

  table.header([* \# *], [*Pregunta*], [*Justificación*]),

  [1.],
  [Cuando necesitaste encontrar un dispositivo específico, ¿cuál fue tu primer instinto: ir al dashboard, a "Dispositivos" o a "Habitaciones"?],
  [Revela el modelo mental del usuario sobre la organización de la información y valida la arquitectura del sistema.],

  [2.],
  [¿Hubo algún momento en el que no supieras en qué sección de la aplicación estabas? Si es así, ¿qué te generó esa confusión?],
  [Evalúa la visibilidad del estado de navegación y la orientación del usuario.],

  [3.],
  [¿La cantidad de opciones en el menú lateral te pareció adecuada, o sentiste que faltaba o sobraba algo?],
  [Analiza si hay sobrecarga cognitiva o ausencia de funcionalidades esperadas.]
)

=== Bloque 2: Control de dispositivos

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },

  table.header([* \# *], [*Pregunta*], [*Justificación*]),

  [4.],
  [Cuando cambiaste el estado de un dispositivo, ¿el sistema te dio suficiente información para saber que el cambio se aplicó?],
  [Evalúa el feedback visual del sistema.],

  [5.],
  [¿Alguno de los íconos o controles te resultó confuso o no supiste qué hacía sin probarlo?],
  [Detecta problemas de affordance y comprensión.],

  [6.],
  [Si pudieras controlar un solo dispositivo desde la pantalla de inicio, ¿cuál sería y por qué?],
  [Identifica prioridades de uso del usuario.]
)

=== Bloque 3: Perfiles de familia y restricciones

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },

  table.header([* \# *], [*Pregunta*], [*Justificación*]),

  [7.],
  [¿Te pareció lógico que el adolescente pueda ver el estado de la alarma pero no controlarla?],
  [Evalúa la comprensión del modelo de permisos.],

  [8.],
  [¿El selector de perfil en la parte superior te resultó fácil de encontrar y usar?],
  [Verifica visibilidad y accesibilidad del componente.]
)

=== Bloque 4: Experiencia General

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },

  table.header([* \# *], [*Pregunta*], [*Justificación*]),

  [9.],
  [Si tuvieras que describir esta aplicación a alguien que nunca la usó, ¿qué le dirías en una frase?],
  [Evalúa la propuesta de valor percibida.],

  [10.],
  [¿Hubo algo que te generara frustración, inseguridad o incomodidad durante el uso?],
  [Detecta fricciones no relevadas previamente.]
)

// ====================================
// 6. PROTOTIPOS SITIO WEB
// ====================================

= Prototipos sitio web

== Overview -- Resumen de propiedades

La pantalla de overview es la primera vista al iniciar sesión. Presenta un saludo personalizado y un resumen de todas las propiedades del usuario, mostrando para cada una la cantidad de dispositivos activos y el consumo energético. Debajo se listan los dispositivos críticos de todas las propiedades y las rutinas favoritas con acceso rápido para ejecutarlas. Al pie, un resumen energético general con métricas clave (consumo total, dispositivos activos, cantidad de propiedades y dispositivos críticos).

#figure(
  image("assets/image31.png", width: 100%),
  caption: [Overview -- Resumen de propiedades],
)

== Inicio -- Dashboard de propiedad

Al seleccionar una propiedad, se accede al dashboard principal. En la parte superior se muestra una vista isométrica 3D de la vivienda con las habitaciones etiquetadas y los dispositivos activos señalados mediante indicadores de color. A la izquierda se encuentra la barra de navegación lateral con acceso a Inicio, Dispositivos, Habitaciones, Rutinas, Historial, Consumo y Configuración. Debajo de la vista 3D se listan los dispositivos favoritos como tarjetas con su estado y toggle de encendido/apagado, junto a las rutinas configuradas con opción de ejecución inmediata.

#figure(
  image("assets/image40.png", width: 100%),
  caption: [Inicio -- Dashboard de una propiedad (Casa Martinez)],
)

== Dispositivos

La sección de dispositivos presenta una grilla de tarjetas, cada una con el icono del tipo de dispositivo, su nombre, la habitación a la que pertenece, el estado actual (encendido/apagado con detalle según tipo) y un toggle de control. Se incluyen filtros por tipo de dispositivo y por habitación, un botón de favorito en cada tarjeta y un botón prominente "+ Nuevo dispositivo" para agregar nuevos dispositivos a la propiedad.

#figure(
  image("assets/image41.png", width: 100%),
  caption: [Dispositivos],
)

== Habitaciones

La vista de habitaciones organiza los dispositivos por su ubicación física. Cada habitación se muestra como una tarjeta que lista los dispositivos vinculados con su toggle de encendido/apagado y un botón de edición. Las habitaciones sin dispositivos muestran el mensaje "Sin dispositivos vinculados". Cada tarjeta incluye un desplegable "+ Vincular dispositivo" para asociar dispositivos existentes, además de botones para editar o eliminar la habitación. En la esquina superior se ubica el botón "+ Nueva habitación".

#figure(
  image("assets/image19.png", width: 100%),
  caption: [Habitaciones],
)

== Rutinas

La página de rutinas muestra las automatizaciones configuradas como tarjetas horizontales. Cada rutina incluye su nombre, una descripción de las acciones que ejecuta, el horario programado, los días de la semana en que se activa y la cantidad de acciones. Se proporcionan botones de "Ejecutar Ahora" para activación manual y "Ver detalle" para inspeccionar o modificar la rutina. Un toggle permite habilitar o deshabilitar cada rutina, y el botón de favorito permite marcarlas para acceso rápido desde el dashboard.

#figure(
  image("assets/image27.png", width: 100%),
  caption: [Rutinas],
)

// ====================================
// 7. PROTOTIPOS APLICACIÓN MÓVIL
// ====================================

= Prototipos aplicación móvil

== Rediseño de la pantalla de inicio

La versión inicial presentaba una distribución de elementos con igual relevancia visual entre todas las funciones, lo que dificultaba identificar las acciones prioritarias. El término "Home" generaba confusión con "habitación", el buscador era poco distinguible y el espacio disponible estaba mal aprovechado.

Como solución, se rediseñó la pantalla restringiéndola a los dispositivos y rutinas favoritas, decisión respaldada por las entrevistas. Se incorporó una barra de menú superior que permite cambiar de propiedad fácilmente.

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 16pt,
      image("assets/image32.png", width: 100%),
      image("assets/image2.png", width: 100%),
    )
  ),
  caption: [Pantalla de inicio: primer prototipo (izquierda) y versión final (derecha)],
)

== Rediseño de la sección de dispositivos

La versión inicial presentaba múltiples problemas de usabilidad. El término "Espacios" para referirse a las habitaciones resultaba poco representativo. No existía la posibilidad de editar dispositivos ni habitaciones una vez creados. El menú de propiedades era poco intuitivo y las acciones equivalentes presentaban botones de distintos colores.

Como respuesta, se renombró la sección de "Espacios" a "Dispositivos", se incorporó un buscador de dispositivos y se habilitaron opciones para agregar, editar y eliminar tanto dispositivos como habitaciones.

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 16pt,
      image("assets/image17.png", width: 80%),
      image("assets/image6.png", width: 80%),
    )
  ),
  caption: [Sección de dispositivos: primer prototipo (izquierda) y versión final (derecha)],
)

== Rediseño de la sección de perfil

Tras las entrevistas, se determinó que la mayoría de los usuarios consultaba el historial y el consumo de forma esporádica. Se decidió agruparlas dentro de la sección de perfil, evitando que ocupen un lugar prominente en la navegación principal.

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 16pt,
      image("assets/image4.png", width: 80%),
      image("assets/image38.png", width: 80%),
    )
  ),
  caption: [Sección de perfil: primer prototipo (izquierda) y versión final (derecha)],
)

== Rediseño del menú de gestión de propiedades

El menú original era poco intuitivo para el cambio de propiedad, por lo que se optó por un selector más claro y accesible.

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 16pt,
      image("assets/image23.png", width: 80%),
      image("assets/image1.png", width: 80%),
    )
  ),
  caption: [Cambio y gestión de propiedad: prototipo inicial (izquierda) y versión final (derecha)],
)

== Prototipos en orientación horizontal

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image21.png", width: 100%),
    image("assets/image7.png", width: 100%),
    image("assets/image9.png", width: 100%),
    image("assets/image8.png", width: 100%),
  ),
  caption: [Vistas en orientación horizontal],
)

== Versión tablet

#figure(
  box(width: 80%,
    grid(
      columns: 2,
      gutter: 16pt,
      image("assets/image14.png", width: 100%),
      image("assets/image26.png", width: 100%),
    )
  ),
  caption: [Adaptación para tablet],
)

// ====================================
// 8. FLUJOS DE INTERACCION
// ====================================

= Flujos de interacción

== Creación de rutinas

Se decidió agrupar los pasos de crear una rutina en una sola ventana en la versión móvil, junto con agregar horarios default para reducir la cantidad de interacciones complejas.

#figure(
  image("assets/image22.png", width: 80%),
  caption: [Diagrama de flujo: creación de rutinas (web vs. móvil)],
)

=== Versión web

#figure(
  image("assets/image20.png", width: 100%),
  caption: [Página de rutinas -- web],
)

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image42.png", width: 100%),
    image("assets/image43.png", width: 100%),
    image("assets/image16.png", width: 100%),
    image("assets/image24.png", width: 100%),
  ),
  caption: [4 pasos del wizard de creación de rutinas],
)

=== Versión móvil

#figure(
  image("assets/image36.png", width: 25%),
  caption: [Página de rutinas -- móvil],
)

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 12pt,
      image("assets/image37.png", width: 100%),
      image("assets/image28.png", width: 100%),
    )
  ),
  caption: [Menu de creación de rutinas -- móvil],
)

== Desvincular un dispositivo de una habitación

No había suficiente espacio en la aplicación móvil para tener un boton de desvincular junto a cada dispositivo, o una página separada para las habitaciones, por lo que las habitaciones aparecen como grupos en dispositivos y la acción de desvincular es desde la configuración de las habitaciones.

#figure(
  image("assets/image18.png", width: 80%),
  caption: [Diagrama de flujo: desvincular dispositivo (web vs. móvil)],
)

=== Versión web

#figure(
  image("assets/image10.png", width: 51%),
  caption: [Botón de desvincular en página web],
)

=== Versión móvil

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 12pt,
      image("assets/image15.png", width: 100%),
      image("assets/image30.png", width: 100%),
    )
  ),
  caption: [Página de dispositivos y configuración de habitaciones -- móvil],
)

== Agregar un nuevo dispositivo

#figure(
  image("assets/image33.png", width: 80%),
  caption: [Diagrama de flujo: agregar un nuevo dispositivo],
)

#figure(
  box(width: 65%,
    grid(
      columns: 2,
      gutter: 12pt,
      image("assets/image25.png", width: 100%),
      image("assets/image12.png", width: 100%),
    )
  ),
  caption: [Configuración del nuevo dispositivo -- móvil],
)

// ====================================
// 9. DECISIONES DE DISEÑO Y USABILIDAD
// ====================================

= Decisiones de diseño y usabilidad

Para fundamentar cada decisión, nos basamos en los principios de Interaccion Persona-Computadora (HCI) vistos en la materia:

- *Heuristicas de Nielsen:* Visibilidad del estado, correspondencia con el mundo real, control del usuario, consistencia, prevencion de errores, reconocimiento, flexibilidad, estética minimalista y ayuda.
- *Leyes de Gestalt:* Proximidad, similitud, region común y cierre.
- *Leyes de Interaccion:* Ley de Fitts (tamaño de objetivos) y Ley de Hick (toma de decisiones).
- *Niveles de Norman:* Respuestas viscerales (estética), conductuales (uso) y reflexivas (satisfacción).
- *Carga Cognitiva:* Minimizar el esfuerzo mental mediante una estructura clara.
- *Diseño Centrado en el Usuario (UCD):* Proceso de iteración basado en evaluaciones de usabilidad.

== Decisiones de diseño (Identidad y estética)

=== Identidad visual y logo

*Decisión:* Un logo hexagonal con una casa minimalista y ondas de WiFi. \
*Justificación:* Se busca transmitir tecnología y seguridad (nivel visceral). El hexágono da una sensación de estructura solida, y el uso de la ley de cierre de Gestalt permite que el logo sea reconocible incluso en tamaños reducidos.

=== Paleta de colores

*Decisión:* Tema oscuro profundo con indigo para acciones y ámbar para estados activos. \
*Justificación:* El tema oscuro reduce la fatiga visual en entornos hogareños. El indigo permite identificar rápidamente que es interactivo (similitud) y el ámbar resalta lo que está encendido. Todos los colores cumplen con el estándar WCAG AA.

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
  swatch("#fbbf24", "Ambar", "#fbbf24"),
)

#v(0.3em)
#text(size: 10pt, weight: "bold")[Semanticos]
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

=== Tipografia y jerarquía

*Decisión:* Familia tipográfica única (Inter) con pesos variables. \
*Justificación:* Usar una sola fuente ayuda a no saturar al usuario (menor carga cognitiva). La jerarquía se marca con tamaño y grosor del texto de forma natural.

=== Sistema de iconografía

*Decisión:* Iconos SVG personalizados en lugar de emojis. \
*Justificación:* Garantizan consistencia visual en todas las plataformas (Nielsen \#4). Al compartir el mismo grosor de línea y estilo, se facilita su reconocimiento rápido.

=== Agrupamiento visual (Cards)

*Decisión:* Uso de tarjetas con bordes suaves y espaciado consistente. \
*Justificación:* Aplicamos las leyes de proximidad y region común de Gestalt para que el usuario entienda que los controles de un dispositivo pertenecen a ese dispositivo especifico.

=== Variables CSS como fuente de verdad

*Decisión:* Centralización de tokens (colores, espacios, tamaños) en un sistema de variables CSS. \
*Justificación:* Garantiza que la consistencia (Nielsen \#4) se mantenga de forma técnica. Un cambio de tono tras una evaluación de usabilidad impacta en toda la app al instante.

== Decisiones de usabilidad e interacción

=== Estructura de navegación (Sidebar)

*Decisión:* Barra lateral izquierda con 6 secciones principales, colapsable. \
*Justificación:* Al limitar las opciones aplicamos la Ley de Hick, acelerando la toma de decisiones. Mantener los iconos visibles al colapsar permite reconocer las secciones sin recordar sus nombres.

=== Orientacion (Breadcrumbs)

*Decisión:* Migas de pan clickeables en la parte superior. \
*Justificación:* Funcionan como un indicador de lugar constante para evitar que el usuario se sienta perdido. Ademas, ofrecen una "salida de emergencia" rápida (control y libertad).

=== Arquitectura multi-casa

*Decisión:* Jerarquía clara de Propiedad > Sección > Dispositivo. \
*Justificación:* Mapeamos la estructura física del mundo real al sistema (Nielsen \#2). El selector de casas en la parte superior refleja el modelo mental del usuario.

=== Vista Overview (Resumen general)

*Decisión:* Una pantalla principal que resume lo crítico de todas las propiedades. \
*Justificación:* El usuario puede ver estados de alerta o rutinas favoritas sin navegar casa por casa, reduciendo drasticamente la carga de trabajo y el tiempo de respuesta.

=== Interaccion directa con dispositivos

*Decisión:* Botones y controles de gran tamaño (mínimo 44px). \
*Justificación:* Aplicamos la Ley de Fitts: objetivos más grandes son más fáciles y rápidos de clickear, reduciendo errores accidentales.

=== Feedback y estado del sistema

*Decisión:* Notificaciones instantáneas (toasts) y animaciones de estado. \
*Justificación:* El sistema debe responder siempre (Nielsen \#1). Si una luz se enciende, el icono brilla de inmediato, previniendo la incertidumbre de no saber si el comando funcionó.

=== Prevención de errores y valores por defecto

*Decisión:* Mensajes de confirmación antes de borrar y sliders pre-seteados en valores comunes. \
*Justificación:* Es mejor prevenir el error que reportarlo. Los valores por defecto (como el brillo al 80%) ahorran pasos innecesarios.

=== Terminologia y lenguaje

*Decisión:* Uso de lenguaje cotidiano (español rioplatense) y consistencia léxica. \
*Justificación:* Evitamos la jerga técnica. Usar siempre "Hogar" o "Rutina" ayuda a que el usuario construya un modelo mental solido.

=== Libertad y control

*Decisión:* Todas las acciones son reversibles (Undos conceptuales). \
*Justificación:* Poder apagar una rutina en lugar de borrarla, o desvincular un dispositivo sin eliminarlo, le da al usuario la confianza para explorar sin miedo.

=== Experiencia de entrada (Splash Screen)

*Decisión:* Animación de bienvenida inmersiva pero skipeable. \
*Justificación:* Establece el tono de calidad de la app (nivel visceral) y usa la metáfora de "entrar" a la casa. Al permitir saltearla, respetamos el control del usuario.

=== Estrategia de prototipado (Auto-login)

*Decisión:* Bypass de la pantalla de login para las pruebas de usabilidad. \
*Justificación:* Durante las evaluaciones con usuarios (UCD), el foco esta en el control de la casa y no en la gestión de contraseñas. Esto reduce la fricción inicial.

// ====================================
// ANEXO
// ====================================

#pagebreak()

= Anexo <anexo>

== Observaciones participativas: resultados por escenario

=== Encender y apagar un dispositivo individual (Google Home web y móvil)

- *Luis (experto):* Entra directamente a la pantalla de inicio, encuentra la habitación, toca el icono de la luz y la apaga en aproximadamente menos de 10 segundos. Nota que el móvil es más cómodo ya que los iconos de la web le parecen pequeños y reconoce que el estado de encendido/apagado podria ser más claro en la web.

- *Sofia (media):* Tarda unos segundos en escanear la pantalla de inicio. Trata de encontrar la barra de busqueda (no existe) y luego encuentra el dispositivo a través de las habitaciones. Menciona que pensaba que los dispositivos estarian en una lista todos juntos y no por cuartos. Prefiere la versión móvil.

- *Laura (sin experiencia):* Al principio no sabe por donde empezar. Toca el nombre y se va a una pantalla de detalles. Vuelve atrás y toca el icono que ahi lo enciende. No le parecen claros los botones.

- *Cecilia (baja-media):* Navega con fluidez relativa. Encuentra el dispositivo rápido pero duda de si realmente quedo encendido ya que el cambio de color del icono es sutil. Prefiere la versión móvil.

=== Crear una rutina/automatización (Google Home web y móvil, Amazon Alexa web)

- *Luis (experto):* En Google Home encuentra "Rutinas" velozmente. Crea la rutina con facilidad pero destaca que hay demasiados pasos. En Alexa siente que la web está desactualizada y que los menus están anidados de forma poco logica.

- *Sofia (media):* Al principio no logra encontrar "Rutinas" en Google Home. Luego sigue el flujo pero se traba confundiendo el "que debe pasar" con el "que debe activar la rutina". En Alexa directamente se rindio.

- *Laura (sin experiencia):* No logra completar el escenario en ninguna de las dos aplicaciones sin asistencia. Le confunde la cantidad de pantallas.

- *Cecilia (baja-media):* Logra llevar a cabo el escenario en Google Home pero opina que sería más sencillo si se le proveyera una plantilla. En Alexa le parece confuso que se llame "rutina" pero funcione diferente a Google.

=== Agrupar dispositivos por habitación (Amazon Alexa web, Home Assistant móvil)

- *Luis (experto):* En Alexa encuentra "Grupos" rápidamente. En Home Assistant reconoce que es una app para perfiles tecnicos.

- *Sofia (media):* En Alexa logra crear un grupo pero no ve el sentido de tener "grupo" y "habitación" en una misma aplicación. En Home Assistant no logra completar la tarea.

- *Laura (sin experiencia):* En Alexa no logra encontrar donde crear grupos. No relaciona la palabra "grupo" con lo que quiere hacer. En Home Assistant abandona la tarea.

- *Cecilia (baja-media):* En Alexa completa la tarea por prueba y error. En Home Assistant logra orientarse por los iconos pero le cuesta completar la tarea sola.

=== Activar una escena predefinida (Google Home móvil, Home Assistant móvil)

- *Luis (experto):* En Google Home activa la escena sin problema. Reconoce que no se ve sí la escena está activa o no. En Home Assistant le interesa la personalización.

- *Sofia (media):* En Google Home no distingue sí lo que esta en la pantalla es una escena o un dispositivo individual. En Home Assistant no logra encontrar las escenas.

- *Laura (sin experiencia):* Pregunta primero qué es una escena. No entiende el escenario en ninguna de las dos aplicaciones.

- *Cecilia (baja-media):* En Google Home activa la escena bastante rápido pero duda de si se activó realmente. En Home Assistant no completa la tarea.

=== Incorporar nuevo dispositivo (Amazon Alexa web, Home Assistant móvil)

- *Luis (experto):* En Alexa agrega el dispositivo sin dificultad. Apreciaria poder agregar varios dispositivos a la vez. En Home Assistant valora las opciones pero reconoce que es demasiado para el usuario común.

- *Sofia (media):* En Alexa sigue los pasos del asistente. Le parece exhaustivo buscar manualmente en la lista y sugiere un buscador. En Home Assistant se rinde.

- *Laura (sin experiencia):* En Alexa se traba en el primer paso porque el asistente pregunta si el dispositivo es compatible.

- *Cecilia (baja-media):* En Alexa completa el proceso pero lo siente largo para algo simple. En Home Assistant logra iniciar el proceso pero no completarlo.

#pagebreak()

== Evaluación predictiva: resultados por persona

=== Persona 1

- Cerrar las cortinas a medias no es intuitivo ni directo.
- Sin division por sala, si hay muchos dispositivos con el mismo nombre habria un problema de identificacion.
- El término "ejecutar" no queda claro para el usuario.
- La organización por pisos y cuartos marea más de lo que suma. Propone que directamente aparezca en el home que luces están prendidas.
- Si una palabra/habitación es clickeable, el resto también debería serlo (consistencia).
- En algunos lugares la letra es muy chica y el contraste es insuficiente.
- Poca consistencia visual en la sección de cuartos y rutinas.
- Propone eliminar la sección de cuartos porque resta más de lo que suma.
- El switch para encender/apagar debe ser siempre el mismo en toda la app.

=== Persona 2

- Reaccion positiva al ver el gráfico de la casa.
- Modificar el gráfico de la casa es poco intuitivo.
- El boton para agregar dispositivos tiene texto muy pequeno.
- Es muy difícil darse cuenta de como agregar un dispositivo: el color y la disposición son muy distintos a los de agregar habitación.
- No queda claro sí en una rutina algo se esta encendiendo o apagando.
- Si algo aparece en gris dentro de una rutina parece deshabilitado en lugar de apagado.
- Falta consistencia en las líneas de la interfaz.
- La creación de elementos en general es muy compleja y sin consistencia.
- El gráfico de la casa es demasiado complejo. Propone que sea solo una vista visual no editable.

=== Persona 3

- Los elementos son muy pequeños y difíciles de leer.
- Agregar cosas es antiintuitivo (coincide con las otras personas).
- El encender/apagar de ciertos dispositivos con "sí/no" es muy extraño.
- Las rutinas no se pueden editar.
- El historial registra absolutamente todo, lo cual satura la vista. Debería poder filtrarse o agruparse.

#pagebreak()

== Evaluación empírica: resultados por perfil

=== Power User - Participante: Luis

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observación*]),
  [1], [Controlar una lámpara desde el dashboard],
  [Lo hizo sencillamente y mucho más rápido de lo estimado. Mencionó: "Qué copado esto!" al ver el esquema de los cuartos.],
  [2], [Crear una rutina "Buenos días"],
  [Hizo la rutina fácilmente dentro de los 4 pasos. Se pregunto: "Cuál es la del pasillo?" pero eligio la que pensaba que era.],
  [3], [Verificar la seguridad del hogar],
  [Lo hizo velozmente desde la pantalla de inicio porque se acordaba de haberlos visto.],
  [4], [Cambiar al perfil adolescente],
  [Se hizo sin problema, entendiendo las restricciones del nuevo usuario.],
  [5], [Consultar el consumo eléctrico],
  [Lo hizo en menos de 30 segundos.],
  [6], [Recuperarse de navegación accidental],
  [Se completó velozmente.],
  [7], [Verificar restricción de configuración],
  [Se rindió a los 15 segundos. Se frustró.],
)

=== Carolina / Gestora del hogar (Participante: Cecilia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observación*]),
  [1], [Controlar una lámpara desde el dashboard],
  [Encontró la lámpara en el esquema de la casa dentro de los 30 segundos.],
  [2], [Crear una rutina "Buenos días"],
  [Creo la rutina sin retroceder. Solo busco un poco la lámpara del pasillo.],
  [3], [Verificar la seguridad del hogar],
  [Encontró alarma y puerta en menos de 1 minuto usando el menú lateral.],
  [4], [Cambiar al perfil adolescente],
  [Lo hizo velozmente. Opino: "El adolescente debería poder cambiar la alarma ante una emergencia."],
  [5], [Consultar el consumo eléctrico],
  [Pudo completarlo pero al principio fue a "Historial".],
  [6], [Recuperarse de navegación accidental],
  [Lo hizo velozmente.],
  [7], [Verificar restricción de configuración],
  [Le genero mucha ansiedad. Dijo: "Dónde está? No lo puedo hacer."],
)

=== Marta / Usuario tradicional (Participante: Hannah)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observación*]),
  [1], [Controlar una lámpara desde el dashboard],
  [Se hizo sin problema. Dedujo que lo que brillaba era la lámpara.],
  [2], [Crear una rutina "Buenos días"],
  [Lo hizo velozmente pero retrocedio porque clickeo dos veces un dispositivo y lo desagrego.],
  [3], [Verificar la seguridad del hogar],
  [Logró la tarea en menos de 1 minuto. Pensó brevemente que la barra estaba arriba.],
  [4], [Cambiar al perfil adolescente],
  [Se hizo exitosamente aunque le costo buscar donde cambiar el perfil.],
  [5], [Consultar el consumo eléctrico],
  [Lo hizo velozmente sin problema.],
  [6], [Recuperarse de navegación accidental],
  [Hecho velozmente.],
  [7], [Verificar restricción de configuración],
  [Se rindió al segundo. Dijo: "No bueno, no puedo, no lo encuentro."],
)

=== Marta / Usuario tradicional (Participante: Sofia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observación*]),
  [1], [Controlar una lámpara desde el dashboard],
  [Lo hizo rápidamente. Accedió a la lámpara desde el esquema del cuarto.],
  [2], [Crear una rutina "Buenos días"],
  [Lo hizo velozmente, sin errores y sin retroceder.],
  [3], [Verificar la seguridad del hogar],
  [Recordaba haber visto los dispositivos en la pantalla principal.],
  [4], [Cambiar al perfil adolescente],
  [Exitoso. Mencionó: "El coso ese para cambiar de perfil tendría que ser más grande."],
  [5], [Consultar el consumo eléctrico],
  [Lo hizo rápido. Mencionó: "Me hace acordar al consumo del iPhone."],
  [6], [Recuperarse de navegación accidental],
  [Lo hizo rápido usando el atajo del extremo superior izquierdo.],
  [7], [Verificar restricción de configuración],
  [Se rindió al segundo. Dijo: "Debería aparecer Configuración, de última que no lo pueda acceder pero tiene que estar."],
)

#pagebreak()

== Evaluación participativa: respuestas por perfil

=== Valentina / Power user (Participante: Luis)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dispositivos."],
  [2], [Momento de confusión en la navegación], ["No."],
  [3], [Opciones del menú lateral], ["Me pareció adecuada."],
  [4], [Feedback al cambiar estado de dispositivo], ["Sí."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["Las luces ya que siempre todos las dejan prendidas."],
  [7], [Comprensión de restricción del perfil adolescente], ["Sí."],
  [8], [Lógica de ver pero no controlar la alarma], ["Sí."],
  [9], [Selector de perfil fácil de encontrar], ["Sí."],
  [10], [Descripción de la app en una frase], ["Gestionador de hogares."],
  [11], [Frustración durante el uso], ["Al intentar buscar Configuración, que al final ni estaba."],
  [12], [Facilidad general de uso (1-5)], ["4."],
)

=== Gestor del hogar - Participante: Cecilia

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusión en la navegación], ["No. Solo me generó confusión que no encontre Configuración."],
  [3], [Opciones del menú lateral], ["En el de adolescente faltaba Configuración."],
  [4], [Feedback al cambiar estado de dispositivo], ["Sí."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["La luz y la alarma. La luz porque me la olvido de apagar y la alarma por seguridad."],
  [7], [Comprensión de restricción del perfil adolescente], ["Sí, porque es menor de edad."],
  [8], [Lógica de ver pero no controlar la alarma], ["Debería poder controlarla porque ante un incendio debería tener acceso."],
  [9], [Selector de perfil fácil de encontrar], ["Sí."],
  [10], [Descripción de la app en una frase], ["Administrador de dispositivos."],
  [11], [Frustración durante el uso], ["No se podia ver bien la pantalla por el contraste de la luz."],
  [12], [Facilidad general de uso (1-5)], ["4."],
)

=== Usuario tradicional - Participante: Hannah

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusión en la navegación], ["No."],
  [3], [Opciones del menú lateral], ["Esta bien pero Configuración debería estar en el header o hacerse más visible."],
  [4], [Feedback al cambiar estado de dispositivo], ["Sí."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["La lámpara principal porque me la olvido de apagar."],
  [7], [Comprensión de restricción del perfil adolescente], ["Sí."],
  [8], [Lógica de ver pero no controlar la alarma], ["Esta bien."],
  [9], [Selector de perfil fácil de encontrar], ["No. Esperaba que estuviera en la barra lateral o cerca del nombre de la casa."],
  [10], [Descripción de la app en una frase], ["Seguridad."],
  [11], [Frustración durante el uso], ["Lo de la configuración."],
  [12], [Facilidad general de uso (1-5)], ["3,5."],
)

=== Usuario tradicional - Participante: Sofia

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusión en la navegación], ["No."],
  [3], [Opciones del menú lateral], ["Esta bien."],
  [4], [Feedback al cambiar estado de dispositivo], ["Sí."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["El aire acondicionado porque siempre lo dejo prendido."],
  [7], [Comprensión de restricción del perfil adolescente], ["Sí."],
  [8], [Lógica de ver pero no controlar la alarma], ["Sí."],
  [9], [Selector de perfil fácil de encontrar], ["A mi sí pero seguramente que para todos no."],
  [10], [Descripción de la app en una frase], ["Control de dispositivos de hogares."],
  [11], [Frustración durante el uso], ["Cuando no pude encontrar Configuración."],
  [12], [Facilidad general de uso (1-5)], ["4,5."],
)
