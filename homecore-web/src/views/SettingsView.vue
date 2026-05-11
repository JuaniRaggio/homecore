<template>
  <div class="settings-view">
    <h1 class="view-title">Configuracion</h1>

    <!-- Perfil -->
    <section class="settings-section">
      <div class="form-card settings-card">
        <h2 class="card-title">Perfil</h2>
        <div class="card-divider" />
        <div class="info-row">
          <span class="info-label">Nombre</span>
          <span class="info-value">{{ userName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Email</span>
          <span class="info-value">{{ userEmail }}</span>
        </div>
      </div>
    </section>

    <!-- Cambiar contrasena -->
    <section class="settings-section">
      <div class="form-card settings-card">
        <h2 class="card-title">Cambiar contrasena</h2>
        <div class="card-divider" />

        <div class="field-group">
          <label class="field-label">Contrasena actual</label>
          <div class="input-wrap">
            <input
              v-model="currentPassword"
              :type="showCurrent ? 'text' : 'password'"
              placeholder="Tu contrasena actual"
              class="field-input"
            />
            <button class="btn-show" @click="showCurrent = !showCurrent">Mostrar</button>
          </div>
        </div>

        <div class="field-group">
          <label class="field-label">Nueva contrasena</label>
          <div class="input-wrap">
            <input
              v-model="newPassword"
              :type="showNew ? 'text' : 'password'"
              placeholder="Minimo 8 caracteres"
              class="field-input"
            />
            <button class="btn-show" @click="showNew = !showNew">Mostrar</button>
          </div>
        </div>

        <div class="field-group">
          <label class="field-label">Confirmar nueva contrasena</label>
          <div class="input-wrap">
            <input
              v-model="confirmPassword"
              :type="showConfirm ? 'text' : 'password'"
              placeholder="Repite tu nueva contrasena"
              class="field-input"
            />
            <button class="btn-show" @click="showConfirm = !showConfirm">Mostrar</button>
          </div>
        </div>

        <div v-if="passwordError" class="msg msg--error">{{ passwordError }}</div>
        <div v-if="passwordSuccess" class="msg msg--success">{{ passwordSuccess }}</div>

        <button class="btn-save" @click="handleChangePassword">Guardar cambios</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toast'

const authStore = useAuthStore()
const toast = useToastStore()

const userName = computed(() => authStore.user?.name ?? '')
const userEmail = computed(() => authStore.user?.email ?? '')

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const showCurrent = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)
const passwordError = ref('')
const passwordSuccess = ref('')
const loading = ref(false)

async function handleChangePassword() {
  passwordError.value = ''
  passwordSuccess.value = ''

  if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
    passwordError.value = 'Completa todos los campos'
    return
  }
  if (newPassword.value.length < 8) {
    passwordError.value = 'La nueva contrasena debe tener al menos 8 caracteres'
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = 'Las contrasenas no coinciden'
    return
  }

  loading.value = true
  try {
    const result = await authStore.changePassword(currentPassword.value, newPassword.value)

    if (result.success) {
      passwordSuccess.value = 'Contraseña actualizada correctamente'
      toast.show('Contraseña actualizada', 'success')
      currentPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    } else {
      passwordError.value = result.error || 'No se pudo cambiar la contrasena. Verifica que la contrasena actual sea correcta.'
      toast.show('No se pudo cambiar la contrasena. Verifica que la contrasena actual sea correcta.', 'error')
    }
  } catch (e) {
    passwordError.value = 'Error inesperado al cambiar la contraseña'
    toast.show('Error inesperado al cambiar la contraseña', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.settings-view {
  padding: 0;
  max-width: 700px;
}

.view-title {
  margin-bottom: 28px;
}

.settings-section {
  margin-bottom: 20px;
}

/* Override: gap mas chico y padding horizontal extra */
.settings-card {
  gap: 16px;
  padding: 20px 24px;
}

.card-divider {
  margin: 0 -24px;
}

/* Info rows (Perfil) */
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-label {
  font-size: var(--font-md);
  color: var(--text-muted);
}

.info-value {
  font-size: var(--font-md);
  color: var(--text-primary);
}

/* Form fields (Cambiar contrasena) */
.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: var(--font-md);
  color: var(--text-muted);
}

.input-wrap {
  display: flex;
  align-items: center;
  background-color: var(--bg-card-alt);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 0 14px;
  gap: 8px;
}

.field-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: var(--font-md);
  padding: 12px 0;
}

.field-input::placeholder {
  color: var(--text-muted);
}

.btn-show {
  background: none;
  border: none;
  color: var(--accent);
  font-size: var(--font-md);
  cursor: pointer;
  padding: 0;
  white-space: nowrap;
}

.btn-show:hover {
  color: var(--accent-hover);
}

/* Feedback messages */
.msg {
  font-size: var(--font-sm);
  padding: 8px 12px;
  border-radius: var(--radius-sm);
}

.msg--error {
  background-color: var(--danger-bg);
  color: var(--danger);
}

.msg--success {
  background-color: var(--success-bg);
  color: var(--success);
}

/* Save button */
.btn-save {
  align-self: flex-end;
  background-color: var(--accent);
  color: var(--text-on-accent);
  border: none;
  border-radius: var(--radius-md);
  padding: 10px 20px;
  font-size: var(--font-md);
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-save:hover {
  opacity: 0.85;
}
</style>
