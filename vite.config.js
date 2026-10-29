import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue()],
    server: {
      // The app calls relative /api/... URLs; in dev, Vite forwards them to the Spring Boot backend.
      // Override the target with VITE_API_PROXY_TARGET in a .env.local file if your backend runs elsewhere.
      proxy: {
        '/api': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:8081',
          changeOrigin: true,
        },
      },
    },
  }
})
