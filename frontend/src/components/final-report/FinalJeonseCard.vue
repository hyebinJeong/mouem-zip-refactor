<script setup>
const props = defineProps({
  salePrice: Number,
  jeonseDeposit: Number,
  jeonseRatio: Number,
  jeonseRatioRating: String,
  username: String,
});
</script>

<template>
  <div class="FinalJeonseCard">
    <div
      class="rounded-3 shadow-sm mx-auto pt-3 pb-2"
      style="max-width: 400px; background-color: #f8f9fa"
    >
      <div class="jeonse-card-wrap px-5 mx-5 text-start">
        <p>예상 매매가 : {{ (salePrice || 0).toLocaleString() }}만원</p>
        <p>전세 보증금 : {{ (jeonseDeposit || 0).toLocaleString() }}만원</p>
        <p>예상 전세가율 : {{ (jeonseRatio || 0).toLocaleString() }}%</p>
      </div>
    </div>
    <p class="mt-5">
      <span>{{ username }}님의 전세가율은 </span>
      <span
        ><span class="fw-bold">{{ jeonseRatioRating }}</span
        >등급입니다.</span
      >
    </p>
  </div>
</template>

<style scoped>
/* .FinalJeonseCard {
} */
</style>
