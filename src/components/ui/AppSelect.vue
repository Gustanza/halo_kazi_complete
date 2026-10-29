<script setup>
defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  options: { type: Array, default: () => [] }, // [{ value, label }] or plain strings
  error: { type: String, default: '' },
})
defineEmits(['update:modelValue'])

function optionValue(option) {
  return typeof option === 'object' ? option.value : option
}
function optionLabel(option) {
  return typeof option === 'object' ? option.label : option
}
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ label }}</label>
    <select
      :value="modelValue"
      class="w-full rounded-lg border bg-[var(--color-surface)] px-3 py-2.5 text-sm transition-colors focus:border-primary focus:outline-none"
      :style="(error ? 'border-color:#ef4444;' : 'border-color: var(--color-border);') + 'color: var(--color-text)'"
      @change="$emit('update:modelValue', $event.target.value)"
    >
      <option v-for="option in options" :key="optionValue(option)" :value="optionValue(option)">
        {{ optionLabel(option) }}
      </option>
    </select>
    <p v-if="error" class="mt-1 text-xs text-red-500">{{ error }}</p>
  </div>
</template>
