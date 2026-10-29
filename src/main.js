import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { reveal } from './directives/reveal'
import { onSessionEnded } from './auth/session'
import './styles/main.css'

// A session that ends mid-page (idle timeout, password changed elsewhere, account suspended) sends the
// user from a signed-in page back to Login, which explains why.
onSessionEnded(() => {
  const current = router.currentRoute.value
  if (current.meta.roles) router.replace({ name: 'login', query: { redirect: current.fullPath } })
})

const app = createApp(App)
app.directive('reveal', reveal)
app.use(router)
app.mount('#app')
