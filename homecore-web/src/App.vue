<template>
  <!-- Layout principal: topbar fija + sidebar fijo + contenido con router-view -->
  <TopBar />
  <SideBar />
  <main class="page-content">
    <router-view />
  </main>
</template>

<script setup>
import TopBar from '@/components/layout/TopBar.vue'
import SideBar from '@/components/layout/SideBar.vue'
</script>

<style>
/*
  El layout general se maneja con CSS global (main.css).
  .page-content ya tiene el margin-left y margin-top necesarios
  para no quedar debajo del topbar/sidebar.
*/
</style>
