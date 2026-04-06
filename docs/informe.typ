// ============================================================
// HomeCore -- TP Gestion de Casas Inteligentes (Primera Entrega)
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
      Pagina #counter(page).display() de #counter(page).final().first()
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
    #text(size: 18pt)[Gestion de Casas Inteligentes]
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
      [Victoria Helena Park], [],
      [Maria Del Pilar Resek], [],
      // Agregar mas integrantes aqui
    )
    #v(2em)
    #text(size: 11pt, fill: gray)[
      #datetime.today().display("[day]/[month]/[year]")
    ]
  ]
  #v(3fr)
]

// ====================================
// INDICE
// ====================================

#page(numbering: none, header: none, footer: none)[
  #outline(
    title: [Indice],
    indent: 1.5em,
    depth: 3,
  )
]

#counter(page).update(1)

// ====================================
// INTRODUCCION
// ====================================

= Introduccion

Este trabajo practico consiste en desarrollar una aplicacion diseñada para la administracion y el control remoto de dispositivos inteligentes en un hogar. Su objetivo principal es permitir al usuario gestionar el estado y funcionamiento de estos dispositivos de manera optima, eficiente y centralizada. La aplicacion consiste en dos interfaces de usuario: un sitio web (accesible desde navegadores de escritorio) y una aplicacion movil (nativa para Android).

Esta primera entrega se enfoca en el diseño de interfaces del sitio web y la aplicacion movil, aplicandose la filosofia de diseño centrado en el usuario (UCD), tal como se vio en clase.

// ====================================
// 1. USUARIOS REPRESENTATIVOS
// ====================================

= Usuarios representativos

Se determinaron las siguientes caracteristicas de los potenciales usuarios de la aplicacion:

- *Ingreso mensual:* Los usuarios de una aplicacion domotica poseen uno o mas dispositivos inteligentes, los cuales estan fuera del alcance de la mayoria de la poblacion. Es por esto que se estima que los usuarios se concentran en los hogares del decil de ingresos mas alto, los cuales tienen un ingreso per capita aproximadamente de 1.699.225 pesos ("El Ingreso Promedio Sube Pero La Brecha Persiste." 2025) segun la EPH (Encuesta Permanente de Hogares) del INDEC.

- *Comodidad tecnologica:* Se requiere un nivel de conocimiento basico sobre el uso de aplicaciones tal como saber que es una notificacion, usar el menu lateral para cambiar de pagina, etc. para aprovechar en su totalidad a la aplicacion.

- *Edad:* La caracteristica de la edad esta estrechamente relacionada a la comodidad tecnologica. Es por esto que se predice que habra un limite en la cantidad de adultos mayores que podran usar la app, asi como tambien lo habra en niños todavia muy pequeños para tener la suficiente responsabilidad que conlleva usar la aplicacion.

// ====================================
// 2. OBSERVACIONES PARTICIPATIVAS
// ====================================

= Observaciones participativas

Durante la etapa de descubrimiento, el enfoque fue el de observacion participativa, una tecnica de evaluacion empirica en la que los participantes interactuan con aplicaciones ya existentes en el mercado mientras se observa y registra aquellas interacciones con el objetivo de construir soluciones de diseño que se vinculen estrechamente con las necesidades del usuario.

La recoleccion de datos se realizo en cortas sesiones individuales de entre 10 y 15 minutos, donde cada participante fue guiado a traves de escenarios ya establecidos sobre aplicaciones reales existentes de gestion de dispositivos inteligentes. Estas sesiones fueron hechas en un formato mixto: presencial o virtual a traves de videollamada (computadora o celular), lo cual logro ampliar el alcance de la muestra.

Durante cada sesion, se tomaron notas sobre las acciones realizadas asi tambien como las expresiones ya sean de confusion o satisfaccion. Tambien se utilizo la tecnica de pensamiento en voz alta, con el objetivo de capturar el modelo mental y las expectativas.

La seleccion de participantes fue variada, incluyendo personas con experiencia previa en el uso de dispositivos inteligentes y personas sin experiencia en el area. Esto se decidio de esta manera por la necesidad de identificar patrones en los futuros usuarios expertos asi como los usuarios novatos dado que la aplicacion busca ser accesible para ambos perfiles. Tambien se trato de buscar diversidad etaria.

== Aplicaciones seleccionadas

Las aplicaciones seleccionadas y sus respectivas justificaciones son las siguientes:

- *Web 1 -- Google Home:* Google Home es una de las plataformas de gestion de hogares inteligentes mas conocida y utilizada mundialmente, soportando una gran variedad de dispositivos. Su interfaz web no solo logra controlar los dispositivos y generar rutinas, lo cual nuestra aplicacion aspira a hacer. Se puede decir que esta aplicacion es una de las que lidera el campo de gestion de dispositivos inteligentes y esta en constante desarrollo, recientemente incorporando a Gemini (inteligencia artificial) a sus servicios.

- *Web 2 -- Amazon Alexa:* Alexa es la principal aplicacion competidora de Google Home, ofreciendo servicios como gestionar dispositivos inteligentes, crear rutinas y administrar grupos de dispositivos por habitacion. En lo que respecta a interfaces visuales, Alexa tiene una filosofia de diseño distinta a Google Home, ofreciendo mas opciones y configuraciones; lo cual enriquece el analisis de que nivel de complejidad y control agrada a los usuarios.

- *Movil 1 -- Google Home:* La version movil de Google Home es la referencia mas directa de la aplicacion movil a diseñar. Esta aplicacion logra controlar dispositivos, crear escenas y rutinas, asi como recibir notificaciones; las cuales son funciones que se desea que nuestra aplicacion tenga. La version movil de Google Home adapta los flujos de interaccion de la web a pantallas pequeñas, manteniendo aun asi su diseño simple y limpio.

- *Movil 2 -- Home Assistant:* Home Assistant es una plataforma de codigo abierto con una comunidad que no solo es muy activa, sino que tambien posee un mayor nivel tecnico. Su inclusion en las aplicaciones a analizar se debe a que permite observar como usuarios sin experiencia interactuan con una interfaz mas densa en informacion y opciones. Esto informa que elementos de organizacion son necesarios sin perder al usuario novato.

== Escenarios preestablecidos

Los escenarios preestablecidos para los participantes, asi como la justificacion, son los siguientes:

#table(
  columns: (auto, auto, 1fr, 1fr),
  align: (center, left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Aplicacion*], [*Escenario*], [*Justificacion*]),
  [1],
  [Google Home\ (web y movil)],
  [Encender una luz especifica de una habitacion determinada y luego apagarla.],
  [Es la accion mas basica y probablemente la mas frecuente. Permite entender como el usuario localiza un dispositivo dentro de la interfaz, exponiendo que tan intuitiva es.],

  [2],
  [Google Home\ (web y movil),\ Amazon Alexa (web)],
  [Crear una rutina que apague todas las luces del hogar a las 23 hs de forma automatica.],
  [La creacion de rutinas es una de las funcionalidades principales. Este escenario permite ver como el usuario busca y utiliza esta funcionalidad, identificando errores frecuentes o patrones.],

  [3],
  [Amazon Alexa (web),\ Home Assistant\ (movil)],
  [Organizar/agrupar los dispositivos en habitaciones.],
  [La organizacion de dispositivos por habitaciones es un modelo mental que los usuarios traen de su realidad. Permite evaluar la intuitividad y accesibilidad de la categorizacion y agrupacion.],

  [4],
  [Google Home\ (movil),\ Home Assistant\ (movil)],
  [Activar una escena que baje las luces y cierre las persianas.],
  [Permite observar como los usuarios manejan multiples dispositivos como si fueran uno, y si identifican visualmente que escenas tienen disponibles y su estado actual.],

  [5],
  [Amazon Alexa (web),\ Home Assistant\ (movil)],
  [Simular agregar un nuevo dispositivo al sistema y asignarle a una habitacion.],
  [Permite identificar que informacion y asistencia visual del sistema facilita a ambos perfiles de usuario en el proceso de incorporacion de dispositivos.],
)

