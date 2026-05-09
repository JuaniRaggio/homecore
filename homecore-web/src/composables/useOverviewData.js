import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'
import { normalizeDevice } from '@/utils/device-helpers'
import { friendlyError } from '@/utils/friendly-error'

const MAX_CONCURRENT = 3

export function useOverviewData() {
  const devicesByHome = ref({})
  const deviceTypes = ref([])
  const loading = ref(false)
  const error = ref(null)

  const allDevices = computed(() =>
    Object.values(devicesByHome.value).flat()
  )

  function enrichHome(home) {
    const devices = devicesByHome.value[home.id] || []
    const active = devices.filter(d => d.isOn)
    const consumption = active.reduce((sum, d) => {
      const dt = deviceTypes.value.find(t => String(t.id) === String(d.typeId))
      return sum + (dt?.powerUsage ?? 0)
    }, 0)

    return {
      ...home,
      totalDevices: devices.length,
      activeDevices: active.length,
      consumption: Math.round(consumption),
    }
  }

  const criticalDevices = computed(() =>
    allDevices.value.filter(d => {
      if (d.type === 'alarm' && d.isOn) return true
      if (d.type === 'door' && d.statusText === 'Abierta') return true
      return false
    })
  )

  const totalConsumption = computed(() =>
    allDevices.value
      .filter(d => d.isOn)
      .reduce((sum, d) => {
        const dt = deviceTypes.value.find(t => String(t.id) === String(d.typeId))
        return sum + (dt?.powerUsage ?? 0)
      }, 0)
  )

  const totalActiveDevices = computed(() =>
    allDevices.value.filter(d => d.isOn).length
  )

  const totalDeviceCount = computed(() => allDevices.value.length)

  async function fetchAllHomesDevices(homes) {
    loading.value = true
    error.value = null
    const limit = pLimit(MAX_CONCURRENT)

    try {
      const [types, allRooms] = await Promise.all([
        api.getDeviceTypes().catch(() => []),
        api.getAllRooms().catch(() => []),
      ])
      deviceTypes.value = types

      // Agrupar rooms por homeId para evitar N llamadas a getRooms
      const roomsByHome = {}
      for (const room of allRooms) {
        const hid = String(room.home?.id)
        if (!roomsByHome[hid]) roomsByHome[hid] = []
        roomsByHome[hid].push(room)
      }

      const result = {}
      await Promise.all(
        homes.map(home => limit(async () => {
          const rooms = roomsByHome[String(home.id)] || []
          const roomBatches = await Promise.all(
            rooms.map(room => limit(async () => {
              try {
                const roomDevices = await api.getDevices(room.id)
                return roomDevices.map(d => normalizeDevice(d, room.name))
              } catch {
                return []
              }
            }))
          )
          result[home.id] = roomBatches.flat()
        }))
      )
      devicesByHome.value = result
    } catch (e) {
      error.value = friendlyError(e)
    } finally {
      loading.value = false
    }
  }

  return {
    devicesByHome,
    loading,
    error,
    allDevices,
    criticalDevices,
    totalConsumption,
    totalActiveDevices,
    totalDeviceCount,
    enrichHome,
    fetchAllHomesDevices,
  }
}
