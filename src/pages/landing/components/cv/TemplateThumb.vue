<script setup>
import { Check } from 'lucide-vue-next'

const props = defineProps({
  template: { type: Object, required: true },
  selected: { type: Boolean, default: false },
})
defineEmits(['select'])
</script>

<template>
  <button
    type="button"
    class="group relative aspect-[3/4] w-full shrink-0 overflow-hidden rounded-xl border-2 p-2 transition-all duration-300 hover:-translate-y-1 hover:shadow-lg"
    :style="selected ? 'border-color:#FD5C20' : 'border-color:var(--color-border)'"
    @click="$emit('select', template.id)"
  >
    <div
      class="flex h-full w-full flex-col gap-1.5 rounded-lg p-2"
      :style="{ backgroundColor: props.template.scheme === 'dark' ? '#1b2440' : '#ffffff' }"
    >
      <div class="flex items-center gap-1.5">
        <div class="h-5 w-5 rounded-full" :style="{ backgroundColor: props.template.accent }" />
        <div class="h-1.5 flex-1 rounded-full" :style="{ backgroundColor: props.template.scheme === 'dark' ? '#3a4568' : '#e6e8eb' }" />
      </div>
      <div class="mt-1 h-1 w-3/4 rounded-full" :style="{ backgroundColor: props.template.accent }" />
      <div class="h-1 w-1/2 rounded-full" :style="{ backgroundColor: props.template.scheme === 'dark' ? '#3a4568' : '#e6e8eb' }" />
      <div class="mt-2 space-y-1">
        <div v-for="n in 4" :key="n" class="h-1 rounded-full" :style="{ backgroundColor: props.template.scheme === 'dark' ? '#2a3458' : '#f1f2f4', width: n % 2 ? '90%' : '65%' }" />
      </div>
    </div>
    <Transition name="pop">
      <span
        v-if="selected"
        class="absolute right-1.5 top-1.5 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-white"
      >
        <Check :size="12" :stroke-width="3" />
      </span>
    </Transition>
  </button>
</template>

<style scoped>
.pop-enter-active {
  transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.2s ease;
}
.pop-enter-from {
  opacity: 0;
  transform: scale(0.3);
}
.pop-leave-active {
  transition: opacity 0.15s ease;
}
.pop-leave-to {
  opacity: 0;
}
</style>
