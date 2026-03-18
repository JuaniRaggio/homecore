import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useHomeModelStore = defineStore('homeModel', () => {
  const selectedModel = ref('casa')

  function setModel(model) {
    if (model === 'casa' || model === 'departamento') {
      selectedModel.value = model
    }
  }

  return {
    selectedModel,
    setModel
  }
})
