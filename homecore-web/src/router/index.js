import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

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
    path: '/',
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
        path: 'rutinas/nueva',
        name: 'new-routine',
        component: () => import('../views/NewRoutineView.vue')
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
        component: () => import('../views/SettingsView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Auth guard disabled - auto-login enabled for prototype
// router.beforeEach((to) => {
//   const authStore = useAuthStore()
//   if (!to.meta.public && !authStore.isAuthenticated) {
//     return { name: 'login' }
//   }
// })

export default router
