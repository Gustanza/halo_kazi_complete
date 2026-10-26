<script setup>
const props = defineProps({
  variant: { type: String, default: 'warm' }, // 'warm' | 'cool' | 'mixed'
})

const palettes = {
  warm: ['#FD5C20', '#FFB199'],
  cool: ['#B064F7', '#6EC6FF'],
  mixed: ['#FD5C20', '#B064F7'],
}
const colors = palettes[props.variant] || palettes.warm
</script>

<template>
  <div class="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden="true">
    <div
      class="absolute -left-24 -top-32 h-80 w-80 rounded-full opacity-20 blur-3xl dark:opacity-25"
      :style="{ background: colors[0] }"
    />
    <div
      class="absolute -right-20 top-1/4 h-96 w-96 rounded-full opacity-[0.15] blur-3xl dark:opacity-20"
      :style="{ background: colors[1] }"
    />
  </div>
</template>
