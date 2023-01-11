import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  define: {
    'process.env': process.env
  },
  base: "/usermgt/",
  server: {
    proxy: {
      "/usermgt/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/usermgt/downloads": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      }
    },
  },
  build: {
    chunkSizeWarningLimit: 2048
  }
})