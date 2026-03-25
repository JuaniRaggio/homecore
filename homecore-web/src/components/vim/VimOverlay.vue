<template>
  <div class="vim-layer">
    <!-- CRT Scanlines -->
    <div v-if="vim.showCrt.value" class="vim-scanlines"></div>
    <div v-if="vim.showCrt.value" class="vim-vignette"></div>

    <!-- Matrix Rain -->
    <canvas v-show="vim.showMatrix.value" ref="matrixCanvas" class="vim-matrix"></canvas>

    <!-- Mode flash on change -->
    <Transition name="vim-flash">
      <div class="vim-mode-flash" :class="'vim-mode-flash--' + vim.mode.value.toLowerCase()" :key="vim.mode.value"></div>
    </Transition>

    <!-- Help overlay -->
    <Teleport to="body">
      <div v-if="vim.showHelp.value" class="vim-help-backdrop" @click="vim.showHelp.value = false">
        <div class="vim-help-window" @click.stop>
          <pre class="vim-help-text">{{ helpText }}</pre>
        </div>
      </div>
    </Teleport>

    <!-- Status Line -->
    <div class="vim-statusline">
      <span class="vim-sl__mode" :class="'vim-sl__mode--' + vim.mode.value.toLowerCase()">
        {{ vim.modeDisplay.value }}
      </span>
      <span class="vim-sl__file">{{ vim.routeFile.value }}</span>
      <span class="vim-sl__msg" :class="'vim-sl__msg--' + vim.statusType.value">
        {{ vim.statusMessage.value }}
      </span>
      <span class="vim-sl__right">
        <span class="vim-sl__profile">{{ profileLabel }}</span>
        <span class="vim-sl__pos">{{ (vim.focusIndex.value + 1) || 0 }}:1</span>
      </span>
    </div>
  </div>
</template>

<script setup>
import { inject, computed, ref, watch, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../../stores/auth'

const vim = inject('vim')
const authStore = useAuthStore()
const matrixCanvas = ref(null)
let matrixInterval = null

const profileLabel = computed(() => {
  const p = authStore.activeProfile
  return p.name + ' [' + p.role + ']'
})

const helpText = `
  _   _                       ____
 | | | | ___  _ __ ___   ___ / ___|___  _ __ ___
 | |_| |/ _ \\| '_ \` _ \\ / _ \\ |   / _ \\| '__/ _ \\
 |  _  | (_) | | | | | |  __/ |__| (_) | | |  __/
 |_| |_|\\___/|_| |_| |_|\\___|\\____\\___/|_|  \\___|
                                      vim mode

 NAVIGATION                        GOTO  (g...)
 ----------                        -----------
 j / k      Focus next / prev      gh  Dashboard     gd  Dispositivos
 h / l      Focus left / right     gr  Habitaciones  gt  Rutinas
 Enter      Activate focused       gi  Historial     gc  Consumo
 Space      Activate focused       gs  Configuracion
 gg         First element          G   Last element
 0          Dashboard
 1-6        Sidebar item #

 MODES                             ACTIONS
 -----                             -------
 i          INSERT mode            p   Cycle profile
 :          COMMAND mode           t   Toggle focused device
 Esc        NORMAL / close         u   Undo (go back)
 ?          Toggle this help       /   Search (WIP)

 COMMANDS (:)
 -----------
 :q         Logout                 :crt      Toggle CRT effects
 :wq        Save & quit            :matrix   Toggle matrix rain
 :w         Save (fake)            :profile <name>  Switch profile
 :help      Show help              :<n>      Jump to element #n
 :!cmd      No shell ;)            :sudo rm -rf /   Try it.

                              Press ? or Esc to close
`

// --- Matrix rain ---
function startMatrix() {
  const canvas = matrixCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight

  const fontSize = 14
  const columns = Math.floor(canvas.width / fontSize)
  const drops = new Array(columns).fill(1)
  const chars = 'アイウエオカキクケコサシスセソタチツテト0123456789ABCDEF{}[]<>/\\|=+-_*&^%$#@!~'

  function draw() {
    ctx.fillStyle = 'rgba(15, 15, 20, 0.05)'
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    ctx.font = fontSize + 'px monospace'

    for (let i = 0; i < drops.length; i++) {
      const char = chars[Math.floor(Math.random() * chars.length)]
      // Alternate between indigo and amber for HomeCore palette
      ctx.fillStyle = Math.random() > 0.85 ? '#f59e0b' : '#6366f1'
      ctx.fillText(char, i * fontSize, drops[i] * fontSize)

      if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
        drops[i] = 0
      }
      drops[i]++
    }
  }

  matrixInterval = setInterval(draw, 50)
}

function stopMatrix() {
  if (matrixInterval) { clearInterval(matrixInterval); matrixInterval = null }
  const canvas = matrixCanvas.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx.clearRect(0, 0, canvas.width, canvas.height)
  }
}

watch(() => vim.showMatrix.value, (val) => {
  if (val) startMatrix()
  else stopMatrix()
})

function handleResize() {
  if (matrixCanvas.value && vim.showMatrix.value) {
    matrixCanvas.value.width = window.innerWidth
    matrixCanvas.value.height = window.innerHeight
  }
}

