#set document(
  title: "Human Computer Interaction",
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
      [Juan Ignacio Raggio], [], [#datetime.today().display("[day]/[month]/[year]")],
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

#set list(indent: 1em, marker: ("•", "◦", "▪"))
#set enum(indent: 1em, numbering: "1.a.")

#show raw.where(block: false): box.with(
  fill: luma(240),
  inset: (x: 3pt, y: 0pt),
  outset: (y: 3pt),
  radius: 2pt,
)

#show raw.where(block: true): block.with(
  fill: luma(240),
  inset: 10pt,
  radius: 4pt,
  width: 100%,
)

#show link: underline
// ====================================
// PORTADA
// ====================================

#align(center)[
  #v(1em)
  #text(size: 24pt, weight: "bold")[HCI]
  #v(0.5em)
  #text(size: 18pt)[Teoria HCI]
  #v(0.5em)
  #text(size: 12pt, fill: gray)[
    Toma de notas Teorica \
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


= Camino de la materia

- Fundamentos - 4 Semanas de teoria intensa

- Desarrollo web - 6 semanas de implemenntacion UI web

- Desarrollo Android - 5 Implementacion UI Android


== El cambio mas importante

La calificacion de cada una de las 3 entregas se compondra de dos elementos:

- Nota grupal: representa la evaluacion de los artefactos presentados por el grupo
- Nota individual

\

= Que es HCI?

*Human Computer Interaction* es una disciplina que se enfoca en la interaccion entre la persona y las computadoras

\

== Como se crea una interaccion adecuada

Tiene que ser...

1. *Funcional*: debe hacer la tarea para la que fue creada
2. *Usable*: debe ser facil de usar
3. *Agradable*: debe ser atractivo

\

== Enfoque multidisciplinario

La necesidad de crear una interaccion persona-computadora
que tambien fuera facil y eficiente para los usuarios
menos experimentados se volvio cada vez mas vital


Si bien la ciencia de la computadora evoluciona con el paso
del tiempo, la ciencia cognitiva y la ingenieria de factores humanos
no lo hacen necesariamente a la misma velocidad. Por lo tanto, varios
de los aspectos que pudieron hacer que una interfaz sea exitosa en la
decada de los 70 aun se encuentran vigentes

\

== Terminos confusos

- UI design: es el proceso que utilizan los graphic designers para
  crear interfaces en software o dispositivos computarizados

- *Info. architecture*: es la crreacion de una estructura en una interfaz
  de usuario, que le permita a este ultimo comprender donde se encuentra la info.
  de su interes

- *Interaction design*: Se encarga de que la interfaz sea facil de usar

- *User experience design*: es el proceso que permite crear productos
  que brinden experiencias significativas y relevantes a los usuarios

\

== Usabilidad

El grado de *eficacia*, *eficiencia* y *satisfaccion* con la que los *usuarios
especificos* pueden lograr *objetivos especificos* en *contextos especificos*

- *eficacia*: Preciso y completar la tarea
- *eficiencia*: los recursos invertidos en funcion a la precision y completitud
  con la que los usuarios alcanzan sus objetivos
- *satisfaccion*: sentimiento de placer y contento, que se produce al hacer
  uso del producto para completar la tarea

#nota[
  Es importante especificar estos 3 elementos de forma correcta para obtener
  el mayor grado de eficacia, eficiencias y satisfaccion posible

  - *usuario*: representa el "quien?"
  - *tarea*: representa el "para que?"
  - *contexto*: representa el "como?" y "donde?"
]
\

== Modelos mentales

- Es uno de los conceptos mas importantes en la interaccion persona computadora

- Se trata de una representacion interna de la realidad externa, es decir, una forma
  de representar la realidad dentro de la mente

- Tienen un papel importante en la cognicion, el razonamiento y la toma de decisiones

- Representa lo que el usuario cree sobre el sistema en cuestion

- Ayudan al usuario a predecir como funcionara un sistema y por tanto, influyen
  en como interactua con una interfaz

\

=== Como podemos usarlos?

*Hacer que el sistema se ajuste a los modelos mentales de los usuarios*, suponiendo que
los modelos

*Cuando no se pueda cambiar la interfaz de usuario, se le puede teachear a los usuarios
un modelo mental mas preciso* para mejorar la experiencia de usuario

\

= UCD - User Centered Design


- Centrado en el designer: El designer, a partir de su vision
  personal, sabe que es lo mejor para la solucion que se quiere
  implementar


