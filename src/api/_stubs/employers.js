// Public "top employers" showcase on the landing page. Stands in for a public listing
// endpoint over the `employer` table (no EmployerController exists yet at all - Employer.java
// is a bare JPA entity). Only APPROVED employers would ever appear here for real.
import { clone, delay } from './helpers'

const EMPLOYERS = [
  { id: 1, name: 'CRDB Bank PLC', initials: 'CRDB', brandColor: '#1F7A3F', industry: 'Banking and Finance', jobsOpen: 20 },
  { id: 2, name: 'NMB Bank PLC', initials: 'NMB', brandColor: '#F6861F', industry: 'Banking and Finance', jobsOpen: 18 },
  { id: 3, name: 'Vodacom Tanzania', initials: 'VOD', brandColor: '#E3001B', industry: 'Telecommunication', jobsOpen: 18 },
  { id: 4, name: 'Yas', initials: 'YAS', brandColor: '#FFC700', industry: 'Telecommunication', jobsOpen: 15 },
  { id: 5, name: 'Airtel', initials: 'AIR', brandColor: '#E2231A', industry: 'Telecommunication', jobsOpen: 12 },
]

export async function listTopEmployers({ limit = 5 } = {}) {
  await delay(300)
  return clone(EMPLOYERS.slice(0, limit))
}
