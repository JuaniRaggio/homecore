import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'
import { normalizeDevice, calcConsumption } from '@/utils/device-helpers'
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
    const consumption = calcConsumption(devices, deviceTypes.value)

    const alarms = devices.filter(d => d.type === 'alarm')
    let alarmStatus = 'none'
    if (alarms.length > 0) {
      const armed = alarms.filter(a => a.isOn).length
      if (armed === 0) alarmStatus = 'disarmed'
      else if (armed === alarms.length) alarmStatus = 'armed'
      else alarmStatus = 'partial'
    }

    return {
      ...home,
      totalDevices: devices.length,
      activeDevices: active.length,
      consumption: Math.round(consumption),
      alarmStatus,
    }
  }

  const criticalDevices = computed(() =>
    allDevices.value.filter(d => {
      if (d.type === 'alarm') return true
      if (d.type === 'door' && d.statusText === 'Abierta') return true
      return false
    })
  )

  /**
   * Resumen de alarmas agrupadas por casa.
   * Cada entrada: { homeId, homeName, allArmed, alarms: [{ name, room, isOn }] }
   */
  function getAlarmSummary(homes) {
    return homes
      .map(home => {
        const devices = devicesByHome.value[home.id] || []
        const alarms = devices.filter(d => d.type === 'alarm')
        if (alarms.length === 0) return null
        const allArmed = alarms.every(a => a.isOn)
        return {
          homeId: home.id,
          homeName: home.name,
          allArmed,
          alarms: alarms.map(a => ({ name: a.name, room: a.room, isOn: a.isOn })),
        }
      })
      .filter(Boolean)
  }

  const totalConsumption = computed(() =>
    calcConsumption(allDevices.value, deviceTypes.value)
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
        api.getDeviceTypes().catch(e => { console.error('[overview] Error cargando tipos de dispositivo:', e); return [] }),
        api.getAllRooms().catch(e => { console.error('[overview] Error cargando habitaciones:', e); return [] }),
      ])
      deviceTypes.value = types

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
              } catch (e) {
                console.error(`[overview] Error cargando dispositivos de habitacion ${room.name}:`, e)
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
    getAlarmSummary,
  }
}
