<template>
  <div class="device-detail">
    <div class="detail-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h1 class="view-title">{{ device.name }}</h1>
      <span class="device-room">{{ device.room }}</span>
    </div>

    <div class="detail-body">
      <div class="status-card">
        <div class="status-row">
          <span class="status-label">Estado</span>
          <span class="status-value" :class="device.isOn ? 'status--on' : 'status--off'">
            {{ device.isOn ? 'Encendido' : 'Apagado' }}
          </span>
        </div>
        <ToggleSwitch :model-value="device.isOn" @update:model-value="togglePower" />
      </div>

      <div class="controls-card">
        <h2 class="controls-title">Controles</h2>

        <template v-if="device.type === 'light'">
          <div class="control-row">
            <span class="control-label">Brillo</span>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="brightness"
              class="slider"
            />
            <span class="control-value">{{ brightness }}%</span>
          </div>
          <div class="control-row">
            <span class="control-label">Color</span>
            <input type="color" v-model="color" class="color-picker" />
            <span class="control-value">{{ color }}</span>
          </div>
        </template>

        <template v-else-if="device.type === 'door'">
          <div class="control-row">
            <span class="control-label">Cerradura</span>
            <button class="btn-control" :class="locked ? 'btn-control--danger' : 'btn-control--success'" @click="locked = !locked">
              <i :class="locked ? 'fa-solid fa-lock' : 'fa-solid fa-lock-open'"></i>
              {{ locked ? 'Bloqueada' : 'Desbloqueada' }}
            </button>
          </div>
        </template>

        <template v-else-if="device.type === 'curtain'">
          <div class="control-row">
            <span class="control-label">Posicion</span>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="position"
              class="slider"
            />
            <span class="control-value">{{ position }}%</span>
          </div>
          <div class="control-row">
            <button class="btn-control btn-control--sm" @click="position = 0">Cerrar</button>
            <button class="btn-control btn-control--sm" @click="position = 50">Media</button>
            <button class="btn-control btn-control--sm" @click="position = 100">Abrir</button>
          </div>
        </template>

        <!-- ALARM: zonas -->
        <!-- WATER/FAUCET: abrir/cerrar -->
        <template v-else-if="device.type === 'water'">
          <div class="control-row">
            <span class="control-label">Caudal</span>
            <button class="btn-control" :class="device.isOn ? 'btn-control--success' : ''" @click="togglePower">
              <i class="fa-solid fa-droplet"></i>
              {{ device.isOn ? 'Abierto' : 'Cerrado' }}
            </button>
          </div>
        </template>

        <!-- DEFAULT: solo toggle -->
        <template v-else>
          <p class="no-controls">Este dispositivo solo tiene encendido/apagado.</p>
        </template>
      </div>

      <div class="history-card">
        <h2 class="controls-title">Actividad reciente</h2>
        <div class="history-list">
          <div v-for="entry in recentHistory" :key="entry.id" class="history-entry">
            <span class="history-action">{{ entry.action }}</span>
            <span class="history-date">{{ formatDate(entry.date) }}</span>
          </div>
          <p v-if="recentHistory.length === 0" class="no-controls">Sin actividad registrada.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
</style>
