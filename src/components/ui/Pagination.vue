<script setup>
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = defineProps({
  modelValue: { type: Number, required: true }, // current page, 1-based
  totalPages: { type: Number, required: true },
})
const emit = defineEmits(['update:modelValue'])

const pages = computed(() => Array.from({ length: props.totalPages }, (_, i) => i + 1))

function go(page) {
  if (page < 1 || page > props.totalPages || page === props.modelValue) return
  emit('update:modelValue', page)
}
</script>

<template>
  <nav v-if="totalPages > 1" class="flex items-center justify-between py-4" aria-label="Pagination">
    <button
      type="button"
      class="flex items-center gap-1 text-xs font-semibold transition disabled:opacity-40"
      style="color: var(--color-text-muted)"
      :disabled="modelValue === 1"
      @click="go(modelValue - 1)"
    >
      <ChevronLeft :size="16" /> Previous
    </button>

    <div class="flex items-center gap-1.5">
      <button
        v-for="page in pages"
        :key="page"
        type="button"
        class="flex h-8 w-8 items-center justify-center rounded-xl text-xs font-bold transition"
        :class="page === modelValue ? 'bg-primary text-white' : ''"
        :style="page === modelValue ? '' : 'color: var(--color-text-muted)'"
        @click="go(page)"
      >
        {{ page }}
      </button>
    </div>

    <button
      type="button"
      class="flex items-center gap-1 text-xs font-semibold transition hover:text-primary disabled:opacity-40"
      style="color: var(--color-text)"
      :disabled="modelValue === totalPages"
      @click="go(modelValue + 1)"
    >
      Next <ChevronRight :size="16" />
    </button>
  </nav>
</template>
