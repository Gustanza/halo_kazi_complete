<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  align: { type: String, default: 'left' }, // 'left' | 'center'
})
</script>

<template>
  <div :class="align === 'center' ? 'text-center' : 'text-left'">
    <h2 class="text-3xl font-extrabold tracking-tight sm:text-4xl" style="color: var(--color-text)">
      {{ title }}
    </h2>
    <p v-if="subtitle" class="mt-2 text-base" style="color: var(--color-text-muted)">
      {{ subtitle }}
    </p>
  </div>
</template>
