<script setup>
import { X } from 'lucide-vue-next'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
  size: { type: String, default: 'md' }, // sm | md | lg
})
const emit = defineEmits(['close'])

const widths = { sm: 'max-w-sm', md: 'max-w-md', lg: 'max-w-2xl' }

function onBackdrop() {
  emit('close')
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center p-4" role="dialog" aria-modal="true">
        <div class="absolute inset-0 bg-black/50" @click="onBackdrop" />
        <div class="card-surface relative w-full p-6" :class="widths[size]">
          <button
            type="button"
            class="absolute right-4 top-4 flex h-8 w-8 items-center justify-center rounded-full transition hover:bg-primary-50 dark:hover:bg-white/10"
            style="color: var(--color-text-muted)"
            aria-label="Close"
            @click="emit('close')"
          >
            <X :size="18" />
          </button>
          <h2 v-if="title" class="pr-8 text-lg font-bold" style="color: var(--color-text)">{{ title }}</h2>
          <div :class="title ? 'mt-4' : ''">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 180ms ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
</style>
