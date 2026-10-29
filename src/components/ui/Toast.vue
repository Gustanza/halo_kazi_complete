<script setup>
import { CheckCircle2, XCircle, Info, X } from 'lucide-vue-next'
import { useToast } from '../../composables/useToast'

const { toasts, dismiss } = useToast()

const icons = { success: CheckCircle2, error: XCircle, info: Info }
const colors = {
  success: 'text-emerald-500',
  error: 'text-red-500',
  info: 'text-primary',
}
</script>

<template>
  <Teleport to="body">
    <div class="pointer-events-none fixed inset-x-0 top-4 z-[100] flex flex-col items-center gap-2 px-4 sm:items-end sm:right-4 sm:left-auto">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="card-surface pointer-events-auto flex w-full max-w-sm items-start gap-3 p-4 shadow-lg sm:w-96"
        >
          <component :is="icons[toast.type]" :size="20" class="shrink-0" :class="colors[toast.type]" />
          <p class="flex-1 text-sm" style="color: var(--color-text)">{{ toast.message }}</p>
          <button
            type="button"
            class="shrink-0"
            style="color: var(--color-text-muted)"
            aria-label="Dismiss"
            @click="dismiss(toast.id)"
          >
            <X :size="16" />
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: opacity 200ms var(--ease-out), transform 200ms var(--ease-out);
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
