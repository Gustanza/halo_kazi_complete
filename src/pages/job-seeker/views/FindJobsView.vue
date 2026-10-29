
<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search,
  MapPin,
  Bookmark,
  BriefcaseBusiness,
  Clock3,
  ChevronLeft,
  ChevronRight,
  ArrowRight,
  SlidersHorizontal,
  BellRing,
  FileText,
  Star,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const router = useRouter()

const searchText = ref('')
const locationFilter = ref('')
const categoryFilter = ref('All Categories')
const jobTypeFilter = ref('All Jobs')
const currentPage = ref(1)
const pageSize = 3
const savedIds = ref([2])

const jobs = ref([
  {
    id: 1,
    title: 'Frontend Developer',
    company: 'Google',
    location: 'Dar es Salaam, Tanzania',
    category: 'Technology',
    type: 'Full time',
    salary: 'TSh 1,500,000 - 2,500,000',
    posted: 'Posted 2 days ago',
    description: 'We are looking for a skilled frontend Developer to join our team and help build amazing products for millions of users.',
    logo: 'G',
    color: '#fff1f1',
    logoColor: '#ea4335',
    tags: ['React', 'Typescript'],
    badge: 'Remote Friendly',
  },
  {
    id: 2,
    title: 'Full Stack Developer',
    company: 'TechSolution Ltd',
    location: 'Dar es Salaam, Tanzania',
    category: 'Technology',
    type: 'Full time',
    salary: 'TSh 1,200,000 - 2,000,000',
    posted: 'Posted 5 days ago',
    description: 'We are looking for a talented Full Stack Developer with experience in modern web technologies to join our growing team.',
    logo: '⬡',
    color: '#eee7ff',
    logoColor: '#7138e8',
    tags: ['Typescript'],
    badge: 'Remote Friendly',
  },
  {
    id: 3,
    title: 'Customer Care Agency',
    company: 'Vodacom Tanzania',
    location: 'Dar es Salaam, Tanzania',
    category: 'Customer Service',
    type: 'Full time',
    salary: 'TSh 600,000 - 900,000',
    posted: 'Posted 6 days ago',
    description: 'Join our customer care team and help us deliver world class support to our valued customers.',
    logo: 'v',
    color: '#fff0f0',
    logoColor: '#e21b2d',
    tags: ['React', 'Typescript'],
    badge: 'Hybrid',
  },
  {
    id: 4,
    title: 'Data Analyst',
    company: 'Smart Finance Ltd',
    location: 'Dar es Salaam, Tanzania',
    category: 'Finance',
    type: 'Full time',
    salary: 'TSh 900,000 - 1,400,000',
    posted: 'Posted 1 day ago',
    description: 'Analyze business data and prepare reports that support better decisions.',
    logo: 'S',
    color: '#d9f5e5',
    logoColor: '#16864a',
    tags: ['Excel', 'SQL'],
    badge: 'Hybrid',
  },
  {
    id: 5,
    title: 'UI/UX Designer',
    company: 'Creative Hub',
    location: 'Arusha, Tanzania',
    category: 'Design',
    type: 'Part time',
    salary: 'TSh 700,000 - 1,000,000',
    posted: 'Posted 3 days ago',
    description: 'Create intuitive digital experiences, wireframes, and prototypes for web products.',
    logo: 'C',
    color: '#f1e2ff',
    logoColor: '#8543e8',
    tags: ['Figma', 'Prototyping'],
    badge: 'Remote Friendly',
  },
  {
    id: 6,
    title: 'Network Support Officer',
    company: 'Connect Services',
    location: 'Mwanza, Tanzania',
    category: 'Technology',
    type: 'Full time',
    salary: 'TSh 650,000 - 950,000',
    posted: 'Posted 6 days ago',
    description: 'Troubleshoot network issues and support day-to-day IT infrastructure.',
    logo: 'N',
    color: '#e4eaff',
    logoColor: '#3858a8',
    tags: ['Networking', 'IT Support'],
    badge: 'On-site',
  },
])

const filteredJobs = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  const location = locationFilter.value.trim().toLowerCase()

  return jobs.value.filter((job) => {
    const matchesSearch =
      !query ||
      job.title.toLowerCase().includes(query) ||
      job.company.toLowerCase().includes(query) ||
      job.description.toLowerCase().includes(query) ||
      job.tags.some((tag) => tag.toLowerCase().includes(query))

    const matchesLocation =
      !location || job.location.toLowerCase().includes(location)

    const matchesCategory =
      categoryFilter.value === 'All Categories' ||
      job.category === categoryFilter.value

    const matchesType =
      jobTypeFilter.value === 'All Jobs' ||
      (jobTypeFilter.value === 'Remote' && job.badge === 'Remote Friendly') ||
      job.type === jobTypeFilter.value

    return matchesSearch && matchesLocation && matchesCategory && matchesType
  })
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(filteredJobs.value.length / pageSize)),
)

const visibleJobs = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredJobs.value.slice(start, start + pageSize)
})

const firstResult = computed(() =>
  filteredJobs.value.length ? (currentPage.value - 1) * pageSize + 1 : 0,
)

const lastResult = computed(() =>
  Math.min(currentPage.value * pageSize, filteredJobs.value.length),
)

function resetPage() {
  currentPage.value = 1
}

function toggleSaved(id) {
  savedIds.value = savedIds.value.includes(id)
    ? savedIds.value.filter((savedId) => savedId !== id)
    : [...savedIds.value, id]
}

function setJobType(type) {
  jobTypeFilter.value = type
  resetPage()
}

