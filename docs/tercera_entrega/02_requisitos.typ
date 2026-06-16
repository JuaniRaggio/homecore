#import "template.typ": req, fig, figrow

= Requisitos implementados

En esta sección se describen los requisitos implementados en la aplicación móvil HomeCore, desarrollada para la gestión y automatización de dispositivos inteligentes dentro de un hogar. Se detallan tanto los requisitos funcionales, vinculados a las acciones que el sistema permite realizar al usuario, como los requisitos no funcionales, relacionados con atributos de calidad y arquitectura del software.

== Requisitos Funcionales (RF)

Los requisitos funcionales implementados definen las operaciones principales disponibles para los usuarios dentro de la aplicación.

#req[RF1 – Registro de Cuenta]
La aplicación permite a nuevos usuarios crear una cuenta mediante el ingreso de correo electrónico y contraseña. Este proceso constituye el punto de acceso inicial al sistema y habilita la utilización de las funcionalidades de HomeCore.

#figrow(("images/image39.png", "images/image54.png"), [Imágenes 1 y 2: Pasos para registrarse en HomeCore])

#req[RF2 – Verificación de Cuenta]
Una vez creada la cuenta, el sistema implementa un mecanismo de validación mediante código de verificación enviado al correo electrónico del usuario. Este procedimiento asegura la autenticidad de la dirección ingresada y activa la cuenta para su utilización.

#fig("images/image4.png", [Imagen 3: Verificación de cuenta])

#req[RF3 – Recuperación de Contraseña]
El sistema incorpora un flujo de recuperación de acceso para usuarios que olvidaron su contraseña. A través del correo electrónico registrado se envía un código de seguridad que permite validar al restablecer las credenciales.

#figrow(("images/image7.png", "images/image34.png"), [Imágenes 4 y 5: Pasos para recuperar contraseña])

#req[RF4 – Cambio de Contraseña]
Los usuarios autenticados pueden modificar su contraseña actual desde la sección de perfil, utilizando el botón “Cambiar contraseña” e introduciendo la antigua contraseña y posteriormente la nueva contraseña.

#figrow(("images/image49.png", "images/image45.png"), [Imágenes 6 y 7: Pasos para cambiar contraseña])

#req[RF5 – Inicio de Sesión]
La autenticación de usuarios se realiza mediante validación de credenciales contra la API del sistema. Una vez verificadas, se obtiene un token de acceso que permite cargar y mantener la sesión activa del usuario.

#fig("images/image5.png", [Imagen 8: Inicio de sesión HomeCore])

#req[RF6 – Cierre de Sesión]
La aplicación permite cerrar sesión de forma segura invalidando el token almacenado localmente y redirigiendo al usuario fuera del entorno autenticado.

#fig("images/image33.png", [Imagen 9: Cerrar sesión])

#req[RF7 – Gestión de Dispositivos]
HomeCore permite agregar, editar y eliminar dispositivos inteligentes asociados al hogar, tales como luces, aires acondicionados u otros dispositivos.

#figrow(("images/image43.png", "images/image19.png"), [Imágenes 10 y 11: Gestión de dispositivos (ejemplo lámpara)])

#req[RF8 – Consulta de Dispositivos]
El sistema proporciona una visualización del estado actual de los dispositivos registrados, según cada habitación en el que se lo asoció, a través de la sección “Devices” del menú de navegación.

#fig("images/image14.png", [Imagen 12: Consulta de Dispositivos])

#req[RF9 – Control de Dispositivos]
Al seleccionar el contenedor correspondiente a un dispositivo dentro de la interfaz, el usuario puede interactuar directamente con este para gestionar su funcionamiento (Ver Imagen 11). La aplicación permite encender, apagar y modificar parámetros específicos de los dispositivos, como brillo o temperatura.

#req[RF11 – Consulta de Rutinas]
La aplicación incluye un módulo de automatización que permite visualizar rutinas pre configuradas a través de la sección “Routines” del menú de navegación.

#fig("images/image13.png", [Imagen 13: Sección de Rutinas de HomeCore])

#req[RF12 – Ejecución de Rutinas]
Las rutinas configuradas pueden ejecutarse manualmente mediante un botón de activación “ejecutar ahora” o pre configurarlas para un horario y día en particular, permitiendo controlar múltiples dispositivos de manera simultánea.

#fig("images/image50.png", [Imagen 14: Ejecución de rutina])

#req[RF13 – Consultar acciones realizadas]
En la sección “Actividad” del menú de navegación se implementó un historial que permite visualizar las acciones realizadas dentro de la aplicación.

#fig("images/image31.png", [Imagen 15: Historial de las acciones realizadas])

#req[RF14 – Gestión de Habitaciones]
El sistema permite crear, modificar y organizar habitaciones dentro del hogar seleccionado, facilitando la distribución de los dispositivos.

#figrow(("images/image21.png", "images/image37.png"), [Imágenes 16 y 17: Pasos para crear una habitación])

#figrow(("images/image26.png", "images/image40.png"), [Imágenes 18 y 19: Gestión de Habitaciones])

