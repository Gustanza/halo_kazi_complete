<script setup>
defineProps({
  icon: { type: [Object, Function], required: true },
  step: { type: Number, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
})
</script>

<template>
  <div class="flex flex-col items-center text-center">
    <div class="relative mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-white text-primary shadow-card dark:bg-white/10">
      <component :is="icon" :size="24" :stroke-width="2" />
      <span class="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-[10px] font-bold text-white">
        {{ step }}
      </span>
    </div>
    <h3 class="text-sm font-semibold" style="color: var(--color-text)">{{ title }}</h3>
    <p class="mt-1 text-xs" style="color: var(--color-text-muted)">{{ description }}</p>
  </div>
</template>
