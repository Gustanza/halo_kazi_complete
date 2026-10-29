// A job seeker's bookmarked jobs - back_resources/docs/Halokazi_API.pdf.
import { getJson, request } from './http'

/** -> { vacancyIds: [...] } */
export const getSavedJobIds = () => getJson('/api/me/saved-jobs/ids', { auth: true })

export const saveJob = (vacancyId) => request('PUT', `/api/me/saved-jobs/${vacancyId}`, { auth: true })

export const unsaveJob = (vacancyId) => request('DELETE', `/api/me/saved-jobs/${vacancyId}`, { auth: true })
