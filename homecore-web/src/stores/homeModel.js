import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRoomsStore } from './rooms'
import { floorPlans, getPlanBounds } from '../components/home3d/floorPlans.js'
import { computeAutoPosition } from '../components/home3d/roomPlacement.js'

export const useHomeModelStore = defineStore('homeModel', () => {
  const selectedModel = ref('casa')

  // Array of room layouts currently placed in the 3D scene
  const roomLayouts = ref([])

  // Multi-floor state
  const activeFloor = ref(0)
  const floorCount = ref(1)

  // Exterior configuration (perimeter padding + exterior device anchors)
  const exteriorConfig = ref({
    perimeterPadding: 1.5,
    deviceAnchors: {}
  })

  /**
   * Rooms on the currently active floor.
   */
  const activeFloorLayouts = computed(() =>
    roomLayouts.value.filter(r => r.floor === activeFloor.value)
  )

  /**
   * Computed plan object with the same shape as the hardcoded presets.
   * rooms = only the active floor's rooms (for rendering).
   * _allRooms = every room across all floors (for alarm perimeter bounds).
   */
  const currentPlan = computed(() => {
    return {
      rooms: activeFloorLayouts.value,
      exterior: exteriorConfig.value,
      _allRooms: roomLayouts.value
    }
  })

  /**
   * Load a preset floor plan into roomLayouts and exteriorConfig.
   */
  function loadPreset(modelName) {
    const preset = floorPlans[modelName]
    if (!preset) return

    // Deep copy rooms from preset (backwards-compatible: default floor 0)
    roomLayouts.value = preset.rooms.map(r => ({
      roomId: r.roomId,
      label: r.label,
      x: r.x,
      z: r.z,
      width: r.width,
      depth: r.depth,
      wallHeight: r.wallHeight,
      walls: { ...r.walls },
      deviceAnchors: r.deviceAnchors ? { ...r.deviceAnchors } : undefined,
      floor: r.floor ?? 0
    }))

    // Deep copy exterior
    exteriorConfig.value = {
      perimeterPadding: preset.exterior.perimeterPadding,
      deviceAnchors: { ...preset.exterior.deviceAnchors }
    }

    // Reset floor state
    activeFloor.value = 0
    const maxFloor = roomLayouts.value.reduce((max, r) => Math.max(max, r.floor), 0)
    floorCount.value = maxFloor + 1
  }

  /**
   * Switch to a different model and load its preset.
   */
  function setModel(model) {
    selectedModel.value = model
    // Load preset if available, otherwise use casa as fallback
    const preset = floorPlans[model]
    if (preset) {
      loadPreset(model)
    } else {
      loadPreset('casa')
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

    // Only consider rooms on the same floor for auto-positioning
    const sameFloorLayouts = roomLayouts.value.filter(r => r.floor === activeFloor.value)
    const pos = computeAutoPosition(sameFloorLayouts, defaultWidth, defaultDepth)

    roomLayouts.value.push({
      roomId: room.id,
      label: room.name,
      x: pos.x,
      z: pos.z,
      width: defaultWidth,
      depth: defaultDepth,
      wallHeight: 2.8,
      walls: { north: true, south: true, east: true, west: true },
      floor: activeFloor.value
    })
  }

  /**
   * Remove a room from the 3D layout.
   */
  function removeRoomFromLayout(roomId) {
    roomLayouts.value = roomLayouts.value.filter(r => r.roomId !== roomId)
  }

  /**
   * Switch to a specific floor.
   */
  function setActiveFloor(index) {
    if (index >= 0 && index < floorCount.value) {
      activeFloor.value = index
    }
  }

  /**
   * Add a new floor and switch to it.
   */
  function addFloor() {
    floorCount.value++
    activeFloor.value = floorCount.value - 1
  }

  /**
   * Remove a floor only if it has no rooms.
   * Adjusts floor indices for rooms on higher floors.
   */
  function removeFloor(index) {
    const roomsOnFloor = roomLayouts.value.filter(r => r.floor === index)
    if (roomsOnFloor.length > 0) return
    if (floorCount.value <= 1) return

    // Adjust floor indices for rooms above the removed floor
    for (const r of roomLayouts.value) {
      if (r.floor > index) {
        r.floor--
      }
    }

    floorCount.value--

    // Adjust active floor if needed
    if (activeFloor.value >= floorCount.value) {
      activeFloor.value = floorCount.value - 1
    } else if (activeFloor.value > index) {
      activeFloor.value--
    }
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
    activeFloor,
    floorCount,
    activeFloorLayouts,
    exteriorConfig,
    currentPlan,
    setModel,
    loadPreset,
    addRoomToLayout,
    removeRoomFromLayout,
    setActiveFloor,
    addFloor,
    removeFloor,
    initialize
  }
})
