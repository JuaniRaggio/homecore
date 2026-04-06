# HomeCore - Decisiones de Diseño y Usabilidad

Para fundamentar cada decisión, nos basamos en los principios de Interacción Persona-Computadora (HCI) vistos en la materia:

- **Heurísticas de Nielsen:** Visibilidad del estado, correspondencia con el mundo real, control del usuario, consistencia, prevención de errores, reconocimiento, flexibilidad, estética minimalista y ayuda.
- **Leyes de Gestalt:** Proximidad, similitud, región común y cierre.
- **Leyes de Interacción:** Ley de Fitts (tamaño de objetivos) y Ley de Hick (toma de decisiones).
- **Niveles de Norman:** Respuestas viscerales (estética), conductuales (uso) y reflexivas (satisfacción).
- **Carga Cognitiva:** Minimizar el esfuerzo mental mediante una estructura clara.
- **Diseño Centrado en el Usuario (UCD):** Proceso de iteración basado en evaluaciones de usabilidad.

---

## I. Decisiones de Diseño (Identidad y Estética)

En esta sección nos enfocamos en el "look & feel" de la aplicación y cómo construimos una marca coherente y profesional.

### 1. Identidad Visual y Logo
**Decisión**: Un logo hexagonal con una casa minimalista y ondas de WiFi.
**Justificación**: Queríamos que la marca transmita tecnología y seguridad (nivel visceral). El hexágono da una sensación de estructura sólida, y el uso de la ley de cierre de Gestalt permite que el logo sea reconocible incluso en tamaños muy reducidos en la barra lateral.

### 2. Paleta de Colores
**Decisión**: Tema oscuro profundo con índigo (`#818cf8`) para acciones y ámbar (`#fbbf24`) para estados activos.
**Justificación**: El tema oscuro reduce la fatiga visual en entornos hogareños. Usamos el índigo para que el usuario identifique rápido qué es interactivo (similitud) y el ámbar para resaltar lo que está encendido. Todos los colores cumplen con el estándar WCAG AA para garantizar que cualquiera pueda leer la interfaz sin esfuerzo.

### 3. Tipografía y Jerarquía
**Decisión**: Familia tipográfica única (Inter) con pesos variables.
**Justificación**: Usar una sola fuente ayuda a no saturar al usuario (menor carga cognitiva). La jerarquía se marca con el tamaño y el grosor del texto para que el ojo sepa qué leer primero (títulos vs. descripciones) de forma natural.

