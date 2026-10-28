<script setup>
import { computed, ref } from 'vue'
import {
  Bell,
  Search,
  Plus,
  MapPin,
  BriefcaseBusiness,
  Tag,
  MoreVertical,
  ChevronLeft,
  ChevronRight,
  Briefcase,
  Code,
  Megaphone,
  UserCheck,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const searchText = ref('')
const activeTab = ref('my-alerts')

const jobAlerts = ref([
  {
    id: 1,
    title: 'IT Support Specialist',
    company: 'Apex Solutions Ltd',
    location: 'Dar es Salaam',
    experience: '2-4 years',
    category: 'IT & Software',
    emailFrequency: 'Email you when new IT Support Specialist jobs are posted.',
    lastNotified: 'Last notified: 2 days ago',
    active: true,
    icon: Briefcase,
    iconBg: '#ffe5e5',
    iconColor: '#ff5a0a',
  },
  {
    id: 2,
    title: 'Software Developer',
    company: 'TechWave Tanzania',
    location: 'Dar es Salaam',
    experience: '1-3 years',
    category: 'IT & Software',
    emailFrequency: 'Email you when new Software Developer jobs are posted.',
    lastNotified: 'Last notified: 1 day ago',
    active: true,
    icon: Code,
    iconBg: '#ede9fe',
    iconColor: '#7c3aed',
  },
  {
    id: 3,
    title: 'Marketing Specialist',
    company: 'Bright Media Ltd',
    location: 'Dar es Salaam',
    experience: '1-3 years',
    category: 'Marketing & Communications',
    emailFrequency: 'Email you when new Marketing Specialist jobs are posted.',
    lastNotified: 'Last notified: 3 days ago',
    active: true,
    icon: Megaphone,
    iconBg: '#e0f2fe',
    iconColor: '#0284c7',
  },
  {
    id: 4,
    title: 'Customer Service Representative',
    company: 'Zuri Group',
    location: 'Arusha',
    experience: '0-2 years',
    category: 'Customer Service',
    emailFrequency: 'Email you when new Customer Service Representative jobs are posted.',
    lastNotified: 'Last notified: 7 days ago',
    active: false,
    icon: UserCheck,
    iconBg: '#f1f5f9',
    iconColor: '#475569',
  },
])

const filteredAlerts = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  if (!query) return jobAlerts.value

  return jobAlerts.value.filter((alert) =>
    [alert.title, alert.company, alert.location, alert.category]
      .join(' ')
      .toLowerCase()
      .includes(query),
  )
})

