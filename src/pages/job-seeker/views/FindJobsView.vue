
<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search,
  MapPin,
  Bookmark,
  BriefcaseBusiness,
  Clock3,
  ArrowRight,
  SlidersHorizontal,
  BellRing,
  Star,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import Pagination from '../../../components/ui/Pagination.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as jobsApi from '../../../api/jobs'
import * as applicationsApi from '../../../api/applications'
import { getLookups } from '../../../api/publicApi'
import { useToast } from '../../../composables/useToast'

const router = useRouter()
const toast = useToast()

const searchText = ref('')
const locationFilter = ref('') // region code
const categoryFilter = ref('') // category code
const jobTypeFilter = ref('') // employment type code; '' = all
const currentPage = ref(1)
const pageSize = 3

const categories = ref([]) // {code, nameEn}[]
const regions = ref([]) // {code, nameEn}[]
const employmentTypes = ref([]) // {code, nameEn}[]
const jobTypeOptions = computed(() => [
  { code: '', label: 'All Jobs' },
  ...employmentTypes.value.map((t) => ({ code: t.code, label: t.nameEn })),
])
const jobs = ref([])
const total = ref(0)
const loading = ref(true)
const savedCount = ref(0)

let debounceTimer = null

async function load() {
  loading.value = true
  try {
    const result = await jobsApi.listJobs({
      search: searchText.value,
      location: locationFilter.value,
      category: categoryFilter.value,
      employmentType: jobTypeFilter.value,
      page: currentPage.value,
      pageSize,
    })
    jobs.value = result.items
    total.value = result.total
    const savedIds = await jobsApi.listSavedJobIds()
    savedCount.value = savedIds.length
  } catch {
    toast.error('Could not load jobs. Please try again.')
  } finally {
    loading.value = false
  }
}

function resetPage() {
  currentPage.value = 1
}

watch([locationFilter, categoryFilter, jobTypeFilter, currentPage], load)
watch(searchText, () => {
  resetPage()
  window.clearTimeout(debounceTimer)
  debounceTimer = window.setTimeout(load, 300)
})

onMounted(async () => {
  try {
    const lookups = await getLookups()
    categories.value = lookups.categories
    regions.value = lookups.regions
    employmentTypes.value = lookups.employmentTypes
  } catch {
    // Filters just stay empty; search/pagination still work without them.
  }
  load()
})
onUnmounted(() => window.clearTimeout(debounceTimer))

function setJobType(code) {
  jobTypeFilter.value = code
  resetPage()
}

async function toggleSaved(job) {
  const wasSaved = job.saved
  job.saved = !wasSaved
  savedCount.value += wasSaved ? -1 : 1
  try {
    await (wasSaved ? jobsApi.unsaveJob(job.id) : jobsApi.saveJob(job.id))
  } catch {
    job.saved = wasSaved
    savedCount.value += wasSaved ? 1 : -1
    toast.error('Could not update saved jobs. Please try again.')
  }
}

async function applyForJob(jobId) {
  try {
    const alreadyApplied = await applicationsApi.hasAppliedTo(jobId)
    if (alreadyApplied) {
      toast.info('You already applied to this job. Check your Applications page for status.')
    }
  } catch {
    // Non-blocking check - fall through to the application page either way.
  }
  router.push(`/dashboard/apply/${jobId}`)
}

