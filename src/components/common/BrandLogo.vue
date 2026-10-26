<script setup>
defineProps({
  size: { type: Number, default: 40 },
})
</script>

<template>
  <img
    src="/halotel-logo.png"
    alt="Halokazi logo"
    :width="size"
    :height="size"
    class="shrink-0 object-contain"
    :style="{ width: size + 'px', height: size + 'px' }"
  />
</template>
