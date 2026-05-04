<template>
  <div class="settings-view">
    <h1 class="view-title">Configuracion</h1>

    <!-- Seccion: Perfil -->
    <section class="settings-section">
      <h2 class="section-title">Perfil</h2>
      <div class="settings-card">
        <div class="profile-row">
          <div class="avatar-lg">JR</div>
          <div class="profile-info">
            <span class="profile-name">Juani Raggio</span>
            <span class="profile-email">juani@example.com</span>
          </div>
          <button class="btn-secondary">Editar perfil</button>
        </div>
      </div>
    </section>

    <!-- Seccion: Casa -->
    <section class="settings-section">
      <h2 class="section-title">Propiedad</h2>
      <div class="settings-card">
        <div class="setting-row">
          <div class="setting-label">
            <span class="setting-name">Nombre</span>
            <span class="setting-value">Casa Martinez</span>
          </div>
          <button class="btn-secondary">Cambiar</button>
        </div>
        <div class="setting-row">
          <div class="setting-label">
            <span class="setting-name">Direccion</span>
            <span class="setting-value">Av. Libertador 1234, CABA</span>
          </div>
          <button class="btn-secondary">Cambiar</button>
        </div>
      </div>
    </section>

    <!-- Seccion: Usuarios y permisos -->
    <section class="settings-section">
      <h2 class="section-title">Usuarios con acceso</h2>
      <div class="settings-card">
        <div v-for="user in users" :key="user.email" class="user-row">
          <div class="user-avatar">{{ user.initials }}</div>
          <div class="user-info">
            <span class="user-name">{{ user.name }}</span>
            <span class="user-email">{{ user.email }}</span>
          </div>
          <span class="user-role" :class="`role--${user.role}`">{{ user.roleLabel }}</span>
        </div>
        <button class="btn-add-user">
          <i class="fa-solid fa-plus"></i> Invitar usuario
        </button>
      </div>
    </section>

    <!-- Seccion: Notificaciones -->
    <section class="settings-section">
      <h2 class="section-title">Notificaciones</h2>
      <div class="settings-card">
        <div v-for="pref in notifPrefs" :key="pref.key" class="notif-row">
          <div class="notif-label">
            <span class="notif-name">{{ pref.label }}</span>
            <span class="notif-desc">{{ pref.description }}</span>
          </div>
          <ToggleSwitch :model-value="pref.enabled" @update:model-value="pref.enabled = $event" />
        </div>
      </div>
    </section>

    <!-- Seccion: Zona peligrosa -->
    <section class="settings-section">
      <h2 class="section-title section-title--danger">Zona peligrosa</h2>
      <div class="settings-card settings-card--danger">
        <div class="setting-row">
          <div class="setting-label">
            <span class="setting-name">Eliminar casa</span>
            <span class="setting-value">Se eliminaran todos los dispositivos, rutinas e historial.</span>
          </div>
          <button class="btn-danger">Eliminar</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'

// Mock data
const users = ref([
  { name: 'Juani Raggio',   email: 'juani@example.com',    initials: 'JR', role: 'admin',  roleLabel: 'Admin' },
  { name: 'Maria Lopez',    email: 'maria@example.com',    initials: 'ML', role: 'user',   roleLabel: 'Usuario' },
  { name: 'Pedro Gomez',    email: 'pedro@example.com',    initials: 'PG', role: 'guest',  roleLabel: 'Invitado' },
])

const notifPrefs = ref([
  { key: 'security',   label: 'Seguridad',          description: 'Alarma activada, puerta forzada, etc.',     enabled: true  },
  { key: 'devices',    label: 'Dispositivos',       description: 'Cambios de estado en dispositivos criticos', enabled: true  },
  { key: 'routines',   label: 'Rutinas',            description: 'Rutina ejecutada o fallida',                 enabled: false },
  { key: 'energy',     label: 'Consumo energetico', description: 'Alerta si se supera el umbral diario',      enabled: true  },
])
</script>

<style scoped>
.settings-view {
  padding: 0;
  max-width: 700px;
}

.view-title {
  font-size: var(--font-3xl);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 28px;
  padding: 0 5px;
}

.settings-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}

.section-title--danger {
  color: var(--danger);
}

.settings-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.settings-card--danger {
  border-color: rgba(248, 113, 113, 0.3);
}

/* Profile */
.profile-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-lg {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--font-xl);
  color: var(--text-on-accent);
}

.profile-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.profile-name {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.profile-email {
  font-size: var(--font-base);
  color: var(--text-muted);
}

/* Setting rows */
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.setting-label {
  display: flex;
  flex-direction: column;
}

.setting-name {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.setting-value {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

/* Users */
.user-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background-color: var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--font-sm);
  color: var(--text-primary);
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.user-email {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.user-role {
  font-size: var(--font-sm);
  font-weight: 600;
  padding: 3px 10px;
  border-radius: var(--radius-full);
}

.role--admin {
  background-color: rgba(129, 140, 248, 0.15);
  color: var(--accent);
}

.role--user {
  background-color: rgba(52, 211, 153, 0.15);
  color: var(--success);
}

.role--guest {
  background-color: rgba(132, 148, 167, 0.15);
  color: var(--text-muted);
}

.btn-add-user {
  background: none;
  border: 1px dashed var(--border);
  color: var(--text-muted);
  border-radius: var(--radius-md);
  padding: 10px;
  font-size: var(--font-base);
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}

.btn-add-user:hover {
  border-color: var(--accent);
  color: var(--accent);
}

/* Notificaciones */
.notif-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.notif-label {
  display: flex;
  flex-direction: column;
}

.notif-name {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.notif-desc {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

/* Buttons */
.btn-secondary {
  background: none;
  border: 1px solid var(--border);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  padding: 6px 14px;
  font-size: var(--font-base);
  cursor: pointer;
  transition: border-color 0.2s;
  white-space: nowrap;
}

.btn-secondary:hover {
  border-color: var(--accent);
}

.btn-danger {
  background: none;
  border: 1px solid rgba(248, 113, 113, 0.4);
  color: var(--danger);
  border-radius: var(--radius-md);
  padding: 6px 14px;
  font-size: var(--font-base);
  cursor: pointer;
  transition: background-color 0.2s;
  white-space: nowrap;
}

.btn-danger:hover {
  background-color: rgba(248, 113, 113, 0.1);
}
</style>
