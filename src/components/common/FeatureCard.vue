<script setup>
defineProps({
  icon: { type: [Object, Function], required: true },
  iconBg: { type: String, default: '#FD5C20' },
  title: { type: String, required: true },
  description: { type: String, required: true },
  variant: { type: String, default: 'card' }, // 'card' | 'plain'
})
</script>

<template>
  <div class="group" :class="variant === 'card' ? 'card-surface card-hover p-6' : ''" :style="{ '--glow': iconBg }">
    <div
      class="mb-4 flex h-11 w-11 items-center justify-center rounded-full text-white transition-transform duration-300 group-hover:scale-110"
      :style="{ backgroundColor: iconBg }"
    >
      <component :is="icon" :size="20" :stroke-width="2" />
    </div>
    <h3 class="text-base font-semibold" style="color: var(--color-text)">{{ title }}</h3>
    <p class="mt-1 text-sm leading-relaxed" style="color: var(--color-text-muted)">{{ description }}</p>
  </div>
</template>
