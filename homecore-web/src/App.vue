<template>
  <TopBar v-if="!isAuthPage" />
  <router-view />
  <ToastContainer />
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from '@/components/layout/TopBar.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { useAuthStore } from '@/stores/auth'
import { useRoutineScheduler } from '@/services/routineScheduler'

const route = useRoute()
const authStore = useAuthStore()

const isAuthPage = computed(() => {
  const authRoutes = ['login', 'register', 'verify', 'recover']
  return authRoutes.includes(route.name)
})

useRoutineScheduler()

onMounted(() => {
  authStore.initializeAuth()
})
</script>
