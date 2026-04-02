#set document(title: "Evaluación Participativa")
#set page(margin: 2.5cm)
#set text(lang: "es", font: "Linux Libertine", size: 11pt)
#set par(justify: true, leading: 0.65em)
#show heading.where(level: 1): it => [
  #set text(size: 16pt, weight: "bold")
  #block(above: 1.5em, below: 0.8em)[#it.body]
]
#show heading.where(level: 2): it => [
  #set text(size: 13pt, weight: "bold")
  #block(above: 1.2em, below: 0.6em)[#it.body]
]
#show heading.where(level: 3): it => [
  #set text(size: 11pt, weight: "bold")
  #block(above: 1em, below: 0.4em)[#it.body]
]

= Evaluación Participativa
== Entrevista y encuesta

Primero queremos obtener información del perfil en relación a las tecnologías dentro del hogar, para ello se realizaron las siguientes preguntas:

=== Datos personales:

- *Nombre* \
  Permite identificar cada respuesta en caso de necesitar aclaraciones o hacer seguimiento de la información.

- *Edad* \
  Ayuda a analizar si el uso y la relación con la tecnología cambia según la generación (por ejemplo, personas más jóvenes vs. mayores).

- *Ocupación* \
  Permite entender el contexto del usuario. Algunas ocupaciones están más expuestas a la tecnología que otras, lo que puede influir en la familiaridad con dispositivos y aplicaciones.

- *Integrantes de la familia* \
  Permite comprender el entorno en el que vive la persona, ya que la convivencia puede influir en el uso, control y responsabilidades sobre los dispositivos.

- *Tipo de hogar* \
  El tipo y tamaño del hogar afecta directamente cómo se utilizan los dispositivos y cómo podría implementarse una solución tecnológica (por ejemplo, automatización o control).

=== Preguntas:

- *¿Qué tan familiarizado estás con la tecnología?* \
  Permite clasificar a los usuarios según su nivel de conocimiento tecnológico, lo cual es clave para diseñar una aplicación accesible tanto para principiantes como para usuarios avanzados.

- *¿Cuántos dispositivos tecnológicos utilizás en tu hogar?* \
  Ayuda a dimensionar el nivel de interacción tecnológica en el hogar y la complejidad del ecosistema de dispositivos que el usuario maneja.

- *¿Qué tipo de dispositivos inteligentes utilizás en tu hogar?* \
  Permite identificar qué dispositivos son más comunes y relevantes para los usuarios, orientando el diseño hacia compatibilidades reales.

- *¿Cuáles son los que más utilizás?* \
  Ayuda a detectar los dispositivos prioritarios para el usuario, lo que permite enfocar la aplicación en lo más usado.

- *¿Podrías mencionar qué aplicaciones utilizás para manejar estos dispositivos?* \
  Permite conocer herramientas actuales del usuario y detectar oportunidades de mejora frente a soluciones existentes.

- *¿Utilizás más un smartphone o una página web? ¿Por qué?* \
  Ayuda a definir la plataforma principal de uso (mobile o web) y entender las preferencias del usuario.

- *Según tu experiencia, ¿qué cambiarías o qué desventajas encontrás en las aplicaciones que utilizás?* \
  Permite identificar pain points (problemas) actuales, fundamentales para proponer una solución superadora.

- *¿Controlás tus dispositivos principalmente desde dentro de casa o de forma remota?* \
  Ayuda a entender el contexto de uso (presencial vs remoto), clave para definir funcionalidades de la aplicación.

- *¿Qué tan seguido revisás o ajustás la configuración de las apps que usás?* \
  Permite evaluar el nivel de interacción avanzada del usuario con la tecnología.

- *Cuando querés cambiar el estado de un dispositivo, ¿qué es lo primero que buscás?* \
  Ayuda a entender el comportamiento del usuario dentro de una interfaz y optimizar la usabilidad.

- *¿Te gustaría recibir notificaciones del sistema? ¿De qué tipo?* \
  Permite identificar qué tipo de información es relevante para el usuario y evitar sobrecarga de notificaciones.

- *¿Qué es lo primero que hacés cuando llegás a tu casa?* \
  Ayuda a detectar patrones de comportamiento y posibles automatizaciones útiles.

- *¿Cuándo fue la última vez que tuviste un problema con un electrodoméstico? ¿Qué ocurrió?* \
  Permite identificar situaciones problemáticas reales en el uso cotidiano.

- *¿Cuál de estas situaciones te genera más ansiedad?* \
  Ayuda a detectar puntos de fricción emocional, importantes para diseñar soluciones que generen tranquilidad.

