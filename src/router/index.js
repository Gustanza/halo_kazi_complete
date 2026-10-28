import { createRouter, createWebHistory } from 'vue-router'

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
    meta: { title: 'Dashboard' },
  },
  {
    path: '/dashboard/profile',
    name: 'profile',
    component: () => import('../pages/job-seeker/views/ProfileView.vue'),
    meta: { title: 'My Profile', description: 'Manage your personal details, work history and skills so employers see your best self.' },
  },
  {
    path: '/dashboard/cv-builder',
    name: 'cv-builder-app',
    component: () => import('../pages/job-seeker/views/PlaceholderView.vue'),
    meta: { title: 'CV Builder', description: 'Build a professional CV in minutes using our guided template editor.' },
  },
  {
    path: '/dashboard/my-cvs',
    name: 'my-cvs',
    component: () => import('../pages/job-seeker/views/PlaceholderView.vue'),
    meta: { title: 'My CVs', description: 'All the CVs you have created and downloaded will show up here.' },
  },
  {
    path: '/dashboard/find-jobs',
    name: 'find-jobs',
    component: () => import('../pages/job-seeker/views/FindJobsView.vue'),
    meta: { title: 'Find Jobs', description: 'Search and filter thousands of job opportunities that match your profile.' },
  },
  {
  path: '/dashboard/apply/:jobId',
  name: 'job-application',
  component: () => import( '../pages/job-seeker/views/JobApplicationView.vue')
  },
  {
  path: '/dashboard/application-success',
  name: 'application-success',
  component: () => import('../pages/job-seeker/views/JobApplicationSuccessView.vue')
 },
  {
    path: '/dashboard/saved-jobs',
    name: 'saved-jobs',
    component: () => import('../pages/job-seeker/views/SavedJobsView.vue'),
    meta: { title: 'Saved Jobs', description: 'Jobs you bookmark while browsing will be saved here for later.' },
  },
  {
    path: '/dashboard/applications',
    name: 'applications',
    component: () => import('../pages/job-seeker/views/PlaceholderView.vue'),
    meta: { title: 'Applications', description: 'Track the status of every job you have applied to.' },
  },
  {
    path: '/dashboard/job-alerts',
    name: 'job-alerts',
    component: () => import('../pages/job-seeker/views/JobAlertsView.vue'),
    meta: { title: 'Job Alerts', description: 'Set up alerts and get notified when matching jobs are posted.' },
  },
  {
    path: '/dashboard/settings',
    name: 'settings',
    component: () => import('../pages/job-seeker/views/PlaceholderView.vue'),
    meta: { title: 'Settings', description: 'Manage your account, notification and privacy preferences.' },
  },

  // ---------- Admin portal (not built yet) ----------
  { path: '/admin', name: 'admin', component: () => import('../pages/admin/views/AdminHomeView.vue') },

  // ---------- Employer portal (not built yet) ----------
  { path: '/employer', name: 'employer', component: () => import('../pages/employer/views/EmployerHomeView.vue') },
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

export default router
