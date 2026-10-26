<script setup>
import { Send, Bookmark, BellRing, FolderOpen, ArrowRight } from 'lucide-vue-next'

const icons = { applications: Send, saved: Bookmark, alerts: BellRing, cvs: FolderOpen }

defineProps({
  statKey: { type: String, required: true },
  value: { type: [Number, String], required: true },
  label: { type: String, required: true },
  to: { type: String, required: true },
  color: { type: String, default: '#FD5C20' },
})
</script>

<template>
  <RouterLink :to="to" class="card-surface card-hover group block p-5">
    <div class="flex h-10 w-10 items-center justify-center rounded-full text-white" :style="{ backgroundColor: color }">
      <component :is="icons[statKey]" :size="18" />
    </div>
    <p class="mt-4 text-2xl font-extrabold" style="color: var(--color-text)">{{ value }}</p>
    <p class="mt-0.5 text-sm" style="color: var(--color-text-muted)">{{ label }}</p>
    <span class="mt-2 inline-flex items-center gap-1 text-xs font-semibold text-primary">
      View all
      <ArrowRight :size="12" class="transition-transform duration-200 group-hover:translate-x-0.5" />
    </span>
  </RouterLink>
</template>