- *¿Cuál es tu rutina antes de salir?* \
  Permite identificar hábitos repetitivos que podrían automatizarse.

- *¿Hay algún electrodoméstico que revises dos o tres veces antes de irte o antes de dormir? ¿Por qué?* \
  Permite detectar dispositivos críticos para el usuario y oportunidades de mejora en control o seguridad.

- *¿Hay algún electrodoméstico que NO tocarías remotamente aunque pudieras?* \
  Ayuda a identificar límites de confianza en la automatización.

- *¿Discutiste alguna vez con alguien de tu casa por cómo usa los electrodomésticos? ¿Qué te molestaba?* \
  Permite detectar conflictos de uso y problemas de convivencia relacionados con la tecnología.

- *¿Hay alguna tarea relacionada con electrodomésticos automatizados que odiás hacer?* \
  Permite identificar tareas repetitivas o molestas que podrían simplificarse.

- *¿Hay algún electrodoméstico que sabés que gasta mucho pero igual usás sin control? ¿Por qué seguís haciéndolo?* \
  Permite entender la relación entre consumo, conciencia y comportamiento real del usuario.

#pagebreak()

== Primera entrevista

=== Entrevista: Uso de tecnología y dispositivos en el hogar

#let q(interviewer, answer) = {
  block(above: 0.7em, below: 0.3em)[
    #text(weight: "bold")[Entrevistadora:] #interviewer
  ]
  block(above: 0.3em, below: 0.5em)[
    #text(weight: "bold")[Entrevistado:] #answer
  ]
}

#q(
  [Buenas, estamos haciendo un relevamiento sobre el uso de tecnología en el hogar. ¿Te puedo hacer unas preguntas?],
  [Sí, no hay problema.]
)
#q(
  [Para empezar, ¿me podés contar tu nombre, edad y a qué te dedicás?],
  [Mi nombre es Eduardo Daniel Resek, tengo 61 años y soy odontólogo.]
)
#q(
  [¿Con quién vivís y cómo es tu hogar?],
  [Vivo con mi esposa en una casa de un piso.]
)
#q(
  [¿Qué tan familiarizado estás con la tecnología?],
  [Aprendo rápido tecnologías nuevas.]
)
#q(
  [¿Cuántos dispositivos tecnológicos utilizás en tu hogar?],
  [Entre 3 y 5 dispositivos.]
)
#q(
  [¿Qué tipo de dispositivos inteligentes utilizás?],
  [Utilizo un scanner odontológico, el automóvil, smartphone, laptop y la alarma del hogar.]
)
#q(
  [¿Cuáles son los que más utilizás?],
  [Principalmente todos los que mencioné.]
)
#q(
  [¿Qué aplicaciones utilizás para manejar estos dispositivos?],
  [Uso aplicaciones como MyChevrolet y MyVerisure, entre otras.]
)
#q(
  [¿Preferís usar smartphone o página web? ¿Por qué?],
  [Prefiero usar el smartphone.]
)
#q(
  [Según tu experiencia, ¿qué desventajas encontrás en la tecnología que utilizás?],
  [La dependencia de internet, de la red eléctrica y también la posible filtración de datos personales. Antes, por ejemplo, el teléfono de línea no dependía de estas cosas.]
)
#q(
  [¿Controlás tus dispositivos desde casa o de forma remota?],
  [Ambos, aunque principalmente desde dentro de casa.]
)
#q(
  [¿Con qué frecuencia revisás o ajustás la configuración de las apps?],
  [A veces.]
)
#q(
  [¿Y el historial de uso?],
  [Nunca.]
)
#q(
  [Cuando querés cambiar el estado de un dispositivo, ¿qué hacés primero?],
  [Scrolleo hasta encontrar el dispositivo en el menú principal.]
)
#q(
  [¿Te gustaría recibir notificaciones del sistema?],
  [Sí, sobre consumo eléctrico elevado y el estado de los dispositivos, como si se apagan o desconectan.]
)
#q(
  [¿Qué es lo primero que hacés cuando llegás a tu casa?],
  [Abro la heladera.]
)
#q(
  [¿Cuándo fue la última vez que tuviste un problema con un electrodoméstico?],
  [Cuando el WiFi no se conectaba al televisor.]
)
#q(
  [¿Qué situaciones te generan más ansiedad?],
  [Ninguna en particular.]
)
#q(
  [¿Cuál es tu rutina antes de salir de casa?],
  [No tengo una rutina específica.]
)
#q(
  [¿Hay algún electrodoméstico que revises varias veces antes de salir?],
  [No.]
)
#q(
  [¿Hay algún dispositivo que no controlarías remotamente?],
  [No.]
)
#q(
  [¿Alguna vez discutiste con alguien por el uso de electrodomésticos?],
  [No.]
)
#q(
  [¿Hay alguna tarea relacionada con dispositivos automatizados que no te guste hacer?],
  [No.]
)
#q(
  [¿Hay algún electrodoméstico que sabés que consume mucho pero igual usás?],
  [Sí, el lavarropas y el freezer, pero son necesarios.]
)
#q(
  [Si tuvieras que priorizar funciones en una app, ¿cómo las ordenarías?],
  [Primero dispositivos favoritos, después rutinas, luego consumo y por último historial.]
)

