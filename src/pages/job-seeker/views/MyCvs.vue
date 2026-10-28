<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import {
  House, UserRound, BookOpen, BriefcaseBusiness, Bookmark,
  Download, Eye, Send, Bell, Moon, ChevronDown, MoreVertical,
  Search, Settings, FileText, Plus, Save, X
} from 'lucide-vue-next'

const router = useRouter()

const activeTab = ref('All CVs')
const sortBy = ref('Most Recent')
const openMenu = ref(null)

const cvs = ref([
  {
    id: 1,
    title: 'Professional CV',
    description: 'Modern and clean CV with professional layout. Suitable for corporate and tech roles.',
    created: '12 Apr 2025',
    updated: '2 days ago',
    status: 'Active',
    default: true,
    theme: 'professional',
  },
  {
    id: 2,
    title: 'Creative CV',
    description: 'Clean and modern design with a creative touch. Great for marketing and design roles.',
    created: '5 Mar 2025',
    updated: '1 week ago',
    status: 'Active',
    default: false,
    theme: 'creative',
  },
  {
    id: 3,
    title: 'Minimal CV',
    description: 'Simple and professional. Perfect for traditional industries and general applications.',
    created: '20 Feb 2025',
    updated: '3 weeks ago',
    status: 'Active',
    default: false,
    theme: 'minimal',
  },
  {
    id: 4,
    title: 'Student CV',
    description: 'Simple and clean CV designed for internship and entry-level positions.',
    created: '10 Jan 2025',
    updated: '2 months ago',
    status: 'Active',
    default: false,
    theme: 'student',
  },
])

const filteredCvs = computed(() => {
  let result = [...cvs.value]

  if (activeTab.value === 'Active') {
    result = result.filter(cv => cv.status === 'Active')
  } else if (activeTab.value === 'Inactive') {
    result = result.filter(cv => cv.status === 'Inactive')
  }

  if (sortBy.value === 'Name A-Z') {
    result.sort((a, b) => a.title.localeCompare(b.title))
  } else if (sortBy.value === 'Oldest First') {
    result.reverse()
  }

  return result
})

function buildNewCv() {
  router.push('/dashboard/cv-builder')
}

function useCv(cv) {
  router.push({
    path: '/dashboard/cv-builder',
    query: { cv: cv.id },
  })
}

function toggleStatus(cv) {
  cv.status = cv.status === 'Active' ? 'Inactive' : 'Active'
  openMenu.value = null
}

function setDefault(cv) {
  cvs.value.forEach(item => {
    item.default = item.id === cv.id
  })
  openMenu.value = null
}

