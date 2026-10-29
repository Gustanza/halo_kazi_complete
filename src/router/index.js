import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const sectionRedirect = (hash) => () => ({ path: '/', hash })

const routes = [
  // ---------- Landing (public marketing site) ----------
  {
    path: '/',
    component: () => import('../pages/landing/LandingLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('../pages/landing/views/LandingView.vue') },
      { path: 'login', name: 'login', component: () => import('../pages/landing/views/LoginView.vue') },
      { path: 'register', name: 'register', component: () => import('../pages/landing/views/RegisterView.vue') },
      { path: 'verify-otp', name: 'verify-otp', component: () => import('../pages/landing/views/VerifyOtpView.vue') },
      { path: 'forgot-password', name: 'forgot-password', component: () => import('../pages/landing/views/ForgotPasswordView.vue') },
      { path: 'reset-password', name: 'reset-password', component: () => import('../pages/landing/views/ResetPasswordView.vue') },
    ],
  },
  // Old standalone marketing routes now live as sections on the single-page landing view.
  { path: '/jobs', redirect: sectionRedirect('#jobs') },
  { path: '/employers', redirect: sectionRedirect('#employers') },
  { path: '/cv-builder', redirect: sectionRedirect('#cv-builder') },
  { path: '/about', redirect: sectionRedirect('#about') },

  // ---------- Job seeker (authenticated app) ----------
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('../pages/job-seeker/views/DashboardView.vue'),
    meta: { title: 'Dashboard', requiresAuth: true },
  },
  {
    path: '/dashboard/profile',
    name: 'profile',
    component: () => import('../pages/job-seeker/views/ProfileView.vue'),
    meta: { title: 'My Profile', description: 'Manage your personal details, work history and skills so employers see your best self.', requiresAuth: true },
  },
  {
    path: '/dashboard/cv-builder',
    name: 'cv-builder-app',
    component: () => import('../pages/job-seeker/views/CvBuilder.vue'),
    meta: { title: 'Choose CV Template', requiresAuth: true },
  },
  {
    path: '/dashboard/cv-template',
    name: 'cv-template',
    component: () => import('../pages/job-seeker/views/CvTemplate.vue'),
    meta: { title: 'CV Template', requiresAuth: true },
  },
  {
    path: '/dashboard/my-cvs',
    name: 'my-cvs',
    component: () => import('../pages/job-seeker/views/MyCvs.vue'),
    meta: { title: 'My CVs', requiresAuth: true },
  },
  {
    path: '/dashboard/view-cv',
    name: 'view-cv',
    component: () => import('../pages/job-seeker/views/ViewCv.vue'),
    meta: { title: 'View CV', requiresAuth: true },
  },
  {
    path: '/dashboard/find-jobs',
    name: 'find-jobs',
    component: () => import('../pages/job-seeker/views/FindJobsView.vue'),
    meta: { title: 'Find Jobs', description: 'Search and filter thousands of job opportunities that match your profile.', requiresAuth: true },
  },
  {
    path: '/dashboard/apply/:jobId',
    name: 'job-application',
    component: () => import('../pages/job-seeker/views/JobApplicationView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/dashboard/application-success',
    name: 'application-success',
    component: () => import('../pages/job-seeker/views/JobApplicationSuccessView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/dashboard/saved-jobs',
    name: 'saved-jobs',
    component: () => import('../pages/job-seeker/views/SavedJobsView.vue'),
    meta: { title: 'Saved Jobs', description: 'Jobs you bookmark while browsing will be saved here for later.', requiresAuth: true },
  },
  {
    path: '/dashboard/applications',
    name: 'applications',
    component: () => import('../pages/job-seeker/views/Applications.vue'),
    meta: { title: 'Applications', requiresAuth: true },
  },
  {
    path: '/dashboard/view-application',
    name: 'view-application',
    component: () => import('../pages/job-seeker/views/ViewApplication.vue'),
    meta: { title: 'View Application', requiresAuth: true },
  },
  {
    path: '/dashboard/job-alerts',
    name: 'job-alerts',
    component: () => import('../pages/job-seeker/views/JobAlertsView.vue'),
    meta: { title: 'Job Alerts', description: 'Set up alerts and get notified when matching jobs are posted.', requiresAuth: true },
  },
  {
    path: '/dashboard/notifications',
    name: 'notifications',
    component: () => import('../pages/job-seeker/views/NotificationsView.vue'),
    meta: { title: 'Notifications', requiresAuth: true },
  },
  {
    path: '/dashboard/settings',
    name: 'settings',
    component: () => import('../pages/job-seeker/views/Settings.vue'),
    meta: { title: 'Settings', requiresAuth: true },
  },

  // ---------- Admin portal (not built yet) ----------
  { path: '/admin', name: 'admin', component: () => import('../pages/admin/views/AdminHomeView.vue') },

  // ---------- Employer portal (not built yet) ----------
  { path: '/employer', name: 'employer', component: () => import('../pages/employer/views/EmployerHomeView.vue'), meta: { requiresAuth: true } },

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

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated.value) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
