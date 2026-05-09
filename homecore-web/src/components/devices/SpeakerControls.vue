<template>
  <div>
    <div v-if="currentSong" class="control-row">
      <span class="control-label">Reproduciendo</span>
      <span class="control-value">{{ currentSong.title || currentSong }}</span>
    </div>
    <div class="control-row">
      <span class="control-label">Volumen</span>
      <input
        type="range"
        :min="volLimits.min"
        :max="volLimits.max"
        :step="volLimits.step"
        :value="volume"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:volume', Number($event.target.value))"
        @change="$emit('change:volume', Number($event.target.value))"
      />
      <span class="control-value">{{ volume }}</span>
    </div>
    <div class="control-row">
      <span class="control-label">Genero</span>
      <select
        class="control-select"
        :value="genre"
        :disabled="disabled"
        @change="$emit('update:genre', $event.target.value)"
      >
        <option v-for="opt in genreOptions" :key="opt" :value="opt">{{ opt }}</option>
      </select>
    </div>
    <div class="control-buttons">
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'previousSong')">
        <i class="fa-solid fa-backward-step"></i>
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'play')">
        <i class="fa-solid fa-play"></i>
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'pause')">
        <i class="fa-solid fa-pause"></i>
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'stop')">
        <i class="fa-solid fa-stop"></i>
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'nextSong')">
        <i class="fa-solid fa-forward-step"></i>
      </button>
    </div>
    <div v-if="playlist.length" class="control-playlist">
      <span class="control-label">Playlist</span>
      <ol class="playlist-list">
        <li v-for="(song, i) in playlist" :key="i" class="playlist-item" :class="{ 'playlist-item--active': currentSong && (song.title === currentSong.title || song === currentSong) }">
          {{ song.title || song }}
        </li>
      </ol>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  volume: { type: Number, default: 5 },
  genre: { type: String, default: 'pop' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
  playlist: { type: Array, default: () => [] },
  currentSong: { type: [Object, String], default: null },
})

defineEmits(['update:volume', 'change:volume', 'update:genre', 'action'])

const volLimits = computed(() => ({
  min: props.limits.volume?.min ?? 0,
  max: props.limits.volume?.max ?? 10,
  step: props.limits.volume?.step ?? 1,
}))

const genreOptions = computed(() => props.limits.genreOptions ?? ['clasica', 'country', 'dance', 'latina', 'pop', 'rock'])
</script>

<!-- Estilos en controls.css global -->