function viewCv(cv) {
  router.push({
    path: '/dashboard/view-cv',
    query: { cv: cv.id },
  })
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-3 text-slate-800">
      <!-- Page heading -->
      <section class="rounded-xl border border-orange-200 bg-white px-5 py-4 shadow-sm">
        <h1 class="text-3xl font-extrabold">My CVs</h1>
        <p class="mt-1 text-sm">
          Manage your CVs and create new ones. Keep your profiles up to date
          to increase your chances of getting hired.
        </p>
      </section>

      <!-- Tabs and sorting -->
      <section
        class="flex flex-col gap-3 rounded-xl border border-orange-200 bg-white px-3 py-2 sm:flex-row sm:items-center sm:justify-between"
      >
        <div class="flex flex-wrap gap-2 sm:gap-5">
          <button
            v-for="tab in ['All CVs', 'Active', 'Inactive']"
            :key="tab"
            @click="activeTab = tab"
            class="border-b-2 px-3 py-2 text-xs font-medium transition"
            :class="activeTab === tab
              ? 'border-orange-500 text-orange-600'
              : 'border-transparent text-slate-500 hover:text-orange-500'"
          >
            {{ tab }}<span v-if="tab === 'All CVs'">({{ cvs.length }})</span>
            <span v-else>({{ cvs.filter(cv => cv.status === tab).length }})</span>
          </button>
        </div>

        <label class="flex items-center gap-2 text-xs font-semibold">
          Sort by
          <select
            v-model="sortBy"
            class="rounded-lg border border-slate-200 bg-white px-2 py-2 font-normal outline-none focus:border-orange-400"
          >
            <option>Most Recent</option>
            <option>Name A-Z</option>
            <option>Oldest First</option>
          </select>
        </label>
      </section>

      <!-- CV cards -->
      <section
        v-for="cv in filteredCvs"
        :key="cv.id"
        class="relative rounded-xl border border-orange-200 bg-white p-3 sm:px-5 sm:py-3"
      >
        <div class="flex flex-col gap-3 sm:flex-row sm:gap-5">
          <!-- CV preview thumbnail -->
          <div
            class="flex h-32 w-28 shrink-0 self-start overflow-hidden rounded-md border border-slate-100 bg-slate-50 p-1"
          >
            <div
              class="flex h-full w-full overflow-hidden rounded-sm"
              :class="{
                'bg-white': cv.theme === 'professional',
                'bg-slate-800': cv.theme === 'creative',
                'bg-slate-100': cv.theme === 'minimal',
                'bg-violet-50': cv.theme === 'student',
              }"
            >
              <div
                class="w-[34%] p-1"
                :class="{
                  'bg-blue-50': cv.theme === 'professional',
                  'bg-slate-700': cv.theme === 'creative',
                  'bg-slate-800': cv.theme === 'minimal',
                  'bg-violet-100': cv.theme === 'student',
                }"
              >
                <div
                  class="mx-auto mb-2 h-5 w-5 rounded-full"
                  :class="{
                    'bg-blue-200': cv.theme === 'professional',
                    'bg-slate-400': cv.theme === 'creative',
                    'bg-slate-500': cv.theme === 'minimal',
                    'bg-violet-300': cv.theme === 'student',
                  }"
                ></div>
                <div
                  v-for="n in 5"
                  :key="n"
                  class="mb-1 h-1 rounded"
                  :class="{
                    'bg-blue-200': cv.theme === 'professional',
                    'bg-slate-500': cv.theme === 'creative',
                    'bg-slate-400': cv.theme === 'minimal',
                    'bg-violet-300': cv.theme === 'student',
                  }"
                ></div>
              </div>

              <div class="flex-1 p-1.5">
                <div
                  class="mb-1 h-2 w-4/5 rounded-sm"
                  :class="cv.theme === 'creative' || cv.theme === 'minimal'
                    ? 'bg-white'
                    : 'bg-slate-400'"
                ></div>
                <div
                  v-for="n in 3"
                  :key="n"
                  class="mb-1 h-1 w-full rounded-sm"
                  :class="cv.theme === 'creative' || cv.theme === 'minimal'
                    ? 'bg-slate-400'
                    : 'bg-slate-200'"
                ></div>
                <div class="mb-1 mt-3 h-1.5 w-3/5 rounded-sm bg-orange-200"></div>
                <div
                  v-for="n in 7"
                  :key="n"
                  class="mb-1 h-1 w-full rounded-sm"
                  :class="cv.theme === 'creative' || cv.theme === 'minimal'
                    ? 'bg-slate-400'
                    : 'bg-slate-200'"
                ></div>
              </div>
            </div>
          </div>

          <!-- CV details -->
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2 pr-6">
              <h2 class="text-base font-bold text-slate-900">{{ cv.title }}</h2>
              <span
                v-if="cv.default"
                class="rounded-full bg-orange-100 px-2 py-1 text-[10px] font-semibold text-orange-600"
              >
                Default
              </span>
            </div>

            <p class="mt-2 text-xs text-slate-400">
              Created on {{ cv.created }} <span class="mx-1">·</span>
              Updated {{ cv.updated }}
            </p>

            <p class="mt-2 max-w-xl text-xs leading-4 text-slate-600">
              {{ cv.description }}
            </p>

            <div class="mt-2 flex flex-wrap gap-2">
              <span
                v-for="tag in ['PDF', 'DOCX', 'English']"
                :key="tag"
                class="rounded-full bg-slate-100 px-2.5 py-1 text-[10px] font-medium text-slate-600"
              >
                {{ tag }}
              </span>
            </div>

            <!-- Actions -->
            <div class="mt-3 flex flex-wrap items-center justify-start gap-2 sm:justify-end">
              
            <button
                @click="viewCv(cv)"
                class="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 text-xs font-medium hover:bg-slate-50"
            >
                <Eye :size="16" /> View
            </button>

              <button
                @click="useCv(cv)"
                class="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 text-xs font-medium hover:bg-slate-50"
              >
                <Download :size="15" /> Download
              </button>

              <button
                @click="useCv(cv)"
                class="inline-flex items-center gap-2 rounded-lg border border-orange-500 px-3 py-2 text-xs font-semibold text-orange-600 hover:bg-orange-50"
              >
                <Send :size="15" /> Use
              </button>
            </div>
          </div>

          <!-- Status and menu -->
          <div class="absolute right-3 top-3 flex items-center gap-2 sm:right-4">
            <span
              class="rounded-full px-2.5 py-1 text-[10px] font-semibold"
              :class="cv.status === 'Active'
                ? 'bg-green-100 text-green-700'
                : 'bg-slate-100 text-slate-600'"
            >
              {{ cv.status }}
            </span>

            <button
              @click="openMenu = openMenu === cv.id ? null : cv.id"
              class="rounded p-1 text-slate-600 hover:bg-slate-100"
              aria-label="CV options"
            >
              <MoreVertical :size="17" />
            </button>

            <div
              v-if="openMenu === cv.id"
              class="absolute right-0 top-8 z-20 w-40 rounded-lg border border-slate-200 bg-white p-1 shadow-lg"
            >
              <button
                @click="setDefault(cv)"
                class="block w-full rounded px-3 py-2 text-left text-xs hover:bg-orange-50"
              >
                Set as default
              </button>
              <button
                @click="toggleStatus(cv)"
                class="block w-full rounded px-3 py-2 text-left text-xs hover:bg-orange-50"
              >
                {{ cv.status === 'Active' ? 'Mark inactive' : 'Mark active' }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Empty state -->
      <section
        v-if="filteredCvs.length === 0"
        class="rounded-xl border border-orange-200 bg-white p-10 text-center"
      >
        <FileText class="mx-auto mb-3 text-slate-400" :size="35" />
        <h2 class="font-semibold">No CVs found</h2>
        <p class="mt-1 text-sm text-slate-500">
          There are no CVs in this category.
        </p>
      </section>
    </div>
  </DashboardLayout>
</template>
