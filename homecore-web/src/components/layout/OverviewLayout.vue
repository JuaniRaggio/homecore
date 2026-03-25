<template>
  <div class="overview-layout">
    <AppHeader />
    <main class="overview-layout__content">
      <router-view />
    </main>
    <HcToast ref="toast" />
    <VimOverlay />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import AppHeader from './AppHeader.vue'
import HcToast from '../ui/HcToast.vue'
import VimOverlay from '../vim/VimOverlay.vue'
import { useVimMode } from '../../composables/useVimMode'

const toast = ref(null)

const vim = useVimMode()

provide('toast', toast)
provide('vim', vim)
</script>

<style scoped>
.overview-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.overview-layout__content {
  flex: 1;
  padding: var(--hc-space-xl);
  animation: fadeIn 300ms ease;
}
</style>
