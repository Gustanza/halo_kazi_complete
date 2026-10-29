<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import StatusBadge from '../../../components/ui/StatusBadge.vue'
import Pagination from '../../../components/ui/Pagination.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as applicationsApi from '../../../api/applications'
import {
  Search,
  ChevronRight,
  CalendarDays,
  BriefcaseBusiness,
  X,
  ArrowRight,
} from 'lucide-vue-next'

const router = useRouter()

const tabs = [
  { value: '', label: 'All Applications' },
  { value: 'SUBMITTED', label: 'Applied' },
  { value: 'SHORTLISTED', label: 'Shortlisted' },
  { value: 'IN_REVIEW', label: 'In Review' },
  { value: 'REJECTED', label: 'Rejected' },
]

const activeStatus = ref('')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 4

const loading = ref(true)
const applications = ref([])
const total = ref(0)
const tabCounts = ref({})

async function loadTabCounts() {
  const counts = {}
  await Promise.all(
    tabs.map(async (tab) => {
      const result = await applicationsApi.listApplications({ status: tab.value, page: 1, pageSize: 1 })
      counts[tab.value] = result.total
    }),
  )
  tabCounts.value = counts
}

async function load() {
  loading.value = true
  try {
    const result = await applicationsApi.listApplications({
      status: activeStatus.value,
      search: searchQuery.value,
      page: currentPage.value,
      pageSize,
    })
    applications.value = result.items
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function changeTab(tab) {
  activeStatus.value = tab.value
  currentPage.value = 1
}

function resetFilters() {
  searchQuery.value = ''
  activeStatus.value = ''
  currentPage.value = 1
}

function hasActiveFilters() {
  return !!searchQuery.value || !!activeStatus.value
}

function viewApplication(app) {
  router.push({ path: '/dashboard/view-application', query: { application: app.id } })
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

watch([activeStatus, searchQuery, currentPage], load)
onMounted(() => {
  load()
  loadTabCounts()
})
</script>

<template>
  <DashboardLayout>
    <div class="space-y-3">
      <!-- Page heading -->
      <section class="card-surface px-5 py-4">
        <h1 class="text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">My Applications</h1>
        <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
          Track the jobs you've applied to and see where each one stands.
        </p>
      </section>

      <!-- Application tabs -->
      <section class="card-surface p-3">
        <div class="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between">
          <div class="flex flex-wrap gap-2">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              type="button"
              class="rounded-lg border px-3 py-2 text-xs font-medium transition sm:text-sm"
              :style="
                activeStatus === tab.value
                  ? 'border-color:#FD5C20; background-color: var(--color-peach-light); color:#FD5C20'
                  : 'border-color: var(--color-border); color: var(--color-text-muted)'
              "
              @click="changeTab(tab)"
            >
              {{ tab.label }} ({{ tabCounts[tab.value] ?? 0 }})
            </button>
          </div>
        </div>
      </section>

      <!-- Search and filters -->
      <section class="card-surface p-3">
        <div class="grid grid-cols-1 gap-3 sm:grid-cols-[minmax(0,1fr)_auto]">
          <label class="relative block">
            <Search :size="18" class="absolute left-3 top-1/2 -translate-y-1/2" style="color: var(--color-text-muted)" />
            <input
              v-model="searchQuery"
              type="search"
              placeholder="Job title or company"
              class="w-full rounded-lg border bg-transparent py-2.5 pl-9 pr-3 text-sm outline-none transition focus:border-primary"
              style="border-color: var(--color-border); color: var(--color-text)"
              @input="currentPage = 1"
            />
          </label>

          <button
            type="button"
            class="inline-flex shrink-0 items-center justify-center gap-1.5 rounded-lg px-3 py-2 text-sm font-medium text-primary transition hover:bg-primary-50 dark:hover:bg-white/5"
            @click="resetFilters"
          >
            <X :size="15" />
            Clear Filters
          </button>
        </div>
      </section>

      <!-- Loading -->
      <template v-if="loading">
        <Skeleton v-for="i in pageSize" :key="i" variant="card" height="6.5rem" />
      </template>

      <!-- Empty state -->
      <EmptyState
        v-else-if="!applications.length"
        :icon="Search"
        title="No applications found"
        description="Try changing your search or filters."
        :action-label="hasActiveFilters() ? 'Clear Filters' : ''"
        @action="resetFilters"
      />

      <!-- Application cards -->
      <template v-else>
        <section
          v-for="app in applications"
          :key="app.id"
          class="card-surface card-hover cursor-pointer p-3 sm:px-4 sm:py-4"
          @click="viewApplication(app)"
        >
          <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-4">
            <!-- Company logo -->
            <div
              class="flex h-11 w-11 shrink-0 items-center justify-center self-start rounded-lg text-sm font-extrabold sm:self-center"
              :style="{ backgroundColor: app.companyColor + '1a', color: app.companyColor }"
            >
              {{ app.companyInitials }}
            </div>

            <!-- Job details -->
            <div class="min-w-0 flex-1">
              <h2 class="break-words text-sm font-bold sm:text-base" style="color: var(--color-text)">{{ app.jobTitle }}</h2>
              <p class="mt-0.5 text-sm font-semibold" style="color: var(--color-text-muted)">{{ app.company }}</p>

              <div class="mt-2 flex flex-col gap-1.5 text-xs sm:flex-row sm:flex-wrap sm:items-center sm:gap-x-6" style="color: var(--color-text-muted)">
                <span class="inline-flex items-center gap-1.5">
                  <BriefcaseBusiness :size="14" /> {{ app.location }}
                </span>
                <span class="inline-flex items-center gap-1.5">
                  <CalendarDays :size="14" /> Applied on {{ formatDate(app.submittedAt) }}
                </span>
              </div>
            </div>

            <!-- Status -->
            <div class="flex min-w-0 items-center justify-between gap-3 border-t pt-3 sm:w-52 sm:shrink-0 sm:border-0 sm:pt-0" style="border-color: var(--color-border)">
              <div class="min-w-0">
                <StatusBadge :status="app.status" kind="application" />
                <p v-if="app.statusRemarks" class="mt-1.5 break-words text-xs leading-4" style="color: var(--color-text-muted)">
                  {{ app.statusRemarks }}
                </p>
              </div>
              <ChevronRight :size="20" class="shrink-0" style="color: var(--color-text-muted)" />
            </div>
          </div>
        </section>

        <Pagination v-model="currentPage" :total-pages="Math.max(1, Math.ceil(total / pageSize))" />

        <p class="pb-2 text-center text-xs" style="color: var(--color-text-muted)">
          Showing {{ total === 0 ? 0 : (currentPage - 1) * pageSize + 1 }}–{{ Math.min(currentPage * pageSize, total) }} of {{ total }} applications
        </p>
      </template>
    </div>
  </DashboardLayout>
</template>
