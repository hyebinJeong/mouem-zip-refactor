<!-- /src/views/category/CategoryAll.vue -->
<template>
  <div
    class="outer-wrapper d-flex justify-content-center align-items-start"
    style="min-height: 100vh; background-color: white; padding: 5rem"
  >
    <!-- 중앙 박스 -->
    <div
      class="rounded shadow-sm d-flex"
      style="width: 1200px; background-color: #f7f9fc"
    >
      <!-- 좌측 사이드바 -->
      <div
        class="border-end"
        style="width: 240px; padding: 2rem 1.5rem; background-color: #f7f9fc"
      >
        <h6 class="fw-bold mb-4">관리자 페이지</h6>
        <ul class="nav flex-column">
          <li class="nav-item mb-2">
            <router-link
              to="/category/term"
              class="nav-link"
              :class="{ active: isActive('/category/term') }"
            >
              용어 관리
            </router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link
              to="/category"
              class="nav-link"
              :class="{
                active: isActive('/category') && !$route.path.includes('/term'),
              }"
            >
              카테고리 관리
            </router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link
              to="/category/special"
              class="nav-link"
              :class="{ active: isActive('/category/special') }"
            >
              특약사항 관리
            </router-link>
          </li>
        </ul>
      </div>

      <!-- 우측 콘텐츠 -->
      <div class="flex-grow-1 bg-white rounded-end" style="width: 100%">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();

const isActive = (path) => {
  if (path === '/category') {
    // 정확히 /category 일 때만 true
    return route.path === '/category';
  }
  return route.path.startsWith(path);
};
</script>

<style scoped>
.nav-link {
  color: #000;
}
.nav-link.active {
  font-weight: bold;
  color: #007bff;
  /* background-color: #e9f0ff; */
  border-radius: 5px;
}
</style>
