import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'

export function useHomeData() {
  const route = useRoute()
  const devicesStore = useDevicesStore()
  const roomsStore = useRoomsStore()

  const homeId = computed(() => route.params.homeId)

  function fetchHomeData() {
    if (!homeId.value) return
    devicesStore.fetchAllForHome(homeId.value)
    devicesStore.fetchDeviceTypes()
    roomsStore.fetchRooms(homeId.value)
  }

  onMounted(fetchHomeData)

  return { homeId, devicesStore, roomsStore }
}
