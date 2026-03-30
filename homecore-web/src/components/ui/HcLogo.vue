<template>
  <svg
    :class="['hc-logo', `hc-logo--${size}`]"
    viewBox="0 0 44 52"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <polygon points="22,0 44,13 44,39 22,52 0,39 0,13" fill="#0C447C" stroke="#378ADD" stroke-width="0.7"/>
    <polygon points="22,7 36,15 36,36 8,36 8,15" fill="none" stroke="#B5D4F4" stroke-width="1" stroke-linejoin="round"/>
    <polygon points="22,3 38,14 6,14" fill="#378ADD"/>
    <rect x="16" y="24" width="12" height="12" rx="1.5" fill="#042C53"/>
    <path d="M 14,30 A 8,8 0 0,1 30,30" fill="none" stroke="#85B7EB" stroke-width="0.9" opacity="0.5"/>
    <circle cx="22" cy="34" r="1.5" fill="#378ADD"/>
  </svg>
</template>

<script setup>
defineProps({
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg', 'xl'].includes(v)
  }
})
</script>

<style scoped>
.hc-logo {
  display: block;
  flex-shrink: 0;
}

.hc-logo--sm {
  width: 28px;
  height: 33px;
}

.hc-logo--md {
  width: 36px;
  height: 42px;
}

.hc-logo--lg {
  width: 56px;
  height: 66px;
}

.hc-logo--xl {
  width: 120px;
  height: 140px;
}
</style>
