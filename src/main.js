import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { reveal } from './directives/reveal'
import { useAuthStore } from './stores/auth'
import './styles/main.css'

const app = createApp(App)
app.directive('reveal', reveal)
app.use(router)

// Resolve any stored token to a user before the router's guard runs on first navigation.
useAuthStore()
  .restoreSession()
  .finally(() => app.mount('#app'))
