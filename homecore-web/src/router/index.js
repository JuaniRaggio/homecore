import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useHomesStore } from '@/stores/homes'

import HomeLayout from '@/components/layout/HomeLayout.vue'

const LoginView = () => import('@/views/LoginView.vue')
const RegisterView = () => import('@/views/RegisterView.vue')
const VerifyView = () => import('@/views/VerifyView.vue')
const RecoverView = () => import('@/views/RecoverView.vue')
const OverviewView = () => import('@/views/OverviewView.vue')
const NewPropertyView = () => import('@/views/NewPropertyView.vue')
const HomeView = () => import('@/views/HomeView.vue')
const DevicesView = () => import('@/views/DevicesView.vue')
const DeviceDetailView = () => import('@/views/DeviceDetailView.vue')
const RoomsView = () => import('@/views/RoomsView.vue')
const RoutinesView = () => import('@/views/RoutinesView.vue')
const NewRoutineView = () => import('@/views/NewRoutineView.vue')
const RoutineDetailView = () => import('@/views/RoutineDetailView.vue')
const EditRoutineView = () => import('@/views/EditRoutineView.vue')
const HistoryView = () => import('@/views/HistoryView.vue')
const ConsumptionView = () => import('@/views/ConsumptionView.vue')
const SettingsView = () => import('@/views/SettingsView.vue')
const EditDeviceView = () => import('@/views/EditDeviceView.vue')
const RoomDetailView = () => import('@/views/RoomDetailView.vue')

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true }
  },
  {
    path: '/registro',
    name: 'register',
    component: RegisterView,
    meta: { public: true }
  },
  {
    path: '/verificar',
    name: 'verify',
    component: VerifyView,
    meta: { public: true }
  },
  {
    path: '/recuperar',
    name: 'recover',
    component: RecoverView,
    meta: { public: true }
  },
  {
    path: '/overview',
    name: 'overview',
    component: OverviewView
  },
  {
    path: '/nueva-propiedad',
    name: 'new-property',
    component: NewPropertyView
  },
  {
    path: '/casa/:homeId',
    component: HomeLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: HomeView
      },
      {
        path: 'dispositivos',
        name: 'devices',
        component: DevicesView
      },
      {
        path: 'dispositivos/:id',
        name: 'device-detail',
        component: DeviceDetailView
      },
      {
        path: 'dispositivos/:id/editar',
        name: 'edit-device',
        component: EditDeviceView
      },
      {
        path: 'habitaciones',
        name: 'rooms',
        component: RoomsView
      },
      {
        path: 'habitaciones/:roomId',
        name: 'room-detail',
        component: RoomDetailView
      },
      {
        path: 'rutinas',
        name: 'routines',
        component: RoutinesView
      },
      {
        path: 'rutinas/nueva',
        name: 'new-routine',
        component: NewRoutineView
      },
      {
        path: 'rutinas/:routineId',
        name: 'routine-detail',
        component: RoutineDetailView
      },
      {
        path: 'rutinas/:routineId/editar',
        name: 'edit-routine',
        component: EditRoutineView
      },
      {
        path: 'historial',
        name: 'history',
        component: HistoryView
      },
      {
        path: 'consumo',
        name: 'consumption',
        component: ConsumptionView
      },
      {
        path: 'configuracion',
        name: 'settings',
        component: SettingsView
      }
    ]
  },
  {
    path: '/',
    redirect: '/overview'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.public) return

  if (!authStore.isAuthenticated) {
    return { name: 'login' }
  }

  if (to.params.homeId) {
    const homesStore = useHomesStore()
    homesStore.syncFromRoute(to.params.homeId)
  }
})

export default router
