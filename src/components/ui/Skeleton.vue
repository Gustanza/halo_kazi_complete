<script setup>
defineProps({
  // 'line' - a single text-line bar; 'card' - a card-sized block; 'circle' - an avatar-sized circle.
  variant: { type: String, default: 'line' },
  width: { type: String, default: '' },
  height: { type: String, default: '' },
})
</script>

<template>
  <div
    class="animate-pulse rounded-md"
    :class="variant === 'circle' ? 'rounded-full' : variant === 'card' ? 'rounded-2xl' : ''"
    :style="{
      backgroundColor: 'var(--color-border)',
      width: width || (variant === 'circle' ? '2.5rem' : '100%'),
      height: height || (variant === 'card' ? '8rem' : variant === 'circle' ? '2.5rem' : '0.875rem'),
    }"
  />
</template>