- Centrado en el contenido: La informacion es la base para
  organizar la aplicacion y su estructura de navegacion

- Centrado en la tecnologia: Todo gira en torno a la tecnologia
  elegida para implementar una solucion


== Beneficios de UCD

El usuario va a recibir lo que esperaba.


=== No solo es cumplir objetivos

#error[
  No involucrar al usuario a tiempo...

  - Como el customer lo explico
  - Como el PL lo entendio
  - Como el ingeniero lo diseno
  - Como el programador lo escribio
  - Como el ejecutivo de ventas lo describio
  - Como el proyecto fue documentado
  - Como se instalaron las instalaciones
  - Como el usuario paga
  - Como el soporte lo soporto
  - Lo que el usuario realmente necesitaba

  Todos estos errores suelen cometerse cuando no se usa UCD
]


=== UCD segun ISO 13407

1. Especificar el contexto de uso
2. Especificar requisitos
3. Producir soluciones de diseno
4. Evaluacion si seguimos o no con este proceso (volver al punto 1)


== Pasos


=== Especificar el contexto de uso

- Objetivos: identificar a las personas a las que se dirige el
  producto, para que lo usaran y en que condiciones


- Consideraciones:
  - Descubrir el modelo mental de los usuarios finales
  - Analizar profundamente sus destrezas
  - Pensar alternativas de una mejor solucion que la que tienen.


#importante[
  Metodos y tecnicas:

    - Observacion: decirle a un usuario que use una aplicacion,
      dandole primero instrucciones; con esto tenemos que entender
      que "problemas" tiene y como reacciona frente a nuestra
      interfaz
    - Eye tracking: Seria hacer un trackeo de los ojos mientras el
      usuario utiliza la app, esto nos marca claramente en donde
      esta la atencion del usuario en terminos visuales
    - Entrevistas: Es presencial
    - Encuestas: Es virtual o formularios genericos mas masivos
    - Analitica Web: Con autorizacion del usuario podemos observar
      cuanto tarda en hacer un click, diferencia de tiempo entre
      clicks, que botones se usan mas, que zonas no se usan, se
      desvian los usuarios de los flujos?
]

\

=== Especificar el contexto de uso - Analitica Web

Se define como la medicion, recoleccion, analisis y documentacion
de datos de internet con el objetivo de comprender y optimizar el
uso de la web

\

=== Especificar requisitos

- Objetivos: identificar y detallar los objetivos de los usuarios

- Consideraciones: Satisfacer claramente los objetivos y el 
  alcance definido

- Metodos y tecnicas: modelos de personas

\

==== Modelos de personas

Una "persona" representa un grupo o subconjunto de los usuarios de
un sistema que presentan similitudes en:

- Comportamiento
- Metas
- Motivaciones
- Necesidades

\

=== Producir soluciones de diseno - Ej.: Card sorting

Le das a un usuario un grupo de cartas con palabras clave que van
a estar en tu interfaz, le pedis que agrupe las cartas segun un 
criterio que considere y te guardas esa forma de agrupamiento para
poder entender como piensa el usuario

\

=== Evaluacion

- Objetivos: Comprobar si las soluciones cumplen con el contexto
  de uso y satisface las necesidades del usuario

- Consideraciones
  - Listado de los aspectos positivos y negativos encontrados en
    el prototipo "respecto" a los "requisitos"
  - Decidir si se sigue o no con otra iteracion mas

- Metodos y tecnicas: Evaluaciond e usabilidad

\

= UCD - Tecnicas para especificar el contexto de uso y los requisitos

#importante[
  Estas tecnicas se tienen que aplicar en el trabajo
]

== Usuarios representativos

#error[
  Si pensas en usuarios representativos para "pagina de 
  universidad", los usuarios representativos NO son alumnos y
  docentes (esos son modelos de persona). Los UR es variando 
  caracteristicas mas particulares/atributos como "edad", "genero",
  "comfort tecnologico", "nivel socioeconomico", etc.
]

#importante[
  - *Si vos tomas siempre un rango de edad X, tiene que haber una 
    justificacion*
  - *Cuando hablas de valores de los atributos, hay que ser 
    precisos, tenemos que usar rangos claros o sustento usando
    recursos como "2 veces el sueldo minimo"*
]

\

== Aspectos claves

- Como se encuentran integradas las distintas actividades 
  particulares en una actividad mas grande de comportamiento?

- Cuales son las similitudes y diferencias que presentan las 
  personas?

  _Resumirlas mediante los modelos de persona_

\

== Entrevista

