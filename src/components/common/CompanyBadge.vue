<script setup>
defineProps({
  initials: { type: String, required: true },
  color: { type: String, default: '#FD5C20' },
  size: { type: Number, default: 48 },
})
</script>

<template>
  <div
    class="flex shrink-0 items-center justify-center rounded-xl font-bold text-white"
    :style="{ backgroundColor: color, width: size + 'px', height: size + 'px', fontSize: size * 0.32 + 'px' }"
  >
    {{ initials.slice(0, 3) }}
  </div>
</template>
