import './assets/main.css';
import 'bootstrap/dist/css/bootstrap.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';
import { useAuthStore } from '@/stores/auth';

async function bootstrap() {
  const app = createApp(App);

  app.use(createPinia());

  const auth = useAuthStore();
  await auth.tryRefresh();

  app.use(router);
  app.mount('#app');
}

bootstrap();