== Conclusiones de las observaciones

Segun las respuestas de cada participante (detalladas en el Anexo), se definieron varias conclusiones:

- En cuanto a la comparacion entre la version movil y la web, los perfiles novatos prefirieron la primera ya que el uso les resultaba mas natural y comodo.

- Crear rutinas se les hizo demasiado complejo a los usuarios novatos, los cuales sugirieron que se muestren ejemplos o plantillas para crearlas.

- Se observo confusion porque muchos participantes no comprendian la diferencia entre _rutina_, _grupo_ o _escena_, y la diferenciacion visual no era suficiente.

- La falta de feedback tras activar una accion genero mucha incertidumbre.

- Varios participantes concluyeron que Home Assistant es demasiado complejo para aquellos que no tienen mucha experiencia con este tipo de aplicaciones.

// ====================================
// 3. ENTREVISTAS Y ENCUESTAS
// ====================================

= Entrevistas y encuestas

Se realizaron una serie de 4 entrevistas y una encuesta mediante Google Forms. Las preguntas, asi como la justificacion de ellas, fueron las siguientes:

#table(
  columns: (1fr, 1fr),
  align: (left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Pregunta*], [*Justificacion*]),

  [Que tan familiarizado estas con la tecnologia?\ _(La uso solo para lo basico / Me manejo bien con apps cotidianas / Aprendo rapido tecnologias nuevas / Soy entusiasta tecnologico)_],
  [Permite clasificar a los usuarios segun su nivel de conocimiento tecnologico, clave para diseñar una aplicacion accesible tanto para principiantes como para avanzados.],

  [Cuantos dispositivos tecnologicos utilizas en tu hogar?\ _(Ninguno / 1--3 / 3--5 / Mas de 5)_],
  [Ayuda a dimensionar el nivel de interaccion tecnologica en el hogar y la complejidad del ecosistema que el usuario maneja.],

  [Que tipo de dispositivos inteligentes utilizas en tu hogar?],
  [Permite identificar que dispositivos son mas comunes y relevantes para los usuarios.],

  [Cuales son los que mas utilizas?],
  [Ayuda a detectar los dispositivos prioritarios para el usuario, lo que permite enfocar la aplicacion en lo mas usado.],

  [Podrias mencionar que aplicaciones utilizas para manejar estos dispositivos?],
  [Permite conocer herramientas actuales del usuario y detectar oportunidades de mejora frente a soluciones existentes.],

  [Utilizas mas un smartphone o una pagina web? Por que?],
  [Ayuda a definir la plataforma principal de uso (mobile o web) y entender las preferencias del usuario.],

  [Segun tu experiencia, que cambiarias o que desventajas encuentras en las aplicaciones que utilizas?],
  [Permite identificar _pain points_ actuales, fundamentales para proponer una solucion superadora.],

  [Controlas tus dispositivos principalmente desde dentro de casa o de forma remota?\ _(Desde dentro / De forma remota / Ambos)_],
  [Ayuda a entender el contexto de uso (presencial o remoto), clave para definir funcionalidades.],

  [Que tan seguido revisas o ajustas la configuracion de las apps que usas?\ _(Nunca / Rara vez / A veces / Frecuentemente)_],
  [Permite evaluar el nivel de interaccion avanzada del usuario con la tecnologia.],

  [Cuando queres cambiar el estado de un dispositivo, que es lo primero que buscas?\ _(Scrolleo hasta encontrarlo / Busco en el historial / Busco el nombre en el buscador)_],
  [Ayuda a entender el comportamiento del usuario dentro de una interfaz y optimizar la usabilidad.],

  [Te gustaria recibir notificaciones del sistema? De que tipo?\ _(Alertas de seguridad / Ejecucion de rutinas / Consumo elevado / Estado de dispositivos / Prefiero no recibir)_],
  [Permite identificar que tipo de informacion es relevante para el usuario y evitar sobrecarga de notificaciones.],

  [Que es lo primero que haces cuando llegas a tu casa?],
  [Ayuda a detectar patrones de comportamiento y posibles automatizaciones utiles.],

  [Cuando fue la ultima vez que tuviste un problema con un electrodomestico? Que ocurrio?],
  [Permite identificar situaciones problematicas reales en el uso cotidiano.],

  [Cual de estas situaciones te genera mas ansiedad?\ _(Salir apurado sin revisar todo / No acordarme si prendi o apague algo / Quedarme pensando si corte el aparato a la noche)_],
  [Ayuda a detectar puntos de friccion emocional, importantes para diseñar soluciones que generen tranquilidad.],

  [Cual es tu rutina antes de salir de tu casa?],
  [Permite identificar habitos repetitivos que podrian automatizarse.],

  [Hay algun electrodomestico que revises dos o tres veces antes de irte o antes de dormir? Por que?],
  [Permite detectar dispositivos criticos y oportunidades de mejora en control o seguridad.],

  [Hay algun electrodomestico que NO tocarias remotamente aunque pudieras?],
  [Ayuda a identificar limites de confianza en la automatizacion.],

  [Discutiste alguna vez con alguien de tu casa por como usa los electrodomesticos?],
  [Permite detectar conflictos de uso y problemas de convivencia relacionados con la tecnologia.],

  [Hay alguna tarea relacionada con electrodomesticos automatizados que odias hacer?],
  [Permite identificar tareas repetitivas o molestas que podrian simplificarse.],

  [Hay algun electrodomestico que sabes que gasta mucho pero igual usas sin control?],
  [Permite entender la relacion entre consumo, conciencia y comportamiento real del usuario.],
)

== Respuestas obtenidas

Se noto un gran desconocimiento sobre las aplicaciones de domotica por parte de los entrevistados. Buena parte solo controlaba sus dispositivos electronicos de manera fisica o directamente no tenia electrodomesticos inteligentes, por lo que funciones mas avanzadas como las rutinas no deberian ser el foco de la aplicacion.

Otra funcion que no es de uso cotidiano para la mayoria de los usuarios es la de consultar el consumo electrico. El 60% prefiere no recibir notificaciones de consumo electrico elevado, mientras que la mayoria ya sabe que electrodomesticos tienen un consumo elevado. Se estima que esto es algo que los usuarios solo revisaran si reciben una factura muy elevada, por lo que se diseñaron las interfaces pensando que seria la funcionalidad menos utilizada.

