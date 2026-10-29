// Singleton toast queue - same module-singleton pattern as useTheme.js. Toast.vue (mounted
// once in App.vue) renders whatever is in `toasts`; any component calls useToast() to push one.
import { ref } from 'vue'

const toasts = ref([])
let nextId = 1

function push(message, { type = 'info', duration = 4000 } = {}) {
  const id = nextId++
  toasts.value.push({ id, message, type })
  if (duration > 0) {
    window.setTimeout(() => dismiss(id), duration)
  }
  return id
}

function dismiss(id) {
  toasts.value = toasts.value.filter((t) => t.id !== id)
}

export function useToast() {
  return {
    toasts,
    success: (message, options) => push(message, { ...options, type: 'success' }),
    error: (message, options) => push(message, { ...options, type: 'error' }),
    info: (message, options) => push(message, { ...options, type: 'info' }),
    dismiss,
  }
}
