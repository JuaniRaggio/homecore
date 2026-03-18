<template>
  <div
    ref="containerRef"
    class="home-scene"
    data-vim="home3d"
    @mousemove="onMouseMove"
    @click="onClick"
  >
    <!-- Tooltip -->
    <div
      v-if="tooltip.visible"
      class="home-scene__tooltip"
      :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }"
    >
      <span class="home-scene__tooltip-name">{{ tooltip.name }}</span>
      <span class="home-scene__tooltip-status">{{ tooltip.status }}</span>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="home-scene__loading">
      <span>Cargando vista 3D...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as THREE from 'three'
import { useDevicesStore } from '../../stores/devices'
import { useHomeModelStore } from '../../stores/homeModel'
import { useHomeScene } from './useHomeScene.js'
import { floorPlans, getPlanBounds } from './floorPlans.js'
import { buildAllRooms, setRoomHighlight } from './roomMeshBuilder.js'
import {
  createDeviceMarker,
  updateDeviceMarkerState,
  animateAlarmPulse,
  animateWaterDrops
} from './deviceMarkers.js'

const router = useRouter()
const devicesStore = useDevicesStore()
const homeModelStore = useHomeModelStore()

const containerRef = ref(null)
const loading = ref(true)
const tooltip = reactive({
  visible: false,
  x: 0,
  y: 0,
  name: '',
  status: ''
})

const { scene, init, dispose, onResize, getCamera, getRenderer } = useHomeScene(containerRef)

const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()

let roomGroups = []
let deviceGroups = []
let hoveredRoomId = null
let animationFrameId = null
let baseMouseX = 0
let baseMouseY = 0

// ---- Build floor plan ----

function clearScene() {
  for (const g of roomGroups) {
    scene.remove(g)
  }
  for (const g of deviceGroups) {
    scene.remove(g)
  }
  roomGroups = []
  deviceGroups = []
}

function buildFloorPlan(modelName) {
  clearScene()
  const plan = floorPlans[modelName]
  if (!plan) return

  // Build rooms
  const rooms = buildAllRooms(plan)
  for (const room of rooms) {
    scene.add(room)
    roomGroups.push(room)
  }

  // Build device markers
  buildDeviceMarkers(plan)
}

function buildDeviceMarkers(plan) {
  // Remove old device groups
  for (const g of deviceGroups) {
    scene.remove(g)
  }
  deviceGroups = []

  const devices = devicesStore.devices

  // Room device anchors
  for (const roomDef of plan.rooms) {
    if (!roomDef.deviceAnchors) continue
    for (const [deviceId, anchor] of Object.entries(roomDef.deviceAnchors)) {
      const device = devicesStore.getById(deviceId)
      if (!device) continue
      const marker = createDeviceMarker(device, anchor, plan)
      if (marker) {
        scene.add(marker)
        deviceGroups.push(marker)
      }
    }
  }

  // Exterior device anchors
  if (plan.exterior && plan.exterior.deviceAnchors) {
    for (const [deviceId, anchor] of Object.entries(plan.exterior.deviceAnchors)) {
      if (!anchor) continue
      const device = devicesStore.getById(deviceId)
      if (!device) continue

      if (anchor.type === 'perimeter') {
        const marker = createDeviceMarker(device, anchor, plan)
        if (marker) {
          scene.add(marker)
          deviceGroups.push(marker)
        }
      } else {
        const marker = createDeviceMarker(device, anchor, plan)
        if (marker) {
          scene.add(marker)
          deviceGroups.push(marker)
        }
      }
    }
  }
}

// ---- Update device states reactively ----

function updateDeviceMarkers() {
  for (const group of deviceGroups) {
    const deviceId = group.userData.deviceId
    if (!deviceId) continue
    const device = devicesStore.getById(deviceId)
    if (!device) continue
    updateDeviceMarkerState(group, device)
  }
}

// ---- Raycasting / Interaction ----

function getMouseNDC(event) {
  const rect = containerRef.value.getBoundingClientRect()
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
}

function raycast(event) {
  const camera = getCamera()
  if (!camera) return null

  getMouseNDC(event)
  raycaster.setFromCamera(mouse, camera)

  // Check device meshes first
  const deviceMeshes = []
  for (const g of deviceGroups) {
    g.traverse((child) => {
      if (child.isMesh) deviceMeshes.push(child)
    })
  }
  const deviceHits = raycaster.intersectObjects(deviceMeshes, false)
  if (deviceHits.length > 0) {
    const hit = deviceHits[0].object
    let deviceId = hit.userData.deviceId
    if (!deviceId && hit.parent) {
      deviceId = hit.parent.userData.deviceId
    }
    if (deviceId) {
      return { type: 'device', deviceId, point: deviceHits[0].point }
    }
  }

  // Check room floors
  const floorMeshes = []
  for (const g of roomGroups) {
    g.traverse((child) => {
      if (child.isMesh && child.userData.isFloor) floorMeshes.push(child)
    })
  }
  const floorHits = raycaster.intersectObjects(floorMeshes, false)
  if (floorHits.length > 0) {
    const roomId = floorHits[0].object.userData.roomId
    return { type: 'room', roomId, point: floorHits[0].point }
  }

  return null
}