Al preguntar sobre las rutinas de las personas se noto que la mayoria tiene un dispositivo inteligente en particular que utiliza mucho, ya sea una alarma, camaras o un instrumento de cocina. Esto llevo a agregar la posibilidad de marcar dispositivos o rutinas como favoritos, los cuales aparecen en el menu inicial de la casa.

// ====================================
// 4. MODELOS DE PERSONAS
// ====================================

= Modelos de personas

== Definicion de atributos

Se seleccionaron los siguientes atributos por ser factores que influyen directamente en como un usuario interactua con una aplicacion de domotica:

#table(
  columns: 3,
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header(
    [*Atributo*], [*Valores posibles*], [*Justificacion*],
  ),
  [Edad],
  [12-17 / 18-30 / 31-50 / 51-70],
  [Se segmenta en 4 rangos porque cada uno presenta diferencias significativas en adopcion tecnologica: 18-30 son nativos digitales, 31-50 adoptaron tecnologia en edad adulta, 51-70 requieren mayor curva de aprendizaje. 12-17 representa a "hijos menores" con responsabilidades crecientes.],

  [Comodidad tecnologica],
  [Novice: funciones basicas (WhatsApp). \
   Average: usa apps variadas, configura dispositivos. \
   Power: experto, usa funciones avanzadas y automatizaciones.],
  [Describe comportamientos observables concretos para evitar ambiguedad.],

  [Composicion del hogar],
  [Solo / Pareja / Familia con hijos (menores/mayores)],
  [Determina usuarios simultaneos, restricciones de acceso y complejidad de rutinas.],

  [Tipo de vivienda],
  [Departamento / Casa],
  [Influye en la cantidad de dispositivos y la estructura de la navegacion (habitaciones).],

  [Experiencia en domotica],
  [Nunca / Alguna app / Ecosistema completo],
  [Determina el modelo mental previo y las expectativas del usuario.],

  [Ingreso mensual],
  [2-4 SM / 4-8 SM / 8+ SM],
  [Determina la inversion potencial en el ecosistema de dispositivos.],
)

== Combinaciones representativas

#table(
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
)

*Justificacion de la seleccion:*

-- *UR1 (La Entusiasta):* Usuaria que busca eficiencia extrema y personalizacion. Validara funciones avanzadas.
-- *UR2 (La Administradora):* Usuario tipico del mercado que busca seguridad familiar. Validara control parental y robustez.
-- *UR3 (La Recien Llegada):* Usuaria con alta friccion potencial. Validara la accesibilidad y claridad de la interfaz.

== Persona 1: Valentina "La Power User"

#grid(
  columns: (auto, 1fr),
  gutter: 20pt,
  stack(
    dir: ttb,
    image("assets/personas/santiago.jpg", width: 4cm),
    v(0.5em),
    align(center)[#text(style: "italic", size: 9pt)["Si puedo automatizarlo, lo hare. Si no, lo programo."]]
  ),
  table(
    columns: (auto, 1fr),
    align: (left, left),
    stroke: 0.5pt,
    inset: 8pt,
    [*Nombre*], [Valentina],
    [*Edad*], [27 años],
    [*Ocupacion*], [Desarrolladora de software (Backend)],
    [*Ubicacion*], [Departamento en Palermo, CABA],
    [*Tecnologia*], [Nivel 3 (Power User)],
    [*Dispositivos*], [12+ (Luces, Cerradura, AC, Sensores, Hub)],
  )
)

=== Bio
Valentina vive sola y trabaja de forma remota. Su casa es su laboratorio. Disfruta optimizando cada aspecto de su vida: desde el cafe que se prepara solo al despertar hasta las luces que cambian de color segun su calendario de reuniones. No tolera la latencia ni las apps que requieren muchos clics para tareas simples.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Centralizacion total: una sola app para todo.
    - Automatizaciones complejas y encadenadas.
    - Analisis de datos (consumo, temperatura).
  ],
  block(fill: rgb("#FFEBEE"), inset: 8pt, radius: 4pt, width: 100%)[
    *Frustraciones*
    - Apps con "paredes cerradas" (no API).
    - Interfaces lentas o con animaciones largas.
    - Falta de control granular (ej. solo on/off).
  ]
)

=== Escenarios de uso
- *Escenario 1 (Eficiencia):* Valentina sale a correr. Al alejarse 100m de su casa (geofencing), la app debe ejecutar "Modo Ausente" sin preguntarle, pero enviando una confirmacion silenciosa.
- *Escenario 2 (Analisis):* Sospecha que el aire acondicionado consume de mas. Entra a la seccion de consumo para comparar el gasto de esta semana vs. la anterior.
- *Escenario 3 (Control):* Durante una pelicula, quiere bajar la intensidad de las luces del living al 15% y cambiar el tono a calido sin salir del dashboard principal.

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
    [*Ocupacion*], [Contadora Publica],
    [*Ubicacion*], [Casa en Martinez, GBA],
    [*Tecnologia*], [Nivel 2 (Average)],
    [*Dispositivos*], [15+ (Alarma, Camaras, Riego, Luces, Porton)],
  )
)

=== Bio
Madre de dos hijos y profesional ocupada. Para ella, la domotica no es un hobby, es una herramienta de seguridad y ahorro de tiempo. Su principal preocupacion es que sus hijos lleguen bien del colegio y que la casa quede protegida cuando la familia sale. Valora la claridad y la confiabilidad por sobre la personalizacion extrema.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Paz mental: saber que la casa esta segura.
    - Control de acceso para sus hijos y servicio.
    - Rutinas de ahorro energetico (luces y riego).
  ],
  block(fill: rgb("#FFEBEE"), inset: 8pt, radius: 4pt, width: 100%)[
    *Frustraciones*
    - Notificaciones falsas o confusas de la alarma.
    - No saber si dejo algo encendido al irse.
    - Procesos de configuracion dificiles de recordar.
  ]
)

=== Escenarios de uso
- *Escenario 1 (Seguridad):* Carolina esta en una reunion y recibe un aviso: "Hijo 1 ingreso a las 16:15". Verifica en la app que la alarma se desactivo correctamente.
- *Escenario 2 (Mantenimiento):* El pronostico anuncia lluvia. Carolina abre la app para desactivar la rutina de riego automatico de esa tarde con un simple toggle.
- *Escenario 3 (Control Parental):* Configura la tablet de su hijo menor para que solo pueda controlar las luces de su cuarto, restringiendo el acceso a la alarma y la cerradura principal.

#pagebreak()

== Persona 3: Marta "La Usuaria Tradicional"

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
    [*Ocupacion*], [Jubilada],
    [*Ubicacion*], [Departamento en Belgrano, CABA],
    [*Tecnologia*], [Nivel 1 (Novice)],
    [*Dispositivos*], [5 (Luces, Persianas, Cerradura)],
  )
)

=== Bio
Marta es una persona activa pero prefiere la simplicidad. Su hijo le instalo dispositivos inteligentes para ayudarla con su movilidad (le cuesta agacharse o subir escaleras). Usa el celular para lo justo y necesario. Si la app parece un "tablero de avion", se asusta y deja de usarla. Necesita botones grandes y lenguaje claro.

