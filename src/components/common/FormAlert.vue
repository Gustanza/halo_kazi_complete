<script setup>
import { AlertCircle, Info } from 'lucide-vue-next'

defineProps({
  tone: { type: String, default: 'error' }, // 'error' | 'info'
})
</script>

<template>
  <div
    role="alert"
    class="flex items-start gap-2.5 rounded-lg border px-3.5 py-3 text-sm"
    :class="
      tone === 'error'
        ? 'border-red-200 bg-red-50 text-red-700 dark:border-red-500/30 dark:bg-red-500/10 dark:text-red-300'
        : 'border-sky-200 bg-sky-50 text-sky-800 dark:border-sky-500/30 dark:bg-sky-500/10 dark:text-sky-200'
    "
  >
    <component :is="tone === 'error' ? AlertCircle : Info" :size="17" class="mt-0.5 shrink-0" />
    <div class="min-w-0 flex-1"><slot /></div>
  </div>
</template>
