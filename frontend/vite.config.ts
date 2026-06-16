import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  build: {
    // bundle directly into the Spring Boot jar's static resources
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true,
  },
});