function toggleAlertStatus(id) {
  const alert = jobAlerts.value.find((a) => a.id === id)
  if (alert) {
    alert.active = !alert.active
  }
}
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-6">

      <!-- Top Search bar -->
      <div class="relative w-full">
        <Search
          :size="18"
          class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"
        />
        <input
          v-model="searchText"
          type="search"
          placeholder="Search for jobs, companies, or skills..."
          class="w-full rounded-2xl border border-gray-200 bg-white py-3 pl-11 pr-4 text-xs font-medium text-gray-900 shadow-sm outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
        />
      </div>

      <!-- Header Section -->
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div class="flex items-start gap-3.5">
          <div
            class="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl shadow-sm"
            style="background: #ffe5d8; color: #ff5a0a"
          >
            <Bell :size="24" />
          </div>
          <div>
            <h1 class="text-2xl font-bold text-gray-900">
              Job Alerts
            </h1>
            <p class="mt-1 text-xs text-gray-500">
              Get notified when new jobs that match your preferences are posted.
            </p>
          </div>
        </div>

        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-xl bg-orange-600 px-5 py-2.5 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700"
          style="background: #ff5a0a"
        >
          <Plus :size="16" /> Create New Alert
        </button>
      </div>

      <!-- Navigation Tabs -->
      <div class="flex items-center gap-8 border-b border-gray-200 text-xs font-semibold">
        <button
          @click="activeTab = 'my-alerts'"
          :class="[
            'pb-3 transition relative flex items-center gap-2',
            activeTab === 'my-alerts' ? 'text-orange-600 font-bold' : 'text-gray-500 hover:text-gray-800'
          ]"
        >
          My Alerts
          <span class="rounded-full bg-orange-600 px-1.5 py-0.5 text-[10px] text-white">
            {{ jobAlerts.filter(a => a.active).length }}
          </span>
          <span
            v-if="activeTab === 'my-alerts'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-orange-600 rounded-t-full"
          ></span>
        </button>

        <button
          @click="activeTab = 'history'"
          :class="[
            'pb-3 transition relative',
            activeTab === 'history' ? 'text-orange-600 font-bold' : 'text-gray-500 hover:text-gray-800'
          ]"
        >
          Alert History
          <span
            v-if="activeTab === 'history'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-orange-600 rounded-t-full"
          ></span>
        </button>

        <button
          @click="activeTab = 'recommended'"
          :class="[
            'pb-3 transition relative',
            activeTab === 'recommended' ? 'text-orange-600 font-bold' : 'text-gray-500 hover:text-gray-800'
          ]"
        >
          Recommended Jobs
          <span
            v-if="activeTab === 'recommended'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-orange-600 rounded-t-full"
          ></span>
        </button>
      </div>

      <!-- Job Alerts List Cards -->
      <div class="space-y-4">
        <article
          v-for="alert in filteredAlerts"
          :key="alert.id"
          class="rounded-2xl border bg-white p-5 shadow-sm transition hover:shadow-md"
          style="border-color: #ffd3bd"
        >
          <div class="flex items-start justify-between gap-4">
            <div class="flex items-start gap-4 min-w-0">
              <!-- Icon Container -->
              <div
                class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl"
                :style="{ backgroundColor: alert.iconBg, color: alert.iconColor }"
              >
                <component :is="alert.icon" :size="22" />
              </div>

              <!-- Details -->
              <div class="min-w-0">
                <div class="flex items-center gap-3 flex-wrap">
                  <h3 class="text-sm font-bold text-gray-900">
                    {{ alert.title }}
                  </h3>
                  <span
                    :class="[
                      'inline-flex items-center rounded-full px-2.5 py-0.5 text-[10px] font-semibold',
                      alert.active ? 'bg-emerald-50 text-emerald-700' : 'bg-gray-100 text-gray-600'
                    ]"
                  >
                    {{ alert.active ? 'Active' : 'Inactive' }}
                  </span>
                </div>

                <p class="mt-0.5 text-xs text-gray-600">
                  {{ alert.company }} &bull; {{ alert.location }}
                </p>

                <!-- Criteria tags -->
                <div class="mt-2.5 flex flex-wrap items-center gap-x-4 gap-y-1.5 text-xs text-gray-500">
                  <span class="inline-flex items-center gap-1.5">
                    <MapPin :size="13" class="text-gray-400" /> {{ alert.location }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <BriefcaseBusiness :size="13" class="text-gray-400" /> {{ alert.experience }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <Tag :size="13" class="text-gray-400" /> {{ alert.category }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Options / Menu -->
            <div class="flex items-center gap-3">
              <button
                type="button"
                class="text-gray-400 hover:text-gray-600 transition p-1"
                title="More actions"
              >
                <MoreVertical :size="18" />
              </button>
            </div>
          </div>

          <!-- Frequency Text & Toggle Switch -->
          <div class="mt-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-t border-gray-100 pt-4">
            <div>
              <p class="text-xs text-gray-800 font-medium">
                {{ alert.emailFrequency }}
              </p>
              <p class="mt-0.5 text-[11px] text-gray-400">
                {{ alert.lastNotified }}
              </p>
            </div>

            <!-- Custom Toggle Switch -->
            <button
              type="button"
              @click="toggleAlertStatus(alert.id)"
              :class="[
                'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none',
                alert.active ? 'bg-orange-600' : 'bg-gray-300'
              ]"
            >
              <span
                :class="[
                  'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-lg ring-0 transition duration-200 ease-in-out',
                  alert.active ? 'translate-x-5' : 'translate-x-0'
                ]"
              />
            </button>
          </div>
        </article>
      </div>

      <!-- Pagination Footer Matching Reference Picture -->
      <div v-if="filteredAlerts.length" class="flex items-center justify-between py-6">
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
