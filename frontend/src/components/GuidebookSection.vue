<script setup>
defineProps({
  title: String,
  items: {
    type: Array,
    default: () => [],
  },
});
</script>

<template>
  <div class="GuidebookSection checklist-section mb-5">
    <h5 class="fw-bold mb-3">{{ title }}</h5>

    <ul class="list-group">
      <li
        v-for="(item, index) in items"
        :key="index"
        class="list-group-item border-0 ps-0"
      >
        <!-- 메인 항목 -->
        <div class="fw-semibold mb-2">{{ index + 1 }}. {{ item.main }}</div>
        <!-- 피해 사례 -->
        <div class="text-secondary small mb-1" v-if="item.warning">
          {{ item.warning }}
        </div>
        <!-- 참고 설명 -->
        <div class="text-green small" v-if="item.note">
          {{ item.note }}
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
/* .GuidebookSection {
} */

.text-green {
  color: #007c06;
}

.text-main {
  color: #1a80e5;
}

/* .text-gray {
  color: #61738a;
} */
</style>
