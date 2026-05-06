<template>
  <TopBar v-if="!isAuthPage" />
  <router-view />
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from '@/components/layout/TopBar.vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAuthStore()

const isAuthPage = computed(() => {
  const authRoutes = ['login', 'register', 'verify', 'recover']
  return authRoutes.includes(route.name)
})

onMounted(() => {
  authStore.initializeAuth()
})
</script>
