<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import ConfirmDialog from '../../../components/ui/ConfirmDialog.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import {
  FileText, MoreVertical, Eye, Download, Trash2,
} from 'lucide-vue-next'
import * as cvApi from '../../../api/cv'
import { useToast } from '../../../composables/useToast'

const router = useRouter()
const toast = useToast()

const defaultTab = ref('All CVs')
const sortBy = ref('Most Recent')
const openMenu = ref(null)
const loading = ref(true)
const cvs = ref([])
const deleteTarget = ref(null)
const deleting = ref(false)

// Visual theme/preview per template id - matches the 5 layouts CvBuilder.vue offers.
const THEME_BY_TEMPLATE = { 1: 'professional', 2: 'modern', 3: 'minimal', 4: 'creative', 5: 'simple' }

async function load() {
  loading.value = true
  try {
    cvs.value = await cvApi.listCvs()
  } catch {
    toast.error('Could not load your CVs.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}

// `isLastUsed` from the backend/stub drives the "Default" badge - the CV a new one starts from.
const cvList = computed(() =>
  cvs.value.map(cv => ({
    ...cv,
    title: cv.cvName,
    description: cv.summary || cv.professionalTitle,
    created: formatDate(cv.createdAt),
    updated: formatDate(cv.updatedAt),
    theme: THEME_BY_TEMPLATE[cv.templateId] || 'simple',
    default: cv.isLastUsed,
    status: cv.isLastUsed ? 'Default' : 'Non Default',
  }))
)

const filteredCvs = computed(() => {
  let result = [...cvList.value]

  if (defaultTab.value === 'Default') {
    result = result.filter(cv => cv.default)
  } else if (defaultTab.value === 'Non Default') {
    result = result.filter(cv => !cv.default)
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

async function setDefault(cv) {
  openMenu.value = null
  await cvApi.setLastUsedCv(cv.id)
  await load()
}

function viewCv(cv) {
  router.push({
    path: '/dashboard/view-cv',
    query: {
      cv: cv.id,
      template: cv.templateId,
    },
  })
}

function confirmDelete(cv) {
  openMenu.value = null
  deleteTarget.value = cv
}

async function deleteCv() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await cvApi.deleteCv(deleteTarget.value.id)
    toast.success('CV deleted.')
    deleteTarget.value = null
    await load()
  } catch {
    toast.error('Could not delete this CV.')
  } finally {
    deleting.value = false
  }
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
            v-for="tab in ['All CVs', 'Default', 'Non Default']"
            :key="tab"
            @click="defaultTab = tab"
            class="border-b-2 px-3 py-2 text-xs font-medium transition"
            :class="defaultTab === tab
              ? 'border-orange-500 text-orange-600'
              : 'border-transparent text-slate-500 hover:text-orange-500'"
          >
            {{ tab }}
            <span>
              ({{ tab === 'All CVs'
                ? cvs.length
                : tab === 'Default'
                  ? cvList.filter(cv => cv.default).length
                  : cvList.filter(cv => !cv.default).length }})
            </span>
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

      <!-- Loading -->
      <template v-if="loading">
        <Skeleton v-for="i in 3" :key="i" variant="card" height="9rem" />
      </template>

      <!-- CV cards -->
      <section
        v-for="cv in filteredCvs"
        v-else
        :key="cv.id"
        class="relative rounded-xl border bg-white p-3 sm:px-5 sm:py-3"
        :class="cv.default
          ? 'border-orange-500 ring-1 ring-orange-200'
          : 'border-orange-200'"
      >
        <div class="flex flex-col gap-3 sm:flex-row sm:gap-5">

          <!-- CV preview thumbnail -->
          <div
            class="flex h-32 w-28 shrink-0 self-start overflow-hidden rounded-md border border-slate-100 bg-slate-50 p-1"
          >
            <div class="relative flex h-full w-full overflow-hidden rounded-sm bg-white">

              <!-- Professional -->
              <template v-if="cv.theme === 'professional'">
                <div class="w-[34%] bg-blue-100 p-1">
                  <div class="mx-auto mb-2 h-5 w-5 rounded-full bg-blue-300"></div>
                  <div v-for="n in 6" :key="n" class="mb-1 h-1 rounded bg-blue-300"></div>
                </div>
                <div class="flex-1 p-1.5">
                  <div class="mb-1 h-2 w-4/5 rounded-sm bg-blue-800"></div>
                  <div v-for="n in 3" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                  <div class="mb-1 mt-3 h-1.5 w-3/5 bg-blue-300"></div>
                  <div v-for="n in 7" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                </div>
              </template>

              <!-- Modern -->
              <template v-else-if="cv.theme === 'modern'">
                <div class="w-[34%] bg-orange-500 p-1">
                  <div class="mx-auto mb-2 h-5 w-5 rounded-full bg-orange-200"></div>
                  <div v-for="n in 6" :key="n" class="mb-1 h-1 rounded bg-orange-100"></div>
                </div>
                <div class="flex-1 p-1.5">
                  <div class="mb-1 h-2 w-4/5 rounded-sm bg-orange-500"></div>
                  <div v-for="n in 3" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                  <div class="mb-1 mt-3 h-1.5 w-3/5 bg-orange-300"></div>
                  <div v-for="n in 7" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                </div>
              </template>

              <!-- Minimal -->
              <template v-else-if="cv.theme === 'minimal'">
                <div class="w-full p-2">
                  <div class="mb-1 h-2 w-4/5 rounded-sm bg-slate-800"></div>
                  <div class="mb-2 h-1 w-3/5 bg-slate-400"></div>
                  <div class="mb-1 mt-3 h-1.5 w-2/5 bg-slate-700"></div>
                  <div v-for="n in 5" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                  <div class="mb-1 mt-3 h-1.5 w-2/5 bg-slate-700"></div>
                  <div v-for="n in 4" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                </div>
              </template>

              <!-- Creative -->
              <template v-else-if="cv.theme === 'creative'">
                <div class="w-[34%] bg-violet-800 p-1">
                  <div class="mx-auto mb-2 h-5 w-5 rounded-full bg-violet-300"></div>
                  <div v-for="n in 6" :key="n" class="mb-1 h-1 rounded bg-violet-300"></div>
                </div>
                <div class="flex-1 p-1.5">
                  <div class="mb-1 h-2 w-4/5 rounded-sm bg-violet-700"></div>
                  <div v-for="n in 3" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                  <div class="mb-1 mt-3 h-1.5 w-3/5 bg-violet-400"></div>
                  <div v-for="n in 7" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                </div>
              </template>

              <!-- Simple -->
              <template v-else>
                <div class="w-full border-t-4 border-slate-800 p-2">
                  <div class="mb-1 h-2 w-4/5 rounded-sm bg-slate-700"></div>
                  <div class="mb-2 h-1 w-3/5 bg-slate-300"></div>
                  <div class="mb-1 mt-3 h-1.5 w-2/5 bg-slate-500"></div>
                  <div v-for="n in 5" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                  <div class="mb-1 mt-3 h-1.5 w-2/5 bg-slate-500"></div>
                  <div v-for="n in 4" :key="n" class="mb-1 h-1 rounded bg-slate-200"></div>
                </div>
              </template>

            </div>
          </div>

          <!-- CV details -->
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2 pr-6">
              <h2 class="text-base font-bold text-slate-900">
                {{ cv.title }}
              </h2>
            </div>

            <p class="mt-2 text-xs text-slate-400">
              Created on {{ cv.created }}
              <span class="mx-1">·</span>
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
            </div>
          </div>

          <!-- Status and menu -->
          <div class="absolute right-3 top-3 flex items-center gap-2 sm:right-4">
            <span
              class="rounded-full px-2.5 py-1 text-[10px] font-semibold"
              :class="cv.default
                ? 'bg-orange-100 text-orange-700'
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
                :disabled="cv.default"
                class="block w-full rounded px-3 py-2 text-left text-xs hover:bg-orange-50 disabled:cursor-default disabled:text-orange-600"
              >
                {{ cv.default ? 'Current Default' : 'Set as Default' }}
              </button>
              <button
                @click="confirmDelete(cv)"
                class="flex w-full items-center gap-2 rounded px-3 py-2 text-left text-xs text-red-500 hover:bg-red-50"
              >
                <Trash2 :size="14" /> Delete
              </button>
            </div>
          </div>

        </div>
      </section>

      <!-- Empty state -->
      <EmptyState
        v-if="!loading && filteredCvs.length === 0"
        :icon="FileText"
        title="No CVs found"
        description="There are no CVs in this category yet."
        action-label="Create your first CV"
        @action="buildNewCv"
      />

      <ConfirmDialog
        :open="!!deleteTarget"
        title="Delete this CV?"
        :description="`'${deleteTarget?.title}' will be permanently deleted.`"
        confirm-label="Delete"
        :loading="deleting"
        @confirm="deleteCv"
        @close="deleteTarget = null"
      />

    </div>
  </DashboardLayout>
</template>