# HomeCore - Decisiones de Diseno

Documento de decisiones de diseno de la interfaz de HomeCore. Cada decision se fundamenta en teoria de Interaccion Persona-Computadora (HCI), referenciando los principios vistos en la materia: heuristicas de Nielsen, principios de Gestalt, Ley de Fitts, Ley de Hick, niveles emocionales de Norman, carga cognitiva (Sweller) y el proceso de Diseno Centrado en el Usuario (UCD, ISO 13407).

---

## 1. Paleta de colores

**Decision**: Tema oscuro con acento principal indigo (`#818cf8`) y acento secundario ambar (`#fbbf24`). Todos los colores de texto cumplen WCAG AA (ratio minimo 4.5:1) sobre sus fondos esperados.

| Token | Hex | Funcion |
|-------|-----|---------|
| `bg-primary` | `#0f0f14` | Fondo principal |
| `bg-secondary` | `#1a1a24` | Cards, sidebar, header |
| `bg-tertiary` | `#252532` | Inputs, items nested |
| `text-primary` | `#f1f5f9` | Titulos, texto principal (~14:1 sobre bg-secondary) |
| `text-secondary` | `#b0bdd0` | Descripciones, metadata (~8:1 sobre bg-secondary) |
| `text-muted` | `#8494a7` | Labels terciarios, timestamps (~5:1 sobre bg-secondary) |
| `accent` | `#818cf8` | Links, botones, indicadores activos |
| `accent-hover` | `#a5b4fc` | Hover de links y botones |
| `accent-warm` | `#fbbf24` | Favoritos, alertas suaves |
| `success` | `#34d399` | Estados activos, confirmaciones |
| `danger` | `#f87171` | Errores, acciones destructivas |
| `border` | `#3a3a4a` | Separadores, bordes de cards |

**Fundamentacion teorica**:

- **Nivel visceral (Norman)**: Los temas oscuros comunican sofisticacion y modernidad, alineandose con las expectativas del usuario para un producto de tecnologia del hogar. El alto contraste entre fondo oscuro y texto claro (`#f1f5f9` sobre `#0f0f14`) supera el ratio 7:1 recomendado por WCAG AAA, maximizando la legibilidad.
- **Peso visual (Gestalt - Similitud)**: El indigo como color de accion primaria crea un punto focal claro en botones y elementos interactivos, guiando la atencion del usuario hacia las acciones principales. El ambar se reserva para estados activos y favoritos, estableciendo una jerarquia visual a traves del color.
- **Carga cognitiva (Sweller)**: La paleta se limita a 4-5 colores funcionales para reducir la carga cognitiva extrinseca y mantener coherencia. Cada color tiene un rol semantico definido: indigo = accion, ambar = alerta/activo, verde = exito, rojo = peligro. Esto respeta el limite de la memoria de trabajo (Miller, 1956): el usuario no necesita aprender significados arbitrarios porque los colores siguen convenciones culturales establecidas (rojo = peligro, verde = exito).
- **Iteracion UCD (ISO 13407)**: La paleta fue ajustada en la fase de evaluacion tras detectar que los colores originales (`#6366f1` para accent, `#94a3b8` para text-secondary, `#64748b` para text-muted) no cumplian WCAG AA, generando una critica recurrente en las evaluaciones de usabilidad: "el texto gris sobre fondo oscuro es dificil de leer". Se incremento la luminosidad de todos los tokens de texto y acentos para cumplir con los ratios minimos de contraste.

---

## 2. Tipografia y jerarquia

**Decision**: Familia tipografica unica Inter, con pesos de 400 (cuerpo) a 700 (titulos). Escala de tamanos definida en variables CSS (0.75rem a 1.875rem).

**Fundamentacion teorica**:

