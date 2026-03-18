# HomeCore - Evaluacion de Usabilidad

## 5.1. Evaluacion Predictiva

### Metodologia

Cada integrante del grupo realiza una inspeccion individual del prototipo web y del prototipo movil utilizando las heuristicas seleccionadas. Luego se hace una puesta en comun donde se consolidan los hallazgos, se eliminan duplicados y se priorizan los problemas segun severidad (critico / mayor / menor / cosmetico).

### Heuristicas seleccionadas: Principios de Jakob Nielsen

Se eligieron las 10 heuristicas de Nielsen por ser el marco mas establecido para evaluacion heuristica de interfaces, directamente alineado con los contenidos de la materia. Se justifica la eleccion de cada principio en funcion de su relevancia especifica para una aplicacion de domotica:

| # | Heuristica | Justificacion para HomeCore |
|---|---|---|
| 1 | **Visibilidad del estado del sistema** | Critico en domotica: el usuario necesita saber en todo momento si la alarma esta armada, si la puerta esta bloqueada, si una rutina se ejecuto. Un estado incorrecto o no visible puede generar riesgos de seguridad reales. Relevante para validar que los toggles, badges y toasts reflejan correctamente el estado de cada dispositivo. |
| 2 | **Correspondencia entre el sistema y el mundo real** | La terminologia debe ser comprensible para todos los perfiles (Santiago/power user, Carolina/average, Roberto/novice). Verificar que no se usan terminos tecnicos ("endpoint", "toggle state") y que las metaforas visuales (icono de alarma, candado en puerta) coinciden con el modelo mental del usuario sobre dispositivos fisicos reales. |
| 3 | **Control y libertad del usuario** | Los usuarios van a cometer acciones no deseadas (desarmar la alarma por error, ejecutar una rutina equivocada). Verificar que existen salidas claras: botones "Atras", modales de confirmacion para acciones destructivas, posibilidad de revertir estados. Especialmente relevante para Roberto, que teme "desconfigurar el sistema". |
| 4 | **Coherencia y estandares** | Verificar que la terminologia es uniforme en toda la aplicacion ("Habitacion" siempre, nunca "cuarto" o "sala"), que los patrones de interaccion son consistentes entre dispositivos (todos usan toggle + slider donde aplica), y que se respetan convenciones de plataforma (web vs. movil). |
| 5 | **Prevencion de errores** | Directamente vinculado con la feature de perfiles de familia: el adolescente no deberia poder desarmar la alarma ni desbloquear puertas. Verificar que las restricciones estan correctamente aplicadas, que los controles se deshabilitan en vez de desaparecer (para mantener el modelo mental), y que los mensajes explican por que no se puede realizar la accion. Tambien aplica a validaciones de formularios y limites en sliders. |
| 6 | **Reconocimiento en lugar de recuerdo** | El usuario no deberia memorizar en que habitacion esta cada dispositivo ni que hace cada rutina. Verificar que las cards muestran nombre + habitacion + estado, que el wizard de rutinas presenta opciones seleccionables, y que la sidebar muestra iconos + texto (no solo iconos). Critico para Roberto, que necesita indicaciones explicitas. |
| 7 | **Flexibilidad y eficiencia de uso** | La interfaz debe servir tanto para Santiago (quiere atajos, dashboard compacto, control rapido) como para Roberto (necesita botones grandes y flujo lineal). Verificar que el dashboard ofrece accesos rapidos a favoritos, que la sidebar es colapsable, y que los perfiles de familia adaptan la interfaz al tipo de usuario. |
| 8 | **Estetica y diseno minimalista** | En una app de domotica con multiples dispositivos, la sobrecarga visual es un riesgo concreto. Verificar que solo se muestra informacion relevante en cada vista, que las cards no tienen datos innecesarios, y que la jerarquia visual (tipografia, color, espaciado) guia la atencion correctamente. |
| 9 | **Ayudar a reconocer, diagnosticar y recuperarse de errores** | Si una rutina falla, si un dispositivo no responde, o si un login es incorrecto, el mensaje debe ser claro y accionable. Verificar que los mensajes de error usan lenguaje natural ("Credenciales incorrectas. Verifica tu email y contrasena.") y no codigos tecnicos. |
| 10 | **Ayuda y documentacion** | Aunque la app deberia ser autoexplicativa, verificar que existen elementos de guia: placeholders en formularios, textos explicativos en el wizard de rutinas, tooltips en controles complejos. Especialmente relevante para Roberto y para el banner de restriccion del perfil adolescente. |

### Criterios adicionales por el dominio

Ademas de Nielsen, se presta atencion especial a:

