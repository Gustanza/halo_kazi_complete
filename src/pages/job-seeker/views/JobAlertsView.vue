<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  Bell,
  Search,
  Plus,
  MapPin,
  Trash2,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import PageHeader from '../../../components/ui/PageHeader.vue'
import AppButton from '../../../components/ui/AppButton.vue'
import AppModal from '../../../components/ui/AppModal.vue'
import AppInput from '../../../components/ui/AppInput.vue'
import ConfirmDialog from '../../../components/ui/ConfirmDialog.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as notificationsApi from '../../../api/notifications'
import { useToast } from '../../../composables/useToast'

const toast = useToast()

const searchText = ref('')
const loading = ref(true)
const jobAlerts = ref([])

const createOpen = ref(false)
const creating = ref(false)
const newKeyword = ref('')
const newLocation = ref('')

const togglingId = ref(null)
const deleteTarget = ref(null)
const deleting = ref(false)

const filteredAlerts = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  if (!query) return jobAlerts.value
  return jobAlerts.value.filter((alert) =>
    [alert.keyword, alert.location].join(' ').toLowerCase().includes(query),
  )
})

async function load() {
  loading.value = true
  try {
    jobAlerts.value = await notificationsApi.listAlerts()
  } catch {
    toast.error('Could not load your job alerts.')
  } finally {
    loading.value = false
  }
}

async function toggleAlertStatus(id) {
  togglingId.value = id
  try {
    await notificationsApi.toggleAlert(id)
    const alert = jobAlerts.value.find((a) => a.id === id)
    if (alert) alert.active = !alert.active
  } catch {
    toast.error('Could not update that alert.')
  } finally {
    togglingId.value = null
  }
}

async function createAlert() {
  if (!newKeyword.value.trim()) return
  creating.value = true
  try {
    const alert = await notificationsApi.createAlert({
      keyword: newKeyword.value.trim(),
      location: newLocation.value.trim() || 'Any location',
    })
    jobAlerts.value = [...jobAlerts.value, alert]
    toast.success('Job alert created.')
    createOpen.value = false
    newKeyword.value = ''
    newLocation.value = ''
  } catch {
    toast.error('Could not create that alert.')
  } finally {
    creating.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await notificationsApi.deleteAlert(deleteTarget.value.id)
    jobAlerts.value = jobAlerts.value.filter((a) => a.id !== deleteTarget.value.id)
    toast.success('Job alert deleted.')
  } catch {
    toast.error('Could not delete that alert.')
  } finally {
    deleting.value = false
    deleteTarget.value = null
  }
}

onMounted(load)
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-6">

      <!-- Top Search bar -->
      <div class="relative w-full">
        <Search :size="18" class="absolute left-4 top-1/2 -translate-y-1/2" style="color: var(--color-text-muted)" />
        <input
          v-model="searchText"
          type="search"
          placeholder="Search your alerts by keyword or location..."
          class="w-full rounded-2xl border bg-[var(--color-surface)] py-3 pl-11 pr-4 text-sm outline-none transition focus:border-primary"
          style="border-color: var(--color-border); color: var(--color-text)"
        />
      </div>

      <PageHeader title="Job Alerts" description="Get notified when new jobs matching your criteria are posted.">
        <template #actions>
          <AppButton @click="createOpen = true">
            <Plus :size="16" /> Create New Alert
          </AppButton>
        </template>
      </PageHeader>

      <div class="space-y-4">
        <template v-if="loading">
          <Skeleton v-for="i in 3" :key="i" variant="card" height="6rem" />
        </template>

        <EmptyState
          v-else-if="!filteredAlerts.length"
          :icon="Bell"
          title="No job alerts yet"
          description="Create an alert and we'll notify you when matching jobs are posted."
          action-label="Create New Alert"
          @action="createOpen = true"
        />

        <article
          v-for="alert in filteredAlerts"
          v-else
          :key="alert.id"
          class="card-surface p-5"
        >
          <div class="flex items-start justify-between gap-4">
            <div class="flex items-start gap-4 min-w-0">
              <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary-50 text-primary dark:bg-white/10">
                <Bell :size="22" />
              </div>

              <div class="min-w-0">
                <div class="flex items-center gap-3 flex-wrap">
                  <h3 class="text-sm font-bold" style="color: var(--color-text)">{{ alert.keyword }}</h3>
                  <span
                    class="inline-flex items-center rounded-full px-2.5 py-0.5 text-[10px] font-semibold"
                    :class="alert.active ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400' : 'bg-slate-100 text-slate-600 dark:bg-white/10 dark:text-slate-300'"
                  >
                    {{ alert.active ? 'Active' : 'Inactive' }}
                  </span>
                </div>
                <p class="mt-1.5 flex items-center gap-1.5 text-xs" style="color: var(--color-text-muted)">
                  <MapPin :size="13" /> {{ alert.location }}
                </p>
                <p class="mt-2 text-xs" style="color: var(--color-text-muted)">
                  Email me when new jobs matching "{{ alert.keyword }}" in {{ alert.location }} are posted.
                </p>
              </div>
            </div>

            <div class="flex items-center gap-2">
              <button
                type="button"
                class="rounded-lg p-1.5 text-red-500 transition hover:bg-red-50 dark:hover:bg-red-500/10"
                aria-label="Delete alert"
                @click="deleteTarget = alert"
              >
                <Trash2 :size="16" />
              </button>
            </div>
          </div>

          <div class="mt-4 flex items-center justify-end border-t pt-4" style="border-color: var(--color-border)">
            <button
              type="button"
              :disabled="togglingId === alert.id"
              class="relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out disabled:opacity-60"
              :class="alert.active ? 'bg-primary' : 'bg-slate-300 dark:bg-white/20'"
              @click="toggleAlertStatus(alert.id)"
            >
              <span
                class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-lg ring-0 transition duration-200 ease-in-out"
                :class="alert.active ? 'translate-x-5' : 'translate-x-0'"
              />
            </button>
          </div>
        </article>
      </div>
    </div>

    <AppModal :open="createOpen" title="Create job alert" size="sm" @close="createOpen = false">
      <form class="space-y-4" @submit.prevent="createAlert">
        <AppInput v-model="newKeyword" label="Keyword or job title" placeholder="e.g. Frontend Developer" required />
        <AppInput v-model="newLocation" label="Location" placeholder="e.g. Dar es Salaam (optional)" />
        <AppButton type="submit" block :loading="creating">Create alert</AppButton>
      </form>
    </AppModal>

    <ConfirmDialog
      :open="!!deleteTarget"
      title="Delete this job alert?"
      :description="deleteTarget ? `You'll stop getting notified about '${deleteTarget.keyword}' jobs.` : ''"
      confirm-label="Delete"
      :loading="deleting"
      @confirm="confirmDelete"
      @close="deleteTarget = null"
    />
  </DashboardLayout>
</template>
