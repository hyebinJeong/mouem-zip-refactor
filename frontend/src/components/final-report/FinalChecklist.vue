<script setup>
import checklistStore from '@/stores/checklistStore';
import { computed } from 'vue';

const props = defineProps({
  checked: { type: Array, required: true },
});

const uncheckedItems = computed(() => {
  return checklistStore.filter((_, index) => !props.checked?.[index]);
});
</script>

<template>
  <div class="FinalChecklist">
    <div
      v-for="(item, index) in uncheckedItems"
      :key="index"
      class="checkedlist-box p-3 rounded mb-3 text-start"
    >
      <p class="mb-0">{{ item }}</p>
    </div>
  </div>
</template>

<style scoped>
.FinalChecklist {
  width: fit-content;
  margin: 0 auto;
}

.checkedlist-box {
  background-color: #fffcd4;
  width: 45vw;
}
</style>
