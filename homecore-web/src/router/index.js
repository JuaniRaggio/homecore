import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useHomesStore } from '../stores/homes'
import AppLayout from '../components/layout/AppLayout.vue'
import OverviewLayout from '../components/layout/OverviewLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/registro',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
    meta: { public: true }
  },
  {
    path: '/verificar',
    name: 'verify',
    component: () => import('../views/VerifyView.vue'),
    meta: { public: true }
  },
  {
    path: '/recuperar',
    name: 'recover',
    component: () => import('../views/RecoverView.vue'),
    meta: { public: true }
  },
  {
    path: '/overview',
    component: OverviewLayout,
    children: [
      {
        path: '',
        name: 'overview',
        component: () => import('../views/OverviewView.vue')
      }
    ]
  },
  {
    path: '/nueva-propiedad',
    component: OverviewLayout,
    children: [
      {
        path: '',
        name: 'new-property',
        component: () => import('../views/NewPropertyView.vue')
      }
    ]
  },
  {
    path: '/:houseId',
    component: AppLayout,
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('../views/DashboardView.vue')
      },
      {
        path: 'dispositivos',
        name: 'devices',
        component: () => import('../views/DevicesView.vue')
      },
      {
        path: 'dispositivos/nuevo',
        name: 'new-device',
        component: () => import('../views/NewDeviceView.vue'),
        meta: { adminOnly: true }
      },
      {
        path: 'dispositivos/:id',
        name: 'device-detail',
        component: () => import('../views/DeviceDetailView.vue')
      },
      {
        path: 'habitaciones',
        name: 'rooms',
        component: () => import('../views/RoomsView.vue')
      },
      {
        path: 'rutinas',
        name: 'routines',
        component: () => import('../views/RoutinesView.vue')
      },
      {
        path: 'rutinas/:id',
        name: 'routine-detail',
        component: () => import('../views/RoutineDetailView.vue')
      },
      {
        path: 'rutinas/nueva',
        name: 'new-routine',
        component: () => import('../views/NewRoutineView.vue'),
        meta: { adminOnly: true }
      },
      {
        path: 'historial',
        name: 'history',
        component: () => import('../views/HistoryView.vue')
      },
      {
        path: 'consumo',
        name: 'consumption',
        component: () => import('../views/ConsumptionView.vue')
      },
      {
        path: 'configuracion',
        name: 'settings',
        component: () => import('../views/SettingsView.vue'),
        meta: { adminOnly: true }
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

  if (to.meta.adminOnly && !authStore.isAdmin) {
    return { name: 'dashboard', params: { houseId: to.params.houseId } }
  }

  if (to.params.houseId) {
    const homesStore = useHomesStore()
    homesStore.syncFromRoute(to.params.houseId)

    if (!homesStore.homeExists(to.params.houseId)) {
      return '/overview'
    }
  }
})

export default router
