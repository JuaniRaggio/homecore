import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/views/HomeView.vue'

// Lazy-load de las vistas secundarias para no cargar todo junto
const DevicesView = () => import('@/views/DevicesView.vue')
const RoomsView = () => import('@/views/RoomsView.vue')
const RoutinesView = () => import('@/views/RoutinesView.vue')
const HistoryView = () => import('@/views/HistoryView.vue')
const ConsumptionView = () => import('@/views/ConsumptionView.vue')
const SettingsView = () => import('@/views/SettingsView.vue')

const routes = [
  // {
  //   path: '/',
  //   name: 'overview',
  //   component: OverviewView
  // },
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/dispositivos',
    name: 'devices',
    component: DevicesView
  },
  {
    path: '/habitaciones',
    name: 'rooms',
    component: RoomsView
  },
  {
    path: '/rutinas',
    name: 'routines',
    component: RoutinesView
  },
  {
    path: '/historial',
    name: 'history',
    component: HistoryView
  },
  {
    path: '/consumo',
    name: 'consumption',
    component: ConsumptionView
  },
  {
    path: '/configuracion',
    name: 'settings',
    component: SettingsView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
