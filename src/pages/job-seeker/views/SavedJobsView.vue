<script setup>
import { computed, ref } from 'vue'
import {
  Bookmark,
  MapPin,
  BriefcaseBusiness,
  Clock3,
  Search,
  ArrowRight,
  ChevronLeft,
  ChevronRight,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const searchText = ref('')
const activeTab = ref('all')
const sortBy = ref('newest')

const savedJobs = ref([
  {
    id: 1,
    title: 'Frontend Developer',
    company: 'Google',
    location: 'Dar es Salaam, Tanzania',
    type: 'Full time',
    salary: 'TSh 1,500,000 - 2,500,000',
    posted: 'Posted 2 days ago',
    description: 'We are looking for a skilled frontend Developer to join our team and help build amazing products for millions of users',
    logo: 'G',
    logoBg: '#e8f0fe',
    logoColor: '#1a73e8',
    status: 'Saved',
    category: 'active',
  },
  {
    id: 2,
    title: 'Full Stack Developer',
    company: 'TechSolution Ltd',
    location: 'Dar es Salaam, Tanzania',
    type: 'Full time',
    salary: 'TSh 1,200,000 - 2,000,000',
    posted: 'Posted 5 days ago',
    description: 'We are looking for a talented Full Stack Developer with experience in Modern web technologies to join our growing team.',
    logo: 'T',
    logoBg: '#eee7ff',
    logoColor: '#7138e8',
    status: 'Saved',
    category: 'active',
  },
  {
    id: 3,
    title: 'Customer Care Agency',
    company: 'Vodacom Tanzania',
    location: 'Dar es Salaam, Tanzania',
    type: 'Full time',
    salary: 'TSh 600,000 - 900,000',
    posted: 'Posted 6 days ago',
    description: 'Join our customer care team and help us deliver world class support to our valued customer',
    logo: 'V',
    logoBg: '#ffe5e5',
    logoColor: '#e60000',
    status: 'Applied',
    category: 'applied',
  },
])

const filteredJobs = computed(() => {
  let result = savedJobs.value

  // Tab filter
  if (activeTab.value === 'active') {
    result = result.filter(j => j.category === 'active')
  } else if (activeTab.value === 'applied') {
    result = result.filter(j => j.category === 'applied')
  } else if (activeTab.value === 'expired') {
    result = result.filter(j => j.category === 'expired')
  }

  // Search filter
  const query = searchText.value.trim().toLowerCase()
  if (query) {
    result = result.filter((job) =>
      [job.title, job.company, job.location, job.description]
        .join(' ')
        .toLowerCase()
        .includes(query),
    )
  }

  return result
})

function removeSavedJob(id) {
  savedJobs.value = savedJobs.value.filter((job) => job.id !== id)
}
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

        <!-- Filter Tabs -->
        <div class="mt-6 flex flex-wrap items-center gap-2 border-t border-gray-100 pt-5">
          <button
            @click="activeTab = 'all'"
            :class="[
              'rounded-xl px-4 py-2 text-xs font-semibold transition',
              activeTab === 'all'
                ? 'bg-orange-600 text-white shadow-sm'
                : 'bg-orange-50/50 text-orange-700 hover:bg-orange-100/60 border border-orange-200/60'
            ]"
          >
            All Saved ({{ savedJobs.length }})
          </button>
          <button
            @click="activeTab = 'active'"
            :class="[
              'rounded-xl px-4 py-2 text-xs font-semibold transition',
              activeTab === 'active'
                ? 'bg-orange-600 text-white shadow-sm'
                : 'bg-orange-50/50 text-orange-700 hover:bg-orange-100/60 border border-orange-200/60'
            ]"
          >
            Active ({{ savedJobs.filter(j => j.category === 'active').length }})
          </button>
          <button
            @click="activeTab = 'expired'"
            :class="[
              'rounded-xl px-4 py-2 text-xs font-semibold transition',
              activeTab === 'expired'
                ? 'bg-orange-600 text-white shadow-sm'
                : 'bg-orange-50/50 text-orange-700 hover:bg-orange-100/60 border border-orange-200/60'
            ]"
          >
            Expired (0)
          </button>
          <button
            @click="activeTab = 'applied'"
            :class="[
              'rounded-xl px-4 py-2 text-xs font-semibold transition',
              activeTab === 'applied'
                ? 'bg-orange-600 text-white shadow-sm'
                : 'bg-orange-50/50 text-orange-700 hover:bg-orange-100/60 border border-orange-200/60'
            ]"
          >
            Applied ({{ savedJobs.filter(j => j.category === 'applied').length }})
          </button>
        </div>
      </header>

      <!-- Saved Job Cards List -->
      <div v-if="filteredJobs.length" class="space-y-4">
        <article
          v-for="job in filteredJobs"
          :key="job.id"
          class="group relative rounded-2xl border bg-white p-5 shadow-sm transition hover:shadow-md"
          style="border-color: #ffd3bd"
        >
          <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
            
            <!-- Left Group: Logo, Title, Company, Meta, & Description -->
            <div class="flex items-start gap-4 min-w-0 flex-1">
              <div
                class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-lg font-bold shadow-sm"
                :style="{ backgroundColor: job.logoBg, color: job.logoColor }"
              >
                {{ job.logo }}
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
                    <BriefcaseBusiness :size="13" class="text-gray-400" /> {{ job.type }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <Clock3 :size="13" class="text-gray-400" /> {{ job.posted }}
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
                    job.status === 'Applied' ? 'bg-blue-100 text-blue-700' : 'bg-emerald-100 text-emerald-700'
                  ]"
                >
                  {{ job.status }}
                </span>

                <button
                  type="button"
                  class="rounded-xl p-1.5 text-orange-600 transition hover:bg-orange-50 shrink-0"
                  :aria-label="`Remove ${job.title} from saved jobs`"
                  title="Remove saved job"
                  @click="removeSavedJob(job.id)"
                >
                  <Bookmark :size="20" class="fill-orange-600" />
                </button>
              </div>

              <!-- Salary Below Status -->
              <div>
                <p class="text-xs font-bold text-gray-900">
                  {{ job.salary }}
                  <span class="font-normal text-gray-500">/ month</span>
                </p>
              </div>

              <!-- Action Button -->
              <div class="flex items-center gap-2 mt-1">
                <RouterLink
                  v-if="job.status === 'Applied'"
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
      <section
        v-else
        class="rounded-2xl border bg-white px-5 py-16 text-center shadow-sm"
        style="border-color: #ffd3bd"
      >
        <div
          class="mx-auto flex h-14 w-14 items-center justify-center rounded-full shadow-sm"
          style="background: #fff0e8; color: #ff5a0a"
        >
          <Bookmark :size="24" />
        </div>

        <h2 class="mt-4 text-base font-bold text-gray-900">
          {{ savedJobs.length ? 'No matching saved jobs' : 'No saved jobs yet' }}
        </h2>

        <p class="mx-auto mt-1.5 max-w-sm text-xs text-gray-500">
          {{
            savedJobs.length
              ? 'Try adjusting your search or tab filter.'
              : 'Save jobs you are interested in and they will appear here.'
          }}
        </p>

        <RouterLink
          to="/dashboard/find-jobs"
          class="mt-5 inline-flex items-center gap-2 rounded-xl px-5 py-2.5 text-xs font-semibold text-white shadow-sm transition hover:opacity-95"
          style="background: #ff5a0a"
        >
          Browse Jobs <ArrowRight :size="16" />
        </RouterLink>
      </section>

      <!-- Pagination Footer Matching Reference Picture Exactly -->
      <div v-if="filteredJobs.length" class="flex items-center justify-between py-6">
        <button class="flex items-center gap-1 text-xs font-semibold text-gray-400 hover:text-gray-600 transition">
          <ChevronLeft :size="16" /> Previous
        </button>

        <div class="flex items-center gap-2">
          <button class="h-8 w-8 rounded-xl bg-orange-600 text-xs font-bold text-white shadow-sm flex items-center justify-center">1</button>
          <button class="h-8 w-8 rounded-xl text-xs font-semibold text-gray-600 hover:bg-gray-100 transition flex items-center justify-center">2</button>
          <button class="h-8 w-8 rounded-xl text-xs font-semibold text-gray-600 hover:bg-gray-100 transition flex items-center justify-center">3</button>
          <span class="px-1 text-gray-400 font-medium">...</span>
          <button class="h-8 w-8 rounded-xl text-xs font-semibold text-gray-600 hover:bg-gray-100 transition flex items-center justify-center">67</button>
          <button class="h-8 w-8 rounded-xl text-xs font-semibold text-gray-600 hover:bg-gray-100 transition flex items-center justify-center">68</button>
        </div>

        <button class="flex items-center gap-1 text-xs font-semibold text-gray-800 hover:text-orange-600 transition">
          Next <ChevronRight :size="16" />
        </button>
      </div>

    </div>
  </DashboardLayout>
</template>