=== Que no preguntar

- "Esto que es importante, que pensas..." $=>$ esto es una 
  *pregunta guiada* y condiciona la respuesta que va a dar

- "Como te gustaria que este implementado tal cosa" $=>$ esto no va
  a aportar porque el estudio de los colores es psicologico y
  ciertos colores generan ciertas emociones y esta estudiado eso,
  no es una cuestion de gustos. *Estas respuestas no nos dan info.
  valiosa*

- Plantear situaciones hipoteticas tipo "Como te sentirias si esta
  funcionalidad estuviera de tal manera" El usuario va a responder
  lo primero que se le venga a la cabeza

- Si pedimos puntuacion, usar escalas que haga que sea relevante
  para todos los usuarios por igual



=== Recomendaciones

- Considerar los diferentes tipos de usuarios del sistema, no
  entrevistar siempre al mismo

- Puede ser dificil conseguir personas para entrevistar. No hay que
  necesariamente entrevistar al perfil exacto, es preferible
  entrevistar a un perfil que aproxime antes que no hacer ninguna
  entrevista

- Hacer *recorridos cognitivos*: consiste en validar que la
  redaccion de las preguntas sea clara y este alineada con el
  modelo mental de la audiencia

  - Reclutar un conjunto de miembros de la potencial audiencia
  - Darle una copia de la encuesta (no editable)
  - Pedirles que resuman con sus propias palabras de que trata cada
    pregunta expliquen el proceso de pensamiento detras de cada
    respuesta

- Realizar *pruebas de funcionamiento*: Validar que el 
  comportamiento de la encuesta sea adecuado
  - Reclutar a algunos colegas
  - Pedirle que pruebe la mecanica de la encuesta varias veces en
    diferentes dispositivos y navegadores

#importante[
  - Si tenes que controlar dispositivos y hay nenes en la casa, hay
    que dar la opcion de restringir contenido para ciertos 
    dispositivos
]


=== Como generar un modelo de persona?

Esto es agrupar usuarios personas en un grupos

- Nombre: nombre real, fantasia, rol

- Atributos: aspectos de la persona que puedan afectar o no su
  interaccion con el sistema

- Dibujo o fotografia representativa: es agradable porque permite
  darle una imagen a esa persona


==== Ejemplo:

- Foto
- Nombre: "Juana"
- Edad: X
- Metas
- Motivaciones
- etc.

#nota[
  Ver plantilla minima que esta publicada en el campus, para mas o
  menos representar la informacion que tenemos que mostrar
]



#error[
  Errores creando los modelos de personas:

  - Nivel tecnologico: #text(fill: red)["Alto"] $=>$ es recontra 
    ambiguo, hay que definir un criterio para decir quien es alto,
    medio o bajo
    
  - No agregar foto $=>$ #text(fill: green)[Es importante agregar 
    foto]

  - No tener en cuenta la informacion accesible por la API

  - Ser muy generalista tipo "Familia", hay que especificar un poco
    mas

]


#doubt[
  Que criterio deberiamos tener para definir la especificidad del
  modelo de persona?
][
  No hay un criterio para poner detalle, si es muy poco detallado,
  despues en el contenido, va a ser muy complicado definir las
  "predicciones de comportamiento"
]


== Que NO hay que hacer

#error[

  - Poner una foto de alguien conocido/famoso, esto sugestiona la
    personalidad ya que es conocida justamente

  - Poner una explicacion que no tiene nada que ver con lo que
    queres analizar del usuario tipo descripciones fisicas

]


== Carga cognitiva

_Es el esfuerzo mental que una persona necesita para realizar una tarea_

- *Reducir abarrotamiento:* sacar todo lo que distraiga el ojo

- *Construir usando los modelos mentales existentes:* los usuarios tienen 
  mucha experiencia usando aplicaciones que respetan una gran cantidad de convenciones

- *Deshacerse de tareas:* Tratar de quitarle carga al usuario, haciendo que la UI realice
  las tareas que no son necesarias. Usar datos biometricos en lugar de passwords o mostrar
  datos previamente ingresados en lugar de volver a ingresarlos, son algunos ejemplos


== Atencion Selectiva


== Peso visual

Usar a nuestro favor la vision de tunel de las personas para que presten atencion en lo
que queremos nosotros


=== Factores que lo afectan

- Forma
- Size
- Color: Nivel de saturacion y el brillo pueden afectar visualmente
- Contraste: Mayor contraste con sus alrededores llama mas la atencion


= Tips importantes