#req[RF15 – Consulta de Habitaciones]
Las habitaciones pueden visualizarse agrupadas en la sección “Devices” del menú de navegación.

#fig("images/image17.png", [Imagen 20: Consulta habitaciones])

#req[RF16 – Vinculación de Dispositivos a Habitaciones]
Cada dispositivo puede asignarse a una habitación específica, permitiendo una administración más estructurada dentro del sistema. Esto es primero durante la creación del dispositivo pero se puede modificar después desde los detalles del dispositivo.

#fig("images/image11.png", [Imagen 21: creación de dispositivo con habitación])

#req[RF17 – Gestión de Hogares]
La aplicación contempla la posibilidad de administrar múltiples hogares desde una misma cuenta de usuario en la sección superior pudiendo desplegar un menú de los hogares previamente agregados.

#fig("images/image2.png", [Imagen 22: Cambio de hogar, con la posibilidad de crear nuevos y modificar el nombre])

#req[RF19 – Vinculación de Habitaciones a Hogares]
Cada habitación creada se encuentra asociada a un hogar específico, es requisito estar dentro de un hogar para crear una habitación de modo que se asocia la nueva habitación al hogar actual. Así permite mantener una estructura jerárquica organizada dentro del sistema.

#req[RF20 – Envío de Notificaciones]
HomeCore implementa un sistema de notificaciones para informar cambios de estado, ejecución de rutinas y eventos relevantes relacionados con el hogar inteligente.

#fig("images/image47.png", [Imagen 23: Notificación luego de encender un velador])

#req[RF21 – Restringir Acceso a Dispositivos]
La aplicación permite restringir dispositivos mediante una contraseña.

#fig("images/image41.png", [Imagen 24: Configuración de un dispositivo restringido])

#req[RF22 – Consultar Consumo Eléctrico]
El sistema permite visualizar el consumo eléctrico total de los dispositivos registrados en el hogar.

#fig("images/image16.png", [Imagen 25: Consultar consumo eléctrico])

#req[RF23 – Planificar Ejecución de Rutinas]
La aplicación incorpora la posibilidad de programar la ejecución automática de rutinas en horarios determinados, facilitando la automatización de tareas dentro del hogar inteligente. Se permite programar ejecución desde la creación o editando la rutina.

#fig("images/image9.png", [Imagen 26: Programar rutina])

== Requisitos No Funcionales (RNF)

Los requisitos no funcionales implementados establecen atributos de calidad esenciales para garantizar una experiencia de usuario eficiente, segura y mantenible.

#req[RNF1 – Internacionalización y localización]
Se implementó soporte para múltiples idiomas, permitiendo al usuario cambiar dinámicamente el idioma de la aplicación entre español e inglés. Automáticamente usa el idioma del dispositivo, pero se puede modificar desde Usuario. La preferencia seleccionada se mantiene almacenada de manera persistente y los cambios se reflejan automáticamente en la interfaz.

#req[RNF2 – Barra de aplicación contextual]
La aplicación incorpora encabezados y barras contextuales adaptadas a cada pantalla principal, brindando accesos rápidos y herramientas de búsqueda específicas según el contenido visualizado por el usuario.

#req[RNF3 – Personalización de la aplicación]
Se desarrolló un sistema de personalización que permite modificar la apariencia visual de la aplicación, donde el usuario puede optar por un modo oscuro o modo claro. A diferencia del idioma no depende de la configuración del dispositivo.

#figrow(("images/image38.png", "images/image10.png"), [Imágenes 27 y 28: Modo oscuro y modo claro])

#req[RNF4 – Adaptabilidad al dispositivo]
La interfaz fue diseñada con un enfoque responsive, permitiendo ajustar automáticamente la distribución de los componentes según el tamaño de pantalla del dispositivo utilizado. Dependiendo del ancho del dispositivo usa un Navigation rail; en pantallas de un ancho mayor a 600 dp, se pueden ver los detalles de un dispositivo o rutina sin salir de la vista principal.

#figrow(("images/image1.png", "images/image25.png"), [Imágenes 29 y 30: Vista de habitaciones desde un dispositivo móvil y una tablet])

Todas las vistas desde una tablet se incluyen en el anexo.

#req[RNF5 – Adaptabilidad a la orientación de pantalla]
La aplicación presenta layouts flexibles capaces de reorganizar sus componentes ante cambios de orientación del dispositivo. Además, ciertos estados y configuraciones se conservan correctamente durante la rotación de pantalla.

#figrow(("images/image1.png", "images/image24.png"), [Imágenes 31 y 32: Vista de Inicio desde un dispositivo móvil horizontal y vertical])

#req[RNF6 – Compatibilidad con Android 10+]
La aplicación fue desarrollada asegurando compatibilidad con Android 10 o versiones superiores, utilizando tecnologías y dependencias actualizadas para garantizar estabilidad y mantenimiento a largo plazo.

Durante el desarrollo se testeó la app en 2 dispositivos, una tablet Tab S9 y un celular S23 FE, ambos con Android 16. También se usó un emulador para testear en Android 17 y asegurar que la aplicación no contenga ninguna restricción de tamaño o orientación que se rompa en esta versión.
