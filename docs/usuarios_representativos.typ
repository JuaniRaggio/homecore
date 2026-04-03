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

// ====================================
// FUNCIONES UTILES
// ====================================

// Funcion para crear una caja de nota/observacion
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

// Funcion para crear una caja de advertencia
#let importante(contenido) = {
  block(
    fill: rgb("#FFF3E0"),
    stroke: rgb("#F57C00") + 1pt,
    inset: 10pt,
    radius: 4pt,
    width: 100%,
  )[
    #text(weight: "bold", fill: rgb("#F57C00"))[Importante:] #contenido
  ]
}

// Funcion para crear una caja de error comun
#let error(contenido) = {
  block(
    fill: rgb("#FFEBEE"),
    stroke: rgb("#D32F2F") + 1pt,
    inset: 10pt,
    radius: 4pt,
    width: 100%,
  )[
    #text(weight: "bold", fill: rgb("#D32F2F"))[Error Comun:] #contenido
  ]
}

// Funcion para crear una caja de tip
#let tip(contenido) = {
  block(
    fill: rgb("#E8F5E9"),
    stroke: rgb("#388E3C") + 1pt,
    inset: 10pt,
    radius: 4pt,
    width: 100%,
  )[
    #text(weight: "bold", fill: rgb("#388E3C"))[Tip:] #contenido
  ]
}

// Funcion para crear una caja de duda con pregunta y respuesta
#let doubt(pregunta, respuesta) = {
  block(
    fill: rgb("#F3E5F5"),
    stroke: rgb("#7B1FA2") + 1pt,
    inset: 10pt,
    radius: 4pt,
    width: 100%,
  )[
    #text(weight: "bold", fill: rgb("#7B1FA2"), size: 11pt)[Pregunta:]
    #v(0.3em)
    #pregunta
    #v(0.5em)
    #line(length: 100%, stroke: 0.5pt + rgb("#7B1FA2"))
    #v(0.5em)
    #text(weight: "bold", fill: rgb("#7B1FA2"), size: 11pt)[Respuesta:]
    #v(0.3em)
    #respuesta
  ]
}

= Usuarios Representativos

== Definicion de atributos

Se seleccionaron los siguientes atributos por ser factores que influyen directamente en como un usuario interactua con una aplicacion de domotica:

#error[
  Esta mal lo de subdividir los rangos de edades en los usuarios representativos.
]

#importante[
  Usar esta data para crear combinaciones distintas de *modelos de persona*
]

#table(
  columns: 3,
  align: (left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
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

#v(1em)

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

-- *UR1 (El Entusiasta):* Usuario que busca eficiencia extrema y personalizacion. Validara funciones avanzadas.
-- *UR2 (La Administradora):* Usuario tipico del mercado que busca seguridad familiar. Validara control parental y robustez.
-- *UR3 (El Recien Llegado):* Usuario con alta friccion potencial. Validara la accesibilidad y claridad de la interfaz.

#pagebreak()

= Modelos de Persona

#block(
  fill: rgb("#E3F2FD"),
  stroke: rgb("#2196F3") + 1pt,
  inset: 10pt,
  radius: 4pt,
  width: 100%,
)[
  #text(weight: "bold", fill: rgb("#1976D2"))[Nota sobre las personas:]
  Los siguientes modelos de persona sintetizan los comportamientos, necesidades y frustraciones de los usuarios identificados. Se incluyen imagenes representativas para facilitar la empatia durante el proceso de diseño.
]

#v(1em)

== Persona 1: Santiago "El Power User"

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
    [*Nombre*], [Santiago],
    [*Edad*], [27 años],
    [*Ocupacion*], [Desarrollador de software (Backend)],
    [*Ubicacion*], [Departamento en Palermo, CABA],
    [*Tecnologia*], [Nivel 3 (Power User)],
    [*Dispositivos*], [12+ (Luces, Cerradura, AC, Sensores, Hub)],
  )
)

=== Bio
Santiago vive solo y trabaja de forma remota. Su casa es su laboratorio. Disfruta optimizando cada aspecto de su vida: desde el cafe que se prepara solo al despertar hasta las luces que cambian de color segun su calendario de reuniones. No tolera la latencia ni las apps que requieren muchos clics para tareas simples.

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
- *Escenario 1 (Eficiencia):* Santiago sale a correr. Al alejarse 100m de su casa (geofencing), la app debe ejecutar "Modo Ausente" sin preguntarle, pero enviando una confirmacion silenciosa.
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

== Persona 3: Roberto "El Usuario Tradicional"

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
    [*Nombre*], [Roberto],
    [*Edad*], [63 años],
    [*Ocupacion*], [Jubilado],
    [*Ubicacion*], [Departamento en Belgrano, CABA],
    [*Tecnologia*], [Nivel 1 (Novice)],
    [*Dispositivos*], [5 (Luces, Persianas, Cerradura)],
  )
)

=== Bio
Roberto es una persona activa pero prefiere la simplicidad. Su hijo le instalo dispositivos inteligentes para ayudarlo con su movilidad (le cuesta agacharse o subir escaleras). Usa el celular para lo justo y necesario. Si la app parece un "tablero de avion", se asusta y deja de usarla. Necesita botones grandes y lenguaje claro.

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
- *Escenario 1 (Comodidad):* Roberto esta viendo la tele y el sol le molesta. Abre la app y toca un boton grande que dice "Bajar Persiana Living".
- *Escenario 2 (Seguridad Nocturna):* Ya en la cama, abre la app para asegurarse de que la puerta de entrada tiene el candado puesto. El icono verde de "Cerrado" le da tranquilidad.
- *Escenario 3 (Asistencia):* Toca una opcion por error y se abre un menu desconocido. Busca un boton de "Atras" o "Inicio" claramente identificado para volver a lo que conoce.

#pagebreak()

= Resumen comparativo de necesidades

#table(
  columns: (auto, 1fr, 1fr, 1fr),
  align: (left, left, left, left),
  stroke: 0.5pt,
  inset: 8pt,
  fill: (x, y) => if y == 0 { gray.lighten(80%) },
  table.header(
    [], [*Santiago*], [*Carolina*], [*Roberto*],
  ),
  [*Prioridad*], [Velocidad y Potencia], [Seguridad y Control], [Simplicidad y Ayuda],
  [*Interfaz*], [Compacta / Muchos datos], [Limpia / Notificaciones], [Grande / Textual],
  [*Navegacion*], [Atajos y Gestos], [Menus categorizados], [Flujo lineal],
  [*Feedback*], [Sutil / Tecnico], [Informativo], [Afirmativo / Guiado],
  [*Error*], [Lo resuelve solo], [Busca el manual/ayuda], [Se frustra / Abandona],
)