#importante[
  - Generar una estructura acorde, tampoco hay que hacer todo enorme.
  - Usar no mas de 4 tipografias.
  - Usar pocos colores
  - Nunca recurrir a la capacidad de memoria del usuario para el uso de nuestra ui (solo cosas personales)
  - Un menu de interfaz web no puede tener mas de 7 opciones
  - Si una tarea requiere una alta carga cognitiva, generalmente significa que hara un uso intensivo de la
    memoria de trabajo
]


#doubt[
  Tiene sentido una interfaz complicada de usar pero que tu objetivo sea que el usuario 
  aprenda y logre ser lo mas eficiente posible en el trabajo que desea realizar?
][

]


= Principios de Gestalt

== Similitud

- color
- tipografia
- orientacion
- movimiento





#nota[
  El color puede modificar el impacto de la distribucion de las figuras o similitudes
]


== Proximidad

#nota[
  Separacion es mas relevante que el color
]

== Cierre

Podes dejar cosas incompletas le da al usuario un indicio de la intencion de la interfaz de forma natural.

#nota[
  - La discontinuidad puede generar contiuidad en la percepcion del usuario
]

#importante[
  Pueden faltar pedazos pero parcialmente, no podes dibujar dos lineas y esperar que
  el usuario imagine que es
]


== Conectividad

#nota[
  Principio mucho mas fuerte que el de proximidad y similitud, que puede romper la sensacion de conexion entre
  dos objetos que no corresponde que esten conectados
]


== Destino comun

Si agregamos animaciones, si dos objetos estan sincronizados hace que los relacionemos entre si

*Ejemplo*:
Cuando te pones en modo borrar aplicaciones, todos los simbolos se mueven de forma identica, lo que sugiere que estas en ese modo
y que lo que esta "temblando" son cosas que podes borrar/mover

*Ejemplo - Destino comun*:
Cuando scrolleas hacia un lado en el celular, claramente las aplicaciones van hacia donde se movieron


== Continuidad

A pesar de que haya una discontinuidad fisica, no quiere decir que el cerebro no lo perciba de forma continua

*Ejemplo:*

En los formularios las cajas se ponen del mismo size y alineadas para que sea mas claro que tiene que llenar los campos de forma secuencial.

Si estuviesen uno al lado del otro y otros abajo, seria mas complicado


= Percepcion motriz - Ley de Fitts

#importante[
  Los movimientos rapidos y los objetivos chiquitos resultan en mayores 
  tasas de error, debido al equilibrio entre velocidad y precision
]

#importante[
  No tendria sentido hacer botones del sizeof *mi dedo (eventualmente)* porque necesita mucha precision el usuario, en conclusion 
]

$ I D = log_2 ((2D)/W) $

$ I P = (I D)/(M T) $

$ M T = a + b . I D = a + b . log_2 ((2D)/W) $


= Ley de Hick

- La *Ley de Hick* es $log_2 (N), N = "numero de opciones"$


= Lenguaje

Hay que usar lenguaje claro para que todos puedan entender y usar la aplicacion


= Emociones

#nota[
  Las emociones proporcionan info. importante para ayudarnos a priorizar la atencion 
  y ser mas eficientes a la hora de tomar decisiones
]


== Nivel visceral

Es una respuesta inconsciente, instintiva y efectiva a un estimulo externo no 
relacionada con la racionalidad o razonamiento


== Nivel conductual

Si la computadora tiene una falla y pierdo el trabajo de las ultimas horas 
seguramente tendre una sensacion de furia, abatimiento, etc.


== Nivel reflexivo

- Es el mas alto, y consiste en pensamientos consientes, interpretacion y razonamiento

- Un jefe que todo el tiempo dice "no servis para nada", no va a servir de nada justamente porque
  todo el tiempo tenes una percepcion negativa de tu trabajo constantemente


== Porque es importante todo esto?

- Nos permite encontrar formas para mitigar las emociones negativas y mejorar las positivas

- Si una emocion sucede a nivel visceral no importa la cantidad de razona

#importante[
  - eficacia
  - eficiencia
  - satisfaccion
]


= Que es un prototipo?

_Version temprana de un producto destinada a ser presentada a los
posibles usuarios y/o para efectuarle algun tipo de pruebas_

== Tipos de prototipos

Se pueden clasificar de diferentes formas:

- Baja (papel): implementados en papel, constituyen una serie de
  dibujos o impresiones, los cuales no permiten una interaccion 
  realista

