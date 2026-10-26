<script setup>
import { reactive } from 'vue'
import { SlidersHorizontal, X } from 'lucide-vue-next'

const filters = reactive({
  location: '',
  jobType: '',
  industry: '',
  level: '',
  salary: '',
  datePosted: '',
})

const fields = [
  { key: 'location', label: 'Location', placeholder: 'Select location' },
  { key: 'jobType', label: 'Job Type', placeholder: 'Select job type' },
  { key: 'industry', label: 'Industry/Sector', placeholder: 'Select industry' },
  { key: 'level', label: 'Experience Level', placeholder: 'Select level' },
  { key: 'salary', label: 'Salary Range', placeholder: 'Select level' },
  { key: 'datePosted', label: 'Date Posted', placeholder: 'Select level' },
]

function clearFilters() {
  for (const key of Object.keys(filters)) filters[key] = ''
}
</script>

<template>
  <aside class="card-surface h-fit p-5">
    <div class="mb-5 flex items-center gap-2">
      <SlidersHorizontal :size="18" class="text-primary" />
      <h3 class="text-base font-semibold" style="color: var(--color-text)">Filter Jobs</h3>
    </div>

    <div class="space-y-4">
      <div v-for="field in fields" :key="field.key">
        <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ field.label }}</label>
        <select
          v-model="filters[field.key]"
          class="w-full rounded-lg border bg-transparent px-3 py-2.5 text-sm transition-colors focus:border-primary focus:outline-none"
          style="border-color: var(--color-border); color: var(--color-text)"
        >
          <option value="">{{ field.placeholder }}</option>
        </select>
      </div>
    </div>

    <button
      type="button"
      class="press mt-5 inline-flex items-center gap-1.5 text-sm font-medium text-primary transition-colors hover:text-primary-600"
      @click="clearFilters"
    >
      <X :size="15" />
      Clear filters
    </button>
  </aside>
</template>
