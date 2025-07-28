<template>
  <div class="custom-card" role="button" @click="$emit('open', term)">
    <h5 class="term-title">{{ term.termName }}</h5>
    <span class="term-badge" :style="{ backgroundColor: term.categoryColor }">{{ term.categoryName }}</span>
    <p class="term-definition">{{ term.termDefine }}</p>
  </div>
</template>

<script>
export default {
  name: 'TermCard',
  props: {
    term: Object
  }
}
</script>

<style scoped>
.custom-card {
  background-color: #EBF3FF;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.1s ease-in-out;

  /* 중앙 정렬용 flex 설정 */
  display: flex;
  flex-direction: column;
  align-items: center;        /* 가로 방향 중앙 */
  justify-content: flex-start;    /* 세로 방향 중앙 */
  text-align: center;         /* 텍스트 정렬도 중앙 */
  height: 190px;

}
.custom-card:hover {
  transform: translateY(-10px);
}

.term-title {
  font-weight: 700;
  margin-bottom: 12px;
  font-size: 1.1rem;
}

.term-badge {
  display: inline-block;
  background-color: rgba(183, 255, 172, 0.45);
  font-size: 0.85rem;
  padding: 4px 10px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.term-definition {
  font-size: 0.95rem;
  color: #333;
  margin: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;   /* 최대 3줄만 보이게 */
  -webkit-box-orient: vertical;
}

.term-summary {
  font-size: 0.95rem;
  color: #333;
  margin: 0;
}
</style>