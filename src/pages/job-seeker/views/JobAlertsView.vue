<script setup>
import { onMounted, ref } from 'vue'
import {
  Bell,
  BriefcaseBusiness,
  MapPin,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import PageHeader from '../../../components/ui/PageHeader.vue'
import AppButton from '../../../components/ui/AppButton.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as notificationsApi from '../../../api/notifications'
import { useToast } from '../../../composables/useToast'

const toast = useToast()

const loading = ref(true)
const saving = ref(false)

const receiveJobAlerts = ref(true)
const categoryIds = ref([])
const regionIds = ref([])
const employmentTypeIds = ref([])

const categories = ref([])
const regions = ref([])
const employmentTypes = ref([])

const matches = ref([])
const matchesLoading = ref(false)

function toggleId(list, id) {
  const index = list.indexOf(id)
  if (index === -1) list.push(id)
  else list.splice(index, 1)
}

async function loadMatches() {
  matchesLoading.value = true
  try {
    matches.value = await notificationsApi.getMatchedVacancies()
  } catch {
    // Non-blocking - the preferences form above is the important part of this page.
    matches.value = []
  } finally {
    matchesLoading.value = false
  }
}

async function load() {
  loading.value = true
  try {
    const [preferences, cats, regs, types] = await Promise.all([
      notificationsApi.getAlertPreferences(),
      notificationsApi.getJobCategories(),
      notificationsApi.getRegions(),
      notificationsApi.getEmploymentTypes(),
    ])
    receiveJobAlerts.value = preferences.receiveJobAlerts
    categoryIds.value = [...preferences.categoryIds]
    regionIds.value = [...preferences.regionIds]
    employmentTypeIds.value = [...preferences.employmentTypeIds]
    categories.value = cats
    regions.value = regs
    employmentTypes.value = types
    loadMatches()
  } catch {
    toast.error('Could not load your job alert preferences.')
  } finally {
    loading.value = false
  }
}

async function savePreferences() {
  saving.value = true
  try {
    await notificationsApi.updateAlertPreferences({
      receiveJobAlerts: receiveJobAlerts.value,
      categoryIds: categoryIds.value,
      regionIds: regionIds.value,
      employmentTypeIds: employmentTypeIds.value,
    })
    toast.success('Job alert preferences saved.')
    loadMatches()
  } catch {
    toast.error('Could not save your preferences. Please try again.')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-4xl space-y-6">
      <PageHeader title="Job Alerts" description="Get notified when new jobs matching your preferences are posted." />

      <template v-if="loading">
        <Skeleton variant="card" height="10rem" />
        <Skeleton variant="card" height="14rem" />
      </template>

      <template v-else>
        <!-- Preferences -->
        <section class="card-surface space-y-5 p-5">
          <div class="flex items-center justify-between gap-4 border-b pb-4" style="border-color: var(--color-border)">
            <div class="flex items-center gap-3">
              <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary-50 text-primary dark:bg-white/10">
                <Bell :size="20" />
              </div>
              <div>
                <p class="text-sm font-bold" style="color: var(--color-text)">Receive job alerts</p>
                <p class="text-xs" style="color: var(--color-text-muted)">
                  Get notified when jobs matching your preferences are posted.
                </p>
              </div>
            </div>
            <button
              type="button"
              class="relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out"
              :class="receiveJobAlerts ? 'bg-primary' : 'bg-slate-300 dark:bg-white/20'"
              @click="receiveJobAlerts = !receiveJobAlerts"
            >
              <span
                class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-lg ring-0 transition duration-200 ease-in-out"
                :class="receiveJobAlerts ? 'translate-x-5' : 'translate-x-0'"
              />
            </button>
          </div>

          <div class="grid gap-5 sm:grid-cols-3">
            <div>
              <p class="mb-2 text-xs font-semibold" style="color: var(--color-text)">Categories</p>
              <div class="max-h-40 space-y-1.5 overflow-y-auto pr-1">
                <label v-for="cat in categories" :key="cat.id" class="flex cursor-pointer items-center gap-2 text-xs" style="color: var(--color-text-muted)">
                  <input type="checkbox" class="accent-primary" :checked="categoryIds.includes(cat.id)" @change="toggleId(categoryIds, cat.id)" />
                  {{ cat.nameEn }}
                </label>
                <p v-if="!categories.length" class="text-xs" style="color: var(--color-text-muted)">No categories available.</p>
              </div>
            </div>

            <div>
              <p class="mb-2 text-xs font-semibold" style="color: var(--color-text)">Regions</p>
              <div class="max-h-40 space-y-1.5 overflow-y-auto pr-1">
                <label v-for="region in regions" :key="region.id" class="flex cursor-pointer items-center gap-2 text-xs" style="color: var(--color-text-muted)">
                  <input type="checkbox" class="accent-primary" :checked="regionIds.includes(region.id)" @change="toggleId(regionIds, region.id)" />
                  {{ region.nameEn }}
                </label>
                <p v-if="!regions.length" class="text-xs" style="color: var(--color-text-muted)">No regions available.</p>
              </div>
            </div>

            <div>
              <p class="mb-2 text-xs font-semibold" style="color: var(--color-text)">Employment types</p>
              <div class="max-h-40 space-y-1.5 overflow-y-auto pr-1">
                <label v-for="type in employmentTypes" :key="type.id" class="flex cursor-pointer items-center gap-2 text-xs" style="color: var(--color-text-muted)">
                  <input type="checkbox" class="accent-primary" :checked="employmentTypeIds.includes(type.id)" @change="toggleId(employmentTypeIds, type.id)" />
                  {{ type.nameEn }}
                </label>
                <p v-if="!employmentTypes.length" class="text-xs" style="color: var(--color-text-muted)">No employment types available.</p>
              </div>
            </div>
          </div>

          <div class="flex justify-end border-t pt-4" style="border-color: var(--color-border)">
            <AppButton :loading="saving" @click="savePreferences">Save preferences</AppButton>
          </div>
        </section>

        <!-- Matches -->
        <section class="space-y-3">
          <h2 class="text-sm font-bold" style="color: var(--color-text)">Matching jobs</h2>

          <template v-if="matchesLoading">
            <Skeleton v-for="i in 2" :key="i" variant="card" height="5rem" />
          </template>

          <EmptyState
            v-else-if="!matches.length"
            :icon="Bell"
            title="No matches yet"
            description="No jobs match your alert preferences yet."
          />

          <RouterLink
            v-for="job in matches"
            v-else
            :key="job.id"
            :to="`/dashboard/apply/${job.id}`"
            class="card-surface card-hover flex items-center gap-4 p-4"
          >
            <div
              class="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg text-sm font-extrabold"
              :style="{ backgroundColor: (job.brandColor || '#FD5C20') + '1a', color: job.brandColor || '#FD5C20' }"
            >
              {{ job.initials }}
            </div>
            <div class="min-w-0 flex-1">
              <h3 class="truncate text-sm font-bold" style="color: var(--color-text)">{{ job.title }}</h3>
              <p class="text-xs font-semibold" style="color: var(--color-text-muted)">{{ job.company }}</p>
              <div class="mt-1 flex flex-wrap gap-x-4 gap-y-1 text-[11px]" style="color: var(--color-text-muted)">
                <span class="inline-flex items-center gap-1"><MapPin :size="12" /> {{ job.location }}</span>
                <span class="inline-flex items-center gap-1"><BriefcaseBusiness :size="12" /> {{ job.type }}</span>
                <span v-if="job.salaryDisplay">{{ job.salaryDisplay }}</span>
              </div>
            </div>
          </RouterLink>
        </section>
      </template>
    </div>
  </DashboardLayout>
</template>
