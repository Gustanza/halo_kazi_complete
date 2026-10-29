import { reactive, ref, shallowRef, watch } from 'vue'
import { searchJobs } from '../../../api/publicApi'
import { toJobCard } from '../data/mappers'

export const emptyFilters = () => ({ q: '', region: '', type: '', category: '', experience: '', salary: '', posted: '' })

/** Open jobs with filters and paging. Changing a filter goes back to page 1; stale responses are discarded. */
export function useJobBoard({ pageSize = 5 } = {}) {
  const filters = reactive(emptyFilters())
  const page = ref(1)
  const jobs = shallowRef([])
  const totalItems = ref(0)
  const totalPages = ref(0)
  const loading = ref(false)
  const error = ref(null)
  let inFlight

  async function load() {
    inFlight?.abort()
    const controller = (inFlight = new AbortController())
    loading.value = true
    error.value = null
    try {
      const result = await searchJobs(filters, { page: page.value, size: pageSize, signal: controller.signal })
      if (result.items.length === 0 && result.totalPages > 0 && page.value > result.totalPages) {
        page.value = result.totalPages // jobs closed since this page was chosen; the page watcher reloads
        return
      }
      jobs.value = result.items.map(toJobCard)
      totalItems.value = result.totalItems
      totalPages.value = result.totalPages
    } catch (e) {
      if (e.name === 'AbortError') return
      error.value = e
      jobs.value = []
    } finally {
      if (inFlight === controller) loading.value = false
    }
  }

  function clearFilters() {
    Object.assign(filters, emptyFilters())
  }

  watch(filters, () => {
    if (page.value !== 1) page.value = 1 // the page watcher reloads
    else load()
  })
  watch(page, load)

  return { filters, page, jobs, totalItems, totalPages, loading, error, load, clearFilters }
}
