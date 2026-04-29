<template>
  <div class="settings-view">
    <h1 class="view-title">Configuracion</h1>

    <!-- TODO: Seccion de perfil de usuario -->
    <!-- Nombre, email, avatar, cambiar contrasena -->

    <!-- TODO: Seccion de gestion de la casa -->
    <!-- Nombre de la casa, direccion, agregar/eliminar casas -->

    <!-- TODO: Seccion de usuarios y permisos -->
    <!-- Lista de usuarios con acceso, roles (admin, usuario, invitado) -->
    <!-- Agregar/eliminar usuarios, cambiar permisos por dispositivo/habitacion -->

    <!-- TODO: Seccion de notificaciones -->
    <!-- Preferencias de notificaciones: que eventos notificar, como (push, email, etc.) -->

    <p class="placeholder">
      Aca va la configuracion general: perfil, casa, usuarios, permisos y notificaciones.
    </p>
  </div>
</template>

<script setup>
// TODO: Importar stores relevantes (usuario, homes, permisos)
// TODO: En onMounted(), cargar configuracion actual
// TODO: Handlers: updateProfile, updateHome, manageUsers, updatePermissions, updateNotifPrefs
</script>

<style scoped>
.settings-view {
  padding: 0;
}

.view-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 20px;
}

.placeholder {
  color: var(--text-muted);
  font-style: italic;
}
</style>
