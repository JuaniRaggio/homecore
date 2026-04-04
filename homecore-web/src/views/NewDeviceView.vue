<template>
  <div class="new-device">
    <button class="new-device__back" @click="$router.back()"><HcIcon name="arrowLeft" size="sm" /> Volver a dispositivos</button>
    <h2>Nuevo dispositivo</h2>

    <div class="new-device__form-card">
      <div class="new-device__form">
        <HcInput v-model="form.name" label="Nombre" placeholder="Ej: Lampara del comedor" :error="errors.name" />

        <div class="new-device__field">
          <label class="new-device__label">Tipo</label>
          <select v-model="form.type" class="new-device__select">
            <option value="" disabled>Seleccionar tipo</option>
            <option v-for="(label, key) in devicesStore.typeLabels" :key="key" :value="key">
              {{ label }}
            </option>
          </select>
          <p v-if="errors.type" class="new-device__error">{{ errors.type }}</p>
        </div>

        <div class="new-device__field">
          <label class="new-device__label">Habitacion</label>
          <select v-model="form.roomId" class="new-device__select">
            <option value="">Sin habitacion</option>
            <option v-for="room in roomsStore.rooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </option>
          </select>
        </div>

        <HcInput v-model="form.consumption" type="number" label="Consumo estimado (W)" placeholder="Ej: 12" />

        <div class="new-device__toggles">
          <HcToggle v-model="form.favorite" label="Marcar como favorito" />
          <HcToggle v-model="form.critical" label="Marcar como critico" />
        </div>

        <div class="new-device__separator"></div>

        <HcToggle v-model="form.usePassword" label="Proteger con contrasena" />

        <template v-if="form.usePassword">
          <HcInput
            v-model="form.password"
            type="password"
            label="Contrasena"
            placeholder="Minimo 4 caracteres"
            :error="errors.password"
          />
          <HcInput
            v-model="form.passwordConfirm"
            type="password"
            label="Confirmar contrasena"
            placeholder="Repetir contrasena"
            :error="errors.passwordConfirm"
          />
        </template>
      </div>

      <div class="new-device__actions">
        <HcButton variant="secondary" @click="$router.back()">Volver</HcButton>
        <HcButton @click="createDevice">Crear dispositivo</HcButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, inject, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDevicesStore } from '../stores/devices'
import { useRoomsStore } from '../stores/rooms'
import { useAuthStore } from '../stores/auth'
import HcButton from '../components/ui/HcButton.vue'
import HcInput from '../components/ui/HcInput.vue'
import HcToggle from '../components/ui/HcToggle.vue'
import HcIcon from '../components/ui/HcIcon.vue'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const authStore = useAuthStore()
const toast = inject('toast')

onMounted(() => {
  if (!authStore.isAdmin) {
    router.replace(`/${route.params.houseId}/dispositivos`)
  }
})

const form = reactive({
  name: '',
  type: '',
  roomId: '',
  consumption: '',
  favorite: false,
  critical: false,
  usePassword: false,
  password: '',
  passwordConfirm: ''
})

const errors = ref({
  name: null,
  type: null,
  password: null,
  passwordConfirm: null
})

function validate() {
  errors.value = { name: null, type: null, password: null, passwordConfirm: null }
  let valid = true

  if (!form.name.trim()) {
    errors.value.name = 'El nombre es obligatorio'
    valid = false
  }
  if (!form.type) {
    errors.value.type = 'Selecciona un tipo de dispositivo'
    valid = false
  }
  if (form.usePassword) {
    if (form.password.length < 4) {
      errors.value.password = 'La contrasena debe tener al menos 4 caracteres'
      valid = false
    }
    if (form.password !== form.passwordConfirm) {
      errors.value.passwordConfirm = 'Las contrasenas no coinciden'
      valid = false
    }
  }

  return valid
}

function createDevice() {
  if (!validate()) return

  devicesStore.addDevice({
    name: form.name.trim(),
    type: form.type,
    roomId: form.roomId || null,
    consumption: Number(form.consumption) || 0,
    favorite: form.favorite,
    critical: form.critical,
    password: form.usePassword ? form.password : null
  })

  toast.value?.show('Dispositivo creado correctamente', 'success')
  router.push(`/${route.params.houseId}/dispositivos`)
}
</script>

<style scoped>
.new-device {
  max-width: 600px;
  margin: 0 auto;
}

.new-device__back {
  background: none;
  border: none;
  color: var(--hc-accent);
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  padding: 0;
  margin-bottom: var(--hc-space-md);
}

.new-device h2 {
  margin-bottom: var(--hc-space-xl);
}

.new-device__form-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-xl);
}

.new-device__form {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.new-device__field {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.new-device__label {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
  color: var(--hc-text-secondary);
}

.new-device__select {
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  padding: 0.625rem 0.875rem;
  font-size: var(--hc-font-size-base);
  outline: none;
  cursor: pointer;
}

.new-device__select:focus {
  border-color: var(--hc-accent);
}

.new-device__error {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-danger);
}

.new-device__toggles {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.new-device__separator {
  border-top: 1px solid var(--hc-border);
}

.new-device__actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--hc-space-sm);
  margin-top: var(--hc-space-xl);
  padding-top: var(--hc-space-lg);
  border-top: 1px solid var(--hc-border);
}
</style>
