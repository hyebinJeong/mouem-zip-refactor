<!-- src/components/JeonseTrendChart.vue -->
<template>
  <!-- 🔹 compact 모드일 땐 패딩/폭 줄이기 -->
  <div :class="['wrap', { compact }]">
    <!-- 제목 문구 변경 -->
    <!-- 주소/동 + 아파트 입력 영역 -->
    <!-- 🔹 compact 모드이거나 hideAddressSearch=true이면 숨김 -->
    <div v-if="!hideAddressSearch && !compact">
      <!-- 주소/동 선택 -->
      <div class="row">
        <label>주소</label>
        <div class="address">
          <input
              class="inp addr-inp"
              v-model="selectedAddr"
              placeholder="   도로명/지번"
              readonly
          />
          <button class="btn" @click="openPostcode">주소 검색</button>
        </div>
        <small v-if="!lawd" class="tip">먼저 주소를 선택하세요.</small>
        <small v-if="bname || bcode" class="tip">
          동: {{ bname }} / 법정동코드: {{ bcode }}
        </small>
      </div>

      <!-- 아파트명 선택(옵션) -->
      <div class="row">
        <label>아파트명 (선택)</label>
        <input
            class="inp apt-inp"
            v-model="aptName"
            placeholder=" 예: 은마, 래미안…"
        />
      </div>
    </div>

    <!-- 🔹 차트 유형 선택 (compact에서도 계속 보이도록) -->
    <div class="row controls">
      <div class="chart-type">
        <span class="tip">차트 유형:</span>
        <button
            class="tab"
            :class="{ active: chartType === 'line' }"
            @click="setChartType('line')"
        >
          꺾은선
        </button>
        <button
            class="tab"
            :class="{ active: chartType === 'bar' }"
            @click="setChartType('bar')"
        >
          막대
        </button>
      </div>
    </div>

    <!-- 로딩 / 에러 / 데이터 없음 상태 -->
    <!-- 로딩은 모드 상관없이 공통으로 보여주기 -->
    <p v-if="loading" class="tip">전세가율 데이터를 불러오는 중이에요…</p>

    <!-- 에러는 compact에선 안 보여줘도 됨(필요하면 조건 빼도 됨) -->
    <p v-else-if="!compact && error" class="err">{{ error }}</p>

    <!-- 데이터 없음: 일반 모드에서만 문구 표시 -->
    <p
        v-else-if="!compact && queried && !error && !items.length"
        class="tip"
    >
      데이터가 없습니다.
    </p>

    <!-- 일반 모드에서만 요약 문구 -->
    <div v-if="!compact && items.length" class="tip">
      포인트: <b>{{ items.length }}</b>개 /
      평균 전세가율: <b>{{ avgRatio.toFixed(1) }}%</b>
    </div>

    <!-- 차트 -->
    <div v-if="items.length" :class="['chartWrap', { compact }]">
      <canvas ref="canvasEl"></canvas>
    </div>

    <!-- 표 (compact에서는 숨김) -->
    <div v-if="!compact && items.length" class="overflow">
      <table class="tbl">
        <thead>
        <tr>
          <th>월</th>
          <th class="r">전세가율(%)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(r, i) in items" :key="i">
          <td>{{ r.ymd }}</td>
          <td class="r">
            {{ r.ratio != null ? r.ratio.toFixed(2) : '-' }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Chart } from 'chart.js/auto'

// ✅ 부모(페이지3 등)에서 넘겨줄 값들
const props = defineProps({
  initialAddr: { type: String, default: '' },
  initialBcode: { type: String, default: '' },
  initialBname: { type: String, default: '' },
  autoFetch: { type: Boolean, default: false }, // true면 자동 조회
  hideAddressSearch: { type: Boolean, default: false }, // true면 주소/아파트 입력 UI 숨김
  // 🔹 FinalReport 같은 “내부 삽입용” 모드
  compact: { type: Boolean, default: false }
})

// ✅ 백엔드 기본 URL
const API_BASE = import.meta.env.VITE_BACKEND ?? 'http://localhost:8080'

// 상태
const selectedAddr = ref(props.initialAddr || '')
const bname = ref(props.initialBname || '')
const bcode = ref(props.initialBcode || '')
const aptName = ref('')

const chartType = ref('line') // 'line' 또는 'bar'
const items = ref([]) // { ymd: 'YYYY-MM', ratio: number }[]
const loading = ref(false)
const error = ref('')
const queried = ref(false)

// 법정동 코드 앞 5자리 -> LAWD_CD
const lawd = computed(() => (bcode.value ? bcode.value.slice(0, 5) : ''))

// 평균 전세가율
const avgRatio = computed(() => {
  const arr = items.value.map(v => v.ratio).filter(v => v != null)
  if (!arr.length) return 0
  return arr.reduce((a, b) => a + b, 0) / arr.length
})

// 차트
const canvasEl = ref(null)
let chartInstance = null

function setChartType(t) {
  chartType.value = t
  if (items.value.length) {
    drawChart(items.value)
  }
}

// 카카오 주소 검색
function openPostcode() {
  if (!window.daum?.Postcode) {
    alert('주소 검색 스크립트를 불러오는 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  new window.daum.Postcode({
    oncomplete(data) {
      selectedAddr.value =
          data.userSelectedType === 'R'
              ? data.roadAddress
              : data.jibunAddress
      bname.value = data.bname || ''
      bcode.value = data.bcode || ''
    }
  }).open()
}

