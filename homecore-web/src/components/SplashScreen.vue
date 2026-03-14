<template>
  <div
    ref="splashRef"
    class="splash"
    :class="{ 'splash--exiting': exiting }"
    @wheel="skipSplash"
    @touchstart="onTouchStart"
    @touchmove="onTouchMove"
    @click="skipSplash"
  >
    <div class="splash__content" :style="contentStyle">
      <div class="splash__logo">
        <HcLogo size="xl" />
      </div>
      <h1 class="splash__title">HomeCore</h1>
      <p class="splash__tagline">Tu hogar inteligente</p>
    </div>

    <p class="splash__hint" :class="{ 'splash__hint--visible': hintVisible }">
      Scroll o click para continuar
    </p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import HcLogo from './ui/HcLogo.vue'

const emit = defineEmits(['done'])

const splashRef = ref(null)
const exiting = ref(false)
const hintVisible = ref(false)
const progress = ref(0)
const touchStartY = ref(0)
let animFrame = null
let hintTimeout = null

const contentStyle = computed(() => {
  const scale = 1 + progress.value * 8
  const opacity = 1 - progress.value * 1.2
  return {
    transform: `scale(${scale})`,
    opacity: Math.max(0, opacity)
  }
})

function skipSplash() {
  if (exiting.value) return
  startExit()
}

function onTouchStart(e) {
  touchStartY.value = e.touches[0].clientY
}

function onTouchMove(e) {
  const diff = touchStartY.value - e.touches[0].clientY
  if (Math.abs(diff) > 30) {
    skipSplash()
  }
}

function startExit() {
  exiting.value = true
  const startTime = performance.now()
  const duration = 800

  function animate(now) {
    const elapsed = now - startTime
    const t = Math.min(elapsed / duration, 1)
    const eased = t * t * (3 - 2 * t)
    progress.value = eased

    if (t < 1) {
      animFrame = requestAnimationFrame(animate)
    } else {
      emit('done')
    }
  }
  animFrame = requestAnimationFrame(animate)
}

onMounted(() => {
  hintTimeout = setTimeout(() => {
    hintVisible.value = true
  }, 1500)
})

onUnmounted(() => {
  if (animFrame) cancelAnimationFrame(animFrame)
  if (hintTimeout) clearTimeout(hintTimeout)
})
</script>

<style scoped>
.splash {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: linear-gradient(135deg, #0a0a12 0%, #0f0f1a 40%, #0d0d18 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
}

.splash__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.25rem;
  transform-origin: center center;
  will-change: transform, opacity;
}

.splash__logo {
  width: 120px;
  height: 140px;
  animation: logoAppear 1s ease-out;
}

.splash__logo svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 0 30px rgba(55, 138, 221, 0.3));
}

.splash__title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 0.05em;
  animation: textAppear 1s ease-out 0.3s both;
}

.splash__tagline {
  font-size: 1rem;
  color: #94a3b8;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  animation: textAppear 1s ease-out 0.6s both;
}

.splash__hint {
  position: absolute;
  bottom: 2.5rem;
  color: #64748b;
  font-size: 0.8rem;
  letter-spacing: 0.05em;
  opacity: 0;
  transition: opacity 0.8s ease;
  animation: none;
}

.splash__hint--visible {
  opacity: 1;
  animation: pulseHint 2s ease-in-out infinite;
}

@keyframes logoAppear {
  0% {
    opacity: 0;
    transform: scale(0.6);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes textAppear {
  0% {
    opacity: 0;
    transform: translateY(16px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseHint {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}
</style>