- Media (digital): Implementados con una design tool, constituyen
  una serie de pantallas que contienen un mayor nivel de detall visual
  y permiten cierto nivel de interaccion sin la necesidad de 
  programacion

- Alta (nativo): *Implementados en algun lenguaje de programacion*, 
  constituye una aplicacion con un nivel de detalle visual e
  interacciones completamente realistas. *Son reutilizables*



== Baja o Alta fidelidad?

El uso de los distintos tipos de prototipos no es excluyente, siendo 
utiles en diferentes momentos del ciclo de vida de desarrollo del
producto.

#error[
  No poner: casa1, casa2, casa3.

  Es importante poner nombres representativos, porque sino los malos
  ejemplos dificultan el entendimiento del contenido
]


=== Baja fidelidad

- Utiles para identificar tempranamente defectos

- Rapidos de construir (y de modificar)

- Economicos porque no precisan de tecnicos expertos

- Eficientes porque se evitan perder horas preciadas de desarrollo
  en la codificacion de prototipos desechables


=== Alta fidelidad

- Eficaces para recolectar datos referidos al rendimiento

- Eficaces para demostrar a los usuarios como se comportara finalmente
  la interfaz de usuario

- Se construyen con herramientas especializadas que ofrecen mas
  detalle y precision

- Necesitan mayor tiempo para su construccion


== Video prototyping

Es una tecnica utilizada para poder mostrar la manera en la que los
usuarios podran interactuar

#nota[
  Esto se puede hacer tanto con prototipos de bajo nivel en papel como 
  se mostro en clase como con prototipos de alto nivel
]


= Recomendaciones para el prototipado

== Figma

_No es obligatorio usarlo_

Pero es una herramienta gratuita, muy usada, completa y gratuita


#nota[
  Hay infinidad de herramientas para prototipado
]


== Sketch

Solo disponible para mac


== Lunacy

== Penpot

= User story maps

- Los equipos suelen basarse en documentos de requisitos y 
  especificaciones funcionales largos y poco eficaces para pasar
  de la vision de un producto a lo que debe incluir como debe funcionar

- En lugar de mantener una conversacion continua sobre los usuarios,
  los problemas, las ideas y las soluciones, los equipos esperan que
  la documentacion distribuida sea suficiente

- Como alternativa, *los user story mapping* son convenientes


#importante[
  - Actividades: ejemplo - "ejecutar una rutina", "encender la tele"
  - Pasos: ejemplo - "loguear", "seleccionar dispositivo"
  - Detalles: ejemplo - "Introducir nombre de usuario", "correo", 
    "password", "presionar un boton"
]


#nota[
  Pueden dividirse y asignar para distribuirnos las tareas
]

= User journey maps

Mapa del punto de vista del usuario de la experiencia del usuario, es
una representacion visual de los pasos que este sigue para alcanzar un 
objetivo, junto con sus pensamientos, sentimientos, etc.


== Componentes

- Persona: Representacion ficticia de un usuario ideal, quien es, que
  necesita, que le molesta, como se comporta

- Etapas del recorrido

- Objetivos: que espera lograr el usuario en cada etapa del recorrido

- Puntos de contacto: canales y momentos donde el usuario interactua
  con la marca

- Pensamientos, emociones y acciones: que piensa, siente y hace en
  cada etapa

- Puntos de dolor: Va de la mano con el item anterior


= Evaluacion de usabilidad

== Evaluacion predictiva

Predecir potenciales problemas que potencialmente puede tener tu UI

#importante[
  No intervienen los usarios, es la unica de las 3 evaluaciones de las
  que *no interviene el usuario*
]

Lo que busca es predecir problemas, tenemos 3 a 5 expertos en 
usabilidad y en base a su experiencia, utilizan heuristicas para
ver si las cumple o no.

Finalmente los expertos nos dan un informe con los problemas que
puede llegar a tener la app.

#nota[
  La idea es practicar esta evaluacion a pesar de que no seamos
  expertos.

  La evaluacion no tiene que ser grupal. Lo ideal es que cada 
  integrante mire con sus propios ojos y escriba lo que piensa, 
  finalmente se hace una puesta en comun y en base a eso se construye
  el informe.
]


=== Evaluacion heuristica

- Constituye una forma temprana, rapida y relativamente economica
  de retroalimentacion

- La eleccion de una correcta heuristica puede sugerirle tambien a los
  designers algunas de las posibles medidas correctivas

- Aplicar una heuristica con eficacia, requiere de cierto nivel de
  conocimiento y experiencia

