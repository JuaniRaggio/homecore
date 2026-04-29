<template>
  <SideBar />
  <main class="page-content">
    <router-view />
  </main>
</template>

<script setup>
import SideBar from '@/components/layout/SideBar.vue'
</script>
