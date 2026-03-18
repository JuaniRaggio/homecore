import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const FOCUSABLE = '.device-card, .routine-card, .sidebar__link, .header__icon-btn, .header__profile-btn, [data-vim]'

const ROUTE_FILES = {
  '/': 'dashboard.vue',
  '/dispositivos': 'devices.vue',
  '/habitaciones': 'rooms.vue',
  '/rutinas': 'routines.vue',
  '/historial': 'history.vue',
  '/consumo': 'consumption.vue',
  '/configuracion': 'settings.vue',
}

const NAV_ROUTES = ['/', '/dispositivos', '/habitaciones', '/rutinas', '/historial', '/consumo']

const GOTO_MAP = {
  h: '/',
  d: '/dispositivos',
  r: '/habitaciones',
  t: '/rutinas',
  i: '/historial',
  c: '/consumo',
  s: '/configuracion',
}

export function useVimMode() {
  const router = useRouter()
  const route = useRoute()
  const authStore = useAuthStore()

  // --- State ---
  const mode = ref('NORMAL')
  const commandBuffer = ref('')
  const showHelp = ref(false)
  const showMatrix = ref(false)
  const showCrt = ref(true)
  const focusIndex = ref(-1)
  const pendingKey = ref(null)
  const statusMessage = ref('')
  const statusType = ref('info')

  // --- Computed ---
  const modeDisplay = computed(() => {
    if (mode.value === 'COMMAND') return ':' + commandBuffer.value
    if (pendingKey.value === 'g') return 'g_'
    return '-- ' + mode.value + ' --'
  })

  const routeFile = computed(() => {
    if (route.path.startsWith('/dispositivos/')) return '~/hc/device-detail.vue'
    return '~/hc/' + (ROUTE_FILES[route.path] || route.path.slice(1) + '.vue')
  })

  // --- Helpers ---
  function getElements() {
    return Array.from(document.querySelectorAll(FOCUSABLE))
  }

  function clearFocus() {
    document.querySelectorAll('.vim-focused').forEach(el => el.classList.remove('vim-focused'))
    focusIndex.value = -1
  }

  function setFocus(idx) {
    const els = getElements()
    if (!els.length) return
    document.querySelectorAll('.vim-focused').forEach(el => el.classList.remove('vim-focused'))
    focusIndex.value = Math.max(0, Math.min(idx, els.length - 1))
    const el = els[focusIndex.value]
    if (el) {
      el.classList.add('vim-focused')
      el.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
    }
  }

  function moveFocus(dir) {
    const els = getElements()
    if (!els.length) return
    if (focusIndex.value < 0) { setFocus(0); return }
    if (dir === 'down' || dir === 'right') setFocus(focusIndex.value + 1)
    if (dir === 'up' || dir === 'left') setFocus(focusIndex.value - 1)
  }

  function activateFocused() {
    const el = getElements()[focusIndex.value]
    if (el) { el.click(); showStatus('Activated', 'success') }
  }

  function showStatus(msg, type = 'info') {
    statusMessage.value = msg
    statusType.value = type
    setTimeout(() => { if (statusMessage.value === msg) statusMessage.value = '' }, 3000)
  }

  // --- Goto handler ---
  function handleGoto(key) {
    pendingKey.value = null
    mode.value = 'NORMAL'
    if (key === 'g') { setFocus(0); showStatus('gg -- top', 'info'); return }
    if (GOTO_MAP[key]) { router.push(GOTO_MAP[key]); showStatus('go ' + GOTO_MAP[key], 'success'); return }
    showStatus('E: Unknown goto g' + key, 'error')
  }

  // --- Command execution ---
  function executeCommand(cmd) {
    const c = cmd.trim()
    if (c === 'q' || c === 'quit') {
      authStore.logout(); router.push('/login'); showStatus('Bye.', 'warning')
    } else if (c === 'help' || c === 'h') {
      showHelp.value = true
    } else if (c.startsWith('profile ')) {
      const name = c.slice(8)
      const p = authStore.familyProfiles.find(f =>
        f.name.toLowerCase().includes(name.toLowerCase()) || f.role === name
      )
      if (p) { authStore.switchProfile(p.id); showStatus('Profile: ' + p.name, 'success') }
      else showStatus('E: Unknown profile: ' + name, 'error')
    } else if (c === 'w' || c === 'write') {
      showStatus('"' + routeFile.value + '" written', 'info')
    } else if (c === 'wq') {
      showStatus('Goodbye.', 'info')
      setTimeout(() => { authStore.logout(); router.push('/login') }, 600)
    } else if (c === 'crt') {
      showCrt.value = !showCrt.value
      showStatus('CRT ' + (showCrt.value ? 'ON' : 'OFF'), 'info')
    } else if (c === 'matrix') {
      showMatrix.value = !showMatrix.value
      showStatus('Matrix ' + (showMatrix.value ? 'ON' : 'OFF'), 'info')
    } else if (c === 'sudo rm -rf /') {
      showStatus('E: Permission denied. Nice try.', 'error')
    } else if (c.startsWith('!')) {
      showStatus('E481: No shell access in HomeCore', 'error')
    } else if (c.match(/^[0-9]+$/)) {
      setFocus(parseInt(c) - 1); showStatus('Line ' + c, 'info')
    } else {
      showStatus('E492: Not a command: ' + c, 'error')
    }
  }

  // --- Main key handler ---
  function handleKeydown(e) {
    const isInput = ['INPUT', 'TEXTAREA', 'SELECT'].includes(e.target.tagName)

    // INSERT mode: only Escape exits
    if (mode.value === 'INSERT') {
      if (e.key === 'Escape') {
        e.preventDefault(); mode.value = 'NORMAL'
        if (isInput) e.target.blur()
      }
      return
    }

    // COMMAND mode: build command string
    if (mode.value === 'COMMAND') {
      e.preventDefault()
      if (e.key === 'Escape') { mode.value = 'NORMAL'; commandBuffer.value = ''; return }
      if (e.key === 'Enter') { executeCommand(commandBuffer.value); mode.value = 'NORMAL'; commandBuffer.value = ''; return }
      if (e.key === 'Backspace') { commandBuffer.value = commandBuffer.value.slice(0, -1); if (!commandBuffer.value) mode.value = 'NORMAL'; return }
      if (e.key.length === 1) commandBuffer.value += e.key
      return
    }

    // Pending 'g' key (GOTO)
    if (pendingKey.value === 'g') { e.preventDefault(); handleGoto(e.key); return }

    // NORMAL mode
    if (mode.value === 'NORMAL') {
      if (isInput && e.key !== 'Escape') return

      switch (e.key) {
        case 'j': case 'ArrowDown': e.preventDefault(); moveFocus('down'); break
        case 'k': case 'ArrowUp': e.preventDefault(); moveFocus('up'); break
        case 'h': case 'ArrowLeft': e.preventDefault(); moveFocus('left'); break
        case 'l': case 'ArrowRight': e.preventDefault(); moveFocus('right'); break
        case 'Enter': case ' ': e.preventDefault(); activateFocused(); break
        case 'i': e.preventDefault(); mode.value = 'INSERT'; break
        case ':': e.preventDefault(); mode.value = 'COMMAND'; commandBuffer.value = ''; break
        case '?': e.preventDefault(); showHelp.value = !showHelp.value; break
        case 'Escape': e.preventDefault(); showHelp.value = false; clearFocus(); break
        case 'g': e.preventDefault(); pendingKey.value = 'g'; break
        case 'G': e.preventDefault(); setFocus(getElements().length - 1); showStatus('G -- bottom', 'info'); break
        case 'u': e.preventDefault(); router.back(); showStatus('undo (back)', 'info'); break
        case 'p':
          e.preventDefault()
          const profiles = authStore.familyProfiles
          const ci = profiles.findIndex(pr => pr.id === authStore.activeProfile.id)
          authStore.switchProfile(profiles[(ci + 1) % profiles.length].id)
          showStatus('Profile: ' + authStore.activeProfile.name, 'success')
          break
        case 't':
          e.preventDefault()
          const focused = getElements()[focusIndex.value]
          if (focused) {
            const toggle = focused.querySelector('.hc-toggle__input')
            if (toggle) { toggle.click(); showStatus('Toggled', 'success') }
            else showStatus('No toggle here', 'warning')
          }
          break
        case '/': e.preventDefault(); showStatus('/ search -- not implemented yet', 'warning'); break
        case '0': e.preventDefault(); router.push('/'); showStatus('[0] Dashboard', 'success'); break
        case '1': case '2': case '3': case '4': case '5': case '6':
          e.preventDefault()
          if (NAV_ROUTES[parseInt(e.key) - 1]) {
            router.push(NAV_ROUTES[parseInt(e.key) - 1])
            showStatus('[' + e.key + '] ' + NAV_ROUTES[parseInt(e.key) - 1], 'success')
          }
          break
        default: break
      }
    }
  }

  // Auto-enter INSERT when clicking into inputs
  function handleFocusIn(e) {
    if (['INPUT', 'TEXTAREA', 'SELECT'].includes(e.target.tagName) && mode.value === 'NORMAL') {
      mode.value = 'INSERT'
    }
  }

  // Reset focus on route change
  watch(() => route.path, () => {
    nextTick(() => clearFocus())
  })

  onMounted(() => {
    window.addEventListener('keydown', handleKeydown)
    document.addEventListener('focusin', handleFocusIn)
  })

  onUnmounted(() => {
    window.removeEventListener('keydown', handleKeydown)
    document.removeEventListener('focusin', handleFocusIn)
  })

  return {
    mode, commandBuffer, showHelp, showMatrix, showCrt,
    focusIndex, pendingKey, statusMessage, statusType,
    modeDisplay, routeFile, showStatus, clearFocus,
  }
}
