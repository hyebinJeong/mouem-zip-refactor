<script setup>
import checklistStore from '@/stores/checklistStore';
import { computed } from 'vue';

const props = defineProps({
  checked: { type: Array, required: true },
});
</script>

<template>
  <div class="FinalChecklist">
    <h2>체크리스트 컴포넌트</h2>
  </div>
</template>

<style scoped>
/* .FinalChecklist {
} */
</style>