#grid(
  columns: (1fr, 1fr),
  gutter: 15pt,
  block(fill: rgb("#E8F5E9"), inset: 8pt, radius: 4pt, width: 100%)[
    *Metas y Objetivos*
    - Comodidad fisica: controlar el entorno desde el sofa.
    - Autonomia: no llamar a su hijo por cada duda.
    - Verificacion rapida de seguridad (puertas).
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
- *Escenario 3 (Asistencia):* Toca una opcion por error y se abre un menu desconocido. Busca un boton de "Atras" o "Inicio" claramente identificado para volver a lo que conoce.

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
  [*Navegacion*], [Atajos y Gestos], [Menus categorizados], [Flujo lineal],
  [*Feedback*], [Sutil / Tecnico], [Informativo], [Afirmativo / Guiado],
  [*Error*], [Lo resuelve solo], [Busca el manual/ayuda], [Se frustra / Abandona],
)

// ====================================
// 5. EVALUACION DE USABILIDAD
// ====================================

= Evaluacion de usabilidad

== Evaluacion predictiva

=== Metodologia

La metodologia utilizada fue que cada integrante realice una inspeccion individual del prototipo web y movil desarrollado por el otro grupo, utilizando las heuristicas seleccionadas. Luego se hizo una puesta en comun donde se consolidaron los hallazgos, eliminaron los duplicados y priorizaron los problemas segun la severidad (critico, mayor, menor, cosmetico).

=== Heuristicas seleccionadas: Principios de Jacob Nielsen

Se eligieron las 10 heuristicas de Nielsen dado que es el marco mas establecido para la evaluacion heuristica de interfaces. Solo las primeras 8 fueron seleccionadas. Se justifica la eleccion de cada principio en la siguiente tabla:

#table(
  columns: (auto, auto, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Heuristica*], [*Justificacion*]),
  [1], [Visibilidad del estado del sistema],
  [Critico en domotica: el usuario necesita saber en todo momento si la alarma esta armada, si la puerta esta bloqueada, si una rutina se ejecuto. Un estado incorrecto o no visible puede generar riesgos de seguridad reales.],
  [2], [Coherencia entre sistema y mundo real],
  [La terminologia debe ser comprensible para todos los perfiles. Se tiene que verificar que las metaforas visuales coinciden con el modelo mental del usuario.],
  [3], [Control y libertad del usuario],
  [Es importante que el usuario tenga control granular y que pueda lograr lo que intenta.],
  [4], [Consistencia y estandares],
  [Los patrones de interaccion tienen que ser consistentes entre dispositivos, respetando las convenciones de plataforma.],
  [5], [Prevencion de errores],
  [Un sistema mal adaptado a distintos usuarios puede generar errores por contexto inadecuado.],
  [6], [Reconocimiento en lugar de memoria],
  [El usuario no tendria que adivinar o recordar que hace cada funcion; la carga cognitiva innecesaria debe minimizarse.],
  [7], [Flexibilidad y eficiencia de uso],
  [Deberian haber caminos rapidos para usuarios frecuentes, asi como personalizacion de la experiencia.],
  [8], [Diseno estetico y minimalista],
  [Con multiples dispositivos, la sobrecarga visual es un riesgo concreto que puede generar confusion innecesaria.],
)

=== Problemas que se incorporan como mejoras

El hallazgo mas contundente --- mencionado de forma independiente por los tres evaluadores --- es que el flujo para agregar elementos (dispositivos, habitaciones y pisos) es complejo y antiintuitivo. Se trata de un problema estructural del flujo de creacion que sera abordado mediante una revision completa de ese recorrido, buscando reducir la cantidad de pasos, unificar el patron de interaccion y hacer mas evidente el punto de entrada.

El *tamaño reducido de los elementos interactivos y del texto* fue señalado por los tres evaluadores como una dificultad real. Se incorporara una revision tipografica y de tamaño de componentes en toda la interfaz, priorizando la legibilidad y el area de toque.

La *falta de consistencia visual y de interaccion* fue identificada por dos evaluadores. Se observaron switches distintos para una misma accion, disposicion variable de elementos equivalentes en distintas secciones, y lineas sin criterio uniforme. Se unificara el componente de switch en toda la app y se establecera un criterio posicional consistente.

El *estado poco claro de los dispositivos* tambien se incorpora. El color de la persiana, el gris utilizado en rutinas y el uso de "si/no" para representar encendido/apagado generaron confusion real. El estado de cada dispositivo debe comunicarse de forma inequivoca a traves de color, icono y etiqueta de texto.

La observacion sobre el *grafico/mapa de la casa* se incorpora parcialmente. Su modo de edicion resulto frustrante para todos los perfiles. Se adoptara la separacion entre la vista visual de solo lectura y la edicion de estructura.

=== Problemas que se descartan o se incorporan parcialmente

La observacion sobre *eliminar la organizacion por pisos y cuartos* se descarta como solucion definitiva. La organizacion jerarquica del hogar es una funcionalidad central que responde a las necesidades identificadas en la investigacion con usuarios. El problema no es su existencia sino su implementacion actual. La solucion es simplificar su presentacion y hacer que el home muestre el estado de los dispositivos de forma directa.

La observacion sobre el *historial que registra todos los eventos sin filtro* se incorpora como mejora de mediano plazo. No representa un problema critico que bloquee el uso de la aplicacion. Se priorizaran primero los problemas estructurales.

== Evaluacion empirica

=== Metodologia

La metodologia fue presencial, con un integrante del grupo observando y tomando notas. Al participante se le dieron instrucciones verbales sobre el escenario, dejandolo interactuar sin guia adicional salvo que se bloquee. Los datos recolectados fueron la tasa de exito, el tiempo de completado, la cantidad de errores y otras observaciones cualitativas.

=== Escenarios seleccionados

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Escenario*], [*Objetivo*], [*Criterio de exito*]),
  [Controlar una lampara desde el dashboard],
  [Validar la eficiencia de la navegacion principal y la interaccion con el slider de brillo.],
  [El usuario localiza la lampara, accede al detalle y ajusta el brillo en menos de 30 segundos sin ayuda.],

  [Crear una rutina "Buenos dias"],
  [Validar la usabilidad del wizard de 4 pasos.],
  [El usuario completa los 4 pasos del wizard y crea la rutina sin errores y sin retroceder mas de una vez.],

  [Verificar la seguridad del hogar antes de dormir],
  [Validar si la estructura de navegacion permite encontrar dispositivos de seguridad rapidamente.],
  [El usuario encuentra y confirma el estado de alarma y puerta en menos de 1 minuto.],

  [Cambiar al perfil adolescente e intentar desarmar la alarma],
  [Validar la implementacion de restricciones por rol.],
  [El usuario cambia de perfil, ve que el control esta deshabilitado y comprende la restriccion sin confusion.],

  [Consultar el consumo electrico de la ultima semana],
  [Validar la encontrabilidad de la seccion de consumo y la legibilidad de los graficos.],
  [El usuario navega a consumo e identifica el dispositivo de mayor consumo en menos de 45 segundos.],

  [Recuperarse de una navegacion accidental],
  [Validar la capacidad de recuperacion y orientacion.],
  [El usuario vuelve al dashboard en menos de 2 clics sin expresar confusion.],

  [Verificar que el perfil adolescente no puede acceder a configuracion],
  [Validar el guard del router y la coherencia de la restriccion visual.],
  [El usuario nota que "Configuracion" no aparece. Si escribe la URL manualmente, es redirigido sin error confuso.],
)

