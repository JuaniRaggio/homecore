<template>
  <SplashScreen v-if="showSplash" @done="showSplash = false" />
  <router-view v-else />
</template>

<script setup>
import { ref } from 'vue'
import SplashScreen from './components/SplashScreen.vue'

const showSplash = ref(true)
</script>