function clearFilters() {
  searchText.value = ''
  locationFilter.value = ''
  categoryFilter.value = ''
  setJobType('')
}
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-3.5">

      <!-- Hero -->
      <section
        class="relative flex min-h-[112px] items-center overflow-hidden rounded-xl border bg-white"
        style="border-color:#ffd3bd"
      >
        <div class="relative z-10 max-w-2xl px-5 py-5 sm:px-7">
          <h1
            class="text-2xl font-extrabold tracking-tight sm:text-3xl"
            style="color:#202b3c"
          >
            Find Your <span style="color:#ff5a0a">Dream Job</span>
          </h1>
          <p class="mt-1 max-w-lg text-sm leading-5 text-slate-700">
            Discover thousands of job opportunities and take the next step
            in your career. Your future is waiting!
          </p>
        </div>

        <div
          class="absolute inset-y-0 right-0 hidden w-[31%] bg-cover bg-center sm:block"
          style="background-image:linear-gradient(90deg,white 0%,transparent 38%),url('https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=700&q=80')"
          role="img"
          aria-label="Professional woman smiling"
        />
      </section>

      <!-- Search bar -->
      <section
        class="rounded-xl border bg-white p-2.5"
        style="border-color:#ffd3bd"
      >
        <div class="grid gap-2 sm:grid-cols-[minmax(180px,1.5fr)_minmax(130px,1fr)_minmax(125px,1fr)_auto]">
          <label
            class="flex min-w-0 items-center gap-2 rounded-md border px-2.5"
            style="border-color:#ffd3bd"
          >
            <Search :size="17" class="shrink-0 text-slate-700" />
            <input
              v-model="searchText"
              type="search"
              placeholder="Job title, company, or keyword"
              class="min-w-0 w-full py-2 text-xs text-slate-800 outline-none placeholder:text-slate-600"
            />
          </label>

          <label
            class="flex min-w-0 items-center gap-2 rounded-md border px-2.5"
            style="border-color:#ffd3bd"
          >
            <MapPin :size="17" class="shrink-0 text-slate-700" />
            <select
              v-model="locationFilter"
              class="min-w-0 w-full bg-white py-2 text-xs text-slate-800 outline-none"
              @change="resetPage"
            >
              <option value="">All locations</option>
              <option v-for="region in regions" :key="region.code" :value="region.code">{{ region.nameEn }}</option>
            </select>
          </label>

          <label
            class="flex min-w-0 items-center gap-2 rounded-md border px-2.5"
            style="border-color:#ffd3bd"
          >
            <SlidersHorizontal :size="15" class="shrink-0 text-slate-700" />
            <select
              v-model="categoryFilter"
              class="min-w-0 w-full bg-white py-2 text-xs text-slate-800 outline-none"
              @change="resetPage"
            >
              <option value="">All Categories</option>
              <option v-for="cat in categories" :key="cat.code" :value="cat.code">{{ cat.nameEn }}</option>
            </select>
          </label>

          <button
            type="button"
            class="inline-flex items-center justify-center gap-2 rounded-md px-4 py-2 text-sm font-semibold text-white transition hover:bg-orange-600"
            style="background:#ff5a0a"
            @click="resetPage(); load()"
          >
            <ArrowRight :size="16" />
            Search
          </button>
        </div>
      </section>

      <!-- Job type chips -->
      <div class="flex flex-wrap gap-2">
        <button
          v-for="opt in jobTypeOptions"
          :key="opt.code"
          type="button"
          class="rounded-md border px-3 py-2 text-xs font-medium transition"
          :style="jobTypeFilter === opt.code
            ? 'background:#ff5a0a;color:white;border-color:#ff5a0a'
            : 'background:white;color:#273244;border-color:#ffd3bd'"
          @click="setJobType(opt.code)"
        >
          {{ opt.label }}
        </button>

        <RouterLink
          to="/dashboard/saved-jobs"
          class="ml-auto inline-flex items-center gap-1.5 rounded-md px-3 py-2 text-xs font-semibold text-slate-700 hover:text-orange-600"
        >
          <Bookmark :size="15" />
          Saved ({{ savedCount }})
        </RouterLink>
      </div>

      <!-- Results -->
      <div class="space-y-2.5">
        <template v-if="loading">
          <Skeleton v-for="i in pageSize" :key="i" variant="card" height="7rem" />
        </template>

        <template v-else>
          <article
            v-for="job in jobs"
            :key="job.id"
            class="rounded-xl border bg-white p-3.5 sm:px-4 sm:py-3.5"
            style="border-color:#ffd3bd"
          >
            <div class="grid gap-3 sm:grid-cols-[minmax(0,1fr)_220px] sm:gap-4">
              <!-- Main job information -->
              <div class="flex min-w-0 gap-3">
                <div
                  class="mt-1 flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-xl font-extrabold"
                  :style="{ backgroundColor: job.companyColor + '1a', color: job.companyColor }"
                >
                  {{ job.companyInitials }}
                </div>

                <div class="min-w-0 flex-1">
                  <h2 class="text-sm font-extrabold leading-5 text-slate-900">
                    {{ job.title }}
                  </h2>
                  <p class="text-xs font-semibold text-slate-800">
                    {{ job.company }}
                    <Star
                      :size="12"
                      class="ml-1 inline text-sky-500"
                      fill="currentColor"
                    />
                  </p>

                  <div class="mt-1.5 flex flex-wrap gap-x-3 gap-y-1 text-[10px] text-slate-700">
                    <span class="inline-flex items-center gap-1">
                      <MapPin :size="12" /> {{ job.location }}
                    </span>
                    <span class="inline-flex items-center gap-1">
                      <BriefcaseBusiness :size="12" /> {{ job.employmentType }}
                    </span>
                    <span class="inline-flex items-center gap-1">
                      <Clock3 :size="12" /> Posted {{ new Date(job.postedAt).toLocaleDateString() }}
                    </span>
                  </div>

                  <p class="mt-1.5 text-[11px] leading-4 text-slate-700">
                    {{ job.description }}
                  </p>
                </div>
              </div>

              <!-- Job tags and actions -->
              <div class="flex flex-col justify-between gap-3 sm:items-end">
                <div class="flex w-full items-start justify-between gap-2 sm:justify-end">
                  <span
                    class="rounded-full px-2.5 py-1 text-[10px] font-medium"
                    :style="job.remote
                      ? 'background:#ddf8e6;color:#18834a'
                      : 'background:#f1f5f9;color:#475569'"
                  >
                    {{ job.remote ? 'Remote Friendly' : 'On-site' }}
                  </span>

                  <button
                    type="button"
                    :aria-label="job.saved ? 'Remove saved job' : 'Save job'"
                    :title="job.saved ? 'Remove saved job' : 'Save job'"
                    class="rounded p-0.5 text-slate-800 transition hover:text-orange-600"
                    @click="toggleSaved(job)"
                  >
                    <Bookmark
                      :size="17"
                      :fill="job.saved ? 'currentColor' : 'none'"
                    />
                  </button>
                </div>

                <div class="flex flex-wrap items-center gap-2 sm:justify-end">
                  <span
                    v-for="tag in job.tags"
                    :key="tag"
                    class="rounded-full bg-slate-100 px-2.5 py-1 text-[10px] text-slate-600"
                  >
                    {{ tag }}
                  </span>
                </div>

                <div class="flex w-full items-center justify-between gap-2 sm:justify-end">
                  <p class="text-[10px] text-slate-600 sm:text-right">
                    <strong v-if="job.salaryMin != null && job.salaryMax != null" class="text-[11px] text-slate-900">
                      {{ job.salaryCurrency }} {{ job.salaryMin.toLocaleString() }} - {{ job.salaryMax.toLocaleString() }}
                    </strong>
                    <strong v-else class="text-[11px] text-slate-500">Salary not disclosed</strong>
                    <span v-if="job.salaryMin != null && job.salaryMax != null" class="block">/ month</span>
                  </p>

                  <button
                    type="button"
                    class="shrink-0 rounded-md px-3 py-2 text-xs font-semibold text-white transition hover:bg-orange-600"
                    style="background:#ff5a0a"
                    title="Job application"
                    @click="applyForJob(job.id)"
                  >
                    Apply Now
                  </button>
                </div>
              </div>
            </div>
          </article>

          <EmptyState
            v-if="!jobs.length"
            :icon="Search"
            title="No jobs found"
            description="Try changing your search terms or filters."
            action-label="Clear filters"
            @action="clearFilters"
          />
        </template>
      </div>

      <!-- Pagination Footer -->
      <Pagination v-if="!loading && jobs.length" v-model="currentPage" :total-pages="Math.max(1, Math.ceil(total / pageSize))" />

      <!-- Job alert prompt for smaller screens -->
      <RouterLink
        to="/dashboard/job-alerts"
        class="flex items-center gap-3 rounded-xl border p-4 sm:hidden"
        style="border-color:#ffd3bd;background:#fff7f2"
      >
        <BellRing :size="23" style="color:#ff5a0a" />
        <div class="flex-1">
          <p class="text-sm font-bold text-slate-900">Get Hired Faster</p>
          <p class="text-xs text-slate-600">
            Create job alerts for new opportunities.
          </p>
        </div>
        <ArrowRight :size="17" style="color:#ff5a0a" />
      </RouterLink>
    </div>
  </DashboardLayout>
</template>
