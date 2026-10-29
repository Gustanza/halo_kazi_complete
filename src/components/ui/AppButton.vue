<script setup>
import { computed } from 'vue'
import { Loader2 } from 'lucide-vue-next'

const props = defineProps({
  variant: { type: String, default: 'primary' }, // primary | outline | ghost | danger
  size: { type: String, default: 'md' }, // sm | md | lg
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  type: { type: String, default: 'button' },
  block: { type: Boolean, default: false },
})

const sizeClasses = {
  sm: 'px-4 py-2 text-xs',
  md: 'px-6 py-3 text-sm',
  lg: 'px-7 py-3.5 text-base',
}

const variantClasses = {
  primary: 'bg-primary text-white shadow-card hover:bg-primary-600 hover:shadow-lg',
  outline: 'border bg-[var(--color-surface)] text-[var(--color-text)] hover:border-primary hover:text-primary',
  ghost: 'text-[var(--color-text)] hover:bg-primary-50 dark:hover:bg-white/5',
  danger: 'bg-red-500 text-white hover:bg-red-600',
}

const classes = computed(() => [
  'inline-flex items-center justify-center gap-2 rounded-full font-semibold transition active:scale-[0.97] disabled:cursor-not-allowed disabled:opacity-60 disabled:active:scale-100',
  sizeClasses[props.size],
  variantClasses[props.variant],
  props.block ? 'w-full' : '',
])
</script>

<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="classes"
    :style="variant === 'outline' ? 'border-color: var(--color-border)' : ''"
  >
    <Loader2 v-if="loading" :size="16" class="animate-spin" />
    <slot />
  </button>
</template>