=== Analisis de los resultados

El registro completo de los resultados se encuentra en el Anexo. Las fricciones detectadas mas relevantes fueron:

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*Escenario*], [*Friccion detectada*], [*Accion de mejora*]),

  [(3) Verificar seguridad],
  [El participante trato de buscar los dispositivos en el grafico del hogar y no los encontro. Luego los encontro en favoritos.],
  [Mejorar/agrandar la vista del hogar para identificar mas facilmente que dispositivos se encuentran activos.],

  [(5) Consultar consumo],
  [Algunos participantes de edad media primero fueron a "Historial" y no a "Consumo".],
  [Reflexionar si ambas opciones, aunque diferentes, pueden generar confusion y extra carga cognitiva.],

  [(7) Acceder a configuracion],
  [Todos los participantes no pudieron completar esta tarea. Al cambiarse al perfil adolescente, la opcion desaparecia, generando frustracion.],
  [Mostrar la opcion deshabilitada con un mensaje explicativo en lugar de ocultarla completamente.],
)

== Evaluacion participativa

=== Metodologia

La evaluacion participativa se llevo a cabo inmediatamente despues de la empirica, por lo cual los participantes son los mismos. Fue un cuestionario semi-estructurado, registrandose las respuestas textuales del participante. Las preguntas estan organizadas en 4 bloques tematicos.

Las respuestas completas se encuentran en el Anexo.

// ====================================
// 6. PROTOTIPOS SITIO WEB
// ====================================

= Prototipos sitio web

#figure(
  image("assets/image31.png", width: 100%),
  caption: [Overview -- Resumen de propiedades],
)

#figure(
  image("assets/image40.png", width: 100%),
  caption: [Inicio -- Dashboard de una propiedad (Casa Martinez)],
)

#figure(
  image("assets/image41.png", width: 100%),
  caption: [Dispositivos],
)

#figure(
  image("assets/image19.png", width: 100%),
  caption: [Habitaciones],
)

#figure(
  image("assets/image27.png", width: 100%),
  caption: [Rutinas],
)

// ====================================
// 7. PROTOTIPOS APLICACION MOVIL
// ====================================

= Prototipos aplicacion movil

== Rediseño de la pantalla de inicio de sesion

La primera version del prototipo presentaba dificultades de legibilidad debido al uso de colores llamativos en el fondo que restaban contraste a los textos, sumado a un tamaño de letra reducido. A esto se añadía una falta de claridad en el flujo de inicio de sesion y la inclusion de una opcion de acceso con Google que, dado que la API no lo permite, resultaba inviable.

Como solucion, se adopto un fondo oscuro que mejora el contraste general, se intensificaron los colores de los botones principales para destacar las acciones relevantes, y se reorganizo el espaciado entre titulos y subtitulos.

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image11.png", width: 100%),
    image("assets/image13.png", width: 100%),
  ),
  caption: [Inicio de sesion: primer prototipo (izquierda) y version final (derecha)],
)

== Rediseño de la pantalla de inicio

La version inicial presentaba una distribucion de elementos con igual relevancia visual entre todas las funciones, lo que dificultaba identificar las acciones prioritarias. El termino "Home" generaba confusion con "habitacion", el buscador era poco distinguible y el espacio disponible estaba mal aprovechado.

Como solucion, se rediseño la pantalla restringiendola a los dispositivos y rutinas favoritas, decision respaldada por las entrevistas. Se incorporo una barra de menu superior que permite cambiar de propiedad facilmente.

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image32.png", width: 100%),
    image("assets/image2.png", width: 100%),
  ),
  caption: [Pantalla de inicio: primer prototipo (izquierda) y version final (derecha)],
)

== Rediseño de la seccion de dispositivos

La version inicial presentaba multiples problemas de usabilidad. El termino "Espacios" para referirse a las habitaciones resultaba poco representativo. No existia la posibilidad de editar dispositivos ni habitaciones una vez creados. El menu de propiedades era poco intuitivo y las acciones equivalentes presentaban botones de distintos colores.

Como respuesta, se renombro la seccion de "Espacios" a "Dispositivos", se incorporo un buscador de dispositivos y se habilitaron opciones para agregar, editar y eliminar tanto dispositivos como habitaciones.

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image17.png", width: 100%),
    image("assets/image6.png", width: 100%),
  ),
  caption: [Seccion de dispositivos: primer prototipo (izquierda) y version final (derecha)],
)

== Rediseño de la seccion de perfil

Tras las entrevistas, se determino que la mayoria de los usuarios consultaba el historial y el consumo de forma esporadica. Se decidio agruparlas dentro de la seccion de perfil, evitando que ocupen un lugar prominente en la navegacion principal.

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image4.png", width: 100%),
    image("assets/image38.png", width: 100%),
  ),
  caption: [Seccion de perfil: primer prototipo (izquierda) y version final (derecha)],
)

== Rediseño del menu de gestion de propiedades

El menu original era poco intuitivo para el cambio de propiedad, por lo que se opto por un selector mas claro y accesible.

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image23.png", width: 100%),
    image("assets/image1.png", width: 100%),
  ),
  caption: [Cambio y gestion de propiedad: prototipo inicial (izquierda) y version final (derecha)],
)

== Prototipos en orientacion horizontal

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image21.png", width: 100%),
    image("assets/image7.png", width: 100%),
    image("assets/image9.png", width: 100%),
    image("assets/image8.png", width: 100%),
  ),
  caption: [Vistas en orientacion horizontal],
)

== Version tablet

#figure(
  grid(
    columns: 2,
    gutter: 16pt,
    image("assets/image14.png", width: 100%),
    image("assets/image26.png", width: 100%),
  ),
  caption: [Adaptacion para tablet],
)

// ====================================
// 8. FLUJOS DE INTERACCION
// ====================================

= Flujos de interaccion

== Creacion de rutinas

Se decidio agrupar los pasos de crear una rutina en una sola ventana en la version movil, junto con agregar horarios default para reducir la cantidad de interacciones complejas.

#figure(
  image("assets/image22.png", width: 80%),
  caption: [Diagrama de flujo: creacion de rutinas (web vs. movil)],
)

=== Version web

#figure(
  image("assets/image20.png", width: 100%),
  caption: [Pagina de rutinas -- web],
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
  caption: [4 pasos del wizard de creacion de rutinas],
)

=== Version movil

#figure(
  image("assets/image36.png", width: 40%),
  caption: [Pagina de rutinas -- movil],
)

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image37.png", width: 100%),
    image("assets/image28.png", width: 100%),
  ),
  caption: [Menu de creacion de rutinas -- movil],
)

== Desvincular un dispositivo de una habitacion

No habia suficiente espacio en la aplicacion movil para tener un boton de desvincular junto a cada dispositivo, o una pagina separada para las habitaciones, por lo que las habitaciones aparecen como grupos en dispositivos y la accion de desvincular es desde la configuracion de las habitaciones.