- Los expertos entrenados en usabilidad a veces son dificiles de
  encontrar y pueden ser costosos


=== Principios de Jakob Nielsen

- *Visibilidad del estado del sistema:* El usuario en todo momento 
  tiene que tener un feedback apropiado de lo que esta sucediendo en 
  la aplicacion. Ej: "Si el usuario presiona agregar dispositivo, 
  tiene que tener un feedback razonable como una notificacion de que 
  se agrego exitosamente"

- *Correspondencia entre el sistema y el mundo real:* La app tiene que
  usar palabras y conceptos que le sean familiares a los usuarios.
  Bajo ninguna circunstancia podemos usar jerga tecnica porque
  confunde al usuario. Ej: "No se pudo acceder al endpoint" => esto
  quiere decir que para los errores siempre tenemos que mostrar un
  mensaje amigable para usuarios no tecnicos

- *Control y libertad del usuario:* De la misma forma que se necesita
  el cartel de #text(fill: green)[Exit] como salida de emergencia,
  el usuario necesita tener acceso claro a operaciones como "undo",
  "salir", etc. sino el *usuario puede tener malas sensaciones*.
  No queremos que la aplicacion controle al usuario sino que el
  usuario *sienta que tiene el control* (aunque quizas no lo tenga)

- *Coherencia y estandares:* Las convenciones deberiamos conservarlas
  porque los modelos mentales de los usarios estan alineados a eso

- *Prevencion de errores:* El usuario se va a equivocar y en caso de
  que esto pase, hay que darle al usuario una buena descripcion de
  porque ocurrio y a travez de un mensaje adecuado, no deberia
  volver a suceder. *Mejor si podemos evitar que el usuario cometa el
  error*. Ej: En un calendario para reservar no deberiamos dejarle
  al usuario reservar en el pasado, no tiene sentido dejarle reservar
  y arrojar un error despues

- *Reconocimiento en lugar de recuerdo:* Minimizar la carga en la
  memoria del usuario

- *Flexibilidad y eficiencia de uso:* Darle experiencia adecuada a 
  todos los usuarios para que los power users puedan ser eficientes
  en la aplicacion. (Como en vim) Tiene que poder ser adaptable a las
  necesidades del usuario

- *Estetica y minimalismo:* Como se menciono anteriormente, solo hay 
  que mostrar lo relevante y evitar el abarrotamiento.

- *Ayudar a los usuarios a reconocer, diagnosticar y recuperarse de 
  errores:*  Si lo errores no se pueden prevenir, darle un feedback
  excelente al usuario para que entienda exactamente lo que tiene que
  hacer para resolverlo.

- *Ayuda y documentacion:* :/


#nota[
  Es importante que todos usemos la misma terminologia en el grupo
  porque sino le generamos confusion al usuario, ej:

  Para referirnos a casa, usar siempre "Hogar" o "Casa" o "Vivienda"
  pero *NUNCA usar varias*.

  Puede ser util crear un grafico vinculado con flechas que defina
  la terminologia unica y las relaciones entre items de la aplicacion
]


#importante[
  Cada plataforma como MAC, Windows, IOS, tienen sus reglas de design
  y cada desarrollador que quiera implementar para cierta plataforma
  una app, tiene que respetar los std. de la plataforma y
  *el objetivo primario es* que si un usuario esta acostumbrado a usar
  IOS, usar nuevas apps sea natural para su modelo mental.

  *No usar cosas nativas puede generar problemas con esto ya que
  los usarios de IOS no tienen el mismo modelo mental que los
  usuarios de Android*
]

== Evaluacion Empirica

Es un metodo de evaluacion en el que se pide a un usuario o un grupo
ejecutar un prototipo y evaluarlo con el objetivo de recolectar
info. para mejorar la usabilidad de un producto.


#nota[
  Segun Nielsen: Probar una interfaz mas o menos temrinada para verificar metas
]


=== Directa:

El investigador esta presente durante la tarea


=== Indirecta

Cuando la tarea es vista por otro medio tal como video o foto de un
intervalo de tiempo


#nota[
  Es importante en este proceso analizar los sentimientos fisicos
  que se notan en el usuario
]


El observador/investigador observa como el usuario realiza la tarea,
antes que esto el investigador tiene que darle indicaciones verbales
de lo que tiene que hacer. Pueden tomarse tambien datos cuantitativos
por ejemplo tasa de error, tiempo, eficiencia, etc.

#nota[
  No esta mal tirarle una ayuda para ver si lo puede terminar de 
  resolver
]