- **Principios de Gestalt** (proximidad, similitud, region comun): Verificar agrupamiento logico en cards, coherencia visual entre dispositivos del mismo tipo, separacion clara entre secciones.
- **Ley de Fitts**: Verificar que los elementos interactivos principales (toggles, botones "Ejecutar", selector de perfil) tienen areas de toque suficientes (minimo 44px).
- **Ley de Hick**: Verificar que los menus no exceden 7 opciones (la sidebar tiene 6 items de navegacion + 1 configuracion).

---

## 5.2. Evaluacion Empirica

### Metodologia

- **Participantes**: 3-5 usuarios que aproximen los perfiles definidos (Santiago/power user, Carolina/gestora del hogar, Roberto/usuario tradicional)
- **Contexto**: Presencial, con un facilitador observando y tomando notas. Se le dan instrucciones verbales al usuario sobre el escenario y se lo deja interactuar sin guia adicional salvo que se bloquee.
- **Herramientas**: Prototipo funcional web, cronometro, planilla de observacion.
- **Datos recolectados**: Tasa de exito, tiempo de completado, cantidad de errores/desvios, observaciones cualitativas (expresiones, dudas, comentarios espontaneos).

### Escenarios / Tareas

| # | Escenario | Objetivo | Criterio de exito | Persona vinculada |
|---|---|---|---|---|
| E1 | **Controlar una lampara desde el dashboard**: Encontrar la lampara del living en el dashboard y cambiar su brillo al 50%. | Validar la eficiencia de la navegacion principal y la interaccion con el slider de brillo. Evalua Nielsen #1 (visibilidad del estado) y Fitts (tamano del slider). | El usuario localiza la lampara, accede al detalle y ajusta el brillo en menos de 30 segundos sin ayuda. | Santiago, Roberto |
| E2 | **Crear una rutina "Buenos dias"**: Crear una rutina que abra las persianas del dormitorio y encienda la lampara del pasillo a las 08:00 de lunes a viernes. | Validar la usabilidad del wizard de 4 pasos. Evalua Nielsen #6 (reconocimiento vs. recuerdo) y carga cognitiva del flujo multi-step. | El usuario completa los 4 pasos del wizard y crea la rutina sin errores de validacion y sin retroceder mas de una vez. | Santiago, Carolina |
| E3 | **Verificar la seguridad del hogar antes de dormir**: Sin instrucciones especificas sobre donde ir, el usuario debe verificar que la alarma esta armada y la puerta principal esta bloqueada. | Validar si la estructura de navegacion permite encontrar dispositivos de seguridad rapidamente. Evalua Nielsen #7 (flexibilidad) y el modelo mental del usuario sobre "seguridad". | El usuario encuentra y confirma el estado de alarma y puerta en menos de 1 minuto. | Carolina, Roberto |
| E4 | **Cambiar al perfil adolescente e intentar desarmar la alarma**: Desde el header, cambiar al perfil "Tomi Raggio" e intentar desarmar la alarma. | Validar la implementacion de restricciones por rol. Evalua Nielsen #5 (prevencion de errores) y Nielsen #1 (visibilidad: el usuario entiende por que esta restringido). | El usuario cambia de perfil exitosamente, encuentra la alarma, ve que el control esta deshabilitado y comprende el mensaje de restriccion sin confusion. | Carolina |
| E5 | **Consultar el consumo electrico de la ultima semana**: Navegar a la seccion de consumo y encontrar el dispositivo que mas consumio en los ultimos 7 dias. | Validar la encontrabilidad de la seccion de consumo y la legibilidad de los graficos. Evalua Nielsen #8 (estetica minimalista) y la eficacia de la visualizacion de datos. | El usuario navega a consumo y identifica el dispositivo de mayor consumo en menos de 45 segundos. | Santiago |
| E6 | **Recuperarse de una navegacion accidental**: El usuario esta en el detalle de un dispositivo y se le pide "volver al inicio". | Validar la capacidad de recuperacion y orientacion. Evalua Nielsen #3 (control y libertad) y la claridad de la navegacion. Especialmente relevante para Roberto ("busca un boton de Atras o Inicio"). | El usuario vuelve al dashboard en menos de 2 clics sin expresar confusion. | Roberto |
| E7 | **Verificar que el perfil adolescente no puede acceder a configuracion**: Con el perfil "Tomi Raggio" activo, intentar acceder a Configuracion. | Validar el guard del router y la coherencia de la restriccion visual (link no visible en sidebar) con el comportamiento del sistema (redireccion si se navega manualmente). | El usuario nota que "Configuracion" no aparece en la sidebar. Si escribe la URL manualmente, es redirigido al dashboard sin mensaje de error confuso. | Carolina |

---

## 5.3. Evaluacion Participativa

### Metodologia

- **Momento**: Inmediatamente despues de completar los escenarios de la evaluacion empirica
- **Formato**: Cuestionario semi-estructurado, presencial o remoto segun la evaluacion empirica. Se registran las respuestas textuales del participante.
- **Participantes**: Los mismos usuarios de la evaluacion empirica
- **Herramientas**: Formulario impreso o digital (Google Forms), grabacion de audio con consentimiento

