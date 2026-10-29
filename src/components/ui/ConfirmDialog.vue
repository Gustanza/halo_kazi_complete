<script setup>
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Are you sure?' },
  description: { type: String, default: '' },
  confirmLabel: { type: String, default: 'Confirm' },
  cancelLabel: { type: String, default: 'Cancel' },
  danger: { type: Boolean, default: true },
  loading: { type: Boolean, default: false },
})
const emit = defineEmits(['confirm', 'cancel', 'close'])
</script>

<template>
  <AppModal :open="open" size="sm" @close="emit('close')">
    <h2 class="text-lg font-bold" style="color: var(--color-text)">{{ title }}</h2>
    <p v-if="description" class="mt-2 text-sm" style="color: var(--color-text-muted)">{{ description }}</p>
    <div class="mt-6 flex justify-end gap-3">
      <AppButton variant="outline" @click="emit('cancel'); emit('close')">{{ cancelLabel }}</AppButton>
      <AppButton :variant="danger ? 'danger' : 'primary'" :loading="loading" @click="emit('confirm')">
        {{ confirmLabel }}
      </AppButton>
    </div>
  </AppModal>
</template>
