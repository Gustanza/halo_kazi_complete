<script setup>
import { computed } from 'vue'

const props = defineProps({
  // Raw backend enum value, e.g. "SUBMITTED", "SHORTLISTED", "ACTIVE", "PENDING_REVIEW".
  status: { type: String, required: true },
  // Which domain's enum this is - each has its own set of statuses and colors.
  kind: { type: String, default: 'application' }, // application | vacancy | employer
})

// Single source of truth for status -> label/color across the whole app. Previously
// Applications.vue and ViewApplication.vue each kept their own copy of this mapping.
const MAPS = {
  application: {
    SUBMITTED: { label: 'Applied', classes: 'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400' },
    IN_REVIEW: { label: 'In Review', classes: 'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400' },
    SHORTLISTED: { label: 'Shortlisted', classes: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400' },
    REJECTED: { label: 'Rejected', classes: 'bg-red-50 text-red-600 dark:bg-red-500/10 dark:text-red-400' },
  },
  vacancy: {
    DRAFT: { label: 'Draft', classes: 'bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-slate-300' },
    ACTIVE: { label: 'Active', classes: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400' },
    CLOSED: { label: 'Closed', classes: 'bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-slate-300' },
    EXPIRED: { label: 'Expired', classes: 'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400' },
    DELETED: { label: 'Deleted', classes: 'bg-red-50 text-red-600 dark:bg-red-500/10 dark:text-red-400' },
  },
  employer: {
    NOT_SUBMITTED: { label: 'Not Submitted', classes: 'bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-slate-300' },
    PENDING_REVIEW: { label: 'Pending Review', classes: 'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400' },
    APPROVED: { label: 'Approved', classes: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400' },
    REJECTED: { label: 'Rejected', classes: 'bg-red-50 text-red-600 dark:bg-red-500/10 dark:text-red-400' },
  },
}

const entry = computed(() => MAPS[props.kind]?.[props.status] ?? { label: props.status, classes: 'bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-slate-300' })
</script>

<template>
  <span class="inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold" :class="entry.classes">
    {{ entry.label }}
  </span>
</template>
