<script setup>
defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  error: { type: String, default: '' },
  required: { type: Boolean, default: false },
})
defineEmits(['update:modelValue'])
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ label }}</label>
    <input
      :value="modelValue"
      :type="type"
      :placeholder="placeholder"
      :required="required"
      class="w-full rounded-lg border bg-transparent px-3 py-2.5 text-sm transition-colors focus:border-primary focus:outline-none"
      :style="(error ? 'border-color:#ef4444;' : 'border-color: var(--color-border);') + 'color: var(--color-text)'"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <p v-if="error" class="mt-1 text-xs text-red-500">{{ error }}</p>
  </div>
</template>
