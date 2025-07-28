<script setup>
import { RouterLink, RouterView } from 'vue-router';
import Header from './components/Header.vue';
</script>
<template>
  <div>
    <Header v-if="!$route.meta.hideHeader" />
    <RouterView />
  </div>
</template>
