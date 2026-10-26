<script setup>
defineProps({
  icon: { type: [Object, Function], required: true },
  iconBg: { type: String, default: '#FD5C20' },
  title: { type: String, required: true },
  description: { type: String, required: true },
  to: { type: String, required: true },
})
</script>

<template>
  <RouterLink :to="to" class="card-hover group flex items-center gap-3 rounded-xl border p-4 hover:border-primary" style="border-color: var(--color-border)">
    <div
      class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-white transition-transform duration-200 group-hover:scale-110"
      :style="{ backgroundColor: iconBg }"
    >
      <component :is="icon" :size="18" />
    </div>
    <div class="min-w-0">
      <p class="text-sm font-semibold" style="color: var(--color-text)">{{ title }}</p>
      <p class="truncate text-xs" style="color: var(--color-text-muted)">{{ description }}</p>
    </div>
  </RouterLink>
</template>