#pagebreak()

== Segunda entrevista

=== Entrevista: Uso de tecnología y dispositivos en el hogar

#let qq(interviewer, answer) = {
  block(above: 0.7em, below: 0.3em)[
    #text(weight: "bold")[Entrevistadora:] #interviewer
  ]
  block(above: 0.3em, below: 0.5em)[
    #text(weight: "bold")[Entrevistada:] #answer
  ]
}

#qq(
  [Hola, ¿te puedo hacer unas preguntas sobre el uso de tecnología en tu hogar?],
  [Sí, claro.]
)
#qq(
  [Para empezar, ¿me podés decir tu nombre, edad y ocupación?],
  [Mi nombre es María Alicia Haiek, tengo 58 años y soy odontóloga.]
)
#qq(
  [¿Con quién vivís y cómo es tu hogar?],
  [Vivo con mi esposo en una casa de un piso.]
)
#qq(
  [¿Qué tan familiarizada estás con la tecnología?],
  [La uso solo para lo básico, como llamadas y redes sociales.]
)
#qq(
  [¿Cuántos dispositivos tecnológicos utilizás en tu hogar?],
  [Entre 3 y 5 dispositivos.]
)
#qq(
  [¿Qué tipo de dispositivos inteligentes utilizás?],
  [Utilizo la Thermomix, la alarma, computadora, parlantes, lavarropas, celular y tablet.]
)
#qq(
  [¿Cuál es el dispositivo que más utilizás?],
  [La Thermomix.]
)
#qq(
  [¿Qué aplicaciones utilizás para manejar estos dispositivos?],
  [Uso Cookidoo para la Thermomix.]
)
#qq(
  [¿Preferís usar smartphone o página web? ¿Por qué?],
  [Prefiero usar la web.]
)
#qq(
  [Según tu experiencia, ¿qué cambiarías o qué desventajas encontrás en las aplicaciones que usás?],
  [Me parecen poco intuitivas.]
)
#qq(
  [¿Controlás tus dispositivos desde casa o de forma remota?],
  [Principalmente desde dentro de casa.]
)
#qq(
  [¿Con qué frecuencia revisás o ajustás la configuración de las apps?],
  [Rara vez.]
)
#qq(
  [¿Y el historial de uso?],
  [Nunca.]
)
#qq(
  [Cuando querés cambiar el estado de un dispositivo, ¿qué hacés primero?],
  [Scrolleo hasta encontrar el dispositivo en el menú principal.]
)
#qq(
  [¿Te gustaría recibir notificaciones del sistema?],
  [Prefiero no recibir notificaciones.]
)
#qq(
  [¿Qué es lo primero que hacés cuando llegás a tu casa?],
  [Desactivo la alarma.]
)
#qq(
  [¿Cuándo fue la última vez que tuviste un problema con un electrodoméstico?],
  [Cuando algo funcionó cuando no debía.]
)
#qq(
  [¿Qué situaciones te generan más ansiedad?],
  [No acordarme si prendí o apagué algo.]
)
#qq(
  [¿Cuál es tu rutina antes de salir de casa?],
  [Conectar la alarma.]
)
#qq(
  [¿Hay algún electrodoméstico que revises varias veces antes de irte? ¿Por qué?],
  [Sí, el lavarropas, porque me da miedo que se incendie.]
)
#qq(
  [¿Hay algún dispositivo que no controlarías remotamente?],
  [La heladera.]
)
#qq(
  [¿Alguna vez discutiste con alguien por el uso de electrodomésticos?],
  [No.]
)
#qq(
  [¿Hay alguna tarea relacionada con dispositivos automatizados que no te guste hacer?],
  [No.]
)
#qq(
  [¿Hay algún electrodoméstico que sabés que consume mucho pero igual usás?],
  [El lavarropas.]
)
#qq(
  [Si tuvieras que priorizar funciones en una app, ¿cómo las ordenarías?],
  [Primero rutinas, después dispositivos favoritos, luego historial y por último consumo.]
)

#pagebreak()

