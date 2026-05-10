import { ref } from 'vue'

const mobileOpen = ref(false)

export function useSidebar() {
  function openMobile() { mobileOpen.value = true }
  function closeMobile() { mobileOpen.value = false }
  function toggleMobile() { mobileOpen.value = !mobileOpen.value }
  return { mobileOpen, openMobile, closeMobile, toggleMobile }
}
