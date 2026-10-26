<script setup>
import { Mail } from 'lucide-vue-next'

defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, required: true },
  buttonText: { type: String, required: true },
  to: { type: String, default: '/register' },
})
</script>

<template>
  <section class="container-page py-10 sm:py-14">
    <div
      v-reveal
      class="relative flex flex-col items-center gap-5 overflow-hidden rounded-[2rem] px-6 py-10 text-center shadow-xl shadow-primary/20 sm:flex-row sm:justify-between sm:px-12 sm:py-12 sm:text-left"
      style="background: linear-gradient(135deg, #ff7a3d, #fd5c20 45%, #e8470f)"
    >
      <div class="pointer-events-none absolute inset-0" aria-hidden="true">
        <div class="absolute -left-10 -top-16 h-56 w-56 rounded-full bg-white/10 blur-2xl" />
        <div class="absolute -bottom-20 -right-10 h-64 w-64 rounded-full bg-black/10 blur-2xl" />
      </div>

      <div class="relative z-10 flex items-center gap-4">
        <div class="hidden h-12 w-12 shrink-0 items-center justify-center rounded-full bg-white/20 text-white sm:flex">
          <Mail :size="22" :stroke-width="2" />
        </div>
        <div>
          <h3 class="text-lg font-bold text-white sm:text-xl">{{ title }}</h3>
          <p class="mt-1 text-sm text-white/90">{{ subtitle }}</p>
        </div>
      </div>
      <RouterLink
        :to="to"
        class="relative z-10 inline-flex shrink-0 items-center justify-center rounded-full bg-white px-6 py-3 text-sm font-semibold text-primary shadow-card transition-colors hover:bg-white/90 active:scale-[0.97]"
        style="transition-property: background-color, transform; transition-duration: 150ms; transition-timing-function: var(--ease-out)"
      >
        {{ buttonText }}
      </RouterLink>
    </div>
  </section>
</template>