### 4. Sistema de Iconografía
**Decisión**: Íconos SVG personalizados en lugar de emojis.
**Justificación**: Los íconos unificados garantizan consistencia visual en todas las plataformas (Nielsen #4). Al compartir el mismo grosor de línea y estilo, el usuario los percibe como parte de un sistema coherente, facilitando su reconocimiento rápido.

### 5. Agrupamiento Visual (Cards)
**Decisión**: Uso de tarjetas con bordes suaves y espaciado consistente.
**Justificación**: Aplicamos las leyes de proximidad y región común de Gestalt para que el usuario entienda que los controles de un dispositivo pertenecen a ese dispositivo específico. La separación clara evita el ruido visual y hace que la interfaz se sienta organizada.

### 6. Variables CSS como Fuente de Verdad
**Decisión**: Centralización de tokens (colores, espacios, tamaños) en un sistema de variables CSS.
**Justificación**: Esto garantiza que la consistencia (Nielsen #4) se mantenga de forma técnica. Si decidimos cambiar un tono de azul tras una evaluación de usabilidad (UCD), el cambio impacta en toda la app al instante, evitando inconsistencias visuales que rompan la experiencia.

---

## II. Decisiones de Usabilidad e Interacción

Aquí detallamos cómo diseñamos el flujo y la respuesta del sistema para que la experiencia sea fluida y sin errores.

### 7. Estructura de Navegación (Sidebar)
**Decisión**: Barra lateral izquierda con 6 secciones principales, colapsable.
**Justificación**: Al limitar las opciones aplicamos la Ley de Hick, acelerando la toma de decisiones. Mantener los íconos visibles al colapsar la barra permite que el usuario reconozca las secciones sin tener que recordar sus nombres.

### 8. Orientación (Breadcrumbs)
**Decisión**: Migas de pan clickeables en la parte superior.
**Justificación**: Funcionan como un indicador de lugar constante para evitar que el usuario se sienta perdido. Además, ofrecen una "salida de emergencia" rápida (control y libertad) para volver a niveles anteriores con un solo click.

### 9. Arquitectura Multi-casa
**Decisión**: Jerarquía clara de Propiedad > Sección > Dispositivo.
**Justificación**: Mapeamos la estructura física del mundo real al sistema (Nielsen #2). El selector de casas en la parte superior refleja el modelo mental del usuario sobre sus pertenencias físicas.

### 10. Vista Overview (Resumen General)
**Decisión**: Una pantalla principal que resume lo crítico de todas las propiedades.
**Justificación**: Buscamos la eficiencia. El usuario puede ver estados de alerta o rutinas favoritas sin navegar casa por casa, lo que reduce drásticamente la carga de trabajo y el tiempo de respuesta.

### 11. Interacción Directa con Dispositivos
**Decisión**: Botones y controles de gran tamaño (mínimo 44px).
**Justificación**: Aplicamos la Ley de Fitts: objetivos más grandes son más fáciles y rápidos de clickear, reduciendo errores accidentales, especialmente en pantallas táctiles o cuando el usuario está apurado.

### 12. Feedback y Estado del Sistema
**Decisión**: Notificaciones instantáneas (toasts) y animaciones de estado.
**Justificación**: El sistema debe responder siempre (Nielsen #1). Si una luz se enciende, el ícono brilla de inmediato. Esto da seguridad y previene la "incertidumbre" de no saber si el comando funcionó.

### 13. Prevención de Errores y Valores por Defecto
**Decisión**: Mensajes de confirmación antes de borrar y sliders pre-seteados en valores comunes.
**Justificación**: Como dice la teoría, es mejor prevenir el error que reportarlo. Los valores por defecto (como el brillo al 80%) ahorran pasos innecesarios, aplicando el principio de "deshacerse de tareas" triviales para el usuario.

### 14. Terminología y Lenguaje
**Decisión**: Uso de lenguaje cotidiano (español rioplatense) y consistencia léxica.
**Justificación**: Evitamos la jerga técnica. Usar siempre "Hogar" o "Rutina" ayuda a que el usuario construya un modelo mental sólido sin confundirse con sinónimos.

### 15. Libertad y Control
**Decisión**: Todas las acciones son reversibles (Undos conceptuales).
**Justificación**: Nada es permanente. Poder apagar una rutina en lugar de borrarla, o desvincular un dispositivo sin eliminarlo, le da al usuario la confianza para explorar la interfaz sin miedo a romper algo.

### 16. Eficiencia para Expertos (Modo Vim)
**Decisión**: Interacción opcional por teclado para navegación rápida.
**Justificación**: Es un acelerador (Nielsen #7). Mientras que el novato usa el mouse, el experto puede navegar con J/K o saltar secciones con comandos, logrando una satisfacción reflexiva alta al dominar la herramienta.

### 17. Experiencia de Entrada (Splash Screen)
**Decisión**: Animación de bienvenida inmersiva pero skipeable.
**Justificación**: Establece el tono de calidad de la app (nivel visceral) y usa la metáfora de "entrar" a la casa. Al permitir saltearla, respetamos el control del usuario sobre su propio tiempo.

### 18. Estrategia de Prototipado (Auto-login)
**Decisión**: Bypass de la pantalla de login para las pruebas de usabilidad.
**Justificación**: Durante las evaluaciones con usuarios (UCD), queremos que el foco esté en el control de la casa y no en la gestión de contraseñas. Esto reduce la fricción inicial y permite ir directo a los flujos de interacción que queremos validar.
