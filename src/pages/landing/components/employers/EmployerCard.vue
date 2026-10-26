<script setup>
import CompanyBadge from '../../../../components/common/CompanyBadge.vue'

defineProps({ employer: { type: Object, required: true } })
</script>

<template>
  <div class="card-surface card-hover flex flex-col items-center gap-3 p-6 text-center hover:border-primary" :style="{ '--glow': employer.brandColor }">
    <CompanyBadge :initials="employer.initials" :color="employer.brandColor" :size="56" />
    <div>
      <h3 class="text-sm font-semibold" style="color: var(--color-text)">{{ employer.name }}</h3>
      <p class="mt-0.5 text-xs" style="color: var(--color-text-muted)">{{ employer.industry }}</p>
    </div>
    <span class="rounded-full bg-primary-50 px-3 py-1 text-xs font-semibold text-primary dark:bg-white/10">
      {{ employer.jobsOpen }} Jobs Open
    </span>
  </div>
</template>