#figure(
  image("assets/image18.png", width: 80%),
  caption: [Diagrama de flujo: desvincular dispositivo (web vs. movil)],
)

=== Version web

#figure(
  image("assets/image10.png", width: 100%),
  caption: [Boton de desvincular en pagina web],
)

=== Version movil

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image15.png", width: 100%),
    image("assets/image30.png", width: 100%),
  ),
  caption: [Pagina de dispositivos y configuracion de habitaciones -- movil],
)

== Agregar un nuevo dispositivo

#figure(
  image("assets/image33.png", width: 80%),
  caption: [Diagrama de flujo: agregar un nuevo dispositivo],
)

#figure(
  grid(
    columns: 2,
    gutter: 12pt,
    image("assets/image25.png", width: 100%),
    image("assets/image12.png", width: 100%),
  ),
  caption: [Configuracion del nuevo dispositivo -- movil],
)

// ====================================
// 9. DECISIONES DE DISENO Y USABILIDAD
// ====================================

= Decisiones de diseño y usabilidad

Para fundamentar cada decision, nos basamos en los principios de Interaccion Persona-Computadora (HCI) vistos en la materia:

- *Heuristicas de Nielsen:* Visibilidad del estado, correspondencia con el mundo real, control del usuario, consistencia, prevencion de errores, reconocimiento, flexibilidad, estetica minimalista y ayuda.
- *Leyes de Gestalt:* Proximidad, similitud, region comun y cierre.
- *Leyes de Interaccion:* Ley de Fitts (tamaño de objetivos) y Ley de Hick (toma de decisiones).
- *Niveles de Norman:* Respuestas viscerales (estetica), conductuales (uso) y reflexivas (satisfaccion).
- *Carga Cognitiva:* Minimizar el esfuerzo mental mediante una estructura clara.
- *Diseno Centrado en el Usuario (UCD):* Proceso de iteracion basado en evaluaciones de usabilidad.

== Decisiones de diseño (Identidad y estetica)

=== Identidad visual y logo

*Decision:* Un logo hexagonal con una casa minimalista y ondas de WiFi. \
*Justificacion:* Se busca transmitir tecnologia y seguridad (nivel visceral). El hexagono da una sensacion de estructura solida, y el uso de la ley de cierre de Gestalt permite que el logo sea reconocible incluso en tamaños reducidos.

=== Paleta de colores

*Decision:* Tema oscuro profundo con indigo para acciones y ambar para estados activos. \
*Justificacion:* El tema oscuro reduce la fatiga visual en entornos hogarenos. El indigo permite identificar rapidamente que es interactivo (similitud) y el ambar resalta lo que esta encendido. Todos los colores cumplen con el estandar WCAG AA.

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

=== Tipografia y jerarquia

*Decision:* Familia tipografica unica (Inter) con pesos variables. \
*Justificacion:* Usar una sola fuente ayuda a no saturar al usuario (menor carga cognitiva). La jerarquia se marca con tamaño y grosor del texto de forma natural.

=== Sistema de iconografia

