<script setup>
import { ref } from 'vue'
import { MapPin, Briefcase, Bookmark } from 'lucide-vue-next'
import CompanyBadge from '../../../../components/common/CompanyBadge.vue'

defineProps({ job: { type: Object, required: true } })
const saved = ref(false)
</script>

<template>
  <RouterLink
    :to="{ path: '/login', query: { redirect: '/dashboard/find-jobs' } }"
    class="card-surface card-hover flex items-center gap-4 p-4 hover:border-primary sm:p-5"
    :style="{ '--glow': job.brandColor }"
  >
    <CompanyBadge :initials="job.initials" :color="job.brandColor" :size="52" />
    <div class="min-w-0 flex-1">
      <h3 class="truncate text-base font-semibold" style="color: var(--color-text)">{{ job.title }}</h3>
      <p class="text-sm font-medium" style="color: var(--color-text-muted)">{{ job.company }}</p>
      <div class="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-xs" style="color: var(--color-text-muted)">
        <span class="inline-flex items-center gap-1">
          <MapPin :size="14" /> {{ job.location }}
        </span>
        <span class="inline-flex items-center gap-1">
          <Briefcase :size="14" /> {{ job.type }}
        </span>
      </div>
    </div>
    <button
      type="button"
      class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full border transition hover:border-primary hover:text-primary active:scale-90"
      :style="{ borderColor: 'var(--color-border)', color: saved ? '#FD5C20' : 'var(--color-text-muted)' }"
      :aria-label="saved ? 'Remove from saved jobs' : 'Save job'"
      @click.stop.prevent="saved = !saved"
    >
      <Bookmark :size="16" :fill="saved ? '#FD5C20' : 'none'" />
    </button>
  </RouterLink>
</template>
