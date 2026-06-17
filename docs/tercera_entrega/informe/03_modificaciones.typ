#import "template.typ": figrow

= Modificaciones del prototipo

La aplicación no sufrió grandes modificaciones respecto al prototipo entregado; las principales fueron el agregado de un nuevo destino a la navigation bar, el cambio de nombre de dispositivos a cuartos y la modificación de los botones para agregar habitaciones, dispositivos y rutinas a Floating action buttons.

== Creación de nuevo destino: Actividad

El nuevo destino combina lo que era Historial y Consumo en la web. Se quería mantener la consistencia interna de la aplicación entre mobile y web (Shneiderman 1: Strive for consistency), pero una Navigation bar tiene un máximo de 5 destinos y deben ser igual de importantes, por lo que se unieron los 2 destinos menos usados. En el prototipo toda esta información estaba en perfil, que ahora tiene solo el perfil del usuario y una sección de ajustes, donde se puede modificar el idioma, el tema de la aplicación y la contraseña.

#figrow(
  ("images/image53.png", "images/image18.png", "images/image28.png"),
  [Imágenes 33, 34 y 35: Usuario vista del prototipo, usuario vista de la aplicación, y actividad en la aplicación],
)

== Modificación del nombre del destino Dispositivos a Habitaciones

La modificación del nombre de Dispositivos a Habitaciones es producto de que la app no permite agregar un dispositivo sin que tenga un cuarto, lo cual no se esperó a la hora de hacer el prototipo. Además el destino agrupa a los dispositivos por habitación, por lo que el nombre es más representativo.

== Pantalla de Habitaciones (anteriormente Dispositivos)

=== Acciones de creación
En el prototipo había dos botones en la fila horizontal (“+ Nuevo Dispositivo” / “+ Nueva Habitación”) ubicados debajo del buscador, lo cual se cambió a un Floating Action Button (FAB) expandible en la esquina inferior derecha que al presionar despliega las dos opciones. Esto se debe a que, en la evaluación predictiva de la primera entrega, se especificó que los botones en fila ocupaban espacio visual importante y, dado que su tamaño era reducido, iba a ser una dificultad para usuarios con movilidad reducida (Ley de Fitts). El diseño actual tiene el botón posicionado en la zona de alcance natural del pulgar, maximiza el área del toque y elimina la competencia visual entre las dos acciones primarias. Además el menú expandible del FAB aplica la Ley de Hick ya que la pantalla no muestra ambas opciones hasta que se necesiten, reduciendo la carga cognitiva visual.

=== Eliminación de botón “Filtros”
En el prototipo existía el botón “Filtros” en fila junto a los botones de creación. Sin embargo, durante la evaluación empírica de la primera entrega, ningún participante usó la función de filtros ya que la mayoría recurrió al buscador. Es por esto que mantener este botón solo generaría carga cognitiva innecesaria (yendo en contra del Nielsen \#8 - principio de diseño estético y minimalista) sin beneficio demostrado. Por ende en el diseño final, se optó por eliminar “Filtros”.

=== Agregación de menú de opciones por habitación
En el prototipo las habitaciones eran agrupadores visuales sin opciones de edición propias visibles en la tarjeta. Sin embargo en el RF14 (gestionar habitaciones), se explicita el poder editar y eliminarlas. Es por esto que en la implementación se adoptó el patrón OverflowMenu (⋮), siendo que el ícono es convencional y reconocible (Nielsen \#4: Consistencia con estándares de plataforma), no ocupando espacio en la tarjeta cuando no se usa. También es importante mencionar que el diálogo de confirmación con el nombre previene la eliminación accidental (Nielsen \#5: Prevención de errores).

#figrow(
  ("images/image35.png", "images/image32.png", "images/image6.png"),
  [Imágenes 36, 37, 38: Comparación entre Habitaciones (actual) y Dispositivos (prototipo)],
)

#figrow(
  ("images/image30.png", "images/image55.png"),
  [Imágenes 39, 40: Flujo de eliminación en Habitaciones],
)

Se decidió utilizar Floating action buttons para que las acciones estén disponibles incluso cuando el usuario se mueve en la pantalla; además aplica la cuarta heurística de Nielsen al tener consistencia externa, ya que el usuario probablemente interactuó con un FAB anteriormente en alguna otra aplicación (WhatsApp, Gmail, X). En el caso de Habitaciones el botón contiene un menú que muestra las 2 opciones.

#figrow(
  ("images/image51.png", "images/image36.png", "images/image42.png"),
  [Imágenes 41, 42 y 43: Dispositivos vista del prototipo, dispositivos vista de la aplicación, y dispositivos con FAB abierto en la aplicación],
)

== Pantalla de rutinas

=== Eliminación de rutinas
Anteriormente, en el prototipo no se mostraba la opción para eliminar. Es por esto que, para garantizar consistencia con el flujo de eliminación en Habitaciones (Nielsen \#4) y para garantizar la libertad del usuario (Nielsen \#4), se usó el mismo OverflowMenu. La confirmación con nombre “¿Eliminar "nombre de rutina"?” aplica el principio de prevención de errores, lo cual provee la confianza necesaria para los usuarios.

=== Cambio de color en botón “Ejecutar ahora”
Se mejoró la diferenciación visual de las acciones de mayor impacto dentro de la aplicación. Para ello, los botones destinados a la ejecución inmediata de rutinas adoptaron un color distintivo respecto de los demás controles, facilitando su identificación por parte del usuario y reduciendo posibles errores de interacción. Además, el estilo fue centralizado para garantizar consistencia visual y una mejor mantenibilidad del sistema.

== Modificaciones generales a partir de la segunda entrega

Hay ligeros cambios en los colores en varias partes de la app respecto al prototipo; esto se debe a que decidimos utilizar los design tokens de la aplicación web, en este caso se encuentran centralizados en `itba.homecore.ui.theme`.

Algunos diseños implementados en la web no se trasladaron a la app por falta de espacios para visualizarlos; este es el caso del overview de las distintas casas y los gráficos del consumo eléctrico. También algunos de los paneles de acciones contextuales de los dispositivos fueron simplificados para que estos mantengan un tamaño similar.
