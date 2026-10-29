import { createRouter, createWebHistory } from 'vue-router'
import { useSession } from '../auth/session'

const sectionRedirect = (hash) => () => ({ path: '/', hash })
const JOB_SEEKER = ['JOB_SEEKER']

const routes = [
  // ---------- Landing (public marketing site) ----------
  {
    path: '/',
    component: () => import('../pages/landing/LandingLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('../pages/landing/views/LandingView.vue') },
      { path: 'login', name: 'login', meta: { guestOnly: true }, component: () => import('../pages/landing/views/LoginView.vue') },
      { path: 'register', name: 'register', meta: { guestOnly: true }, component: () => import('../pages/landing/views/RegisterView.vue') },
      { path: 'verify-otp', name: 'verify-otp', meta: { guestOnly: true }, component: () => import('../pages/landing/views/VerifyOtpView.vue') },
      { path: 'forgot-password', name: 'forgot-password', meta: { guestOnly: true }, component: () => import('../pages/landing/views/ForgotPasswordView.vue') },
    ],
  },
  // Old standalone marketing routes now live as sections on the single-page landing view.
  { path: '/jobs', redirect: sectionRedirect('#jobs') },
  { path: '/employers', redirect: sectionRedirect('#employers') },
  { path: '/cv-builder', redirect: sectionRedirect('#cv-builder') },
  { path: '/about', redirect: sectionRedirect('#about') },

  // ---------- Job seeker (authenticated app) ----------
  // Every /dashboard route needs a signed-in job seeker (meta.roles, enforced in router.beforeEach below).
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('../pages/job-seeker/views/DashboardView.vue'),
    meta: { roles: JOB_SEEKER, title: 'Dashboard' },
  },
  {
    path: '/dashboard/profile',
    name: 'profile',
    component: () => import('../pages/job-seeker/views/ProfileView.vue'),
    meta: { roles: JOB_SEEKER, title: 'My Profile', description: 'Manage your personal details, work history and skills so employers see your best self.' },
  },
  {
    path: '/dashboard/cv-builder',
    name: 'cv-builder-app',
    component: () => import('../pages/job-seeker/views/CvBuilder.vue'),
    meta: { roles: JOB_SEEKER, title: 'Choose CV Template' },
  },
  {
    path: '/dashboard/cv-template',
    name: 'cv-template',
    component: () => import('../pages/job-seeker/views/CvTemplate.vue'),
    meta: { roles: JOB_SEEKER, title: 'CV Template' },
  },
  {
    path: '/dashboard/my-cvs',
    name: 'my-cvs',
    component: () => import('../pages/job-seeker/views/MyCvs.vue'),
    meta: { roles: JOB_SEEKER, title: 'My CVs' },
  },
  {
    path: '/dashboard/view-cv',
    name: 'view-cv',
    component: () => import('../pages/job-seeker/views/ViewCv.vue'),
    meta: { roles: JOB_SEEKER, title: 'View CV' },
  },
  {
    path: '/dashboard/find-jobs',
    name: 'find-jobs',
    component: () => import('../pages/job-seeker/views/FindJobsView.vue'),
    meta: { roles: JOB_SEEKER, title: 'Find Jobs', description: 'Search and filter thousands of job opportunities that match your profile.' },
  },
  {
    path: '/dashboard/apply/:jobId',
    name: 'job-application',
    component: () => import('../pages/job-seeker/views/JobApplicationView.vue'),
    meta: { roles: JOB_SEEKER },
  },
  {
    path: '/dashboard/application-success',
    name: 'application-success',
    component: () => import('../pages/job-seeker/views/JobApplicationSuccessView.vue'),
    meta: { roles: JOB_SEEKER },
  },
  {
    path: '/dashboard/saved-jobs',
    name: 'saved-jobs',
    component: () => import('../pages/job-seeker/views/SavedJobsView.vue'),
    meta: { roles: JOB_SEEKER, title: 'Saved Jobs', description: 'Jobs you bookmark while browsing will be saved here for later.' },
  },
  {
    path: '/dashboard/applications',
    name: 'applications',
    component: () => import('../pages/job-seeker/views/Applications.vue'),
    meta: { roles: JOB_SEEKER, title: 'Applications' },
  },
  {
    path: '/dashboard/view-application',
    name: 'view-application',
    component: () => import('../pages/job-seeker/views/ViewApplication.vue'),
    meta: { roles: JOB_SEEKER, title: 'View Application' },
  },
  {
    path: '/dashboard/job-alerts',
    name: 'job-alerts',
    component: () => import('../pages/job-seeker/views/JobAlertsView.vue'),
    meta: { roles: JOB_SEEKER, title: 'Job Alerts', description: 'Set up alerts and get notified when matching jobs are posted.' },
  },
  {
    path: '/dashboard/notifications',
    name: 'notifications',
    component: () => import('../pages/job-seeker/views/NotificationsView.vue'),
    meta: { roles: JOB_SEEKER, title: 'Notifications' },
  },
  {
    path: '/dashboard/settings',
    name: 'settings',
    component: () => import('../pages/job-seeker/views/Settings.vue'),
    meta: { roles: JOB_SEEKER, title: 'Settings' },
  },

  // ---------- Admin portal (not built yet) ----------
  { path: '/admin', name: 'admin', meta: { roles: ['ADMIN', 'SUPER_ADMIN'] }, component: () => import('../pages/admin/views/AdminHomeView.vue') },

  // ---------- Employer portal (not built yet) ----------
  { path: '/employer', name: 'employer', meta: { roles: ['EMPLOYER'] }, component: () => import('../pages/employer/views/EmployerHomeView.vue') },

  // ---------- 404 ----------
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../pages/landing/views/NotFoundView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to) {
    if (to.hash) {
      return { el: to.hash, top: 96, behavior: 'smooth' }
    }
    return { top: 0 }
  },
})

// meta.roles: only signed-in users with one of these roles. meta.guestOnly: signed-in users go home instead.
router.beforeEach(async (to) => {
  const session = useSession()

  if (to.meta.roles) {
    if (!session.isAuthenticated.value) return { name: 'login', query: { redirect: to.fullPath } }
    // Confirm the token is still alive (it may have idled out); this also counts as activity.
    if (!(await session.refresh())) return { name: 'login', query: { redirect: to.fullPath } }
    if (!to.meta.roles.includes(session.user.value?.role)) return session.homeFor(session.user.value)
  }

  if (to.meta.guestOnly && session.isAuthenticated.value) return session.homeFor(session.user.value)
  return true
})

export default router
