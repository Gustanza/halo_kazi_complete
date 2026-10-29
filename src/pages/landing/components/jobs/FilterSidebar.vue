<script setup>
import { computed } from 'vue'
import { SlidersHorizontal, X } from 'lucide-vue-next'

// modelValue holds the API filter codes: { region, type, category, experience, salary, posted }.
// lookups is the GET /api/public/lookups response (null while loading).
const props = defineProps({
  modelValue: { type: Object, required: true },
  lookups: { type: Object, default: null },
})
const emit = defineEmits(['update:modelValue', 'clear'])

const byName = (items) => (items ?? []).map((item) => ({ value: item.code, label: item.nameEn }))
const byLabel = (items) => (items ?? []).map((item) => ({ value: item.code, label: item.label }))

const fields = computed(() => [
  { key: 'region', label: 'Location', placeholder: 'Any location', options: byName(props.lookups?.regions) },
  { key: 'type', label: 'Job Type', placeholder: 'Any job type', options: byName(props.lookups?.employmentTypes) },
  { key: 'category', label: 'Industry/Sector', placeholder: 'Any industry', options: byName(props.lookups?.categories) },
  { key: 'experience', label: 'Experience Level', placeholder: 'Any level', options: byLabel(props.lookups?.experienceLevels) },
  { key: 'salary', label: 'Salary Range', placeholder: 'Any salary', options: byLabel(props.lookups?.salaryRanges) },
  { key: 'posted', label: 'Date Posted', placeholder: 'Any time', options: byLabel(props.lookups?.datePosted) },
])

function update(key, value) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
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
        <label :for="`filter-${field.key}`" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">
          {{ field.label }}
        </label>
        <select
          :id="`filter-${field.key}`"
          :value="modelValue[field.key]"
          :disabled="!lookups"
          class="w-full rounded-lg border bg-transparent px-3 py-2.5 text-sm transition-colors focus:border-primary focus:outline-none disabled:opacity-60"
          style="border-color: var(--color-border); color: var(--color-text)"
          @change="update(field.key, $event.target.value)"
        >
          <option value="">{{ field.placeholder }}</option>
          <option v-for="option in field.options" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
      </div>
    </div>

    <button
      type="button"
      class="press mt-5 inline-flex items-center gap-1.5 text-sm font-medium text-primary transition-colors hover:text-primary-600"
      @click="emit('clear')"
    >
      <X :size="15" />
      Clear filters
    </button>
  </aside>
</template>
