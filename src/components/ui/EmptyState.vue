<script setup>
defineProps({
  icon: { type: [Object, Function], default: null },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  actionLabel: { type: String, default: '' },
})
defineEmits(['action'])
</script>

<template>
  <div class="card-surface flex flex-col items-center gap-2 px-5 py-12 text-center">
    <div v-if="icon" class="flex h-14 w-14 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
      <component :is="icon" :size="24" />
    </div>
    <h3 class="mt-1 font-bold" style="color: var(--color-text)">{{ title }}</h3>
    <p v-if="description" class="max-w-sm text-sm" style="color: var(--color-text-muted)">{{ description }}</p>
    <button v-if="actionLabel" type="button" class="btn-primary mt-3" @click="$emit('action')">
      {{ actionLabel }}
    </button>
  </div>
</template>