- **Carga cognitiva (Sweller)**: Una unica familia tipografica reduce la carga extrinseca. La diferenciacion se logra a traves del peso y el tamano, no mediante variedad de fuentes. Esto evita que el usuario tenga que procesar multiples estilos visuales, liberando recursos cognitivos para la tarea.
- **Gestalt - Similitud**: Los elementos con la misma funcion comparten el mismo estilo tipografico (todos los labels en font-size-sm/weight-500, todos los titulos en font-size-lg/weight-600). Esto permite al usuario identificar rapidamente la funcion de cada elemento textual sin esfuerzo consciente.
- **Legibilidad**: Inter esta optimizada para pantallas, con apertura de caracteres amplia y soporte nativo de ligaduras. El line-height de 1.6 para texto de cuerpo cumple las recomendaciones de accesibilidad. Esto contribuye directamente a la eficiencia (ISO 9241-11): el usuario puede leer mas rapido con menor fatiga visual.

---

## 3. Estructura de navegacion

**Decision**: Sidebar izquierda con 6 items de navegacion primaria (Inicio, Dispositivos, Habitaciones, Rutinas, Historial, Consumo) + 1 item secundario (Configuracion). La sidebar es colapsable y mantiene iconos visibles en estado colapsado.

**Fundamentacion teorica**:

