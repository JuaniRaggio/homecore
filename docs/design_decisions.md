# HomeCore - Decisiones de Diseño

Este documento detalla las decisiones de diseño de la interfaz de HomeCore, fundamentadas en los principios de Interacción Persona-Computadora (HCI) vistos en la materia: heurísticas de Nielsen, principios de Gestalt, Ley de Fitts, Ley de Hick, niveles emocionales de Norman, carga cognitiva y el proceso de Diseño Centrado en el Usuario (UCD).

---

## 1. Paleta de colores

**Decisión**: Tema oscuro con un tono índigo (`#818cf8`) para acciones principales y ámbar (`#fbbf24`) para estados activos o destacados. Todos los textos cumplen con el estándar WCAG AA (ratio mínimo 4.5:1) para asegurar que se lean bien.

| Token | Hex | Función |
|-------|-----|---------|
| `bg-primary` | `#0f0f14` | Fondo principal |
| `bg-secondary` | `#1a1a24` | Cards, sidebar, header |
| `bg-tertiary` | `#252532` | Inputs, elementos anidados |
| `text-primary` | `#f1f5f9` | Títulos y texto principal |
| `text-secondary` | `#b0bdd0` | Descripciones y metadatos |
| `text-muted` | `#8494a7` | Etiquetas y marcas de tiempo |
| `accent` | `#818cf8` | Botones y elementos activos |
| `accent-hover` | `#a5b4fc` | Hover de botones y links |
| `accent-warm` | `#fbbf24` | Favoritos y alertas suaves |
| `success` | `#34d399` | Confirmaciones y estados activos |
| `danger` | `#f87171` | Errores y acciones destructivas |
| `border` | `#3a3a4a` | Bordes de cards y divisores |

**Fundamentación**:

- **Nivel visceral (Norman)**: El tema oscuro da una sensación de modernidad y sofisticación que los usuarios suelen esperar de un sistema de domótica. Usamos un contraste alto (superando el ratio 7:1 en textos principales) para que la interfaz no solo se vea bien, sino que sea cómoda de leer.
- **Peso visual (Gestalt - Similitud)**: Usamos el color índigo exclusivamente para elementos interactivos. Así, el usuario identifica rápido qué es un botón o un link por pura similitud visual. El ámbar lo reservamos para lo que ya está "encendido" o es "favorito", marcando una jerarquía clara.
- **Carga cognitiva (Sweller)**: No quisimos abrumar con mil colores. Nos mantuvimos en 4 o 5 tonos funcionales que respetan las convenciones culturales (rojo para error, verde para éxito). Esto ayuda a la memoria de trabajo porque el usuario no tiene que "aprender" qué significa cada color; ya lo sabe de antemano.
- **Iteración UCD**: Durante las pruebas notamos que los grises originales eran muy oscuros y costaba leerlos. Siguiendo el proceso de diseño centrado en el usuario, ajustamos la luminosidad de los textos para cumplir con los estándares de accesibilidad y mejorar la experiencia real.

---

## 2. Tipografía y jerarquía

**Decisión**: Usamos la familia tipográfica Inter para todo, variando los pesos entre 400 (cuerpo) y 700 (títulos). La escala de tamaños está estandarizada para mantener la consistencia.

**Fundamentación**:

- **Carga cognitiva (Sweller)**: Usar una sola fuente ayuda a que la interfaz se sienta más limpia y fácil de procesar. En lugar de mezclar estilos, diferenciamos la importancia de los textos con el tamaño y el grosor, lo que cansa menos visualmente.
- **Gestalt - Similitud**: Los elementos que cumplen la misma función (como todas las etiquetas de los dispositivos) comparten el mismo estilo. Esto permite que el usuario escanee la pantalla y entienda la estructura de la información casi sin pensar.
- **Legibilidad**: Inter es una fuente pensada para pantallas pequeñas y paneles digitales. Al tener un buen espaciado entre letras y una altura de línea generosa, logramos que la lectura sea eficiente y rápida, algo clave para una app de control rápido.

---

## 3. Estructura de navegación

**Decisión**: Implementamos una sidebar izquierda con 6 secciones principales (Inicio, Dispositivos, Habitaciones, Rutinas, Historial, Consumo) y una de Configuración. La barra se puede colapsar para ganar espacio, pero mantiene los íconos visibles.

