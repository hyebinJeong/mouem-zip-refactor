// 부하 테스트(RPS 기반)
// 목적: 동시 요청 한계 테스트
import {check} from 'k6';
import {BASE, fetchTokenOnce, getWithToken, postWithToken} from "../helpers/load-helpers2.js";

// 테스트 옵션
export const options = {
    scenarios: {
        load_test: {
            executor: 'ramping-arrival-rate',
            timeUnit: '1s',
            startRate: 10,        // 초당 10req부터 시작
            preAllocatedVUs: 20,  // 기본 VU 풀
            maxVUs: 200,          // 최대 VU 확장
            stages: [
                { target: 30, duration: '1m' },
                { target: 50, duration: '1m' },
                { target: 70, duration: '1m' },
                { target: 0,  duration: '30s' },
            ],
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],                 // 실패율 1% 미만
        http_req_duration: ['p(95)<500', 'p(99)<800'], // 전역 응답속도 기준

        checks: ['rate>0.95'],                         // 전체 check() 통과율
    },
};

const USER_ID = Number(__ENV.USER_ID || 1);

// 체크리스트 POST 빈도 제어(기본 20%) — 데이터 오염 방지용
const CHECKLIST_POST_RATE = Number(__ENV.CHECKLIST_POST_RATE || 0.01);

const DEBUG_LOG      = __ENV.DEBUG === '1'; // 실패 시 바디 로그 출력 토글

export function setup() {
    const token = fetchTokenOnce();

    let reportIds = [], registryIds = [], contractIds = [];

    let r = getWithToken(`${BASE}/api/reports/list?userId=${USER_ID}`, token, { tags: { name: 'reports_list' } });
    if (r.headers['Content-Type']?.includes('application/json') && r.status === 200) {
        try {
            const arr = JSON.parse(r.body);
            reportIds = (Array.isArray(arr) ? arr : (arr.items || [])).map(x => x.reportId).filter(Boolean);
        } catch (e) { console.error('리포트 리스트 파싱 실패:', e.message, String(r.body).slice(0,200)); }
    }

    r = getWithToken(`${BASE}/api/registry/user/${USER_ID}`, token);
    if (r.headers['Content-Type']?.includes('application/json') && r.status === 200) {
        try {
            const arr = JSON.parse(r.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            registryIds = items.map(x => x.registryId || x.id).filter(Boolean);
        } catch (e) { console.error('레지스트리 파싱 실패:', e.message, String(r.body).slice(0,200)); }
    }

    r = getWithToken(`${BASE}/api/contract/list`, token, { tags: { name: 'contract_list' } });
    if (r.headers['Content-Type']?.includes('application/json') && r.status === 200) {
        try {
            const arr = JSON.parse(r.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            contractIds = items.map(c => c.contractId || c.id).filter(Boolean);
        } catch (e) { console.error('계약서 파싱 실패:', e.message, String(r.body).slice(0,200)); }
    }

    return { token, reportIds, registryIds, contractIds };
}

// ---------- 401 시 1회 재발급하는 래퍼 ----------
function getT(url, data, params) {
    let res = getWithToken(url, data.token, params);
    if (res.status === 401) {
        data.token = fetchTokenOnce();
        res = getWithToken(url, data.token, params);
    }
    return res;
}
function postT(url, data, body, params) {
    let res = postWithToken(url, data.token, body, params);
    if (res.status === 401) {
        data.token = fetchTokenOnce();
        res = postWithToken(url, data.token, body, params);
    }
    return res;
}


// -------------------- 액션 정의 --------------------
function getReportsList(data) {
    const res = getT(`${BASE}/api/reports/list?userId=${USER_ID}`, data, { tags: { name: 'reports_list' } });
    check(res, { 'GET /api/reports/list 200': r => r.status === 200 });
}
function getReportDetail(data) {
    if (!data.reportIds.length) return;
    const id  = pick(data.reportIds);
    const res = getT(`${BASE}/api/reports/${id}`, data, { tags: { name: 'reports_detail' } });
    check(res, { 'GET /api/reports/{id} 200': r => r.status === 200 });
}
function getDiagnosisResult(data) {
    if (!data.registryIds.length) return;
    const id  = pick(data.registryIds);
    const res = getT(`${BASE}/api/diagnosis/result?registerId=${id}`, data, { tags: { name: 'diagnosis_result' } });
    check(res, { 'GET /api/diagnosis/result 200|404|400': r => [200,404,400].includes(r.status) });
}
function getSafetyResult(data) {
    if (!data.registryIds.length) return;
    const id  = pick(data.registryIds);
    const res = getT(`${BASE}/api/safety-check/${id}`, data, { tags: { name: 'safety_result' } });
    check(res, { 'GET /api/safety-check/{id} 200|404': r => [200,404].includes(r.status) });
}
function getContractList(data) {
    const res = getT(`${BASE}/api/contract/list`, data, { tags: { name: 'contract_list' } });
    check(res, { 'GET /api/contract/list 200': r => r.status === 200 });
}
function getContractDetail(data) {
    if (!data.contractIds.length) return;
    const id  = pick(data.contractIds);
    const res = getT(`${BASE}/api/contract/${id}`, data, { tags: { name: 'contract_detail' } });
    check(res, { 'GET /api/contract/{id} 200|404': r => [200,404].includes(r.status) });
}
function saveChecklist(data) {
    if (!data.registryIds?.length) return;
    if (Math.random() > CHECKLIST_POST_RATE) return;
    const id = pick(data.registryIds);
    const payload = {
        userId: USER_ID,
        registryId: id,
        checked: Array.from({ length: 9 }, () => Math.random() > 0.5),
    };
    const res = postT(`${BASE}/api/checklist`, data, payload, { tags: { name: 'checklist_save' } });
    check(res, { 'POST /api/checklist 200|201': r => {
            const ok = [200,201].includes(r.status);
            if (!ok && DEBUG_LOG) console.error('[CHECKLIST_FAIL]', 'id=', id, 'status=', res.status, String(res.body).slice(0,200));
            return ok;
        }});
}

// ========== 가중치 기반 액션 선택 ==========
const weighted = [
    { fn: getReportsList,     w: 22 },
    { fn: getReportDetail,    w: 22 },
    { fn: getDiagnosisResult, w: 18 },
    { fn: getSafetyResult,    w: 18 },
    { fn: getContractList,    w: 12 },
    { fn: getContractDetail,  w: 8  },
    { fn: saveChecklist,      w: 0.5 },
];
const totalW = weighted.reduce((s, a) => s + a.w, 0);
function pickAction() {
    let r = Math.random() * totalW;
    for (const a of weighted) { r -= a.w; if (r <= 0) return a.fn; }
    return weighted[0].fn;
}

// ========== 실행 루프 ==========
export default function (data) {
    const fn = pickAction();
    fn(data);
}

// ========== 결과 파일 저장(전/후 비교·회귀 추적용) ==========
export function handleSummary(summary) {
    return { 'summary-load.json': JSON.stringify(summary, null, 2) };
}

// ==========랜덤 선택 유틸==========
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)]; }