= Usabilidad

#importante[
  Los usuarios tienen que pensar lo menos posible, si se tienen
  que hacer una pregunta tipo "como hago X accion?", la respuesta
  deberia ser inmediata. Si lo es, quiere decir que no tenemos
  ninguna carga cognitiva para con el usuario
]

== Cosas que hacen pensar a los usuarios

- Terminologia poco familiar para *todos los usuarios*

- Elementos de una pagina sobre los cuales no se puede hacer click

- Criterios que deben de utilizarse en las busquedas, el usuario:
  - Conoce de antemano como se quiere realizar la busqueda?
  - Por que debe comprender la implementacion interna del 
    mecanismo de busqueda?


= Checklist para pulir design

== Crear una jerarquia visual clara

- Las cosas importantes tienen que ser prominentes 
  (*mas destacados*).

- Aquellos elementos que estan relacionados semanticamente, tiene
  que estarlo tambien visualmente

- Los elementos que son parte de otro elemento deben anidarse 
  visualmente para mostrar dicha relacion

#importante[
  Este ultimo item esta bueno para organizar electrodomesticos con
  habitaciones y habitaciones con casas
]

#nota[
  Todo esto permite encontrar rapidamente lo que quiero, sin tener
  que pensar mucho. Esto soluciona muchos problemas que pueden
  tener los usuarios a la hora de UX
]

== Hacer uso de convenciones existentes

- Son utiles porque esta probado que funcionan. Evitan que los
  usuarios tengan que realizar un esfuerzo cognitivo al pasar
  de un sitio a otro

- Los designers con frecuencia son reacios a utilizarlas. Siempre
  existe la tentacion de reinventar la rueda al tratar de design
  algo nuevo y diferente


#importante[
  Si se desea hacer algo disruptivo, la eficiencia del usuario al
  aprender a usar la app tiene que aumentar significativamente
]


== Minimizar ruido visual

- Abarrotamiento: cuando todo en la pagina llama la atencion, el
  efecto producido puede ser abrumador

- Ruido de fondo: Algunas paginas si bien no tienen un gran 
  abarrotamiento, presentan algunos ruidos visuales que terminan
  agotando al usuario



== Permitar tomar decisiones de manera ciega y mecanica

- Cantidad de clicks que puede realizar un usuario para obtener lo
  que desea sin verse frustrado?



#error[
  Obsesionarse con la cantidad de clicks no tiene sentido, nadie
  se pone a contar los clicks que fue haciendo. *Si hay que 
  hacerle sentir al usuario que esta avanzando en su tarea*
]


== Evitar palabras innecesarias


En muchas paginas, gran parte de las palabras estan solo para
ocupar espacio. El resultado final es que nunca nadie las va a
leer.

*Deshacerse de las palabras que el usuario no va a leer* tiene
numerosos beneficios:

- Reduce el ruido de la pagina
- Hace que el contenido mas util se destaque
- Hace que las paginas sean mas cortas, permitiendo que el
  usuario vea mas sin tener que desplazarse verticalmente


= Navegation design

#importante[
  El proposito primario de la navegacion es que el usuario pueda
  encontrar lo que esta buscando e indicarle donde se encuentra
]

La navegacion tiene otros propositos:

- Proporcionar un sentido de orientacion
- Permitirle al usr. conocer de antemano los contenidos del sitio


== Persisten navigation

La mayoria de sitios que navegamos, tienen elementos que estan 
dispuestos en el mismo lugar a lo largo de todas las pantallas.
Esto es asi para que el usuario siempre sepa que en cierto lugar 
es donde siempre va a estar eso que desea hacer. Ej: Si queres
cerrar algo, en general esta o arriba a la derecha o arriba a la
izq

- *Site ID o logo:* Permite saber que uno se encuentra todavia en 
  el mismo sitio. Generalmente se encuentra en la parte superior 
  de la pantalla

- *Secciones:* Navegacion primaria o de primer nivel, son los 
  links a las secciones principales del sitio.


== Elementos de la navegacion

- Herramientas: Cambiar el idioma, Log in, etc.

- Mecanismo para volver a la paginal principal: Generalmente se
  trata de un boton que permite volver a la pagina principal

- Mecanismo de busqueda: A menos que el sitio sea demasiado chico
  o este bien organizado, debe tener un cuadro de busqueda o un
  link a la pagina de busqueda. La mejor forma es una leyenda que
  diga *search* (no complicarlo)


== Orientacion