- **Ley de Hick**: Las opciones de navegacion se limitan a 6 items primarios. La Ley de Hick establece que el tiempo de decision es proporcional a `log2(N)`, donde N es el numero de opciones. Con 6 items, el tiempo de decision se mantiene bajo. Ademas, esto coincide con el rango de 5-7 elementos que la memoria de trabajo puede procesar simultaneamente (Miller, 1956), lo que se refuerza desde la teoria vista en clase: "Un menu de interfaz web no puede tener mas de 7 opciones".
- **Consistencia y estandares (Nielsen #4)**: La sidebar permanece fija en todas las vistas autenticadas, proporcionando un marco de referencia espacial constante. La posicion activa se resalta con un fondo indigo semitransparente, cumpliendo el principio de navegacion persistente: "la mayoria de sitios tienen elementos dispuestos en el mismo lugar a lo largo de todas las pantallas".
- **Reconocimiento en lugar de recuerdo (Nielsen #6)**: La sidebar colapsable permite expandir el area de contenido cuando se necesita mas espacio, manteniendo los iconos SVG visibles como referencia. El usuario puede reconocer secciones por sus iconos sin necesidad de memorizar significados. En estado expandido, se muestran iconos + texto, eliminando toda ambiguedad.
- **Control y libertad del usuario (Nielsen #3)**: El colapso/expansion de la sidebar es una accion del usuario, no del sistema. El usuario decide cuando quiere mas espacio para contenido y cuando quiere las etiquetas de navegacion visibles.

---

## 4. Breadcrumbs y orientacion

**Decision**: Sistema de breadcrumbs clickeables en el header que muestra la jerarquia: `Casa > Seccion > Detalle`. Cada segmento excepto el ultimo es un link navegable.

**Fundamentacion teorica**:

- **Orientacion (navegacion)**: Como se establece en la teoria de la materia: "Es muy comun que un usuario se pierda, por lo que tenemos que contrarrestar el sentimiento de estar perdido con un indicador de lugar". Los breadcrumbs cumplen exactamente esta funcion, mostrando jerarquicamente como se llego a la pagina actual desde la raiz.
- **Visibilidad del estado del sistema (Nielsen #1)**: Los breadcrumbs comunican en todo momento en que contexto (casa, seccion, sub-pagina) se encuentra el usuario. Por ejemplo, `Casa Martinez > Dispositivos > Lampara principal` le indica al usuario simultaneamente la propiedad activa, la seccion y el dispositivo especifico.
- **Control y libertad del usuario (Nielsen #3)**: Cada segmento es clickeable, lo que permite al usuario "saltar" a cualquier nivel de la jerarquia sin necesidad de usar el boton atras repetidamente. Esto funciona como las "salidas de emergencia" que menciona Nielsen.
- **Profundidad controlada**: Siguiendo la recomendacion de la materia de limitar los breadcrumbs a 3-4 niveles (mas de 5 es excesivo), la profundidad maxima es de 3 segmentos: Casa > Seccion > Detalle.

---

## 5. Arquitectura multi-casa

**Decision**: Soporte para multiples propiedades. La casa seleccionada forma parte de la URL (`/:houseId/dispositivos`), con un selector de casas en la sidebar. La vista Overview (`/overview`) muestra un resumen de todas las propiedades.

**Fundamentacion teorica**:

- **Modelo mental**: Los usuarios de domotica pueden tener multiples propiedades (casa principal, departamento, casa de playa). La arquitectura refleja este modelo mental real, donde el usuario piensa en terminos de "mis propiedades" y luego "los dispositivos de esta propiedad".
- **Correspondencia sistema-mundo real (Nielsen #2)**: La estructura jerarquica (Propiedad > Seccion > Dispositivo) mapea directamente la organizacion fisica del mundo real. El usuario selecciona primero donde (la casa), luego que (la seccion), siguiendo su modelo mental natural.
- **Consistencia y estandares (Nielsen #4)**: La casa activa se refleja consistentemente en tres lugares: el selector de la sidebar, la URL del navegador y el primer segmento del breadcrumb. Esta redundancia refuerza la orientacion del usuario.
- **Prevencion de errores (Nielsen #5)**: Si el usuario navega a una URL con un `houseId` inexistente, el router lo redirige automaticamente a la casa por defecto en lugar de mostrar un estado vacio o un error.

---

## 6. Vista Overview

**Decision**: Vista resumen (`/overview`) sin sidebar, que muestra todas las propiedades con sus estadisticas, dispositivos criticos, rutinas favoritas y metricas energeticas.

**Fundamentacion teorica**:

- **Pagina principal**: Segun la teoria de navegacion: "Los siguientes items tienen que poder identificarse facilmente desde la pagina principal: identidad y mision del sitio, jerarquia del sitio, contenido actualizado, atajos". El Overview cumple todos estos criterios: muestra la identidad (logo + nombre), la jerarquia (cards de propiedades), contenido actualizado (metricas en tiempo real) y atajos (dispositivos criticos, rutinas favoritas).
- **Reconocimiento en lugar de recuerdo (Nielsen #6)**: Los dispositivos criticos y las rutinas favoritas se muestran directamente, sin que el usuario tenga que recordar en que casa o seccion encontrarlos. Cada item incluye el nombre de la propiedad de origen (`Casa Martinez::Alarma principal`), proporcionando contexto completo.
- **Estetica y minimalismo (Nielsen #8)**: La vista usa una jerarquia clara: cards de propiedades arriba, dos columnas (criticos + favoritos) en el medio, metricas abajo. Solo se muestra informacion accionable, sin datos decorativos.
- **Sin sidebar**: Al ser una vista "meta" que abarca todas las propiedades, la sidebar de navegacion por secciones no aplica. Eliminarla reduce la carga visual y comunica que esta vista tiene un proposito diferente al de las vistas de una propiedad individual.

---

## 7. Dimensionamiento de elementos interactivos

**Decision**: Botones con altura minima de 40px (md), 48px (lg). Areas de toque de toggles de 44px. Targets de click nunca menores a 32px.

**Fundamentacion teorica**:

- **Ley de Fitts**: El tiempo para alcanzar un objetivo es funcion de la distancia y el tamano: `MT = a + b * log2(2D/W)`. Los controles de dispositivos (toggles, sliders) tienen areas de interaccion generosas (minimo 44px) para reducir el indice de dificultad. Como se senala en la teoria: "Los movimientos rapidos y los objetivos chiquitos resultan en mayores tasas de error, debido al equilibrio entre velocidad y precision".
- **Convenciones de plataforma**: Se respetan los tamanos minimos recomendados por las guias de plataforma (44pt Apple HIG, 48dp Material Design), anticipando una posible adaptacion movil.
- **Frecuencia de uso**: Los botones "Ejecutar" en rutinas y los toggles de dispositivos son los elementos mas frecuentemente usados, por lo que su tamano es proporcionalmente mayor. Esto aplica el principio de Fitts: los elementos usados con mayor frecuencia deben ser mas faciles de alcanzar.

---

## 8. Agrupamiento de contenido

**Decision**: Cards como unidad de agrupamiento, con bordes sutiles y separacion basada en espaciado. Padding interno de 24px (`--hc-space-lg`), gap entre cards de 16px (`--hc-space-md`), gap entre secciones de 32px (`--hc-space-xl`).

**Fundamentacion teorica**:

- **Gestalt - Proximidad**: Los elementos relacionados (nombre del dispositivo, estado, toggle) se agrupan dentro de la misma card. El gap entre cards (16px) es menor que el gap entre secciones (32px), creando una jerarquia clara de agrupamiento que el usuario percibe sin esfuerzo consciente. Como se vio en teoria: "La separacion es mas relevante que el color" para comunicar agrupamiento.
- **Gestalt - Region comun**: El fondo ligeramente diferenciado de la card (`#1a1a24` sobre `#0f0f14`) crea una region comun que refuerza la percepcion de grupo sin necesidad de bordes pesados. Esto reduce el ruido visual mientras mantiene la organizacion.
- **Gestalt - Conectividad uniforme**: En la vista de habitaciones, los dispositivos vinculados a una habitacion se listan dentro de la misma card, creando una conexion visual entre la habitacion y sus dispositivos. Este principio es "mucho mas fuerte que el de proximidad y similitud" segun la teoria vista.
- **Jerarquia visual**: Los elementos importantes son mas prominentes y los elementos relacionados semanticamente lo estan tambien visualmente. Esto permite encontrar rapidamente lo buscado "sin tener que pensar mucho".

---

## 9. Feedback y estado del sistema

**Decision**: Sistema de feedback multi-nivel: toasts para confirmaciones, estados visuales en tiempo real para dispositivos, badges numericos para notificaciones, indicadores de carga en botones.

**Fundamentacion teorica**:

- **Visibilidad del estado del sistema (Nielsen #1)**: Cada accion del usuario recibe feedback inmediato. Es critico en domotica ya que el usuario necesita saber si la alarma esta armada, si la puerta esta bloqueada, si una rutina se ejecuto:
  - Toggle de dispositivo: cambio visual instantaneo (color, texto de estado).
  - Ejecucion de rutina: toast de confirmacion ("Rutina ejecutada correctamente").
  - Envio de formulario: boton con spinner de carga.
  - Notificaciones pendientes: badge numerico en el icono de campana del header.
- **Tiempos de respuesta**: Las transiciones CSS (150ms-250ms) estan calibradas para sentirse instantaneas pero visibles, dentro del umbral de 100-300ms recomendado por Nielsen para feedback de manipulacion directa.
- **Modelo mental**: Los estados de dispositivos usan metaforas visuales coherentes (icono SVG de lampara con brillo = lampara encendida, icono de candado = puerta bloqueada, animacion de pulso = alarma armada). Estas metaforas aprovechan los modelos mentales existentes del usuario sobre dispositivos fisicos reales (Nielsen #2: correspondencia sistema-mundo real).
- **Nivel conductual (Norman)**: Un feedback deficiente generaria emociones negativas ("furia, abatimiento") si el usuario no puede confirmar que su accion tuvo efecto. El feedback inmediato previene esta situacion.

---

## 10. Prevencion de errores

**Decision**: Validacion inline en tiempo real, modales de confirmacion para acciones destructivas, valores por defecto sensibles.

**Fundamentacion teorica**:

- **Prevencion de errores (Nielsen #5)**: Como se establece en la teoria: "Mejor si podemos evitar que el usuario cometa el error" que tener que mostrarle un mensaje despues.
  - Los formularios de login y registro validan el formato del email en `onBlur`, no en submit, proporcionando feedback temprano.
  - El campo de contrasena muestra los requisitos ("Minimo 8 caracteres") antes de que el usuario cometa el error.
  - La eliminacion de habitaciones y rutinas requiere confirmacion modal con descripcion del impacto ("Los dispositivos vinculados no se eliminaran, solo se desvincularan").
- **Valores por defecto**: Los sliders de brillo comienzan en 80%, los dias de rutina pre-seleccionan Lun-Vie. Esto reduce la cantidad de decisiones requeridas, aplicando el principio de "deshacerse de tareas" (reducir la carga del usuario haciendo que la UI realice las tareas no necesarias).
- **Restricciones**: Los inputs numericos en el wizard de rutinas estan acotados (min 0, max 100), previniendo valores invalidos por construccion. Esto es analogo al ejemplo de la materia: "en un calendario para reservar no deberiamos dejarle al usuario reservar en el pasado".
- **Perfiles de familia**: El perfil adolescente no puede desarmar la alarma ni desbloquear puertas. Los controles se deshabilitan visualmente en lugar de desaparecer, para mantener el modelo mental del usuario sobre que existe en el sistema, y un banner explica la restriccion.

---

## 11. Terminologia consistente

**Decision**: Glosario fijo aplicado en toda la interfaz: "Hogar" (propiedad), "Habitacion" (ambiente), "Dispositivo" (equipo), "Rutina" (automatizacion).

**Fundamentacion teorica**:

- **Consistencia y estandares (Nielsen #4)**: Se evitan sinonimos que podrian causar confusion. El usuario siempre ve "Habitacion" (nunca "cuarto", "sala", "ambiente") y "Dispositivo" (nunca "aparato", "equipo", "sensor"). Como se enfatiza en la teoria: "Es importante que todos usemos la misma terminologia porque sino le generamos confusion al usuario".
- **Modelo mental**: La terminologia refleja la estructura jerarquica del sistema: Hogar contiene Habitaciones, Habitaciones contienen Dispositivos, Rutinas operan sobre Dispositivos. Esta coherencia lexica facilita la construccion de un modelo mental correcto.
- **Correspondencia sistema-mundo real (Nielsen #2)**: Se utiliza espanol rioplatense cotidiano ("Olvide mi contrasena", "Repeti tu contrasena") para acercar la interfaz al contexto cultural del usuario objetivo. Se evita toda jerga tecnica: el usuario nunca vera "endpoint", "toggle state" ni codigos de error internos.

---

## 12. Control y libertad del usuario

**Decision**: Acciones reversibles, sidebar colapsable, filtros en listados, navegacion "Volver", vista detalle con pagina completa.

**Fundamentacion teorica**:

- **Control y libertad del usuario (Nielsen #3)**: De la misma forma que un cartel de "EXIT" funciona como salida de emergencia, el usuario necesita acceso claro a operaciones de reversion:
  - Los toggles de dispositivos son bidireccionales: encender/apagar con un solo click.
  - Las rutinas pueden habilitarse/deshabilitarse sin eliminarlas.
  - El boton "Volver" en vistas de detalle permite salir sin compromiso.
  - Los filtros de dispositivos e historial permiten controlar que informacion se muestra.
- **Undo conceptual**: Desvincular dispositivos de habitaciones no elimina el dispositivo, solo rompe la asociacion. Eliminar habitaciones preserva los dispositivos. Esto protege al usuario de perdidas accidentales de datos.
- **Interfaz no-modal**: Se evitan estados modales excesivos. Los modales solo se usan para confirmacion de acciones destructivas (eliminar rutina, eliminar habitacion), no para flujos primarios. Esto respeta el principio de que "no queremos que la aplicacion controle al usuario sino que el usuario sienta que tiene el control".

---

## 13. Reconocimiento en lugar de recuerdo

**Decision**: Iconos junto a texto en la sidebar, badges de estado en dispositivos, wizard paso-a-paso para rutinas, breadcrumbs de navegacion.

**Fundamentacion teorica**:

- **Reconocimiento en lugar de recuerdo (Nielsen #6)**: Como se establece en la teoria: "Minimizar la carga en la memoria del usuario":
  - La sidebar muestra iconos + texto (no solo iconos) para que el usuario reconozca la seccion sin memorizar significados.
  - Los dispositivos muestran su estado actual en la card (no es necesario navegar al detalle para saber si estan encendidos).
  - El wizard de rutinas presenta las opciones disponibles en cada paso (tipo de accion, dispositivos, dias) en lugar de requerir que el usuario recuerde formatos o comandos.
- **Breadcrumbs visuales**: Los pasos del wizard muestran progreso con numeros y checkmarks, permitiendo saber en que paso se esta y cuales se completaron.
- **Filtros visibles**: Los selectores de filtro en Dispositivos e Historial despliegan las opciones disponibles en dropdowns, no ocultas detras de menus.
- **Carga cognitiva**: Cada decision de diseno busca que "los usuarios tengan que pensar lo menos posible". Si el usuario se pregunta "como hago X accion?", la respuesta deberia ser inmediata. Si lo es, la carga cognitiva es minima.

---

## 14. Sistema de iconografia

**Decision**: Componente SVG personalizado (`HcIcon`) con iconos inline basados en stroke, reemplazando todos los emojis y caracteres unicode de la interfaz.

**Fundamentacion teorica**:

- **Consistencia visual (Nielsen #4)**: Los emojis y entidades HTML se renderizan de forma diferente segun sistema operativo, navegador y dispositivo. Un conjunto de iconos SVG unificado garantiza que cada usuario vea iconos identicos independientemente de la plataforma, manteniendo coherencia visual. Como se menciona en la teoria: "Cada plataforma tiene sus reglas de design... el objetivo primario es que si un usuario esta acostumbrado a cierta plataforma, la experiencia sea natural".
- **Gestalt - Similitud**: Todos los iconos comparten el mismo lenguaje visual: viewBox 24x24, basados en stroke (no relleno), joins redondeados, ancho de stroke consistente de 2px. Esta uniformidad permite al usuario parsear rapidamente los iconos como una categoria de elemento (affordance interactivo) distinta del contenido textual.
- **Escalabilidad y tematizacion**: Los iconos SVG heredan `currentColor`, adaptandose automaticamente al color del texto circundante y respondiendo a cambios de estado (activo, hover, deshabilitado) sin necesidad de variantes separadas.
- **Claridad semantica**: Cada icono esta disenado para su contexto especifico (lampara, puerta, alarma, grifo, persianas, rutinas, etc.) en lugar de depender de aproximaciones con emojis. Esto reduce ambiguedad y refuerza el modelo mental del usuario sobre cada tipo de dispositivo.

---

## 15. Identidad de marca y logo

**Decision**: Logo hexagonal personalizado con silueta de casa y arcos WiFi, usado consistentemente en splash screen, sidebar expandida y sidebar colapsada.

**Fundamentacion teorica**:

- **Nivel visceral (Norman)**: La forma hexagonal comunica tecnologia y precision, mientras que la silueta de la casa establece inmediatamente el dominio del producto (hogar inteligente). Los arcos WiFi refuerzan el aspecto "conectado". La paleta de azules del logo (`#0C447C` base, `#378ADD` acentos) transmite confianza y fiabilidad.
- **Consistencia (Nielsen #4)**: El mismo logo aparece en el splash screen, el header de la sidebar y el estado colapsado de la sidebar. Esta repeticion construye reconocimiento de marca y proporciona un ancla espacial para el usuario.
- **Gestalt - Cierre**: El borde hexagonal crea una region contenida que agrupa la casa y los elementos WiFi en una unica unidad percibida, haciendo el logo reconocible incluso en tamanos pequenos (32px en sidebar colapsada). Esto aplica el principio de cierre: "Podes dejar cosas incompletas y el usuario percibe la intencion de forma natural".
- **Navegacion - Site ID**: El logo funciona como "Site ID" segun los principios de navegacion: "Permite saber que uno se encuentra todavia en el mismo sitio. Generalmente se encuentra en la parte superior de la pantalla". Al ser clickeable, tambien sirve como "mecanismo para volver a la pagina principal".

---

## 16. Splash screen y animacion de entrada

**Decision**: Splash a pantalla completa con el logo HomeCore centrado, seguido de una animacion de zoom-in que transiciona al dashboard. Skipeable via scroll, touch o click.

**Fundamentacion teorica**:

- **Nivel visceral (Norman)**: El splash establece una primera impresion emocional. El logo aparece con una animacion de entrada scale-up (1s ease-out), seguida de un fade-in escalonado del titulo y tagline. Esta secuencia coreografiada crea una sensacion de calidad e intencionalidad antes de cualquier interaccion.
- **Metafora espacial**: La animacion de salida zoom-in (el logo escala 9x mientras se desvanece) crea la percepcion de "entrar" a la casa, una metafora espacial que mapea el acto de abrir la app al acto de caminar por la puerta. Esto aprovecha el modelo mental del usuario sobre llegar a casa.
- **Control y libertad del usuario (Nielsen #3)**: La animacion es completamente skipeable. Un hint ("Scroll o click para continuar") aparece despues de 1.5 segundos para usuarios que no intuyan la interaccion. Se soportan multiples metodos de input (wheel, click, touch swipe) para acomodar diferentes dispositivos y preferencias. Ningun usuario esta obligado a esperar.
- **Revelacion progresiva**: El splash funciona como una pausa breve que separa la fase de "carga" de la fase de "uso", dando tiempo a la aplicacion para inicializar stores y renderizar el layout detras del splash. Esta optimizacion de rendimiento percibido enmascara cualquier delay de renderizado.

---

## 17. Vista de detalle de rutina

**Decision**: Las rutinas se muestran como cards en la vista de listado y al hacer click se navega a una vista de detalle a pagina completa (`/:houseId/rutinas/:id`) con layout de dos columnas: dispositivos afectados (izquierda) y programacion/informacion/acciones (derecha).

**Fundamentacion teorica**:

- **Jerarquia visual**: La expansion inline de las cards rompia el grid del listado y generaba una experiencia incoherente. La vista de detalle a pagina completa ofrece espacio suficiente para mostrar toda la informacion sin comprometer la legibilidad ni la estructura visual.
- **Consistencia (Nielsen #4)**: El patron de navegacion replica el usado en dispositivos (listado con cards > detalle a pagina completa), manteniendo una experiencia predecible. El usuario que ya aprendio a navegar dispositivos no necesita aprender un patron nuevo para rutinas.
- **Reconocimiento (Nielsen #6)**: Cada dispositivo afectado por la rutina se muestra con su icono, nombre, habitacion y las acciones especificas (ej: "Encender", "Brillo 40%") como badges visibles. El usuario puede comprender el alcance completo de la rutina sin ejecutarla.
- **Gestalt - Region comun**: La programacion (hora + dias de la semana) y la informacion (estado, cantidad de acciones, cantidad de dispositivos) se agrupan en el sidebar derecho como cards separadas, facilitando el escaneo rapido de la configuracion.
- **Control y libertad (Nielsen #3)**: Las acciones destructivas (eliminar rutina) requieren confirmacion modal. El toggle de habilitar/deshabilitar es reversible. El boton "Volver" permite regresar al listado sin compromiso.

---

## 18. Selector de casas

**Decision**: Dropdown de seleccion de casa integrado en la sidebar, entre el logo y la navegacion. En estado expandido muestra nombre completo + icono; en estado colapsado muestra solo un icono diferenciado (`swap`).

**Fundamentacion teorica**:

- **Modelo mental**: El selector refleja la jerarquia conceptual del sistema: primero se elige la propiedad, luego se navega dentro de ella. Ubicarlo en la parte superior de la sidebar, antes de la navegacion, refuerza este orden mental.
- **Gestalt - Proximidad**: El selector esta espacialmente proximo a la navegacion pero visualmente separado por un borde, comunicando que es un control de contexto (elige donde) distinto de la navegacion (elige que).
- **Diferenciacion iconografica**: Se utiliza el icono `swap` (dos flechas) en lugar de `home` para evitar confusion con el item de navegacion "Inicio" cuando la sidebar esta colapsada. Esto previene errores (Nielsen #5) al garantizar que dos controles con funciones distintas no compartan la misma representacion visual.
- **Flexibilidad (Nielsen #7)**: Al cambiar de casa, la URL se actualiza automaticamente, permitiendo que usuarios avanzados naveguen directamente mediante URL (ej: `/casa-palermo/dispositivos`).

---

## 19. Estrategia de autenticacion del prototipo

**Decision**: Auto-login con usuario pre-autenticado para la fase de prototipo. Las vistas de login, registro, verificacion y recuperacion se preservan en el codebase pero el guard de autenticacion se bypasea.

**Fundamentacion teorica**:

- **Eficiencia en evaluacion**: Durante las pruebas de usabilidad (evaluacion empirica), requerir credenciales en cada sesion agrega friccion no relacionada con los flujos de interaccion que se estan evaluando. El auto-login permite que los evaluadores accedan inmediatamente al dashboard y prueben controles de dispositivos, rutinas y navegacion sin overhead de gestion de credenciales.
- **Completitud preservada**: Las vistas de autenticacion (Login, Registro, Verificar, Recuperar) permanecen en el router y son accesibles via navegacion directa por URL. Esto permite demostrar el flujo completo cuando es necesario sin mantener dos codebases separados.
- **Validez ecologica**: El usuario pre-autenticado ("Juani Raggio") tiene datos realistas: dos propiedades, preferencias de notificacion y configuracion de PIN. Esto mantiene la validez ecologica durante las pruebas: los evaluadores interactuan con un perfil de usuario creible en lugar de un estado vacio.
- **Iteracion UCD**: Esta decision se enmarca en la fase de "Evaluacion" del ciclo UCD (ISO 13407), donde el objetivo es "comprobar si las soluciones cumplen con el contexto de uso y satisfacen las necesidades del usuario". Eliminar barreras irrelevantes para esta comprobacion es una decision de eficiencia metodologica.

---

## 20. Variables CSS como fuente unica de verdad

**Decision**: Todos los tokens de diseno (colores, tipografia, espaciado, dimensiones de layout) se definen como variables CSS en `design-system.css`. Las dimensiones compartidas entre componentes (`--hc-brand-inset`, `--hc-statusline-height`, `--hc-sidebar-width`) se centralizan para evitar desincronizacion.

**Fundamentacion teorica**:

- **Consistencia (Nielsen #4)**: Las variables CSS garantizan que los mismos valores se usen en toda la aplicacion. Cuando el sidebar expandido usa `padding-left: var(--hc-brand-inset)` y el header usa la misma variable, se garantiza alineacion visual perfecta. Esto evita inconsistencias sutiles que rompen la percepcion de calidad.
- **Mantenibilidad y UCD**: La centralizacion facilita las iteraciones del ciclo UCD (ISO 13407). Cuando la evaluacion indica que un color no cumple WCAG AA, se cambia en un unico lugar y el cambio se propaga a toda la aplicacion. Esto reduce el costo de cada iteracion de "Producir soluciones de diseno > Evaluacion".
- **Reduccion de errores**: Al evitar valores hardcodeados dispersos en multiples archivos, se previenen bugs visuales causados por valores desincronizados (ej: un componente con el color viejo y otro con el nuevo).

---

## 21. Modo Vim (flexibilidad y eficiencia)

**Decision**: Modo de interaccion opcional inspirado en Vim, con navegacion por teclado (j/k/h/l), comandos (`:q`, `:help`), cambio de perfiles, y efectos visuales opcionales (CRT, matrix rain). Barra de estado siempre visible en la parte inferior.

**Fundamentacion teorica**:

- **Flexibilidad y eficiencia de uso (Nielsen #7)**: Como establece la teoria: "Darle experiencia adecuada a todos los usuarios para que los power users puedan ser eficientes en la aplicacion. Tiene que poder ser adaptable a las necesidades del usuario". El modo Vim permite a usuarios avanzados navegar toda la interfaz sin usar el mouse, usando atajos como `gh` (ir al dashboard), `gd` (ir a dispositivos), `t` (toggle del dispositivo enfocado).
- **Nivel reflexivo (Norman)**: Los efectos visuales opcionales (scanlines CRT, matrix rain) y la estetica de terminal generan una experiencia reflexiva positiva para usuarios tecnicos: el placer de usar una herramienta que reconoce su expertise. Esto contribuye a la satisfaccion (ISO 9241-11) de un segmento especifico de usuarios.
- **No intrusivo**: El modo Vim es completamente opcional. La barra de estado ocupa solo 26px de altura y los atajos de teclado no interfieren con el uso normal de la interfaz. Los usuarios que no conocen Vim nunca se ven afectados.
- **Ayuda y documentacion (Nielsen #10)**: El shortcut `?` despliega una referencia completa de todos los comandos disponibles, permitiendo que incluso usuarios curiosos puedan explorar el modo sin memorizacion previa.
