<template>
  <main class="page-content--full">
    <div class="new-property">
      <button class="btn-back" @click="router.push('/overview')">
        <i class="fa-solid fa-arrow-left"></i> Volver al overview
      </button>

      <h1 class="view-title">Nueva propiedad</h1>
      <p class="view-subtitle">Agrega una nueva casa o departamento a tu cuenta</p>

      <div class="form-card">
        <div class="form-group">
          <label class="form-label">Nombre de la propiedad</label>
          <input
            v-model="name"
            type="text"
            class="form-input"
            placeholder="Ej: Casa de playa"
            @keyup.enter="handleCreate"
          />
        </div>

        <div class="form-group">
          <label class="form-label">Direccion (opcional)</label>
          <input
            v-model="address"
            type="text"
            class="form-input"
            placeholder="Ej: Av. Libertador 1234, CABA"
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <div class="form-actions">
          <button class="btn-cancel" @click="router.push('/overview')">Cancelar</button>
          <button class="btn-confirm" @click="handleCreate" :disabled="!name.trim()">
            Crear propiedad
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useHomesStore } from '@/stores/homes'

const router = useRouter()
const homesStore = useHomesStore()

const name = ref('')
const address = ref('')
const errorMsg = ref('')

async function handleCreate() {
  if (!name.value.trim()) return
  errorMsg.value = ''

  try {
    await homesStore.addHome({
      name: name.value.trim(),
      address: address.value.trim()
    })
    router.push('/overview')
  } catch (e) {
    errorMsg.value = e.message || 'Error al crear la propiedad'
  }
}
</script>

<style scoped>
.new-property {
  max-width: 480px;
}


.view-title {
  margin-bottom: 4px;
}

.view-subtitle {
  font-size: var(--font-base);
  color: var(--text-muted);
  margin-bottom: 24px;
}

.form-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-label {
  font-weight: 600;
}

.form-input {
  background-color: var(--bg-main);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 4px;
}

</style>
