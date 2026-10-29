<script setup>
import { computed, onMounted, ref } from 'vue'
import { UserRound, FileEdit, BellRing, Search, ArrowRight } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import StatCard from '../components/dashboard/StatCard.vue'
import RecommendedJobItem from '../components/dashboard/RecommendedJobItem.vue'
import QuickActionItem from '../components/dashboard/QuickActionItem.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import { useSession } from '../../../auth/session'
import * as profileApi from '../../../api/profile'
import { useToast } from '../../../composables/useToast'

const session = useSession()
const toast = useToast()

const loading = ref(true)
const stats = ref([])
const recommendedJobs = ref([])

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good Morning'
  if (hour < 18) return 'Good Afternoon'
  return 'Good Evening'
})

const firstName = computed(() => session.user.value?.fullName?.split(/\s+/)[0] ?? '')

// RecommendedJobItem expects {title, company, initials, brandColor, location, type, isNew} -
// the vacancy-shaped stub returns {title, company, companyInitials, companyColor, location,
// employmentType}, so adapt here rather than reshaping the shared jobs stub.
function toRecommendedJobItem(job) {
  return {
    id: job.id,
    title: job.title,
    company: job.company,
    initials: job.companyInitials,
    brandColor: job.companyColor,
    location: job.location,
    type: job.employmentType,
    isNew: Date.now() - new Date(job.postedAt).getTime() < 1000 * 60 * 60 * 24 * 3,
  }
}

const quickActions = [
  { icon: UserRound, iconBg: '#FD5C20', title: 'Complete profile', description: 'Add more details', to: '/dashboard/profile' },
  { icon: FileEdit, iconBg: '#B064F7', title: 'Build Your CV', description: 'Create a professional CV', to: '/dashboard/cv-builder' },
  { icon: BellRing, iconBg: '#2F8FF5', title: 'Set Jobs Alerts', description: 'Get notified of new jobs', to: '/dashboard/job-alerts' },
  { icon: Search, iconBg: '#2FB573', title: 'Browse Jobs', description: 'Find suitable jobs', to: '/dashboard/find-jobs' },
]

onMounted(async () => {
  try {
    const summary = await profileApi.getDashboardSummary()
    stats.value = summary.stats
    recommendedJobs.value = summary.recommendedJobs.map(toRecommendedJobItem)
  } catch {
    toast.error('Could not load your dashboard. Please refresh the page.')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <DashboardLayout>
    <h1 class="text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">
      {{ greeting }}{{ firstName ? `, ${firstName}` : '' }}!
    </h1>
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">Here, what's happening with your job search today.</p>

    <div class="mt-6 grid grid-cols-2 gap-4 lg:grid-cols-4">
      <template v-if="loading">
        <Skeleton v-for="i in 4" :key="i" variant="card" height="7.5rem" />
      </template>
      <StatCard v-else v-for="stat in stats" :key="stat.key" :stat-key="stat.key" :value="stat.value" :label="stat.label" :to="stat.to" :color="stat.color" />
    </div>

    <div class="card-surface mt-8 p-5 sm:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold" style="color: var(--color-text)">Recommended for you</h2>
          <p class="text-xs" style="color: var(--color-text-muted)">Jobs that match your profile and preferences.</p>
        </div>
        <RouterLink to="/dashboard/find-jobs" class="hidden items-center gap-1 text-sm font-semibold text-primary transition hover:text-primary-600 sm:inline-flex">
          View all jobs <ArrowRight :size="14" />
        </RouterLink>
      </div>
      <div class="mt-5 space-y-3">
        <template v-if="loading">
          <Skeleton v-for="i in 3" :key="i" variant="card" height="4.5rem" />
        </template>
        <RecommendedJobItem v-else v-for="job in recommendedJobs" :key="job.id" :job="job" />
      </div>
    </div>

    <div class="card-surface mt-6 p-5 sm:p-6">
      <h2 class="text-lg font-semibold" style="color: var(--color-text)">Quick Actions</h2>
      <p class="text-xs" style="color: var(--color-text-muted)">Keep your profile fresh and your job search moving.</p>
      <div class="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <QuickActionItem v-for="action in quickActions" :key="action.title" v-bind="action" />
      </div>
    </div>
  </DashboardLayout>
</template>
