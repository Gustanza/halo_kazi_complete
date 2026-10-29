<script setup>
import { onMounted, ref, watch } from 'vue'
import {
  Bookmark,
  MapPin,
  BriefcaseBusiness,
  Clock3,
  Search,
  ArrowRight,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as jobsApi from '../../../api/jobs'
import * as applicationsApi from '../../../api/applications'
import { useToast } from '../../../composables/useToast'

const toast = useToast()
const searchText = ref('')
const sortBy = ref('newest')
const loading = ref(true)
const jobs = ref([])
const appliedJobIds = ref(new Set())

async function load() {
  loading.value = true
  try {
    const result = await jobsApi.listJobs({ savedOnly: true, search: searchText.value, pageSize: 100 })
    let items = result.items
    if (sortBy.value === 'oldest') items = [...items].reverse()
    else if (sortBy.value === 'salary') items = [...items].sort((a, b) => (b.salaryMax ?? 0) - (a.salaryMax ?? 0))
    jobs.value = items

    const appliedChecks = await Promise.all(items.map((job) => applicationsApi.hasAppliedTo(job.id)))
    appliedJobIds.value = new Set(items.filter((_, i) => appliedChecks[i]).map((job) => job.id))
  } catch {
    toast.error('Could not load saved jobs.')
  } finally {
    loading.value = false
  }
}

async function removeSavedJob(job) {
  const previous = jobs.value
  jobs.value = jobs.value.filter((j) => j.id !== job.id)
  try {
    await jobsApi.unsaveJob(job.id)
  } catch {
    jobs.value = previous
    toast.error('Could not remove this job. Please try again.')
  }
}

watch(sortBy, load)
let debounceTimer = null
watch(searchText, () => {
  window.clearTimeout(debounceTimer)
  debounceTimer = window.setTimeout(load, 300)
})

onMounted(load)
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-6">

      <!-- Header with integrated Search & Sort -->
      <header
        class="rounded-2xl border bg-white p-6 shadow-sm"
        style="border-color: #ffd3bd"
      >
        <div class="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
          <div class="flex items-start gap-4">
            <div
              class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl shadow-sm"
              style="background: #ffe5d8; color: #ff5a0a"
            >
              <Bookmark :size="24" />
            </div>
            <div>
              <h1 class="text-2xl font-bold text-gray-900">
                Saved Jobs
              </h1>
              <p class="mt-1 text-sm text-gray-600">
                Here are the jobs you've saved. Keep track of your favorite opportunities and apply when you're ready
              </p>
            </div>
          </div>

          <!-- Search & Sort Controls -->
          <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
            <div class="relative w-full sm:w-64">
              <Search
                :size="16"
                class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                v-model="searchText"
                type="search"
                placeholder="Search saved jobs..."
                class="w-full rounded-xl border border-gray-200 bg-gray-50/50 py-2.5 pl-10 pr-4 text-xs font-medium text-gray-900 outline-none transition focus:border-orange-500 focus:bg-white focus:ring-2 focus:ring-orange-100"
              />
            </div>

            <div class="flex items-center gap-2">
              <span class="text-xs font-medium text-gray-500 whitespace-nowrap">Sort by:</span>
              <select
                v-model="sortBy"
                class="rounded-xl border border-gray-200 bg-gray-50/50 px-3 py-2.5 text-xs font-semibold text-gray-800 outline-none transition focus:border-orange-500 focus:bg-white focus:ring-2 focus:ring-orange-100 cursor-pointer"
              >
                <option value="newest">Newest first</option>
                <option value="oldest">Oldest first</option>
                <option value="salary">Highest salary</option>
              </select>
            </div>
          </div>
        </div>
      </header>

      <template v-if="loading">
        <div class="space-y-4">
          <Skeleton v-for="i in 3" :key="i" variant="card" height="7rem" />
        </div>
      </template>

      <!-- Saved Job Cards List -->
      <div v-else-if="jobs.length" class="space-y-4">
        <article
          v-for="job in jobs"
          :key="job.id"
          class="group relative rounded-2xl border bg-white p-5 shadow-sm transition hover:shadow-md"
          style="border-color: #ffd3bd"
        >
          <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">

            <!-- Left Group: Logo, Title, Company, Meta, & Description -->
            <div class="flex items-start gap-4 min-w-0 flex-1">
              <div
                class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-lg font-bold shadow-sm"
                :style="{ backgroundColor: job.companyColor + '1a', color: job.companyColor }"
              >
                {{ job.companyInitials }}
              </div>

              <div class="min-w-0 flex-1">
                <div class="flex items-center gap-2 flex-wrap">
                  <h3 class="text-sm font-bold text-gray-900 group-hover:text-orange-600 transition">
                    {{ job.title }}
                  </h3>
                </div>

                <p class="mt-0.5 text-xs font-medium text-gray-700 flex items-center gap-1">
                  {{ job.company }}
                  <span class="text-blue-500">✓</span>
                </p>

                <!-- Meta attributes -->
                <div class="mt-2.5 flex flex-wrap items-center gap-x-4 gap-y-1.5 text-xs text-gray-500">
                  <span class="inline-flex items-center gap-1.5">
                    <MapPin :size="13" class="text-gray-400" /> {{ job.location }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <BriefcaseBusiness :size="13" class="text-gray-400" /> {{ job.employmentType }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <Clock3 :size="13" class="text-gray-400" /> Posted {{ new Date(job.postedAt).toLocaleDateString() }}
                  </span>
                </div>

                <!-- Description inside left column -->
                <p class="mt-2.5 text-xs leading-relaxed text-gray-600 line-clamp-2">
                  {{ job.description }}
                </p>
              </div>
            </div>

            <!-- Right Group: Status Badge on Top of Salary, Followed by Action Button & Bookmark Icon -->
            <div class="flex flex-col lg:items-end justify-between gap-3 border-t lg:border-t-0 pt-3 lg:pt-0 border-gray-100 shrink-0">

              <!-- Status Badge on Top -->
              <div class="flex items-center gap-3">
                <span
                  :class="[
                    'inline-flex items-center rounded-full px-3 py-1 text-[11px] font-semibold',
                    appliedJobIds.has(job.id) ? 'bg-blue-100 text-blue-700' : 'bg-emerald-100 text-emerald-700'
                  ]"
                >
                  {{ appliedJobIds.has(job.id) ? 'Applied' : 'Saved' }}
                </span>

                <button
                  type="button"
                  class="rounded-xl p-1.5 text-orange-600 transition hover:bg-orange-50 shrink-0"
                  :aria-label="`Remove ${job.title} from saved jobs`"
                  title="Remove saved job"
                  @click="removeSavedJob(job)"
                >
                  <Bookmark :size="20" class="fill-orange-600" />
                </button>
              </div>

              <!-- Salary Below Status -->
              <div>
                <p v-if="job.salaryMin != null && job.salaryMax != null" class="text-xs font-bold text-gray-900">
                  {{ job.salaryCurrency }} {{ job.salaryMin.toLocaleString() }} - {{ job.salaryMax.toLocaleString() }}
                  <span class="font-normal text-gray-500">/ month</span>
                </p>
                <p v-else class="text-xs font-semibold text-gray-400">Salary not disclosed</p>
              </div>

              <!-- Action Button -->
              <div class="flex items-center gap-2 mt-1">
                <RouterLink
                  v-if="appliedJobIds.has(job.id)"
                  to="/dashboard/applications"
                  class="inline-flex items-center gap-2 rounded-xl bg-orange-600 px-4 py-2 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700"
                >
                  View Application <ArrowRight :size="14" />
                </RouterLink>
                <RouterLink
                 v-else
                 :to="`/dashboard/apply/${job.id}`"
                 class="inline-flex items-center gap-2 rounded-xl bg-orange-600 px-5 py-2 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700"
                 style="background: #ff5a0a"
                 >
                 Apply Now <ArrowRight :size="14" />
                </RouterLink>
              </div>

            </div>

          </div>
        </article>
      </div>

      <!-- Empty state -->
      <EmptyState
        v-else
        :icon="Bookmark"
        title="No saved jobs yet"
        description="Save jobs you are interested in and they will appear here."
        action-label="Browse Jobs"
        @action="$router.push('/dashboard/find-jobs')"
      />

    </div>
  </DashboardLayout>
</template>
