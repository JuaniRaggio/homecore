<template>
  <SideBar />
  <main class="page-content">
    <router-view :key="homeId" />
  </main>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SideBar from '@/components/layout/SideBar.vue'

const route = useRoute()
const homeId = computed(() => route.params.homeId)
</script>
