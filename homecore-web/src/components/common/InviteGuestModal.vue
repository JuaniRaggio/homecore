<template>
  <ModalBase :visible="visible" @close="handleClose">
    <h2 class="modal-title">Invitados</h2>

    <!-- Lista de invitados actuales -->
    <div class="guests-section">
      <p v-if="loadingGuests" class="guests-empty">Cargando...</p>
      <p v-else-if="guests.length === 0" class="guests-empty">Sin invitados aún</p>
      <ul v-else class="guests-list">
        <li v-for="guest in guests" :key="guest.email" class="guest-item">
          <span class="guest-email">{{ guest.email }}</span>
          <button class="guest-remove" :disabled="removingEmail === guest.email" @click="removeGuest(guest.email)" title="Quitar acceso">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </li>
      </ul>
    </div>

    <div class="divider" />

    <!-- Agregar nuevo invitado -->
    <p class="add-label">Agregar por email</p>
    <div class="invite-row">
      <input
        v-model="email"
        class="modal-input invite-input"
        type="email"
        placeholder="email@ejemplo.com"
        :disabled="inviting"
        @keyup.enter="addGuest"
      />
      <button class="btn-confirm invite-btn" @click="addGuest" :disabled="inviting || !email.trim()">
        {{ inviting ? '...' : 'Invitar' }}
      </button>
    </div>
    <p v-if="errorMsg" class="modal-error">{{ errorMsg }}</p>

    <div class="modal-actions">
      <button class="btn-cancel" @click="handleClose">Cerrar</button>
    </div>
  </ModalBase>
</template>

<script setup>
import { ref, watch } from 'vue'
import ModalBase from '@/components/common/ModalBase.vue'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

const props = defineProps({
  visible: { type: Boolean, required: true },
  homeId: { type: [String, Number], required: true },
})

const emit = defineEmits(['close'])

const toast = useToastStore()

const guests = ref([])
const loadingGuests = ref(false)
const email = ref('')
const inviting = ref(false)
const removingEmail = ref('')
const errorMsg = ref('')

watch(() => props.visible, (val) => {
  if (val) {
    email.value = ''
    errorMsg.value = ''
    fetchGuests()
  }
})

async function fetchGuests() {
  loadingGuests.value = true
  try {
    const result = await api.getSharedUsers(props.homeId)
    guests.value = Array.isArray(result) ? result : []
  } catch {
    guests.value = []
  } finally {
    loadingGuests.value = false
  }
}

async function addGuest() {
  const trimmed = email.value.trim()
  if (!trimmed) return
  if (!trimmed.includes('@')) {
    errorMsg.value = 'Ingresá un email válido'
    return
  }
  errorMsg.value = ''
  inviting.value = true
  try {
    await api.shareHome(props.homeId, trimmed)
    toast.show(`Acceso otorgado a ${trimmed}`, 'success')
    email.value = ''
    await fetchGuests()
  } catch (e) {
    errorMsg.value = e.message || 'No se pudo invitar al usuario'
  } finally {
    inviting.value = false
  }
}

async function removeGuest(guestEmail) {
  removingEmail.value = guestEmail
  try {
    await api.unshareHome(props.homeId, guestEmail)
    guests.value = guests.value.filter(g => g.email !== guestEmail)
    toast.show('Acceso eliminado', 'success')
  } catch (e) {
    toast.show(e.message || 'No se pudo eliminar el acceso', 'error')
  } finally {
    removingEmail.value = ''
  }
}

function handleClose() {
  emit('close')
}
</script>

<style scoped>
.guests-section {
  margin-bottom: 12px;
  min-height: 40px;
}

.guests-empty {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.guests-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.guest-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  background-color: var(--bg-main);
  border-radius: var(--radius-md);
  font-size: var(--font-sm);
}

.guest-email {
  color: var(--text-primary);
}

.guest-remove {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: var(--font-sm);
  padding: 2px 6px;
  border-radius: var(--radius-md);
  transition: color 0.2s, background-color 0.2s;
}

.guest-remove:hover:not(:disabled) {
  color: var(--danger);
  background-color: rgba(255, 80, 80, 0.1);
}

.guest-remove:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.divider {
  border: none;
  border-top: 1px solid var(--border);
  margin: 12px 0;
}

.add-label {
  font-size: var(--font-sm);
  color: var(--text-muted);
  margin-bottom: 8px;
}

.invite-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.invite-input {
  flex: 1;
  margin-bottom: 0;
}

.invite-btn {
  white-space: nowrap;
  flex-shrink: 0;
}

.modal-error {
  font-size: var(--font-sm);
  color: var(--danger);
  margin-top: 6px;
}
</style>