*Decision:* Iconos SVG personalizados en lugar de emojis. \
*Justificacion:* Garantizan consistencia visual en todas las plataformas (Nielsen \#4). Al compartir el mismo grosor de linea y estilo, se facilita su reconocimiento rapido.

=== Agrupamiento visual (Cards)

*Decision:* Uso de tarjetas con bordes suaves y espaciado consistente. \
*Justificacion:* Aplicamos las leyes de proximidad y region comun de Gestalt para que el usuario entienda que los controles de un dispositivo pertenecen a ese dispositivo especifico.

=== Variables CSS como fuente de verdad

*Decision:* Centralizacion de tokens (colores, espacios, tamaños) en un sistema de variables CSS. \
*Justificacion:* Garantiza que la consistencia (Nielsen \#4) se mantenga de forma tecnica. Un cambio de tono tras una evaluacion de usabilidad impacta en toda la app al instante.

== Decisiones de usabilidad e interaccion

=== Estructura de navegacion (Sidebar)

*Decision:* Barra lateral izquierda con 6 secciones principales, colapsable. \
*Justificacion:* Al limitar las opciones aplicamos la Ley de Hick, acelerando la toma de decisiones. Mantener los iconos visibles al colapsar permite reconocer las secciones sin recordar sus nombres.

=== Orientacion (Breadcrumbs)

*Decision:* Migas de pan clickeables en la parte superior. \
*Justificacion:* Funcionan como un indicador de lugar constante para evitar que el usuario se sienta perdido. Ademas, ofrecen una "salida de emergencia" rapida (control y libertad).

=== Arquitectura multi-casa

*Decision:* Jerarquia clara de Propiedad > Seccion > Dispositivo. \
*Justificacion:* Mapeamos la estructura fisica del mundo real al sistema (Nielsen \#2). El selector de casas en la parte superior refleja el modelo mental del usuario.

=== Vista Overview (Resumen general)

*Decision:* Una pantalla principal que resume lo critico de todas las propiedades. \
*Justificacion:* El usuario puede ver estados de alerta o rutinas favoritas sin navegar casa por casa, reduciendo drasticamente la carga de trabajo y el tiempo de respuesta.

=== Interaccion directa con dispositivos

*Decision:* Botones y controles de gran tamaño (minimo 44px). \
*Justificacion:* Aplicamos la Ley de Fitts: objetivos mas grandes son mas faciles y rapidos de clickear, reduciendo errores accidentales.

=== Feedback y estado del sistema

*Decision:* Notificaciones instantaneas (toasts) y animaciones de estado. \
*Justificacion:* El sistema debe responder siempre (Nielsen \#1). Si una luz se enciende, el icono brilla de inmediato, previniendo la incertidumbre de no saber si el comando funciono.

=== Prevencion de errores y valores por defecto

*Decision:* Mensajes de confirmacion antes de borrar y sliders pre-seteados en valores comunes. \
*Justificacion:* Es mejor prevenir el error que reportarlo. Los valores por defecto (como el brillo al 80%) ahorran pasos innecesarios.

=== Terminologia y lenguaje

*Decision:* Uso de lenguaje cotidiano (español rioplatense) y consistencia lexica. \
*Justificacion:* Evitamos la jerga tecnica. Usar siempre "Hogar" o "Rutina" ayuda a que el usuario construya un modelo mental solido.

=== Libertad y control

*Decision:* Todas las acciones son reversibles (Undos conceptuales). \
*Justificacion:* Poder apagar una rutina en lugar de borrarla, o desvincular un dispositivo sin eliminarlo, le da al usuario la confianza para explorar sin miedo.

=== Experiencia de entrada (Splash Screen)

*Decision:* Animacion de bienvenida inmersiva pero skipeable. \
*Justificacion:* Establece el tono de calidad de la app (nivel visceral) y usa la metafora de "entrar" a la casa. Al permitir saltearla, respetamos el control del usuario.

=== Estrategia de prototipado (Auto-login)

*Decision:* Bypass de la pantalla de login para las pruebas de usabilidad. \
*Justificacion:* Durante las evaluaciones con usuarios (UCD), el foco esta en el control de la casa y no en la gestion de contraseñas. Esto reduce la friccion inicial.

// ====================================
// ANEXO
// ====================================

#pagebreak()

= Anexo <anexo>

== Observaciones participativas: resultados por escenario

=== Encender y apagar un dispositivo individual (Google Home web y movil)

- *Luis (experto):* Entra directamente a la pantalla de inicio, encuentra la habitacion, toca el icono de la luz y la apaga en aproximadamente menos de 10 segundos. Nota que el movil es mas comodo ya que los iconos de la web le parecen pequeños y reconoce que el estado de encendido/apagado podria ser mas claro en la web.

- *Sofia (media):* Tarda unos segundos en escanear la pantalla de inicio. Trata de encontrar la barra de busqueda (no existe) y luego encuentra el dispositivo a traves de las habitaciones. Menciona que pensaba que los dispositivos estarian en una lista todos juntos y no por cuartos. Prefiere la version movil.

- *Laura (sin experiencia):* Al principio no sabe por donde empezar. Toca el nombre y se va a una pantalla de detalles. Vuelve atras y toca el icono que ahi lo enciende. No le parecen claros los botones.

- *Cecilia (baja-media):* Navega con fluidez relativa. Encuentra el dispositivo rapido pero duda de si realmente quedo encendido ya que el cambio de color del icono es sutil. Prefiere la version movil.

=== Crear una rutina/automatizacion (Google Home web y movil, Amazon Alexa web)

- *Luis (experto):* En Google Home encuentra "Rutinas" velozmente. Crea la rutina con facilidad pero destaca que hay demasiados pasos. En Alexa siente que la web esta desactualizada y que los menus estan anidados de forma poco logica.

- *Sofia (media):* Al principio no logra encontrar "Rutinas" en Google Home. Luego sigue el flujo pero se traba confundiendo el "que debe pasar" con el "que debe activar la rutina". En Alexa directamente se rindio.

- *Laura (sin experiencia):* No logra completar el escenario en ninguna de las dos aplicaciones sin asistencia. Le confunde la cantidad de pantallas.

- *Cecilia (baja-media):* Logra llevar a cabo el escenario en Google Home pero opina que seria mas sencillo si se le proveyera una plantilla. En Alexa le parece confuso que se llame "rutina" pero funcione diferente a Google.

=== Agrupar dispositivos por habitacion (Amazon Alexa web, Home Assistant movil)

- *Luis (experto):* En Alexa encuentra "Grupos" rapidamente. En Home Assistant reconoce que es una app para perfiles tecnicos.

- *Sofia (media):* En Alexa logra crear un grupo pero no ve el sentido de tener "grupo" y "habitacion" en una misma aplicacion. En Home Assistant no logra completar la tarea.

- *Laura (sin experiencia):* En Alexa no logra encontrar donde crear grupos. No relaciona la palabra "grupo" con lo que quiere hacer. En Home Assistant abandona la tarea.

- *Cecilia (baja-media):* En Alexa completa la tarea por prueba y error. En Home Assistant logra orientarse por los iconos pero le cuesta completar la tarea sola.

=== Activar una escena predefinida (Google Home movil, Home Assistant movil)

- *Luis (experto):* En Google Home activa la escena sin problema. Reconoce que no se ve si la escena esta activa o no. En Home Assistant le interesa la personalizacion.

- *Sofia (media):* En Google Home no distingue si lo que esta en la pantalla es una escena o un dispositivo individual. En Home Assistant no logra encontrar las escenas.

- *Laura (sin experiencia):* Pregunta primero que es una escena. No entiende el escenario en ninguna de las dos aplicaciones.

- *Cecilia (baja-media):* En Google Home activa la escena bastante rapido pero duda de si se activo realmente. En Home Assistant no completa la tarea.

=== Incorporar nuevo dispositivo (Amazon Alexa web, Home Assistant movil)

- *Luis (experto):* En Alexa agrega el dispositivo sin dificultad. Apreciaria poder agregar varios dispositivos a la vez. En Home Assistant valora las opciones pero reconoce que es demasiado para el usuario comun.

- *Sofia (media):* En Alexa sigue los pasos del asistente. Le parece exhaustivo buscar manualmente en la lista y sugiere un buscador. En Home Assistant se rinde.

- *Laura (sin experiencia):* En Alexa se traba en el primer paso porque el asistente pregunta si el dispositivo es compatible.

- *Cecilia (baja-media):* En Alexa completa el proceso pero lo siente largo para algo simple. En Home Assistant logra iniciar el proceso pero no completarlo.

#pagebreak()

== Evaluacion predictiva: resultados por persona

=== Persona 1

- Cerrar las cortinas a medias no es intuitivo ni directo.
- Sin division por sala, si hay muchos dispositivos con el mismo nombre habria un problema de identificacion.
- El termino "ejecutar" no queda claro para el usuario.
- La organizacion por pisos y cuartos marea mas de lo que suma. Propone que directamente aparezca en el home que luces estan prendidas.
- Si una palabra/habitacion es clickeable, el resto tambien deberia serlo (consistencia).
- En algunos lugares la letra es muy chica y el contraste es insuficiente.
- Poca consistencia visual en la seccion de cuartos y rutinas.
- Propone eliminar la seccion de cuartos porque resta mas de lo que suma.
- El switch para encender/apagar debe ser siempre el mismo en toda la app.

=== Persona 2

- Reaccion positiva al ver el grafico de la casa.
- Modificar el grafico de la casa es poco intuitivo.
- El boton para agregar dispositivos tiene texto muy pequeno.
- Es muy dificil darse cuenta de como agregar un dispositivo: el color y la disposicion son muy distintos a los de agregar habitacion.
- No queda claro si en una rutina algo se esta encendiendo o apagando.
- Si algo aparece en gris dentro de una rutina parece deshabilitado en lugar de apagado.
- Falta consistencia en las lineas de la interfaz.
- La creacion de elementos en general es muy compleja y sin consistencia.
- El grafico de la casa es demasiado complejo. Propone que sea solo una vista visual no editable.

=== Persona 3

- Los elementos son muy pequeños y dificiles de leer.
- Agregar cosas es antiintuitivo (coincide con las otras personas).
- El encender/apagar de ciertos dispositivos con "si/no" es muy extraño.
- Las rutinas no se pueden editar.
- El historial registra absolutamente todo, lo cual satura la vista. Deberia poder filtrarse o agruparse.

#pagebreak()

== Evaluacion empirica: resultados por perfil

=== Valentina / Power user (Participante: Luis)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observacion*]),
  [1], [Controlar una lampara desde el dashboard],
  [Lo hizo sencillamente y mucho mas rapido de lo estimado. Menciono: "Que copado esto!" al ver el esquema de los cuartos.],
  [2], [Crear una rutina "Buenos dias"],
  [Hizo la rutina facilmente dentro de los 4 pasos. Se pregunto: "Cual es la del pasillo?" pero eligio la que pensaba que era.],
  [3], [Verificar la seguridad del hogar],
  [Lo hizo velozmente desde la pantalla de inicio porque se acordaba de haberlos visto.],
  [4], [Cambiar al perfil adolescente],
  [Se hizo sin problema, entendiendo las restricciones del nuevo usuario.],
  [5], [Consultar el consumo electrico],
  [Lo hizo en menos de 30 segundos.],
  [6], [Recuperarse de navegacion accidental],
  [Se completo velozmente.],
  [7], [Verificar restriccion de configuracion],
  [Se rindio a los 15 segundos. Se frustro.],
)

=== Carolina / Gestora del hogar (Participante: Cecilia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observacion*]),
  [1], [Controlar una lampara desde el dashboard],
  [Encontro la lampara en el esquema de la casa dentro de los 30 segundos.],
  [2], [Crear una rutina "Buenos dias"],
  [Creo la rutina sin retroceder. Solo busco un poco la lampara del pasillo.],
  [3], [Verificar la seguridad del hogar],
  [Encontro alarma y puerta en menos de 1 minuto usando el menu lateral.],
  [4], [Cambiar al perfil adolescente],
  [Lo hizo velozmente. Opino: "El adolescente deberia poder cambiar la alarma ante una emergencia."],
  [5], [Consultar el consumo electrico],
  [Pudo completarlo pero al principio fue a "Historial".],
  [6], [Recuperarse de navegacion accidental],
  [Lo hizo velozmente.],
  [7], [Verificar restriccion de configuracion],
  [Le genero mucha ansiedad. Dijo: "Donde esta? No lo puedo hacer."],
)