Es muy comun que un usuario se pierda, por lo que tenemos que
*contrarrestar el sentimiento de "estar perdido"* con un indicador
de lugar

#nota[
  En sitios con estructura jerarquica, esta bueno tener abajo
  *breadcrumbs* que muestran jerarquicamente como llegaste al
  sitio desde la pagina principal hasta la pagina actual
]

#importante[
  // TODO
  *Implementar breadcrumbs en la app de domotica*, tiene 
  claramente una estructura jerarquica tipo:

  $ "Casa Pilar" >> "Habitacion Juani" >> "Iluminacion" $
]

#doubt[
  Que tanta anidacion tiene sentido?

  Tiene sentido agregar una asbtraccion de tipos "iluminacion",
  "cerraduras", "alertas", "calefaccion", etc.?

  Siento que al usuario le puede dar "fiaca" tener que estar 
  ingresando en tantas capas de abstraccion pero a la vez 
  cuando/si es que el usuario tiene muchos dispositivos puede
  ser util.
][

  3 o 4 opciones de breadcrumbs tiene sentido, ya mas de 5 es
  muchisimo.

  Podriamos hacer que lo personalice el usuario

]



== Pagina principal

*Los siguientes items tienen que poder identificarse facilmente
desde la pagina principal*

- Identidad y mision del sitio

- Jerarquia del sitio

- Adelantos

- Contenido actualizado

- Registracion

- Atajos

- Mostrar al usuario lo que esta buscando



= Aplicaciones Moviles


== Que es un dispositivo Movil

- Dispositivo facil de transportar
- Conectable a internet
- Tactil

_Generalmente hablamos de telefonos inteligentes y tablets_


#importante[
  No hay que considerar una compu chiquita, es importante pensar
  primero en la version chiquita y despues pasar a la grande.
]


== Uso de dispositivos moviles

- 6 de cada 10 argentinos aseguro estar usando mas sus smartphones

- 1 de cada 3 argentinos reconocio usar mas apps como zoom, 
  tiktok, instagram

- *Hay mas gente usando celulares que computadoras*
  - 90.5% usa el celular
  - 37.1% usa computadoras
  - 89.7% usa internet

  _Datos del indec_


== Escritorio versus movil

- Escritorio: Generalmente se usa para hacer tareas lentas y 
  complejas

- Movil: Generalmente se usa para hacer tareas rapidas y simples


== Limite de dispositivos moviles

- Pantallas chiquitas
- Tactil (dificil de escribir)
- Bateria limitada
- Acceso a internet inconsistente

#importante[
  Este link muestra como agarramos los dispositivos moviles:

  #align(center)[#link("https://alistapart.com/article/how-we-hold-our-gadgets/")[#text(fill: blue)[How we hold our gadgets]]]

]

#importante[
  - Mantener la persistencia de sesiones
  - En caso de que sea una app con datos sensibles, podes si pedir
    datos biometricos para que no tenga que poner la pass 500 
    veces
]


= Apps vs. Sitios web responsive


= Programacion Web

- *Estructura*: Establece la semantica y la relacion que existe entre los mismos. _En parte de la estructura tambien
  hay un poco de presentacion_

- *Presentacion*: Formato del contenido

- *Comportamiento*: Como debe reaccionar el contenido a partir de la interaccion del usuario



== Techs

- HTML $->$ estructura

- CSS $->$ Presentacion

- JavaScript $->$ Comportamiento


== Mejora prograsiva

Existe un beneficio adicional denominado *progressive Enhancement* (no confundir con *Gracefull Degradation*)

Este termino se puede resumir con la siguiente frase:

_Una escalera mecanica no se puede romper, solo puede convertirse en una escalera_ - Miguel el Hambriento


#importante[
  Construir interfaces Web utilizando estas buenas practicas, garantiza que las mismas puedan ser utilizadas a pesar
  de que el soporte para JS de nuestro visistante se encuentre desactivado o el css se despliegue de forma lenta
]


= Web Tools

_Herramientas que se usan en la materia_

- WAVE $=>$ Hay plugins
- vue.js devtools
- vuetify
- router
- pinia


_No se puede usar_

- nuxt $=>$ empaqueta todo y funciona todo "magicamente"


== View source code (con boton izq)

Te muestra una foto del html que se envio en el segundo cero. Osea que todavia no cargaron muchismas cosas como
el css o js eventualmente


== Inspect

Esto si te carga todo (creo)

#importante[
  Tenemos que analizar que pasa si perdemos conexion a internet
]