== Tercera entrevista

=== Entrevista: Uso de tecnología y dispositivos en el hogar

#let qqq(interviewer, answer) = {
  block(above: 0.7em, below: 0.3em)[
    #text(weight: "bold")[Entrevistadora:] #interviewer
  ]
  block(above: 0.3em, below: 0.5em)[
    #text(weight: "bold")[Entrevistado:] #answer
  ]
}

#qqq(
  [Hola, estamos analizando cómo las personas usan la tecnología en su vida cotidiana dentro del hogar. ¿Te gustaría participar respondiendo unas preguntas?],
  [Sí, dale.]
)
#qqq(
  [Para comenzar, ¿me podés decir tu nombre, edad y ocupación?],
  [Mi nombre es Manuel Joaquín Pravatto, tengo 22 años y soy estudiante.]
)
#qqq(
  [¿Vivís solo o con otras personas? ¿Cómo es tu hogar?],
  [Vivo solo en un departamento.]
)
#qqq(
  [¿Qué tan familiarizado estás con la tecnología?],
  [Soy bastante entusiasta tecnológico, me gusta probar cosas nuevas seguido.]
)
#qqq(
  [¿Cuántos dispositivos tecnológicos utilizás en tu hogar?],
  [Entre 3 y 5 dispositivos.]
)
#qqq(
  [¿Qué tipo de dispositivos inteligentes utilizás?],
  [Uso televisor, luces inteligentes y reloj.]
)
#qqq(
  [¿Cuál es el que más utilizás?],
  [Las luces.]
)
#qqq(
  [¿Qué aplicaciones utilizás para controlarlos?],
  [Uso WiZ Connected.]
)
#qqq(
  [¿Preferís usar smartphone o página web? ¿Por qué?],
  [Prefiero el smartphone.]
)
#qqq(
  [Según tu experiencia, ¿qué mejorarías de estas aplicaciones?],
  [(No menciona cambios puntuales).]
)
#qqq(
  [¿Controlás los dispositivos dentro de tu casa o también de forma remota?],
  [Principalmente dentro de casa.]
)
#qqq(
  [¿Con qué frecuencia revisás o ajustás la configuración de las apps?],
  [A veces.]
)
#qqq(
  [¿Revisás el historial de uso?],
  [Nunca.]
)
#qqq(
  [Cuando querés cambiar el estado de un dispositivo, ¿qué hacés primero?],
  [Scrolleo hasta encontrar el dispositivo en el menú principal.]
)
#qqq(
  [¿Te gustaría recibir notificaciones del sistema?],
  [Sí, me interesan las alertas de seguridad y el consumo eléctrico elevado.]
)
#qqq(
  [¿Qué es lo primero que hacés cuando llegás a tu casa?],
  [Dejo mis cosas y prendo la computadora.]
)
#qqq(
  [¿Tuviste algún problema reciente con un electrodoméstico?],
  [Sí, a veces me olvido de prender o apagar alguno.]
)
#qqq(
  [¿Qué situaciones te generan más ansiedad?],
  [No acordarme si dejé algo prendido o apagado.]
)
#qqq(
  [¿Cuál es tu rutina antes de salir?],
  [Revisar que todo lo electrónico esté apagado.]
)
#qqq(
  [¿Hay algún electrodoméstico que revises varias veces?],
  [No.]
)
#qqq(
  [¿Hay alguno que no controlarías de forma remota?],
  [Sí, el microondas y la cocina.]
)
#qqq(
  [¿Alguna vez discutiste con alguien por el uso de dispositivos?],
  [No.]
)
#qqq(
  [¿Hay alguna tarea que no te guste hacer con dispositivos automatizados?],
  [Sí, cuando vivía con mi familia y alguien tocaba el interruptor de las luces inteligentes, tenía que volver a configurarlas porque dejaban de seguir la rutina.]
)
#qqq(
  [¿Usás algún dispositivo que sabés que consume mucho?],
  [No.]
)
#qqq(
  [Si tuvieras que priorizar funciones en una app, ¿cómo las ordenarías?],
  [Primero rutinas, después dispositivos favoritos, luego consumo y por último historial.]
)

#pagebreak()

== Cuarta entrevista

=== Entrevista: Uso de tecnología y dispositivos en el hogar

#let qqqq(interviewer, answer) = {
  block(above: 0.7em, below: 0.3em)[
    #text(weight: "bold")[Entrevistadora:] #interviewer
  ]
  block(above: 0.3em, below: 0.5em)[
    #text(weight: "bold")[Entrevistada:] #answer
  ]
}

