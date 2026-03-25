import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export const useHomesStore = defineStore('homes', () => {
  const homes = ref([
    { id: 'casa-martinez', name: 'Casa Martinez' },
    { id: 'depto-palermo', name: 'Depto Palermo' },
    { id: 'casa-playa', name: 'Casa de playa' }
  ])

  const selectedHomeId = ref(homes.value[0].id)

  const selectedHome = computed(() =>
    homes.value.find(h => h.id === selectedHomeId.value)
  )

  const defaultHomeId = computed(() => homes.value[0].id)

  function selectHome(id) {
    const exists = homes.value.some(h => h.id === id)
    if (exists) {
      selectedHomeId.value = id
    }
  }

  function homeExists(id) {
    return homes.value.some(h => h.id === id)
  }

  function syncFromRoute(houseId) {
    if (houseId && homeExists(houseId)) {
      selectedHomeId.value = houseId
    }
  }

  return {
    homes,
    selectedHomeId,
    selectedHome,
    defaultHomeId,
    selectHome,
    homeExists,
    syncFromRoute
  }
})