// 조회 (autoFetch용 + 주소 변경 후 수동으로도 호출 가능)
async function onQuery() {
  if (!lawd.value) return
  error.value = ''
  items.value = []
  queried.value = true
  loading.value = true
  destroyChart()

  try {
    const qs = new URLSearchParams({
      lawd: lawd.value,
      umd: bname.value || '',
      apt: aptName.value || ''
    }).toString()

    const res = await fetch(`${API_BASE}/api/jeonse/ratio/recent?${qs}`, {
      headers: {
        Accept: 'application/json'
      }
    })

    const text = await res.text()
    try {
      const json = JSON.parse(text)

      if (!res.ok) {
        throw new Error(json?.message || '조회 실패')
      }

      const rows = json.rows || []
      items.value = rows.map(r => ({
        ymd: r.ymd, // "YYYY-MM"
        ratio: r.ratio // %
      }))

      if (items.value.length) {
        drawChart(items.value)
      }
    } catch (e) {
      console.error('서버 응답:', text)
      throw new Error('서버가 JSON이 아닌 응답을 보냈어요.')
    }
  } catch (e) {
    error.value = e?.message || '요청 실패'
  } finally {
    loading.value = false
  }
}

function drawChart(list) {
  if (!canvasEl.value) return
  const ctx = canvasEl.value.getContext('2d')
  if (chartInstance) chartInstance.destroy()

  const labels = list.map(v => v.ymd)
  const data = list.map(v => v.ratio)

  chartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [
        {
          label: '전세가율(%)',
          data,
          fill: false,
          tension: 0.25,
          pointRadius: 3
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: { beginAtZero: true, title: { display: true, text: '(%)' } },
        x: { ticks: { autoSkip: false, maxRotation: 0 } }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: ctx =>
                ctx.parsed.y != null ? ` ${ctx.parsed.y}%` : ' 데이터 없음'
          }
        }
      }
    }
  })
}

function destroyChart() {
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }
}

// 카카오 우편번호 스크립트 로드 + autoFetch
onMounted(() => {
  // 주소 검색 UI를 사용하는 경우에만 카카오 우편번호 스크립트 로드
  if (!props.hideAddressSearch && !props.compact && !window.daum?.Postcode) {
    const s = document.createElement('script')
    s.src =
        'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    document.body.appendChild(s)
  }

  // initial 값 + autoFetch=true이면 자동 조회
  if (props.autoFetch && lawd.value) {
    onQuery()
  }
})
</script>

<style scoped>
.wrap {
  max-width: 900px;
  margin: 0px auto;
  padding: 0px 20px;
  text-align: center;
}

/* 🔹 FinalReport에 끼워 넣을 때 더 컴팩트하게 */
.wrap.compact {
  max-width: 100%;
  margin: 8px 0;
  padding: 0;
  text-align: left;
}

.ttl {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 40px;
}

/* compact에서는 제목/여백만 살짝 줄이기 */
.wrap.compact .ttl {
  font-size: 16px;
  margin-bottom: 2px;
}

/* 한 줄(주소, 아파트명, 버튼들) 공통 */
.row {
  margin-bottom: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.row label {
  width: 100%;
  max-width: 600px;
  font-weight: 550;
  text-align: left;
  margin-bottom: 8px;
}

/* 주소 입력 + 버튼 */
.address {
  display: flex;
  gap: 8px;
  width: 100%;
  justify-content: center;
}

/* 주소 입력칸 */
.addr-inp {
  width: 100%;
  max-width: 500px;
  height: 55px;
  border: 1px solid #d1dbe8;
  border-radius: 10px;
  box-sizing: border-box;
}

/* 아파트 입력칸 */
.apt-inp {
  width: 100%;
  max-width: 600px;
  height: 55px;
  border: 1px solid #d1dbe8;
  border-radius: 10px;
  box-sizing: border-box;
}

/* 공통 버튼 */
.btn {
  padding: 10px 14px;
  border: 0;
  background: #64748b;
  color: #fff;
  border-radius: 10px;
  cursor: pointer;
}

.btn:hover {
  background: rgba(100, 116, 139, 0.82);
}

/* 안내 텍스트 / 에러 */
.tip {
  color: #6b7280;
  font-size: 13px;
  margin-top: 15px;
}
.err {
  color: #d00;
  font-size: 14px;
}

/* 차트 유형 토글 컨테이너 */
.controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

/* 차트 유형 토글 */
.chart-type {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
  margin-top: 14px;
}

/* 탭 버튼 (꺾은선 / 막대) */
.tab {
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #d1dbe8;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  margin-top: 15px;
}
.tab.active {
  background: #1a80e5;
  color: #fff;
  border-color: #1a80e5;
}

/* 차트 영역 */
.chartWrap {
  width: 100%;
  max-width: 900px;
  height: 420px;
  margin: 16px auto;
}

/* 🔹 FinalReport에 들어갈 때는 높이/여백 줄이기 */
.chartWrap.compact {
  max-width: 100%;
  height: 240px;
  margin: 8px 0 0 0;
}

/* 표 */
.tbl {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
  font-size: 14px;
}
.tbl th,
.tbl td {
  border-top: 1px solid #eee;
  padding: 8px;
}
.tbl th {
  background: #f8fafc;
  text-align: left;
}
.tbl .r {
  text-align: right;
}
.overflow {
  overflow: auto;
}
</style>