#qqqq(
  [Hola, ¿te puedo hacer unas preguntas sobre el uso de tecnología en tu hogar?],
  [Sí, claro.]
)
#qqqq(
  [Para empezar, ¿me podés decir tu nombre, edad y ocupación?],
  [Mi nombre es Avril Vidal García, tengo 22 años y soy estudiante.]
)
#qqqq(
  [¿Con quién vivís actualmente? ¿Cómo es tu hogar?],
  [Vivo con mi hermana, mi mamá y mi papá. Es una casa grande de un piso.]
)
#qqqq(
  [¿Qué tan familiarizada estás con la tecnología?],
  [Me manejo bien con apps cotidianas. Uso la tecnología para lo básico, como llamadas y redes sociales.]
)
#qqqq(
  [¿Cuántos dispositivos tecnológicos utilizás en tu hogar?],
  [Entre 3 y 5 dispositivos.]
)
#qqqq(
  [¿Qué tipo de dispositivos inteligentes utilizás?],
  [Uso Chromecast, tablet y celular.]
)
#qqqq(
  [¿Cuáles son los que más utilizás en el día a día?],
  [Principalmente el celular y la tablet.]
)
#qqqq(
  [¿Qué aplicaciones utilizás para manejar estos dispositivos?],
  [Uso la app de Xiaomi para la Smart TV y también Enlace Windows.]
)
#qqqq(
  [¿Preferís usar aplicaciones en el celular o páginas web? ¿Por qué?],
  [Prefiero usar el smartphone porque me resulta más práctico y rápido.]
)
#qqqq(
  [Según tu experiencia, ¿qué cambiarías o qué desventajas ves en las aplicaciones que usás?],
  [A veces no son tan intuitivas o pueden fallar, como cuando hay problemas de conexión.]
)
#qqqq(
  [¿Controlás tus dispositivos principalmente desde tu casa o de forma remota?],
  [En su mayoría los controlo dentro de casa.]
)
#qqqq(
  [¿Con qué frecuencia revisás o ajustás la configuración de las apps?],
  [Nunca.]
)
#qqqq(
  [¿Y el historial de uso?],
  [Muy poco, solo cuando necesito buscar algo importante.]
)
#qqqq(
  [Cuando querés cambiar el estado de un dispositivo, ¿qué es lo primero que buscás?],
  [La vinculación del Chromecast, que está en la parte superior derecha.]
)
#qqqq(
  [¿Te gustaría recibir notificaciones del sistema? ¿De qué tipo?],
  [Sí, me gustaría recibir alertas de seguridad, ejecución de rutinas automáticas, consumo eléctrico elevado y estado de los dispositivos.]
)
#qqqq(
  [¿Qué es lo primero que hacés cuando llegás a tu casa?],
  [Entro, prendo la luz de la entrada, las del pasillo y la de mi pieza.]
)
#qqqq(
  [¿Recordás la última vez que tuviste un problema con un electrodoméstico?],
  [Sí, cuando el WiFi no se conectaba al televisor.]
)
#qqqq(
  [¿Qué situaciones te generan más ansiedad en relación a los dispositivos?],
  [Salir apurada sin revisar todo, no acordarme si apagué algo o quedarme pensando si dejé algún aparato prendido.]
)
#qqqq(
  [¿Cuál es tu rutina antes de salir de casa?],
  [Me fijo de llevar todo lo necesario, apago las luces si quedaron prendidas, desenchufo la planchita y apago el ventilador.]
)
#qqqq(
  [¿Hay algún electrodoméstico que revises varias veces antes de irte o antes de dormir?],
  [Sí, la planchita, porque me da miedo dejarla prendida.]
)
#qqqq(
  [¿Hay algún dispositivo que no controlarías de forma remota aunque pudieras?],
  [Sí, las luces de afuera o las luces automáticas.]
)
#qqqq(
  [¿Alguna vez discutiste con alguien de tu casa por el uso de electrodomésticos?],
  [Sí, con mi mamá, porque ella no suele desenchufar los electrodomésticos.]
)
#qqqq(
  [¿Hay alguna tarea relacionada con los electrodomésticos que no te guste hacer?],
  [No particularmente, pero a veces es molesto tener que estar revisando todo.]
)
#qqqq(
  [¿Hay algún electrodoméstico que sabés que consume mucho pero igual usás?],
  [Podría ser el microondas, aunque no estoy segura. También podría ser el colchón eléctrico.]
)
#qqqq(
  [Si tuvieras que priorizar funciones en una app (rutinas, historial, dispositivos y consumo), ¿cómo las ordenarías?],
  [Primero rutinas, después historial, luego dispositivos favoritos y por último consumo.]
)
