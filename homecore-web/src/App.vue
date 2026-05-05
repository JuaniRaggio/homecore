<template>
  <TopBar v-if="!isAuthPage" />
  <router-view />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from '@/components/layout/TopBar.vue'

const route = useRoute()

const isAuthPage = computed(() => {
  const authRoutes = ['login', 'register', 'verify', 'recover']
  return authRoutes.includes(route.name)
})
</script>
