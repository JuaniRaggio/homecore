import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRoomsStore } from './rooms'
import { floorPlans, getPlanBounds } from '../components/home3d/floorPlans.js'
import { computeAutoPosition } from '../components/home3d/roomPlacement.js'

export const useHomeModelStore = defineStore('homeModel', () => {
  const selectedModel = ref('casa')

  // Array of room layouts currently placed in the 3D scene
  const roomLayouts = ref([])

  // Exterior configuration (perimeter padding + exterior device anchors)
  const exteriorConfig = ref({
    perimeterPadding: 1.5,
    deviceAnchors: {}
  })

  /**
   * Computed plan object with the same shape as the hardcoded presets.
   * Only depends on roomLayouts and exteriorConfig (not device state),
   * so it won't trigger unnecessary rebuilds.
   */
  const currentPlan = computed(() => {
    return {
      rooms: roomLayouts.value,
      exterior: exteriorConfig.value
    }
  })

  /**
   * Load a preset floor plan into roomLayouts and exteriorConfig.
   */
  function loadPreset(modelName) {
    const preset = floorPlans[modelName]
    if (!preset) return

    // Deep copy rooms from preset
    roomLayouts.value = preset.rooms.map(r => ({
      roomId: r.roomId,
      label: r.label,
      x: r.x,
      z: r.z,
      width: r.width,
      depth: r.depth,
      wallHeight: r.wallHeight,
      walls: { ...r.walls },
      deviceAnchors: r.deviceAnchors ? { ...r.deviceAnchors } : undefined
    }))

    // Deep copy exterior
    exteriorConfig.value = {
      perimeterPadding: preset.exterior.perimeterPadding,
      deviceAnchors: { ...preset.exterior.deviceAnchors }
    }
  }

  /**
   * Switch to a different model and load its preset.
   */
  function setModel(model) {
    if (model === 'casa' || model === 'departamento') {
      selectedModel.value = model
      loadPreset(model)
    }
  }

  /**
   * Add a room from the rooms store into the 3D layout.
   * Auto-positions it using the placement algorithm.
   * Rooms added this way don't have curated deviceAnchors.
   */
  function addRoomToLayout(roomId) {
    // Don't add if already in layout
    if (roomLayouts.value.find(r => r.roomId === roomId)) return

    const roomsStore = useRoomsStore()
    const room = roomsStore.getById(roomId)
    if (!room) return

    const defaultWidth = 3.5
    const defaultDepth = 3

    const pos = computeAutoPosition(roomLayouts.value, defaultWidth, defaultDepth)

    roomLayouts.value.push({
      roomId: room.id,
      label: room.name,
      x: pos.x,
      z: pos.z,
      width: defaultWidth,
      depth: defaultDepth,
      wallHeight: 2.8,
      walls: { north: true, south: true, east: true, west: true }
      // No deviceAnchors -- HomeScene will auto-compute them
    })
  }

  /**
   * Remove a room from the 3D layout.
   */
  function removeRoomFromLayout(roomId) {
    roomLayouts.value = roomLayouts.value.filter(r => r.roomId !== roomId)
  }

  /**
   * Initialize the store: load the currently selected preset.
   */
  function initialize() {
    loadPreset(selectedModel.value)
  }

  return {
    selectedModel,
    roomLayouts,
    exteriorConfig,
    currentPlan,
    setModel,
    loadPreset,
    addRoomToLayout,
    removeRoomFromLayout,
    initialize
  }
})
