// Turn API responses into the shapes the landing-page cards render.

// The API sends no brand colours, so each company gets a stable colour from this palette.
const BRAND_PALETTE = ['#1F7A3F', '#F6861F', '#E3001B', '#2F8FF5', '#B064F7', '#2FB573', '#E2231A', '#14213D']

export function brandColor(seed) {
  let hash = 0
  for (const ch of String(seed)) hash = (hash * 31 + ch.charCodeAt(0)) | 0
  return BRAND_PALETTE[Math.abs(hash) % BRAND_PALETTE.length]
}

export const toJobCard = (job) => ({
  id: job.id,
  title: job.title,
  company: job.company.name,
  initials: job.company.initials,
  brandColor: brandColor(job.company.name),
  location: job.region.nameEn,
  type: job.employmentType.nameEn,
})

export const toEmployerCard = (employer) => ({
  id: employer.id,
  name: employer.name,
  initials: employer.initials,
  brandColor: brandColor(employer.name),
  industry: employer.industry?.nameEn ?? '',
  jobsOpen: employer.openJobs,
})

export const toTemplate = (template) => ({
  id: template.id,
  name: template.nameEn,
  scheme: template.scheme ?? 'light',
  accent: template.accent ?? '#FD5C20',
})

/** 22 -> "22", 1540 -> "1.5K+", 2300000 -> "2.3M+" (CountUpValue animates the leading number). */
export function formatCount(n) {
  if (n == null) return '–'
  if (n < 1000) return String(n)
  const [value, unit] = n < 1_000_000 ? [n / 1000, 'K'] : [n / 1_000_000, 'M']
  return `${Math.floor(value * 10) / 10}`.replace(/\.0$/, '') + `${unit}+`
}