// Navigate to the application page for the selected job.
function applyForJob(jobId) {
  router.push(`/dashboard/apply/${jobId}`)
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
              @input="resetPage"
            />
          </label>

          <label
            class="flex min-w-0 items-center gap-2 rounded-md border px-2.5"
            style="border-color:#ffd3bd"
          >
            <MapPin :size="17" class="shrink-0 text-slate-700" />
            <input
              v-model="locationFilter"
              type="text"
              placeholder="All locations"
              class="min-w-0 w-full py-2 text-xs text-slate-800 outline-none placeholder:text-slate-600"
              @input="resetPage"
            />
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
              <option>All Categories</option>
              <option>Technology</option>
              <option>Finance</option>
              <option>Design</option>
              <option>Customer Service</option>
            </select>
          </label>

          <button
            type="button"
            class="inline-flex items-center justify-center gap-2 rounded-md px-4 py-2 text-sm font-semibold text-white transition hover:bg-orange-600"
            style="background:#ff5a0a"
            @click="resetPage"
          >
            <ArrowRight :size="16" />
            Search
          </button>
        </div>
      </section>

      <!-- Job type chips -->
      <div class="flex flex-wrap gap-2">
        <button
          v-for="type in ['All Jobs', 'Full time', 'Part time', 'Remote']"
          :key="type"
          type="button"
          class="rounded-md border px-3 py-2 text-xs font-medium transition"
          :style="jobTypeFilter === type
            ? 'background:#ff5a0a;color:white;border-color:#ff5a0a'
            : 'background:white;color:#273244;border-color:#ffd3bd'"
          @click="setJobType(type)"
        >
          {{ type }}
          <span v-if="type === 'All Jobs'">({{ jobs.length }})</span>
        </button>

        <RouterLink
          to="/dashboard/saved-jobs"
          class="ml-auto inline-flex items-center gap-1.5 rounded-md px-3 py-2 text-xs font-semibold text-slate-700 hover:text-orange-600"
        >
          <Bookmark :size="15" />
          Saved ({{ savedIds.length }})
        </RouterLink>
      </div>

      <!-- Results -->
      <div class="space-y-2.5">
        <article
          v-for="job in visibleJobs"
          :key="job.id"
          class="rounded-xl border bg-white p-3.5 sm:px-4 sm:py-3.5"
          style="border-color:#ffd3bd"
        >
          <div class="grid gap-3 sm:grid-cols-[minmax(0,1fr)_220px] sm:gap-4">
            <!-- Main job information -->
            <div class="flex min-w-0 gap-3">
              <div
                class="mt-1 flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-xl font-extrabold"
                :style="{ backgroundColor: job.color, color: job.logoColor }"
              >
                {{ job.logo }}
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
                    <BriefcaseBusiness :size="12" /> {{ job.type }}
                  </span>
                  <span class="inline-flex items-center gap-1">
                    <Clock3 :size="12" /> {{ job.posted }}
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
                  :style="job.badge === 'Hybrid'
                    ? 'background:#f0ddff;color:#7835b8'
                    : job.badge === 'Remote Friendly'
                      ? 'background:#ddf8e6;color:#18834a'
                      : 'background:#f1f5f9;color:#475569'"
                >
                  {{ job.badge }}
                </span>

                <button
                  type="button"
                  :aria-label="savedIds.includes(job.id) ? 'Remove saved job' : 'Save job'"
                  :title="savedIds.includes(job.id) ? 'Remove saved job' : 'Save job'"
                  class="rounded p-0.5 text-slate-800 transition hover:text-orange-600"
                  @click="toggleSaved(job.id)"
                >
                  <Bookmark
                    :size="17"
                    :fill="savedIds.includes(job.id) ? 'currentColor' : 'none'"
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
                  <strong class="text-[11px] text-slate-900">
                    {{ job.salary }}
                  </strong>
                  <span class="block">/ month</span>
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

        <div
          v-if="!visibleJobs.length"
          class="rounded-xl border bg-white px-5 py-12 text-center"
          style="border-color:#ffd3bd"
        >
          <Search :size="28" class="mx-auto text-slate-400" />
          <h3 class="mt-3 font-bold text-slate-900">No jobs found</h3>
          <p class="mt-1 text-sm text-slate-500">
            Try changing your search terms or filters.
          </p>
          <button
            type="button"
            class="mt-4 rounded-md px-4 py-2 text-sm font-semibold text-white"
            style="background:#ff5a0a"
            @click="searchText = ''; locationFilter = ''; categoryFilter = 'All Categories'; setJobType('All Jobs')"
          >
            Clear filters
          </button>
        </div>
      </div>

      <!-- Pagination Footer -->
      <nav
        v-if="filteredJobs.length"
        class="flex items-center justify-between py-6 px-2"
        aria-label="Job results pages"
      >
        <button
          type="button"
          class="flex items-center gap-1 text-xs font-semibold text-slate-400 hover:text-slate-600 transition disabled:opacity-40"
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          <ChevronLeft :size="16" /> Previous
        </button>

        <div class="flex items-center gap-1.5 sm:gap-2">
          <button
            v-for="page in totalPages"
            :key="page"
            type="button"
            class="h-8 w-8 rounded-xl text-xs font-bold transition flex items-center justify-center"
            :style="currentPage === page
              ? 'background:#ff5a0a;color:white;box-shadow:0 1px 2px rgba(0,0,0,0.05)'
              : 'color:#475569'"
            @click="currentPage = page"
          >
            {{ page }}
          </button>
        </div>

        <button
          type="button"
          class="flex items-center gap-1 text-xs font-semibold text-slate-800 hover:text-orange-600 transition disabled:opacity-40"
          :disabled="currentPage === totalPages"
          @click="currentPage++"
        >
          Next <ChevronRight :size="16" />
        </button>
      </nav>

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
