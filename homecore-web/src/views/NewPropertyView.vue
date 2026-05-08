<template>
  <main class="page-content--full">
    <section class="new-property-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i>
        Volver
      </button>
      <h1 class="view-title">Nueva propiedad</h1>
    </section>

    <section class="new-property-form">
      <div class="form-group">
        <label class="form-label">Nombre de la propiedad</label>
        <input v-model="form.name" type="text" placeholder="Ej: Casa Martinez" />
      </div>

      <div class="form-group">
        <label class="form-label">Direccion</label>
        <input v-model="form.address" type="text" placeholder="Ej: Av. Siempreviva 742" />
      </div>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

      <button class="btn-primary" @click="handleCreate" :disabled="loading">
        {{ loading ? 'Creando...' : 'Crear propiedad' }}
      </button>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useHomesStore } from '@/stores/homes'

const router = useRouter()
const homesStore = useHomesStore()

const form = ref({ name: '', address: '' })
const errorMsg = ref('')
const loading = ref(false)

async function handleCreate() {
  errorMsg.value = ''

  if (!form.value.name.trim()) {
    errorMsg.value = 'El nombre de la propiedad es obligatorio'
    return
  }

  loading.value = true
  try {
    await homesStore.addHome(form.value)
    router.push('/overview')
  } catch (e) {
    errorMsg.value = e.message || 'Error al crear la propiedad'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.new-property-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.new-property-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 480px;
}
</style>
