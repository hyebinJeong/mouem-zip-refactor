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
  <div
    class="FinalJeonseCard card p-4 text-start mx-auto"
    style="max-width: 400px"
  >
    <p>예상 매매가:{{ (salePrice || 0).toLocaleString() }}만원</p>
    <p>전세 보증금:{{ (jeonseDeposit || 0).toLocaleString() }}만원</p>
    <p>예상 전세가율:{{ (jeonseRatio || 0).toLocaleString() }}%</p>
    <hr />
    <p class="mt-3">
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
