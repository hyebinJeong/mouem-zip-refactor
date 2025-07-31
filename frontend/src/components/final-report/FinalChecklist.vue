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
    <template v-if="uncheckedItems.length > 0">
      <div
        v-for="(item, index) in uncheckedItems"
        :key="index"
        class="checkedlist-box p-3 rounded mb-3 text-start"
      >
        <p class="mb-0">{{ item }}</p>
      </div>
    </template>
    <div v-else>
      <h3>모든 항목을 체크했어요.</h3>
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
