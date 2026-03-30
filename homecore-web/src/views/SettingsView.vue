<template>
  <div class="settings-page">
    <h2>Configuracion</h2>

    <div class="settings-sections">
      <!-- Perfil -->
      <section class="settings-section">
        <h3>Perfil</h3>
        <div class="settings-section__content">
          <div class="settings-field">
            <span class="settings-field__label">Nombre</span>
            <span class="settings-field__value">{{ authStore.user?.name }}</span>
          </div>
          <div class="settings-field">
            <span class="settings-field__label">Email</span>
            <span class="settings-field__value">{{ authStore.user?.email }}</span>
          </div>
        </div>
      </section>

      <!-- Cambiar contrasena -->
      <section class="settings-section">
        <h3>Cambiar contrasena</h3>
        <form @submit.prevent="handleChangePassword" class="settings-form">
          <HcInput
            v-model="currentPassword"
            type="password"
            label="Contrasena actual"
            placeholder="Tu contrasena actual"
            :error="passwordErrors.current"
          />
          <HcInput
            v-model="newPassword"
            type="password"
            label="Nueva contrasena"
            placeholder="Minimo 8 caracteres"
            :error="passwordErrors.new"
          />
          <HcInput
            v-model="confirmNewPassword"
            type="password"
            label="Confirmar nueva contrasena"
            placeholder="Repeti la nueva contrasena"
            :error="passwordErrors.confirm"
          />
          <div class="settings-form__actions">
            <HcButton type="submit" :loading="changingPassword">Cambiar contrasena</HcButton>
          </div>
        </form>
      </section>

      <!-- Notificaciones -->
      <section class="settings-section">
        <h3>Notificaciones</h3>
        <div class="settings-section__content">
          <div class="settings-toggle-row">
            <div>
              <p class="settings-toggle-row__title">Notificaciones activadas</p>
              <p class="settings-toggle-row__desc">Recibir alertas de dispositivos y rutinas</p>
            </div>
            <HcToggle
              :modelValue="authStore.user?.notificationsEnabled"
              @update:modelValue="authStore.toggleNotifications()"
            />
          </div>
        </div>
      </section>

      <!-- Restriccion de acceso -->
      <section class="settings-section">
        <h3>Restriccion de acceso</h3>
        <div class="settings-section__content">
          <div class="settings-toggle-row">
            <div>
              <p class="settings-toggle-row__title">PIN de seguridad</p>
              <p class="settings-toggle-row__desc">Solicitar PIN para acciones criticas (armar/desarmar alarma, desbloquear puertas)</p>
            </div>
            <HcToggle v-model="pinEnabled" />
          </div>
          <div v-if="pinEnabled" class="settings-pin">
            <HcInput
              v-model="pinValue"
              label="PIN (4 digitos)"
              placeholder="1234"
              hint="PIN actual de la demo: 1234"
            />
          </div>
        </div>
      </section>

      <!-- Hogares -->
      <section class="settings-section">
        <h3>Hogares</h3>
        <div class="settings-section__content">
          <div v-for="home in authStore.user?.homes" :key="home.id" class="settings-home">
            <span class="settings-home__name">{{ home.name }}</span>
            <HcBadge v-if="home.active" variant="primary" size="sm">Activo</HcBadge>
            <HcButton v-else variant="ghost" size="sm" @click="authStore.setActiveHome(home.id)">
              Activar
            </HcButton>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useAuthStore } from '../stores/auth'
import HcInput from '../components/ui/HcInput.vue'
import HcButton from '../components/ui/HcButton.vue'
import HcToggle from '../components/ui/HcToggle.vue'
import HcBadge from '../components/ui/HcBadge.vue'

const authStore = useAuthStore()
const toast = inject('toast')

const currentPassword = ref('')
const newPassword = ref('')
const confirmNewPassword = ref('')
const changingPassword = ref(false)
const passwordErrors = ref({ current: null, new: null, confirm: null })

const pinEnabled = ref(false)
const pinValue = ref('1234')

async function handleChangePassword() {
  passwordErrors.value = { current: null, new: null, confirm: null }

  let valid = true
  if (!currentPassword.value) {
    passwordErrors.value.current = 'Ingresa tu contrasena actual'
    valid = false
  }
  if (!newPassword.value || newPassword.value.length < 8) {
    passwordErrors.value.new = 'La nueva contrasena debe tener al menos 8 caracteres'
    valid = false
  }
  if (newPassword.value !== confirmNewPassword.value) {
    passwordErrors.value.confirm = 'Las contrasenas no coinciden'
    valid = false
  }
  if (!valid) return

  changingPassword.value = true
  await new Promise(r => setTimeout(r, 500))

  const result = authStore.changePassword(currentPassword.value, newPassword.value)
  changingPassword.value = false

  if (result.success) {
    currentPassword.value = ''
    newPassword.value = ''
    confirmNewPassword.value = ''
    toast.value?.show('Contrasena cambiada correctamente', 'success')
  } else {
    passwordErrors.value.current = result.error
  }
}
</script>

<style scoped>
.settings-page {
  max-width: 700px;
}

.settings-page h2 {
  margin-bottom: var(--hc-space-xl);
}

.settings-sections {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.settings-section {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

.settings-section h3 {
  font-size: var(--hc-font-size-lg);
  margin-bottom: var(--hc-space-lg);
  padding-bottom: var(--hc-space-sm);
  border-bottom: 1px solid var(--hc-border);
}

.settings-section__content {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.settings-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--hc-font-size-sm);
}

.settings-field__label {
  color: var(--hc-text-secondary);
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.settings-form__actions {
  display: flex;
  justify-content: flex-end;
}

.settings-toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--hc-space-md);
}

.settings-toggle-row__title {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.settings-toggle-row__desc {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
  margin-top: 0.125rem;
}

.settings-pin {
  padding-top: var(--hc-space-md);
  max-width: 200px;
}

.settings-home {
  display: flex;
  align-items: center;
  gap: var(--hc-space-md);
  padding: var(--hc-space-sm) 0;
}

.settings-home__name {
  flex: 1;
  font-size: var(--hc-font-size-sm);
}
</style>