### Preguntas

Las preguntas estan organizadas en 4 bloques tematicos. Cada pregunta tiene una justificacion que explica que aspecto de la experiencia busca evaluar.

#### Bloque 1: Navegacion y estructura

| # | Pregunta | Justificacion |
|---|---|---|
| P1 | Cuando necesitaste encontrar un dispositivo especifico, cual fue tu primer instinto: ir al dashboard, a "Dispositivos" o a "Habitaciones"? | Revela el modelo mental del usuario sobre la organizacion de la informacion. Permite validar si la arquitectura de informacion (dashboard / dispositivos / habitaciones) coincide con las expectativas. No es una pregunta guiada porque ofrece opciones reales del sistema. |
| P2 | Hubo algun momento en el que no supieras en que seccion de la aplicacion estabas? Si es asi, que te genero esa confusion? | Evalua la visibilidad del estado de navegacion (Nielsen #1) y la eficacia de los indicadores visuales (sidebar active state, titulo en header). Busca detectar problemas de orientacion concretos. |
| P3 | La cantidad de opciones en el menu lateral te parecio adecuada, o sentiste que faltaba o sobraba algo? | Evalua la aplicacion de la Ley de Hick (7+-2 opciones). Permite detectar si la estructura de 6+1 items genera sobrecarga o si el usuario esperaba encontrar algo que no estaba. |

#### Bloque 2: Control de dispositivos

| # | Pregunta | Justificacion |
|---|---|---|
| P4 | Cuando cambiaste el estado de un dispositivo (encender, ajustar brillo, etc.), el sistema te dio suficiente informacion para saber que el cambio se aplico? | Evalua la calidad del feedback (Nielsen #1). Busca detectar si los toasts, cambios de color y textos de estado son suficientes o si el usuario queda en duda sobre si la accion se ejecuto. |
| P5 | Alguno de los iconos o controles te resulto confuso o no supiste que hacia sin probarlo? Cual? | Evalua correspondencia sistema-mundo real (Nielsen #2) y Gestalt (similitud). Identifica metaforas visuales que no funcionan para el modelo mental del participante. |
| P6 | Si pudieras controlar un solo dispositivo desde la pantalla de inicio sin navegar a ninguna otra parte, cual seria y por que? | Revela prioridades del usuario sin ser una pregunta guiada. Permite validar si los favoritos en el dashboard coinciden con lo que cada perfil de persona necesita acceder rapidamente (Santiago: luces, Carolina: alarma, Roberto: persianas). |

#### Bloque 3: Perfiles de familia y restricciones

| # | Pregunta | Justificacion |
|---|---|---|
| P7 | Cuando cambiaste al perfil adolescente y viste que los controles de la alarma estaban deshabilitados, entendiste inmediatamente por que? El mensaje te resulto claro? | Evalua directamente la implementacion de Nielsen #5 (prevencion de errores) y Nielsen #1 (visibilidad del estado). Permite verificar si el banner de restriccion comunica eficazmente la razon y la accion sugerida ("Pedi a un administrador"). |
| P8 | Te parecio logico que el adolescente pueda ver el estado de la alarma pero no controlarla, o esperabas que directamente no apareciera? | Valida la decision de diseno "ver pero no interactuar" vs. ocultar. Se fundamenta en el principio de que mantener la visibilidad preserva el modelo mental del usuario. Si los participantes prefieren ocultar, habria que reconsiderar la decision. |
| P9 | El selector de perfil en la parte superior te resulto facil de encontrar y usar? Lo ubicaste rapidamente? | Evalua la encontrabilidad y affordance del componente de cambio de perfil. Permite detectar si la ubicacion (entre notificaciones y avatar) es natural o si los usuarios lo confunden con otro control. |

#### Bloque 4: Impresion general

| # | Pregunta | Justificacion |
|---|---|---|
| P10 | Si tuvieras que describir esta aplicacion a alguien que nunca la uso, que le dirias en una frase? | Pregunta abierta que revela la percepcion global del usuario y su modelo mental del producto. La respuesta permite evaluar si la comunicacion de la propuesta de valor es efectiva sin condicionar con opciones. |
| P11 | Hubo algo que te generara frustracion, inseguridad o incomodidad durante el uso? | Detecta puntos de dolor emocionales (nivel conductual y reflexivo de Norman). Permite identificar problemas que no se manifiestan en metricas cuantitativas pero afectan la satisfaccion del usuario. |
| P12 | En una escala del 1 al 5, donde 1 es "muy dificil" y 5 es "muy facil", como calificarias la facilidad general de uso? | Metrica cuantitativa de satisfaccion percibida. Se usa escala de 5 puntos (no par, para permitir punto medio) que es comparable entre participantes y entre iteraciones del prototipo. Complementa las respuestas cualitativas con un dato numerico. |