function onMouseMove(event) {
  const hit = raycast(event)
  const container = containerRef.value
  if (!container) return

  // Parallax effect
  const rect = container.getBoundingClientRect()
  baseMouseX = ((event.clientX - rect.left) / rect.width - 0.5) * 2
  baseMouseY = ((event.clientY - rect.top) / rect.height - 0.5) * 2

  // Reset previous hover
  if (hoveredRoomId) {
    const roomGroup = roomGroups.find(g => g.userData.roomId === hoveredRoomId)
    if (roomGroup) setRoomHighlight(roomGroup, false)
    hoveredRoomId = null
  }

  if (hit && hit.type === 'device') {
    container.style.cursor = 'pointer'
    const device = devicesStore.getById(hit.deviceId)
    if (device) {
      const camera = getCamera()
      const renderer = getRenderer()
      if (camera && renderer) {
        const screenPos = hit.point.clone().project(camera)
        const x = (screenPos.x * 0.5 + 0.5) * rect.width
        const y = (-screenPos.y * 0.5 + 0.5) * rect.height
        tooltip.visible = true
        tooltip.x = x + 12
        tooltip.y = y - 8
        tooltip.name = device.name
        tooltip.status = getDeviceStatusText(device)
      }
    }

    // Highlight room that device is in
    if (device && device.roomId) {
      const roomGroup = roomGroups.find(g => g.userData.roomId === device.roomId)
      if (roomGroup) {
        setRoomHighlight(roomGroup, true)
        hoveredRoomId = device.roomId
      }
    }
  } else if (hit && hit.type === 'room') {
    container.style.cursor = 'default'
    tooltip.visible = false
    const roomGroup = roomGroups.find(g => g.userData.roomId === hit.roomId)
    if (roomGroup) {
      setRoomHighlight(roomGroup, true)
      hoveredRoomId = hit.roomId
    }
  } else {
    container.style.cursor = 'default'
    tooltip.visible = false
  }
}

function onClick(event) {
  const hit = raycast(event)
  if (hit && hit.type === 'device') {
    router.push(`/dispositivos/${hit.deviceId}`)
  }
}

function getDeviceStatusText(device) {
  switch (device.type) {
    case 'lamp':
      return device.on ? `Encendida (${device.brightness}%)` : 'Apagada'
    case 'door':
      return device.locked ? 'Bloqueada' : 'Desbloqueada'
    case 'blinds':
      return `Posicion: ${device.position}%`
    case 'alarm':
      return device.armed ? 'Armada' : 'Desarmada'
    case 'faucet':
      return device.on ? `Abierto (${device.flow}%)` : 'Cerrado'
    default:
      return device.on ? 'Encendido' : 'Apagado'
  }
}

// ---- Animation loop for device-specific animations ----

function startDeviceAnimations() {
  function tick(time) {
    if (!containerRef.value) return
    animationFrameId = requestAnimationFrame(tick)

    if (document.hidden) return

    // Alarm pulse
    for (const g of deviceGroups) {
      if (g.userData.deviceType === 'alarm') {
        animateAlarmPulse(g, time)
      }
      if (g.userData.deviceType === 'faucet') {
        animateWaterDrops(g, time)
      }
    }

    // Subtle parallax on camera
    const camera = getCamera()
    if (camera) {
      camera.position.x = 10 + baseMouseX * 0.3
      camera.position.z = 10 + baseMouseY * 0.3
      camera.lookAt(0, 0, 0)
    }
  }
  animationFrameId = requestAnimationFrame(tick)
}

// ---- Watchers ----

watch(() => devicesStore.devices, () => {
  updateDeviceMarkers()
}, { deep: true })

watch(() => homeModelStore.selectedModel, (newModel) => {
  buildFloorPlan(newModel)
})

// ---- Lifecycle ----

let resizeObserver = null

onMounted(async () => {
  await nextTick()
  init()
  buildFloorPlan(homeModelStore.selectedModel)
  startDeviceAnimations()
  loading.value = false

  resizeObserver = new ResizeObserver(() => {
    onResize()
  })
  if (containerRef.value) {
    resizeObserver.observe(containerRef.value)
  }
})

onBeforeUnmount(() => {
  if (animationFrameId !== null) {
    cancelAnimationFrame(animationFrameId)
  }
  if (resizeObserver) {
    resizeObserver.disconnect()
  }
  dispose()
})
</script>

<style scoped>
.home-scene {
  position: relative;
  width: 100%;
  height: 360px;
  overflow: hidden;
}

.home-scene__tooltip {
  position: absolute;
  pointer-events: none;
  background: rgba(15, 15, 20, 0.92);
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 6px;
  padding: 6px 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  z-index: 10;
  max-width: 200px;
  transform: translateY(-100%);
}

.home-scene__tooltip-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--hc-text-primary, #f1f5f9);
}

.home-scene__tooltip-status {
  font-size: 11px;
  color: var(--hc-text-secondary, #94a3b8);
}

.home-scene__loading {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--hc-text-muted, #64748b);
  font-size: 14px;
}
</style>
