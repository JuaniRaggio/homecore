<template>
  <svg
    :class="['hc-icon', `hc-icon--${size}`]"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    v-html="paths[name] || paths.default"
  />
</template>

<script setup>
defineProps({
  name: { type: String, required: true },
  size: { type: String, default: 'md' }
})

const paths = {
  lamp: '<path d="M9 18h6"/><path d="M10 22h4"/><path d="M12 2a7 7 0 0 0-4 12.7V17h8v-2.3A7 7 0 0 0 12 2z"/>',
  door: '<path d="M5 2h11a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H5"/><path d="M5 2v20"/><circle cx="14" cy="12" r="1" fill="currentColor" stroke="none"/>',
  alarm: '<path d="M12 2L2 8v5c0 5.5 3.8 10.7 10 12 6.2-1.3 10-6.5 10-12V8L12 2z"/><path d="M12 8v4l2 2"/>',
  faucet: '<path d="M12 2v6"/><path d="M6 8h12"/><path d="M6 8c0 0-2 0-2 3v2a3 3 0 0 0 3 3h1"/><path d="M18 8c0 0 2 0 2 3v2a3 3 0 0 1-3 3h-1"/><path d="M10 16v2a4 4 0 0 0 4 4h0a4 4 0 0 0 4-4v-2"/>',
  blinds: '<rect x="3" y="3" width="18" height="18" rx="2"/><line x1="3" y1="8" x2="21" y2="8"/><line x1="3" y1="13" x2="21" y2="13"/><line x1="3" y1="18" x2="21" y2="18"/>',
  home: '<path d="M3 12l9-9 9 9"/><path d="M5 10v10a1 1 0 0 0 1 1h3v-6h6v6h3a1 1 0 0 0 1-1V10"/>',
  devices: '<rect x="4" y="4" width="6" height="6" rx="1"/><rect x="14" y="4" width="6" height="6" rx="1"/><rect x="4" y="14" width="6" height="6" rx="1"/><rect x="14" y="14" width="6" height="6" rx="1"/>',
  rooms: '<path d="M3 3h7v7H3z"/><path d="M14 3h7v7h-7z"/><path d="M3 14h7v7H3z"/><path d="M14 14h7v7h-7z"/><line x1="10" y1="3" x2="10" y2="21"/><line x1="3" y1="10" x2="21" y2="10"/>',
  routines: '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
  history: '<path d="M3 3v18h18"/><path d="M7 16l4-4 4 4 5-6"/>',
  consumption: '<path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>',
  settings: '<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09a1.65 1.65 0 0 0-1.08-1.51 1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09a1.65 1.65 0 0 0 1.51-1.08 1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1.08 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1.08z"/>',
  bell: '<path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/>',
  chevronDown: '<path d="M6 9l6 6 6-6"/>',
  arrowLeft: '<path d="M19 12H5"/><path d="M12 19l-7-7 7-7"/>',
  star: '<polygon points="12,2 15.1,8.3 22,9.2 17,14.1 18.2,21 12,17.8 5.8,21 7,14.1 2,9.2 8.9,8.3" fill="none"/>',
  starFilled: '<polygon points="12,2 15.1,8.3 22,9.2 17,14.1 18.2,21 12,17.8 5.8,21 7,14.1 2,9.2 8.9,8.3" fill="currentColor" stroke="none"/>',
  edit: '<path d="M17 3a2.83 2.83 0 0 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>',
  close: '<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>',
  check: '<polyline points="20 6 9 17 4 12"/>',
  warning: '<path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>',
  info: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>',
  collapseLeft: '<polyline points="15 18 9 12 15 6"/>',
  collapseRight: '<polyline points="9 18 15 12 9 6"/>',
  unlink: '<line x1="18" y1="6" x2="6" y2="18"/>',
  lock: '<rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>',
  swap: '<path d="M16 3l4 4-4 4"/><path d="M20 7H4"/><path d="M8 21l-4-4 4-4"/><path d="M4 17h16"/>',
  default: '<circle cx="12" cy="12" r="10"/>'
}
</script>

<style scoped>
.hc-icon {
  display: inline-block;
  vertical-align: middle;
  flex-shrink: 0;
}

.hc-icon--xs { width: 14px; height: 14px; }
.hc-icon--sm { width: 16px; height: 16px; }
.hc-icon--md { width: 20px; height: 20px; }
.hc-icon--lg { width: 24px; height: 24px; }
.hc-icon--xl { width: 32px; height: 32px; }
.hc-icon--2xl { width: 48px; height: 48px; }
</style>
