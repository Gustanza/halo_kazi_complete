import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getSavedJobIds, saveJob, unsaveJob } from '../../../api/savedJobsApi'
import { useSession } from '../../../auth/session'

/**
 * Bookmark state for job cards. Job seekers' saves go to the API; guests are sent to log in;
 * other roles can't save jobs, so `canSave` is false for them.
 */
export function useSavedJobs() {
  const session = useSession()
  const router = useRouter()
  const savedIds = ref(new Set())

  const isJobSeeker = computed(() => session.user.value?.role === 'JOB_SEEKER')
  const canSave = computed(() => !session.isAuthenticated.value || isJobSeeker.value)

  async function load() {
    if (!isJobSeeker.value) {
      savedIds.value = new Set()
      return
    }
    try {
      savedIds.value = new Set((await getSavedJobIds()).vacancyIds)
    } catch (error) {
      console.warn('[saved jobs]', error.message)
    }
  }

  /** Optimistic: the icon flips at once and flips back if the API refuses. */
  async function toggle(vacancyId) {
    if (!session.isAuthenticated.value) {
      router.push({ name: 'login', query: { redirect: '/#jobs' } })
      return
    }
    const wasSaved = savedIds.value.has(vacancyId)
    const next = new Set(savedIds.value)
    wasSaved ? next.delete(vacancyId) : next.add(vacancyId)
    savedIds.value = next
    try {
      await (wasSaved ? unsaveJob(vacancyId) : saveJob(vacancyId))
    } catch (error) {
      const reverted = new Set(savedIds.value)
      wasSaved ? reverted.add(vacancyId) : reverted.delete(vacancyId)
      savedIds.value = reverted
      console.warn('[saved jobs]', error.message)
    }
  }

  watch(isJobSeeker, load)

  return { savedIds, canSave, load, toggle, isSaved: (id) => savedIds.value.has(id) }
}
