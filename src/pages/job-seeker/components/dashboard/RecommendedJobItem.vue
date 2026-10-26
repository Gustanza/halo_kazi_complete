<script setup>
import { ref } from 'vue'
import { MapPin, Briefcase, Bookmark } from 'lucide-vue-next'
import CompanyBadge from '../../../../components/common/CompanyBadge.vue'

defineProps({ job: { type: Object, required: true } })
const saved = ref(false)
</script>

<template>
  <div class="card-hover flex items-center gap-4 rounded-xl border p-4 hover:border-primary" style="border-color: var(--color-border)">
    <CompanyBadge :initials="job.initials" :color="job.brandColor" :size="48" />
    <div class="min-w-0 flex-1">
      <div class="flex items-center gap-2">
        <h3 class="truncate text-sm font-semibold" style="color: var(--color-text)">{{ job.title }}</h3>
        <span v-if="job.isNew" class="rounded-full bg-primary-50 px-2 py-0.5 text-[10px] font-bold text-primary dark:bg-white/10">
          New
        </span>
      </div>
      <p class="text-xs font-medium" style="color: var(--color-text-muted)">{{ job.company }}</p>
      <div class="mt-1.5 flex flex-wrap items-center gap-x-3 gap-y-1 text-[11px]" style="color: var(--color-text-muted)">
        <span class="inline-flex items-center gap-1"><MapPin :size="12" /> {{ job.location }}</span>
        <span class="inline-flex items-center gap-1"><Briefcase :size="12" /> {{ job.type }}</span>
      </div>
    </div>
    <button
      type="button"
      class="press flex h-8 w-8 shrink-0 items-center justify-center rounded-full border transition-colors hover:border-primary"
      :style="{ borderColor: 'var(--color-border)', color: saved ? '#FD5C20' : 'var(--color-text-muted)' }"
      :aria-label="saved ? 'Remove from saved jobs' : 'Save job'"
      @click="saved = !saved"
    >
      <Bookmark :size="14" :fill="saved ? '#FD5C20' : 'none'" />
    </button>
  </div>
</template>
