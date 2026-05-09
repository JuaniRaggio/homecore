import { ref } from 'vue'

export function useConfirmAction() {
  const visible = ref(false)
  const targetId = ref(null)
  const loading = ref(false)

  function request(id) {
    targetId.value = id
    visible.value = true
  }

  function close() {
    visible.value = false
  }

  async function confirm(action) {
    if (loading.value) return
    loading.value = true
    try {
      await action(targetId.value)
      visible.value = false
    } finally {
      loading.value = false
    }
  }

  return { visible, targetId, loading, request, close, confirm }
}
