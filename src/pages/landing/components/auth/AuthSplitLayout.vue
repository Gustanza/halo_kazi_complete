<script setup>
import { UserRound, FileText, Send, Bell, ShieldCheck } from 'lucide-vue-next'

defineProps({
  headingPrefix: { type: String, required: true },
  headingHighlight: { type: String, required: true },
  subtitle: { type: String, required: true },
  showIllustration: { type: Boolean, default: true },
})

const features = [
  { icon: UserRound, label: 'Search Jobs', description: 'Search thousands of jobs from various companies', color: '#FD5C20' },
  { icon: FileText, label: 'Build Your CV', description: 'Use the CV builder to create a professional CV in minutes', color: '#B064F7' },
  { icon: Send, label: 'Submit Application', description: 'Submit applications directly to employers through our platform', color: '#2FB573' },
  { icon: Bell, label: 'Receive Notifications', description: 'Get instant updates on new jobs that suit you', color: '#2F8FF5' },
]
</script>

<template>
  <div class="container-page grid gap-10 py-10 lg:grid-cols-2 lg:items-start lg:gap-16 lg:py-16">
    <div class="flex flex-col">
      <div v-reveal>
        <h1 class="text-3xl font-extrabold leading-tight sm:text-4xl" style="color: var(--color-text)">
          {{ headingPrefix }} <span class="text-primary">{{ headingHighlight }}</span>
        </h1>
        <p class="mt-3 max-w-md text-base" style="color: var(--color-text-muted)">{{ subtitle }}</p>
      </div>

      <div class="mt-8 space-y-5">
        <div
          v-for="(feature, i) in features"
          :key="feature.label"
          class="card-surface card-hover flex items-center gap-4 p-4"
          v-reveal="80 + i * 70"
        >
          <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-full text-white" :style="{ backgroundColor: feature.color }">
            <component :is="feature.icon" :size="20" />
          </div>
          <div>
            <p class="text-sm font-semibold" style="color: var(--color-text)">{{ feature.label }}</p>
            <p class="text-xs" style="color: var(--color-text-muted)">{{ feature.description }}</p>
          </div>
        </div>
      </div>

      <div
        v-if="showIllustration"
        class="mt-8 hidden aspect-[4/3] w-full items-center justify-center rounded-2xl sm:flex"
        style="background-color: var(--color-peach-light)"
        v-reveal="400"
      >
        <svg viewBox="0 0 200 150" class="h-2/3 w-2/3">
          <rect x="30" y="90" width="140" height="10" rx="5" fill="#FD5C20" opacity="0.25" />
          <rect x="55" y="45" width="90" height="55" rx="8" fill="#14213D" opacity="0.85" />
          <rect x="63" y="53" width="74" height="38" rx="3" fill="#F3F3F3" />
          <circle cx="100" cy="30" r="16" fill="#FD5C20" />
          <rect x="85" y="46" width="30" height="22" rx="6" fill="#FD5C20" opacity="0.85" />
        </svg>
      </div>
    </div>

    <div class="flex flex-col items-center">
      <div class="card-surface w-full p-6 sm:p-8" v-reveal="120">
        <slot />
      </div>
      <div class="mt-6 flex items-center justify-center gap-2 text-center text-xs" style="color: var(--color-text-muted)">
        <ShieldCheck :size="14" class="shrink-0 text-primary" />
        <span>Usalama wako ni muhimu kwetu.<br class="sm:hidden" /> Halokazi haitashiriki namba yako na mtu yoyote.</span>
      </div>
    </div>
  </div>
</template>
