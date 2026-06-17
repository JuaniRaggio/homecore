#import "template.typ": figrow

= Decisiones de usabilidad tomadas durante la etapa de implementación

== Adaptación al factor de forma y a la orientación

La interfaz se adapta a distintos tamaños de pantalla manteniendo una distribución organizada y consistente de los elementos. La cantidad de columnas se ajusta dinámicamente según el espacio disponible, permitiendo aprovechar mejor el área de visualización y reducir la necesidad de desplazamiento en pantallas más amplias. Además, se preserva el estado de elementos clave de la interfaz, como búsquedas y selecciones realizadas por el usuario, garantizando la continuidad de las tareas ante cambios de orientación del dispositivo.

== Diseño gráfico: colores, tipografía e iconografía

=== Colores
La paleta se gestiona mediante un sistema de design tokens centralizado, sincronizado con la versión web, lo que garantiza coherencia de identidad visual entre plataformas. El tema oscuro es el predeterminado (fondo \#0F0F14, acento \#818CF8), decisión fundamentada en la reducción de fatiga visual en un contexto de uso domiciliario, frecuentemente nocturno y con poca luz. Se respeta una semántica de color estricta, atendiendo una corrección de la entrega anterior: el rojo se reserva exclusivamente para acciones destructivas y errores, nunca para estados normales de dispositivos; el verde indica dispositivos activos. Para el tema claro se eligió deliberadamente una paleta de grises suaves (\#E9EBF0) en lugar de blanco puro, decisión de accesibilidad orientada a reducir el deslumbramiento y mantener un contraste cómodo. Se ajustó el color de acento utilizado en textos para garantizar un nivel adecuado de contraste sobre fondos claros, mejorando la legibilidad y el cumplimiento de criterios de accesibilidad visual.

#figrow(
  ("images/image46.png", "images/image27.png", "images/image44.png"),
  [Imágenes 44, 45 y 46: Colores mencionados en 3.2.1],
)

=== Tipografía
Se definió tipografía “Roboto” con escala tipográfica tokenizada (de 12sp a 28sp) y jerarquías diferenciadas por peso: negrita para títulos, semi-negrita para subtítulos y peso normal para el cuerpo. El color del texto no se fija en la tipografía, sino que deriva del esquema de color activo (onSurface y onBackground), de modo que la legibilidad se adapta automáticamente al tema —aplicación práctica de la separación entre contenido y presentación.

=== Iconografía
Se implementó un sistema de iconografía basado en Material Icons, donde cada categoría de dispositivo se representa mediante un ícono específico y un color distintivo. Esta diferenciación visual permite identificar rápidamente el tipo de dispositivo y mejora la comprensión de la información presentada, favoreciendo una navegación más intuitiva dentro de la aplicación.

Se corrigieron iconografías ambiguas señaladas en la entrega anterior: se emplea el ícono de papelera para eliminar y la estrella para favoritos, convención aceptada en Android. Los elementos accionables respetan el tamaño táctil mínimo de 48dp e incluyen descripciones de contenido (contentDescription), atendiendo criterios de accesibilidad.

== Aplicación de contenidos de la materia

Las decisiones se fundamentan en contenidos abordados en la cursada, aplicados al contexto específico de la aplicación:

*Heurísticas de Nielsen:* Visibilidad del estado del sistema: cada acción sobre un dispositivo refleja su nuevo estado con retroalimentación inmediata (contenedor de estado, indicador de carga durante la ejecución de rutinas). Prevención de errores: toda acción destructiva exige confirmación mediante un diálogo que nombra explícitamente la entidad afectada. Reconocer antes que recordar: agrupación de la configuración bajo el ícono de ajustes.

*Carga cognitiva:* La separación de la información de identidad respecto de las acciones de configuración reduce la carga de procesamiento en la pantalla de perfil.

*Consistencia y estándares:* El uso de componentes Material Design 3 y de un sistema de tokens compartido garantiza coherencia interna y con las convenciones de la plataforma Android.

== Vinculación con los Modelos de Persona

Las decisiones de diseño se orientaron a las necesidades de los tres Modelos de Persona definidos en la primera etapa, cuyas características específicas fundamentaron soluciones concretas de la aplicación:

*Marta (63 años, nivel tecnológico básico).* Su perfil prioriza interfaces simples, flujos lineales y controles grandes con etiquetas claras. Esta característica fundamentó decisiones concretas: las etiquetas de la barra de navegación inferior se muestran completas en una sola línea (por ejemplo “Dispositivos”, evitando truncados o abreviaturas), reforzando el reconocimiento por sobre el recuerdo; el contenedor de estado del dispositivo, con color verde o gris y texto explícito, le permite saber de un vistazo qué quedó encendido sin interpretar íconos ambiguos; la diferenciación cromática del botón “Ejecutar ahora” le señala con claridad cuál es la acción de consecuencias amplias; y las confirmaciones que nombran la entidad afectada previenen errores irreversibles en un perfil que no asocia consecuencias técnicas a cada acción. Los controles respetan además el tamaño táctil mínimo de 48dp, atendiendo a una menor precisión motriz fina.

*Carolina (42 años, nivel tecnológico intermedio).* Su prioridad es la seguridad familiar y la eficiencia en la gestión cotidiana del hogar. Esto fundamentó la pantalla de Inicio organizada en torno a dispositivos y rutinas favoritos, que centraliza lo más consultado y evita navegar por todas las secciones; las barras de búsqueda contextuales en Dispositivos y Rutinas, que le permiten localizar un elemento puntual sin recorrer listas largas; y la grilla de dispositivos de densidad variable, que en pantallas anchas ofrece una vista panorámica para verificar el estado de varios dispositivos de seguridad simultáneamente. El contenedor de estado destacado también responde a su necesidad de monitoreo rápido.

*Valentina (27 años, nivel tecnológico avanzado).* Busca automatizaciones complejas, control granular y análisis de datos. Su perfil fundamentó la programación horaria de rutinas, que se ejecutan automáticamente en los días y horarios configurados sin intervención manual; los controles específicos por tipo de dispositivo, que exponen el control granular que un usuario avanzado espera (brillo, temperatura, modos, posición). La personalización de tema e idioma también atiende a su expectativa de control sobre la experiencia.
