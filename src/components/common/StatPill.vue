<script setup>
import CountUpValue from './CountUpValue.vue'

defineProps({
  icon: { type: [Object, Function], required: true },
  value: { type: String, required: true },
  label: { type: String, required: true },
})
</script>

<template>
  <div class="flex items-center gap-3">
    <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
      <component :is="icon" :size="20" :stroke-width="2" />
    </div>
    <div>
      <p class="text-xl font-extrabold leading-none" style="color: var(--color-text)">
        <CountUpValue :value="value" />
      </p>
      <p class="mt-1 text-sm" style="color: var(--color-text-muted)">{{ label }}</p>
    </div>
  </div>
</template>