**Fundamentación**:

- **Ley de Hick**: Limitamos las opciones del menú a 6 ítems principales para que elegir a dónde ir sea casi instantáneo. Como vimos en clase, tener más de 7 opciones aumenta mucho el tiempo que el usuario tarda en decidir.
- **Consistencia (Nielsen #4)**: La sidebar está siempre en el mismo lugar. Esto genera un marco de referencia constante: el usuario siempre sabe que para moverse de sección tiene que mirar a la izquierda, lo que le da seguridad en la navegación.
- **Reconocimiento en lugar de recuerdo (Nielsen #6)**: Al colapsar la barra, dejamos los íconos a la vista. No hace falta que el usuario "recuerde" qué hay en cada sección; los íconos sirven como pistas visuales para reconocer el camino.
- **Control y libertad (Nielsen #3)**: El usuario tiene el control de expandir o colapsar la barra según prefiera más espacio de trabajo o más claridad en los nombres de las secciones.

---

## 4. Breadcrumbs (Migas de pan)

**Decisión**: Agregamos breadcrumbs en el header para mostrar la ubicación actual (ej: `Casa > Dispositivos > Lámpara`). Todos los niveles son clickeables excepto el último.

**Fundamentación**:

- **Orientación**: Como se mencionó en la teoría, es muy común que un usuario se sienta perdido en aplicaciones con mucha profundidad. Los breadcrumbs actúan como un indicador de lugar constante que contrarresta esa sensación.
- **Visibilidad del estado del sistema (Nielsen #1)**: El sistema te dice todo el tiempo dónde estás parado (el contexto). Si estás viendo una lámpara, sabés a qué casa y a qué sección pertenece solo con mirar arriba.
- **Control y libertad (Nielsen #3)**: Funcionan como una "salida de emergencia". Si el usuario quiere volver dos niveles atrás, no tiene que apretar el botón de "atrás" del navegador varias veces; simplemente hace un click en el nivel deseado.

---

## 5. Arquitectura multi-casa

**Decisión**: La aplicación permite gestionar varias propiedades. La casa seleccionada se refleja en la URL y se puede cambiar desde un selector en la sidebar.

**Fundamentación**:

- **Modelo mental**: Diseñamos esto pensando en cómo un usuario organiza su vida: primero piensa en "la casa de la playa" o "el departamento" y luego entra a ver qué pasa ahí. La estructura de la app mapea ese modelo mental de forma natural.
- **Correspondencia con el mundo real (Nielsen #2)**: El flujo (Elegir casa -> Elegir habitación -> Actuar sobre dispositivo) sigue la lógica física de cómo nos movemos en el mundo real.
- **Prevención de errores (Nielsen #5)**: Si alguien intenta entrar a una URL de una casa que no existe (por un error al escribir o un link viejo), el sistema lo redirige automáticamente a su casa principal en lugar de mostrar una pantalla vacía o un error feo.

---

## 6. Vista Overview (Resumen)

**Decisión**: Creamos una vista de "Vistazo General" que resume lo más importante de todas las casas: dispositivos críticos encendidos, rutinas favoritas y consumo energético.

**Fundamentación**:

- **Página principal**: Siguiendo los principios de navegación, el Overview muestra la "misión" de la app: dar control total. Concentra la jerarquía del sitio y los atajos más usados en un solo lugar.
- **Reconocimiento (Nielsen #6)**: No hace falta que el usuario entre casa por casa para ver si dejó la alarma apagada. El sistema le muestra lo "crítico" de entrada, facilitando el reconocimiento de problemas sin esfuerzo.
- **Estética y minimalismo (Nielsen #8)**: Eliminamos la sidebar en esta vista para que el usuario se enfoque 100% en los datos. Solo mostramos información accionable, quitando cualquier ruido visual innecesario.

---

## 7. Tamaño de elementos interactivos

**Decisión**: Los botones y controles tienen un tamaño mínimo de 40px o 48px, y las áreas de toque de los switches son generosas (44px).

**Fundamentación**:

- **Ley de Fitts**: Cuanto más grande y cerca esté un objetivo, más fácil es clickearlo. Diseñamos los controles de luz y alarmas con áreas grandes para que, incluso si el usuario está apurado o usa una pantalla táctil, no le "pifie" al botón.
- **Convenciones de plataforma**: Respetamos los estándares de Apple y Google (44-48px) para que la app se sienta profesional y esté lista para usarse en dispositivos móviles en el futuro.

---

## 8. Agrupamiento de contenido

**Decisión**: Usamos cards para agrupar elementos relacionados, con espacios (paddings y gaps) consistentes de 16px, 24px y 32px.

**Fundamentación**:

- **Gestalt - Proximidad**: Al poner el nombre, el ícono y el switch de un dispositivo cerca entre sí y dentro de una caja (card), el usuario entiende al instante que pertenecen a la misma unidad. Como vimos en clase, la separación por espacios es mucho más efectiva que usar colores distintos.
- **Gestalt - Región común**: El fondo sutilmente distinto de las cards crea un límite visual claro. Esto ayuda a organizar la pantalla sin necesidad de poner bordes gruesos que ensucien la vista.
- **Jerarquía visual**: Los elementos más importantes están más arriba o son más grandes, permitiendo que el usuario encuentre lo que busca sin tener que pensar demasiado.

---

## 9. Feedback y estado del sistema

**Decisión**: Implementamos notificaciones (toasts), estados visuales en tiempo real (íconos que brillan cuando algo se enciende) y spinners de carga en los botones.

**Fundamentación**:

- **Visibilidad del estado del sistema (Nielsen #1)**: En domótica esto es vital. Si tocás un botón para cerrar una puerta, necesitás saber YA si se cerró. Por eso usamos cambios de color inmediatos y mensajes de confirmación.
- **Tiempos de respuesta**: Las animaciones duran entre 150ms y 250ms. Es el tiempo justo para que se note el cambio pero se sienta instantáneo, cumpliendo con los umbrales de percepción que recomienda Nielsen.
- **Nivel conductual (Norman)**: Un feedback claro evita la frustración. Si el usuario hace algo y no pasa nada, se siente inseguro. El feedback constante le da la tranquilidad de que el sistema está respondiendo a sus órdenes.

---

## 10. Prevención de errores

**Decisión**: Validación de formularios mientras escribís, carteles de confirmación para borrar cosas y valores sugeridos por defecto.

**Fundamentación**:

- **Prevención de errores (Nielsen #5)**: Como se dice en la teoría: "es mejor evitar que el usuario se equivoque a tener que avisarle después". Por eso, si estás borrando una rutina, te preguntamos antes para evitar un click accidental.
- **Valores por defecto**: Los sliders de brillo aparecen en 80% y las rutinas pre-seleccionan los días laborales. Esto ahorra trabajo al usuario y evita que tenga que configurar todo desde cero cada vez.
- **Restricciones**: No dejamos que pongas valores imposibles (como un brillo de 200%). El sistema te limita físicamente para que no puedas cometer ese error, igual que el ejemplo del calendario que no deja elegir fechas pasadas.

---

## 11. Terminología consistente

**Decisión**: Usamos siempre las mismas palabras: "Hogar", "Habitación", "Dispositivo" y "Rutina". Evitamos términos técnicos o sinónimos confusos.

**Fundamentación**:

- **Consistencia (Nielsen #4)**: Como se enfatiza en clase, usar distintos nombres para lo mismo (como decir "cuarto" y después "ambiente") confunde. Mantener un glosario fijo ayuda a que el usuario aprenda a usar la app más rápido.
- **Correspondencia con el mundo real (Nielsen #2)**: Usamos un lenguaje cotidiano y cercano (español rioplatense como "Olvidé mi contraseña"). Evitamos palabras de programador como "endpoint" o "toggle" para que cualquier persona pueda entender qué está pasando.

---

## 12. Control y libertad del usuario

**Decisión**: Todas las acciones importantes se pueden deshacer o cancelar. Hay botones de "Volver" claros y las rutinas se pueden apagar sin tener que borrarlas.

**Fundamentación**:

- **Control y libertad (Nielsen #3)**: El usuario debe sentir que él manda, no la aplicación. Los botones de retroceso y las opciones de "cancelar" funcionan como las salidas de emergencia de los edificios: te dan la tranquilidad de que podés explorar sin romper nada.
- **Interfaz no-modal**: Tratamos de no usar ventanas que bloqueen toda la pantalla a menos que sea estrictamente necesario (como una confirmación de borrado). Queremos que el usuario se mueva libremente por la app.

---

## 13. Reconocimiento antes que recuerdo

**Decisión**: Ponemos íconos al lado de los nombres, mostramos el estado de las luces directamente en la lista y usamos un asistente paso a paso para crear rutinas.

**Fundamentación**:

- **Reconocimiento en lugar de recuerdo (Nielsen #6)**: Es mucho más fácil reconocer un ícono de una lámpara encendida que tener que recordar si la habías dejado prendida. Al mostrar toda la info relevante a simple vista, liberamos la carga mental del usuario.
- **Asistentes (Wizards)**: Al crear una rutina, te vamos guiando paso a paso. No tenés que "recordar" qué dispositivos tenés o qué pueden hacer; el sistema te los muestra para que elijas.

---

## 14. Sistema de iconografía

**Decisión**: Diseñamos un conjunto de íconos SVG propios (`HcIcon`) que son consistentes en toda la app, reemplazando a los emojis.

**Fundamentación**:

- **Consistencia visual (Nielsen #4)**: Los emojis se ven distintos en un iPhone que en una PC con Windows. Usar nuestros propios íconos garantiza que todos los usuarios vean la misma interfaz, sin sorpresas visuales.
- **Gestalt - Similitud**: Todos los íconos tienen el mismo grosor de línea y estilo. Esto ayuda a que el ojo los identifique rápido como "elementos de control" y no como simple decoración.

---

## 15. Identidad y Logo

**Decisión**: El logo es una casa dentro de un hexágono con ondas de WiFi. Se usa en la pantalla de carga y en la barra lateral.

**Fundamentación**:

- **Nivel visceral (Norman)**: La forma geométrica da una imagen de tecnología y confianza. El color azul refuerza esa sensación de seguridad, algo fundamental cuando estás controlando las puertas de tu casa.
- **Gestalt - Cierre**: Aunque el dibujo de la casa es simple, el cerebro completa la forma gracias al hexágono que la contiene. Esto hace que el logo se reconozca incluso cuando es muy chiquito, como en la barra lateral colapsada.

---

## 16. Splash Screen (Pantalla de inicio)

**Decisión**: Agregamos una animación de bienvenida con el logo que se puede saltear haciendo click o scroll.

**Fundamentación**:

- **Nivel visceral y reflexivo (Norman)**: Una buena animación de entrada da una sensación de calidad y "terminación" del producto. Genera una respuesta emocional positiva antes de empezar a usar la app.
- **Metáfora espacial**: La animación de "zoom" hacia el logo da la sensación de estar entrando a la casa. Es una forma visual de preparar al usuario para la tarea que va a realizar.
- **Control y libertad (Nielsen #3)**: Sabemos que las animaciones pueden cansar si las ves mil veces. Por eso, dejamos que el usuario la salteé con un simple click, priorizando su tiempo si tiene apuro por apagar una luz.

---

## 17. Modo Vim (Para usuarios avanzados)

**Decisión**: Incluimos un modo opcional para navegar con el teclado (usando teclas como J, K, H, L), pensado para quienes prefieren no usar el mouse.

**Fundamentación**:

- **Flexibilidad y eficiencia (Nielsen #7)**: Una buena interfaz debe servir tanto al novato como al experto. Los "power users" pueden ser mucho más rápidos usando atajos de teclado, y este modo les da esa flexibilidad sin molestar a los demás.
- **Nivel reflexivo (Norman)**: Para un usuario técnico, usar comandos de teclado y ver efectos visuales estilo "terminal" genera satisfacción y una conexión especial con la herramienta, ya que siente que la app "habla su idioma".
- **Ayuda y documentación (Nielsen #10)**: Si alguien activa el modo por error o tiene curiosidad, apretando la tecla `?` aparece una guía completa de cómo usarlo. Siempre hay ayuda disponible para no dejar al usuario trabado.