onMounted(() => window.addEventListener('resize', handleResize))
onUnmounted(() => { window.removeEventListener('resize', handleResize); stopMatrix() })
</script>

<style>
/* ============================================
   VIM FOCUS GLOW (global, not scoped)
   ============================================ */
.vim-focused {
  outline: 2px solid var(--hc-accent) !important;
  outline-offset: 2px;
  box-shadow: 0 0 12px rgba(99, 102, 241, 0.4), 0 0 24px rgba(99, 102, 241, 0.15) !important;
  transition: outline 100ms ease, box-shadow 100ms ease;
  position: relative;
}

.vim-focused::after {
  content: attr(data-vim-idx);
  position: absolute;
  top: -8px;
  left: -8px;
  background: var(--hc-accent);
  color: white;
  font-size: 10px;
  font-weight: 700;
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', monospace;
  padding: 0 4px;
  border-radius: 3px;
  line-height: 16px;
  z-index: 100;
}
</style>

<style scoped>
/* ============================================
   CRT EFFECTS
   ============================================ */
.vim-scanlines {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9998;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0.03) 0px,
    rgba(0, 0, 0, 0.03) 1px,
    transparent 1px,
    transparent 3px
  );
  animation: scanlineFlicker 8s linear infinite;
}

.vim-vignette {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9997;
  background: radial-gradient(
    ellipse at center,
    transparent 55%,
    rgba(0, 0, 0, 0.35) 100%
  );
}

@keyframes scanlineFlicker {
  0%, 100% { opacity: 0.8; }
  25% { opacity: 0.7; }
  50% { opacity: 0.9; }
  75% { opacity: 0.75; }
}

/* ============================================
   MATRIX RAIN
   ============================================ */
.vim-matrix {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9996;
  opacity: 0.3;
}

/* ============================================
   MODE FLASH
   ============================================ */
.vim-mode-flash {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9995;
  opacity: 0;
}

.vim-flash-enter-active {
  transition: opacity 80ms ease-out;
}

.vim-flash-enter-from {
  opacity: 1;
}

.vim-mode-flash--normal { background: rgba(99, 102, 241, 0.08); }
.vim-mode-flash--insert { background: rgba(34, 197, 94, 0.08); }
.vim-mode-flash--command { background: rgba(245, 158, 11, 0.08); }

/* ============================================
   HELP OVERLAY
   ============================================ */
.vim-help-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  z-index: 10001;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 150ms ease;
}

.vim-help-window {
  background: #0d0d12;
  border: 1px solid #6366f1;
  border-radius: 6px;
  box-shadow: 0 0 60px rgba(99, 102, 241, 0.25), 0 0 120px rgba(99, 102, 241, 0.08);
  max-width: 920px;
  width: 94vw;
  max-height: 88vh;
  overflow-y: auto;
  padding: 0;
}

.vim-help-text {
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', 'Consolas', monospace;
  font-size: 15px;
  line-height: 1.6;
  color: #c8c8d8;
  padding: 32px 40px;
  margin: 0;
  white-space: pre;
  overflow-x: auto;
}

/* ============================================
   STATUS LINE
   ============================================ */
.vim-statusline {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: var(--hc-statusline-height);
  background: #111118;
  border-top: 1px solid #2e2e3e;
  display: flex;
  align-items: center;
  gap: 0;
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', 'Consolas', monospace;
  font-size: 12px;
  z-index: 10000;
  color: #64748b;
  user-select: none;
}

.vim-sl__mode {
  padding: 0 12px;
  height: 100%;
  display: flex;
  align-items: center;
  font-weight: 700;
  font-size: 11px;
  letter-spacing: 0.05em;
  min-width: 120px;
}

.vim-sl__mode--normal {
  background: rgba(99, 102, 241, 0.2);
  color: #818cf8;
}

.vim-sl__mode--insert {
  background: rgba(34, 197, 94, 0.2);
  color: #4ade80;
}

.vim-sl__mode--command {
  background: rgba(245, 158, 11, 0.2);
  color: #fbbf24;
  font-weight: 400;
}

.vim-sl__file {
  padding: 0 12px;
  color: #94a3b8;
  border-right: 1px solid #2e2e3e;
  height: 100%;
  display: flex;
  align-items: center;
}

.vim-sl__msg {
  flex: 1;
  padding: 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.vim-sl__msg--success { color: #4ade80; }
.vim-sl__msg--warning { color: #fbbf24; }
.vim-sl__msg--error { color: #f87171; }
.vim-sl__msg--info { color: #94a3b8; }

.vim-sl__right {
  display: flex;
  align-items: center;
  height: 100%;
  margin-left: auto;
}

.vim-sl__profile {
  padding: 0 12px;
  border-left: 1px solid #2e2e3e;
  height: 100%;
  display: flex;
  align-items: center;
  color: #94a3b8;
}

.vim-sl__pos {
  padding: 0 12px;
  border-left: 1px solid #2e2e3e;
  height: 100%;
  display: flex;
  align-items: center;
  color: #64748b;
  min-width: 60px;
  justify-content: flex-end;
}
</style>