=== Marta / Usuaria tradicional (Participante: Hannah)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observacion*]),
  [1], [Controlar una lampara desde el dashboard],
  [Se hizo sin problema. Dedujo que lo que brillaba era la lampara.],
  [2], [Crear una rutina "Buenos dias"],
  [Lo hizo velozmente pero retrocedio porque clickeo dos veces un dispositivo y lo desagrego.],
  [3], [Verificar la seguridad del hogar],
  [Logro la tarea en menos de 1 minuto. Penso brevemente que la barra estaba arriba.],
  [4], [Cambiar al perfil adolescente],
  [Se hizo exitosamente aunque le costo buscar donde cambiar el perfil.],
  [5], [Consultar el consumo electrico],
  [Lo hizo velozmente sin problema.],
  [6], [Recuperarse de navegacion accidental],
  [Hecho velozmente.],
  [7], [Verificar restriccion de configuracion],
  [Se rindio al segundo. Dijo: "No bueno, no puedo, no lo encuentro."],
)

=== Marta / Usuaria tradicional (Participante: Sofia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Escenario*], [*Observacion*]),
  [1], [Controlar una lampara desde el dashboard],
  [Lo hizo rapidamente. Accedio a la lampara desde el esquema del cuarto.],
  [2], [Crear una rutina "Buenos dias"],
  [Lo hizo velozmente, sin errores y sin retroceder.],
  [3], [Verificar la seguridad del hogar],
  [Recordaba haber visto los dispositivos en la pantalla principal.],
  [4], [Cambiar al perfil adolescente],
  [Exitoso. Menciono: "El coso ese para cambiar de perfil tendria que ser mas grande."],
  [5], [Consultar el consumo electrico],
  [Lo hizo rapido. Menciono: "Me hace acordar al consumo del iPhone."],
  [6], [Recuperarse de navegacion accidental],
  [Lo hizo rapido usando el atajo del extremo superior izquierdo.],
  [7], [Verificar restriccion de configuracion],
  [Se rindio al segundo. Dijo: "Deberia aparecer Configuracion, de ultima que no lo pueda acceder pero tiene que estar."],
)

#pagebreak()

== Evaluacion participativa: respuestas por perfil

=== Valentina / Power user (Participante: Luis)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dispositivos."],
  [2], [Momento de confusion en la navegacion], ["No."],
  [3], [Opciones del menu lateral], ["Me parecio adecuada."],
  [4], [Feedback al cambiar estado de dispositivo], ["Si."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["Las luces ya que siempre todos las dejan prendidas."],
  [7], [Comprension de restriccion del perfil adolescente], ["Si."],
  [8], [Logica de ver pero no controlar la alarma], ["Si."],
  [9], [Selector de perfil facil de encontrar], ["Si."],
  [10], [Descripcion de la app en una frase], ["Gestionador de hogares."],
  [11], [Frustracion durante el uso], ["Al intentar buscar Configuracion, que al final ni estaba."],
  [12], [Facilidad general de uso (1-5)], ["4."],
)

=== Carolina / Gestora del hogar (Participante: Cecilia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusion en la navegacion], ["No. Solo me genero confusion que no encontre Configuracion."],
  [3], [Opciones del menu lateral], ["En el de adolescente faltaba Configuracion."],
  [4], [Feedback al cambiar estado de dispositivo], ["Si."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["La luz y la alarma. La luz porque me la olvido de apagar y la alarma por seguridad."],
  [7], [Comprension de restriccion del perfil adolescente], ["Si, porque es menor de edad."],
  [8], [Logica de ver pero no controlar la alarma], ["Deberia poder controlarla porque ante un incendio deberia tener acceso."],
  [9], [Selector de perfil facil de encontrar], ["Si."],
  [10], [Descripcion de la app en una frase], ["Administrador de dispositivos."],
  [11], [Frustracion durante el uso], ["No se podia ver bien la pantalla por el contraste de la luz."],
  [12], [Facilidad general de uso (1-5)], ["4."],
)

=== Marta / Usuaria tradicional (Participante: Hannah)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusion en la navegacion], ["No."],
  [3], [Opciones del menu lateral], ["Esta bien pero Configuracion deberia estar en el header o hacerse mas visible."],
  [4], [Feedback al cambiar estado de dispositivo], ["Si."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["La lampara principal porque me la olvido de apagar."],
  [7], [Comprension de restriccion del perfil adolescente], ["Si."],
  [8], [Logica de ver pero no controlar la alarma], ["Esta bien."],
  [9], [Selector de perfil facil de encontrar], ["No. Esperaba que estuviera en la barra lateral o cerca del nombre de la casa."],
  [10], [Descripcion de la app en una frase], ["Seguridad."],
  [11], [Frustracion durante el uso], ["Lo de la configuracion."],
  [12], [Facilidad general de uso (1-5)], ["3,5."],
)

=== Marta / Usuaria tradicional (Participante: Sofia)

#table(
  columns: (auto, 1fr, 1fr),
  align: (center, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header([*\#*], [*Pregunta*], [*Respuesta*]),
  [1], [Primer instinto para encontrar un dispositivo], ["Dashboard."],
  [2], [Momento de confusion en la navegacion], ["No."],
  [3], [Opciones del menu lateral], ["Esta bien."],
  [4], [Feedback al cambiar estado de dispositivo], ["Si."],
  [5], [Iconos o controles confusos], ["No."],
  [6], [Dispositivo a controlar desde el inicio], ["El aire acondicionado porque siempre lo dejo prendido."],
  [7], [Comprension de restriccion del perfil adolescente], ["Si."],
  [8], [Logica de ver pero no controlar la alarma], ["Si."],
  [9], [Selector de perfil facil de encontrar], ["A mi si pero seguramente que para todos no."],
  [10], [Descripcion de la app en una frase], ["Control de dispositivos de hogares."],
  [11], [Frustracion durante el uso], ["Cuando no pude encontrar Configuracion."],
  [12], [Facilidad general de uso (1-5)], ["4,5."],
)
