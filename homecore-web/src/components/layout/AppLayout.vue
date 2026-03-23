<template>
  <div class="app-layout">
    <AppSidebar :collapsed="sidebarCollapsed" @toggle="sidebarCollapsed = !sidebarCollapsed" />
    <div class="app-layout__main" :class="{ 'app-layout__main--expanded': sidebarCollapsed }">
      <AppHeader />
      <main class="app-layout__content">
        <router-view />
      </main>
    </div>
    <HcToast ref="toast" />
    <VimOverlay />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'
import HcToast from '../ui/HcToast.vue'
import VimOverlay from '../vim/VimOverlay.vue'
import { useVimMode } from '../../composables/useVimMode'

const sidebarCollapsed = ref(false)
const toast = ref(null)

const vim = useVimMode()

provide('toast', toast)
provide('vim', vim)
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
}

.app-layout__main {
  margin-left: var(--hc-sidebar-width);
  transition: margin-left var(--hc-transition-base);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: var(--hc-statusline-height);
}

.app-layout__main--expanded {
  margin-left: var(--hc-sidebar-collapsed);
}

.app-layout__content {
  flex: 1;
  padding: var(--hc-space-xl);
  animation: fadeIn 300ms ease;
}
</style>
