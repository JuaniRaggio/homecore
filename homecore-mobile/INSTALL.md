# Instructivo de instalacion - HomeCore Mobile

Aplicacion Android (Jetpack Compose) del Grupo 15. Se conecta a la API remota de
la catedra (`https://hci.it.itba.edu.ar/api/`), por lo que solo requiere conexion
a internet; no hay backend local que levantar.

## Requisitos

Dispositivo fisico o emulador con:

- Android 10 (API 29) o superior. Probado en API 29 (Android 10) y API 35 (Android 15).
- Aproximadamente 100 MB de espacio libre.
- Conexion a internet (WiFi o datos moviles).

## Opcion A: instalar el APK en un dispositivo fisico

1. Habilitar la instalacion de aplicaciones de origenes desconocidos:
   Configuracion -> Seguridad (o Aplicaciones) -> Instalar apps desconocidas, y
   permitir la fuente desde la que se va a abrir el archivo (navegador o gestor de
   archivos).
2. Transferir `app-debug.apk` al dispositivo (cable USB, Google Drive, o enlace de
   descarga).
3. Abrir el archivo `app-debug.apk` desde el gestor de archivos y confirmar la
   instalacion.
4. Abrir la aplicacion HomeCore y aceptar el permiso de notificaciones si aparece.

## Opcion B: instalar el APK en un emulador

1. Iniciar un emulador (AVD) con API 29 o superior (recomendado: Pixel con
   Google APIs).
2. Arrastrar `app-debug.apk` sobre la ventana del emulador, o instalar por linea
   de comandos:

   ```
   adb install app-debug.apk
   ```

## Primera ejecucion

1. Abrir HomeCore. Aparece la pantalla de inicio de sesion.
2. Iniciar sesion con una cuenta existente o registrar una nueva (el codigo de
   verificacion llega por email).
3. Si aparece un error de red, verificar la conexion a internet del dispositivo o
   emulador.

## Compilar desde el codigo fuente (opcional)

El proyecto se compila con Gradle (no requiere abrir Android Studio):

```
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

La base URL y la API key se leen de `local.properties` (incluido en esta entrega)
o de variables de entorno, a traves de `BuildConfig`:

```properties
HCI_API_KEY=<api key del grupo>
HCI_API_BASE_URL=https://hci.it.itba.edu.ar/api/
```
