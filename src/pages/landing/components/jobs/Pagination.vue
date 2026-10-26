<script setup>
import { computed } from 'vue'
import { ChevronRight } from 'lucide-vue-next'

const props = defineProps({
  modelValue: { type: Number, default: 1 },
  total: { type: Number, required: true },
})
const emit = defineEmits(['update:modelValue'])

const visiblePages = computed(() => {
  const pages = [1, 2, 3]
  if (props.total > 3) pages.push('ellipsis')
  if (props.total > 3) pages.push(props.total)
  return pages
})

function go(page) {
  if (page === 'ellipsis') return
  emit('update:modelValue', page)
}
</script>

<template>
  <nav class="flex items-center justify-center gap-2" aria-label="Pagination">
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
      @click="go(Math.min(modelValue + 1, total))"
    >
      <ChevronRight :size="16" />
    </button>
  </nav>
</template>
