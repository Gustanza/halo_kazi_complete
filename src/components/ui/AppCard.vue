<script setup>
defineProps({
  hover: { type: Boolean, default: false },
  padded: { type: Boolean, default: true },
})
</script>

<template>
  <div class="card-surface" :class="[hover ? 'card-hover' : '', padded ? 'p-5' : '']">
    <slot />
  </div>
</template>
