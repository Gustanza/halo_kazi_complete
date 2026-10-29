<script setup>
import { computed } from 'vue'
import { ChevronRight } from 'lucide-vue-next'

const props = defineProps({
  modelValue: { type: Number, default: 1 },
  total: { type: Number, required: true },
})
const emit = defineEmits(['update:modelValue'])

// First, last and the current page with its neighbours; gaps collapse into an ellipsis.
const visiblePages = computed(() => {
  const { total, modelValue: current } = props
  const wanted = new Set([1, total, current - 1, current, current + 1])
  const pages = [...wanted].filter((p) => p >= 1 && p <= total).sort((a, b) => a - b)
  return pages.flatMap((p, i) => (i > 0 && p - pages[i - 1] > 1 ? ['ellipsis', p] : [p]))
})

function go(page) {
  if (page === 'ellipsis') return
  emit('update:modelValue', page)
}
</script>

<template>
  <nav v-if="total > 1" class="flex items-center justify-center gap-2" aria-label="Pagination">
    <button
      v-for="(page, idx) in visiblePages"
      :key="idx"
      type="button"
      :disabled="page === 'ellipsis'"
      class="press flex h-10 min-w-10 items-center justify-center rounded-full border px-3 text-sm font-medium transition-colors disabled:cursor-default disabled:active:scale-100"
      :style="
        page === modelValue
          ? 'background-color: #FD5C20; border-color: #FD5C20; color: #fff'
          : 'border-color: var(--color-border); color: var(--color-text)'
      "
      @click="go(page)"
    >
      {{ page === 'ellipsis' ? '…' : page }}
    </button>
    <button
      type="button"
      class="press flex h-10 w-10 items-center justify-center rounded-full border transition-colors hover:border-primary hover:text-primary"
      style="border-color: var(--color-border); color: var(--color-text)"
      aria-label="Next page"
      :disabled="modelValue >= total"
      @click="go(Math.min(modelValue + 1, total))"
    >
      <ChevronRight :size="16" />
    </button>
  </nav>
</template>
