<script setup>
import { getGradeColor } from '@/composables/final-report/useGradeColor';
import { computed } from 'vue';

const props = defineProps({
  salePrice: Number,
  jeonseDeposit: Number,
  jeonseRatio: Number,
  jeonseRatioRating: String,
  username: String,
});

const gradeColorStyle = computed(() => getGradeColor(props.jeonseRatioRating));
</script>

<template>
  <div class="FinalJeonseCard">
    <div
      class="rounded-3 shadow-sm mx-auto pt-3 pb-3"
      style="max-width: 640px; width: 100%; background-color: #f8f9fa"
    >
      <div class="px-4 text-start">
        <p class="mb-0 fs-6 fw-semibold">
          예상 매매가 : {{ (salePrice || 0).toLocaleString() }}만원
        </p>
        <p class="mb-0 fs-6 fw-semibold">
          전세 보증금 : {{ (jeonseDeposit || 0).toLocaleString() }}만원
        </p>
        <p class="mb-0 fs-6 fw-semibold">
          예상 전세가율 : {{ (jeonseRatio || 0).toLocaleString() }}%
        </p>
      </div>
    </div>
    <h5
      class="mt-4 text-center fs-5 fw-normal text-nowrap"
      style="max-width: 640px; margin: 0 auto"
    >
      <span>{{ username }}님의 전세가율은 </span>
      <span
        ><span class="fw-bold" :style="{ color: gradeColorStyle }"
          >{{ jeonseRatioRating }}등급</span
        >입니다.</span
      >
    </h5>
  </div>
</template>

<style scoped>
@media (max-width: 576px) {
  .FinalJeonseCard h5 {
    white-space: normal;
    max-width: 100% !important;
  }
}
</style>
