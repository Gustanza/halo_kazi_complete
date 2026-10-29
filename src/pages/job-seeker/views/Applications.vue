<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import {
  Search,
  MapPin,
  ChevronDown,
  ChevronRight,
  ChevronLeft,
  CalendarDays,
  BriefcaseBusiness,
  X,
  ArrowRight
} from 'lucide-vue-next'

const activeTab = ref('All Applications')
const searchQuery = ref('')
const selectedLocation = ref('All locations')
const selectedStatus = ref('All statuses')
const currentPage = ref(1)

const pageSize = 4

const applications = ref([
  {
    id: 1,
    title: 'Frontend Developer',
    company: 'Google',
    location: 'Dar es Salaam, Tanzania',
    applied: '12 Apr 2026',
    status: 'Shortlisted',
    nextStep: 'Technical Interview',
    logo: 'G',
    logoStyle: 'google',
  },
  {
    id: 2,
    title: 'Full Stack Developer',
    company: 'TechSolution Ltd',
    location: 'Dar es Salaam, Tanzania',
    applied: '17 May 2026',
    status: 'Interview',
    nextStep: 'Attend Interview',
    logo: '⬡',
    logoStyle: 'tech',
  },
  {
    id: 3,
    title: 'Customer Care Agency',
    company: 'Vodacom Tanzania',
    location: 'Dar es Salaam, Tanzania',
    applied: '25 August 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'v',
    logoStyle: 'vodacom',
  },
  {
    id: 4,
    title: 'IT Support Specialist',
    company: 'CRDB Bank PLC',
    location: 'Dar es Salaam, Tanzania',
    applied: '09 July 2026',
    status: 'Rejected',
    nextStep: 'Not selected for this role',
    logo: 'CRDB',
    logoStyle: 'crdb',
  },
  {
    id: 5,
    title: 'UI/UX Designer',
    company: 'Creative Studio',
    location: 'Arusha, Tanzania',
    applied: '15 August 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'CS',
    logoStyle: 'creative',
  },
  {
    id: 6,
    title: 'Software Engineer',
    company: 'Tanzania Tech',
    location: 'Dar es Salaam, Tanzania',
    applied: '20 August 2026',
    status: 'Shortlisted',
    nextStep: 'Technical Interview',
    logo: 'TT',
    logoStyle: 'tech',
  },
  {
    id: 7,
    title: 'Product Designer',
    company: 'Digital Solutions',
    location: 'Dodoma, Tanzania',
    applied: '02 September 2026',
    status: 'Interview',
    nextStep: 'Attend Interview',
    logo: 'DS',
    logoStyle: 'creative',
  },
  {
    id: 8,
    title: 'Business Analyst',
    company: 'Finance Group',
    location: 'Dar es Salaam, Tanzania',
    applied: '10 September 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'FG',
    logoStyle: 'tech',
  },
  {
    id: 9,
    title: 'Network Administrator',
    company: 'Telecom Services',
    location: 'Arusha, Tanzania',
    applied: '18 September 2026',
    status: 'Rejected',
    nextStep: 'Not selected for this role',
    logo: 'TS',
    logoStyle: 'creative',
  },
  {
    id: 10,
    title: 'Junior Developer',
    company: 'WebWorks Ltd',
    location: 'Dodoma, Tanzania',
    applied: '25 September 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'W',
    logoStyle: 'tech',
  },
])

const tabs = [
  'All Applications',
  'Applied',
  'Shortlisted',
  'Interview',
  'Rejected',
]

const statusClasses = {
  Applied: 'bg-green-100 text-green-700',
  Shortlisted: 'bg-green-100 text-green-700',
  Interview: 'bg-sky-100 text-sky-700',
  Rejected: 'bg-slate-200 text-slate-600',
}

const locations = computed(() => [
  'All locations',
  ...new Set(applications.value.map(app => app.location)),
])

const tabCount = (tab) => {
  if (tab === 'All Applications') {
    return applications.value.length
  }

  return applications.value.filter(
    app => app.status === tab
  ).length
}

const filteredApplications = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()

  return applications.value.filter(app => {
    const matchesTab =
      activeTab.value === 'All Applications' ||
      app.status === activeTab.value

    const matchesSearch =
      !query ||
      app.title.toLowerCase().includes(query) ||
      app.company.toLowerCase().includes(query) ||
      app.location.toLowerCase().includes(query)

    const matchesLocation =
      selectedLocation.value === 'All locations' ||
      app.location === selectedLocation.value

    const matchesStatus =
      selectedStatus.value === 'All statuses' ||
      app.status === selectedStatus.value

    return (
      matchesTab &&
      matchesSearch &&
      matchesLocation &&
      matchesStatus
    )
  })
})

