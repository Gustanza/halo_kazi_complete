<script setup>
defineProps({
  modelValue: { type: String, default: '' },
  label: { type: String, default: '' },
  placeholder: { type: String, default: '' },
  rows: { type: Number, default: 4 },
  error: { type: String, default: '' },
})
defineEmits(['update:modelValue'])
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ label }}</label>
    <textarea
      :value="modelValue"
      :rows="rows"
      :placeholder="placeholder"
      class="w-full resize-y rounded-lg border bg-transparent px-3 py-2.5 text-sm transition-colors focus:border-primary focus:outline-none"
      :style="(error ? 'border-color:#ef4444;' : 'border-color: var(--color-border);') + 'color: var(--color-text)'"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <p v-if="error" class="mt-1 text-xs text-red-500">{{ error }}</p>
  </div>
</template>
