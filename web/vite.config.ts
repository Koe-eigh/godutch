import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'build'
  },
  server: {
    port: 5173,
    strictPort: true,
    open: false,
    proxy: {
      '/api': {
        target: process.env.API_URL || 'http://localhost:8080',
        changeOrigin: true,
        // Docker の nginx と同様に、/api プレフィックスを削除して転送
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