const totalPages = computed(() =>
  Math.max(
    1,
    Math.ceil(
      filteredApplications.value.length / pageSize
    )
  )
)

const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize

  return filteredApplications.value.slice(
    start,
    start + pageSize
  )
})

function changeTab(tab) {
  activeTab.value = tab
  currentPage.value = 1
}

function resetFilters() {
  searchQuery.value = ''
  selectedLocation.value = 'All locations'
  selectedStatus.value = 'All statuses'
  activeTab.value = 'All Applications'
  currentPage.value = 1
}

function updatePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

function viewApplication(app) {
  router.push({
    path: '/dashboard/view-application',
    query: {
      application: app.id,
    },
  })
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-3 text-slate-800">

      <!-- Page heading -->
      <section
        class="rounded-xl border border-orange-200 bg-white px-5 py-4"
      >
        <h1 class="text-3xl font-extrabold text-slate-800 sm:text-3xl">
          My Applications
        </h1>

        <p class="mt-1 text-sm">
          Discover thousands of job opportunities and take the next step
          in your career. Your future is waiting!
        </p>
      </section>

      <!-- Application tabs -->
      <section
        class="rounded-xl border border-orange-200 bg-white p-3"
      >
        <div
          class="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between"
        >

          <div class="flex flex-wrap gap-2">
            <button
              v-for="tab in tabs"
              :key="tab"
              @click="changeTab(tab)"
              class="rounded-lg border px-3 py-2 text-xs font-medium transition sm:text-sm"
              :class="
                activeTab === tab
                  ? 'border-orange-400 bg-orange-50 text-orange-600'
                  : 'border-orange-200 bg-white text-slate-600 hover:bg-orange-50'
              "
            >
              {{ tab }} ({{ tabCount(tab) }})
            </button>
          </div>

          <button
            @click="currentPage = 1"
            class="inline-flex shrink-0 items-center justify-center gap-2 rounded-lg bg-orange-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-orange-700"
          >
            <ArrowRight :size="17" />
            Search
          </button>

        </div>
      </section>

      <!-- Search and filters -->
      <section
        class="rounded-xl border border-orange-200 bg-white p-3"
      >
        <div
          class="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(0,1.5fr)_minmax(0,1fr)_minmax(0,1fr)_auto]"
        >

          <!-- Search -->
          <label class="relative block">
            <Search
              :size="18"
              class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
            />

            <input
              v-model="searchQuery"
              @input="currentPage = 1"
              type="search"
              placeholder="Job title, company, or keyword"
              class="w-full rounded-lg border border-orange-200 bg-white py-2.5 pl-9 pr-3 text-sm outline-none transition placeholder:text-slate-500 focus:border-orange-400 focus:ring-1 focus:ring-orange-100"
            />
          </label>

          <!-- Location -->
          <label class="relative block">
            <MapPin
              :size="18"
              class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-600"
            />

            <select
              v-model="selectedLocation"
              @change="currentPage = 1"
              class="w-full appearance-none rounded-lg border border-orange-200 bg-white py-2.5 pl-9 pr-9 text-sm outline-none focus:border-orange-400"
            >
              <option
                v-for="location in locations"
                :key="location"
              >
                {{ location }}
              </option>
            </select>

            <ChevronDown
              :size="16"
              class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-600"
            />
          </label>

          <!-- Status -->
          <label class="relative block">
            <BriefcaseBusiness
              :size="17"
              class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-600"
            />

            <select
              v-model="selectedStatus"
              @change="currentPage = 1"
              class="w-full appearance-none rounded-lg border border-orange-200 bg-white py-2.5 pl-9 pr-9 text-sm outline-none focus:border-orange-400"
            >
              <option>All statuses</option>
              <option>Applied</option>
              <option>Shortlisted</option>
              <option>Interview</option>
              <option>Rejected</option>
            </select>

            <ChevronDown
              :size="16"
              class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-600"
            />
          </label>

          <!-- Clear filters -->
          <button
            @click="resetFilters"
            class="inline-flex items-center justify-center gap-1.5 rounded-lg px-3 py-2 text-sm font-medium text-blue-700 transition hover:bg-blue-50"
          >
            <X :size="15" />
            Clear Filters
          </button>

        </div>
      </section>

      <!-- Application cards -->
      <section
        v-for="app in paginatedApplications"
        :key="app.id"
        @click="viewApplication(app)"
        class="cursor-pointer rounded-xl border border-orange-200 bg-white p-3 transition hover:border-orange-300 hover:shadow-sm sm:px-4 sm:py-4"
      >

        <div
          class="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-4"
        >

          <!-- Company logo -->
          <div
            class="flex h-11 w-11 shrink-0 items-center justify-center self-start rounded-lg border border-slate-100 bg-white text-lg font-bold sm:self-center"
            :class="{
              'text-blue-500': app.logoStyle === 'google',
              'text-violet-700': app.logoStyle === 'tech',
              'text-red-500': app.logoStyle === 'vodacom',
              'text-green-600': app.logoStyle === 'crdb',
              'text-orange-600': app.logoStyle === 'creative',
            }"
          >

            <span
              v-if="app.logoStyle === 'google'"
              class="text-2xl font-bold"
            >
              G
            </span>

            <span
              v-else-if="app.logoStyle === 'vodacom'"
              class="text-2xl"
            >
              ◉
            </span>

            <span
              v-else
              class="text-sm font-extrabold"
            >
              {{ app.logo }}
            </span>

          </div>

          <!-- Job details -->
          <div class="min-w-0 flex-1">

            <h2
              class="break-words text-sm font-bold text-slate-900 sm:text-base"
            >
              {{ app.title }}
            </h2>

            <div
              class="mt-0.5 flex flex-wrap items-center gap-1.5"
            >
              <span
                class="text-sm font-semibold text-slate-800"
              >
                {{ app.company }}
              </span>

              <span
                class="text-blue-500"
                title="Verified company"
              >
                ✦
              </span>
            </div>

            <div
              class="mt-2 flex flex-col gap-1.5 text-xs text-slate-600 sm:flex-row sm:flex-wrap sm:items-center sm:gap-x-6"
            >

              <span
                class="inline-flex items-center gap-1.5"
              >
                <MapPin :size="14" />
                {{ app.location }}
              </span>

              <span
                class="inline-flex items-center gap-1.5"
              >
                <CalendarDays :size="14" />
                Applied on {{ app.applied }}
              </span>

            </div>

          </div>

          <!-- Status and next step -->
          <div
            class="flex min-w-0 items-center justify-between gap-3 border-t border-slate-100 pt-3 sm:w-52 sm:shrink-0 sm:flex-row sm:items-center sm:border-0 sm:pt-0"
          >

            <div class="min-w-0">

              <span
                class="inline-flex rounded-full px-3 py-1 text-[11px] font-semibold"
                :class="statusClasses[app.status]"
              >
                {{ app.status }}
              </span>

              <p
                class="mt-1.5 break-words text-xs leading-4 text-slate-700"
              >
                {{ app.nextStep }}
              </p>

            </div>

            <!-- Arrow -->
            <ChevronRight
              :size="20"
              class="shrink-0 text-slate-700"
            />

          </div>

        </div>

      </section>

      <!-- Empty state -->
      <section
        v-if="filteredApplications.length === 0"
        class="rounded-xl border border-orange-200 bg-white px-5 py-12 text-center"
      >

        <Search
          :size="36"
          class="mx-auto mb-3 text-slate-400"
        />

        <h2 class="font-semibold text-slate-800">
          No applications found
        </h2>

        <p class="mt-1 text-sm text-slate-500">
          Try changing your search or filters.
        </p>

        <button
          @click="resetFilters"
          class="mt-4 rounded-lg bg-orange-600 px-4 py-2 text-sm font-semibold text-white hover:bg-orange-700"
        >
          Clear Filters
        </button>

      </section>

      <!-- Pagination -->
      <section
        v-if="filteredApplications.length > 0"
        class="flex flex-wrap items-center justify-center gap-2 py-3 sm:gap-4"
      >

        <!-- Previous -->
        <button
          @click="updatePage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="inline-flex items-center gap-1 rounded-lg px-2 py-2 text-sm transition hover:bg-orange-50 disabled:cursor-not-allowed disabled:opacity-40 sm:px-3"
        >
          <ChevronLeft :size="17" />
          Previous
        </button>

        <!-- Page numbers -->
        <button
          v-for="page in totalPages"
          :key="page"
          @click="updatePage(page)"
          class="h-9 min-w-9 rounded-lg px-3 text-sm font-medium transition"
          :class="
            currentPage === page
              ? 'bg-orange-600 text-white'
              : 'text-slate-700 hover:bg-orange-100'
          "
        >
          {{ page }}
        </button>

        <!-- Next -->
        <button
          @click="updatePage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="inline-flex items-center gap-1 rounded-lg px-2 py-2 text-sm transition hover:bg-orange-50 disabled:cursor-not-allowed disabled:opacity-40 sm:px-3"
        >
          Next
          <ChevronRight :size="17" />
        </button>

      </section>

      <!-- Results count -->
      <p
        class="pb-2 text-center text-xs text-slate-500"
      >
        Showing
        {{
          filteredApplications.length === 0
            ? 0
            : (currentPage - 1) * pageSize + 1
        }}
        –
        {{
          Math.min(
            currentPage * pageSize,
            filteredApplications.length
          )
        }}
        of {{ filteredApplications.length }} applications
      </p>

    </div>
  </DashboardLayout>
</template>