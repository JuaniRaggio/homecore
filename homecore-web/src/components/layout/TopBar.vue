<template>
  <header class="topbar">
    <!-- Logo de la app -->
    <div class="topbar__left">
      <i class="fa-solid logo-icon"></i>
      <span class="logo-text">HomeCore</span>
    </div>

    <!-- Nombre de la propiedad activa (viene del store de homes) -->
    <div class="topbar__center">
      <!-- TODO: Bindear al nombre de la casa activa desde el store -->
      Casa Martinez
    </div>

    <div class="topbar__right">
      <!-- Boton de notificaciones -->
      <!-- TODO: Al hacer click, abrir panel/dropdown de notificaciones -->
      <!-- TODO: El badge debe mostrar la cantidad real de notificaciones sin leer (del store) -->
      <div class="notif-wrap">
        <button class="btn-notif" @click="/* TODO: togglear panel de notificaciones */">
          <i class="fa-regular fa-bell"></i>
        </button>
        <span class="notif-badge">1</span>
      </div>

      <!-- Menu de usuario -->
      <!-- TODO: Al hacer click, abrir dropdown con opciones (cerrar sesion, perfil, etc.) -->
      <div class="topbar__user">
        <span class="user-name">Juani Raggio</span>
        <i class="fa-solid fa-chevron-down"></i>
        <div class="avatar">JR</div>
      </div>
    </div>
  </header>
</template>

<script setup>
// TODO: Importar el store de notificaciones para el badge
// TODO: Importar el store de usuario para nombre y avatar
// TODO: Importar el store de homes para el nombre de la casa activa
</script>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--topbar-h);
  background-color: var(--bg-topbar);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 100;
}

.topbar__left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--accent);
}

.topbar__center {
  font-size: 15px;
  font-weight: 300;
  color: var(--text-muted);
}

.topbar__right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notif-wrap {
  position: relative;
}

.btn-notif {
  background: none;
  border: none;
  color: var(--text-muted);
  font-size: 18px;
  cursor: pointer;
}

.notif-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: #e74c3c;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.topbar__user {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
}
</style>
