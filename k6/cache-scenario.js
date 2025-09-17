import {check, sleep} from 'k6';
import {getWithAuth, postWithAuth, ensureToken, BASE} from "./helpers.js";

// 테스트 옵션
export const options = {
    vus: 20,
    duration: '2m',
    thresholds: {
        'http_req_duration{name:reports_list}': ['p(95)<300'],
        'http_req_duration{name:reports_detail}': ['p(95)<300'],
        'http_req_duration{name:diagnosis_result}': ['p(95)<300'],
        'http_req_duration{name:safety_result}': ['p(95)<300'],
        'http_req_duration{name:contracts_list}': ['p(95)<300'],
        'http_req_duration{name:contracts_detail}': ['p(95)<300'],
        'http_req_duration{name:guide_checklist}': ['p(95)<300'],
        'http_req_duration{name:checklist_save}': ['p(95)<300'],
        http_req_duration: ['p(95)<500'],
        checks: ['rate>0.95'],
    },
};

const USER_ID = __ENV.USER_ID || 1;
const WARMUP_CREATE  = __ENV.WARMUP_CREATE === '1';
const WARMUP_LIMIT   = Number(__ENV.WARMUP_LIMIT || 5);

// 체크리스트 POST 빈도 제어(기본 20%) — 데이터 오염 방지용
const CHECKLIST_POST_RATE = Number(__ENV.CHECKLIST_POST_RATE || 0.2);

export function setup() {
    ensureToken();

    let reportIds = [];
    let registryIds = [];
    let contractIds = [];

    // 1) 리포트 리스트
    const rReports = getWithAuth(`${BASE}/api/reports/list?userId=${USER_ID}`, { tags: { name: 'reports_list' } });
    if (rReports.headers['Content-Type']?.includes('application/json') && rReports.status === 200) {
        try {
            const arr = JSON.parse(rReports.body);
            reportIds = (Array.isArray(arr) ? arr : (arr.items || [])).map(r => r.reportId || r.id).filter(Boolean);
        } catch (e) {
            console.error('리포트 리스트 파싱 실패:', e.message, String(rReports.body).slice(0, 200));
        }
    }

    // 2) 내 등기부등본 분석 목록
    const rRegs = getWithAuth(`${BASE}/api/registry/user/${USER_ID}`);
    if (rRegs.headers['Content-Type']?.includes('application/json') && rRegs.status === 200) {
        try {
            const arr   = JSON.parse(rRegs.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            registryIds = items.map(x => x.registryId || x.id).filter(Boolean);
        } catch (e) {
            console.error('레지스트리 파싱 실패:', e.message, String(rRegs.body).slice(0, 200));
        }
    }

    // 2-1) 선택적 워밍업 (리포트 자동 생성)
    // DB에 이미 있는 데이터만으로 테스트하고 싶다면, 아래 if 블록을 주석 처리해도 됨.
    if (WARMUP_CREATE && registryIds.length) {
        reportIds.push(...warmupReports(registryIds));
    }

    // 3) 계약서 리스트
    const rContracts = getWithAuth(`${BASE}/api/contracts/list`, { tags: { name: 'contracts_list' } });
    if (rContracts.headers['Content-Type']?.includes('application/json') && rContracts.status === 200) {
        try {
            const arr   = JSON.parse(rContracts.body);
            const items = Array.isArray(arr) ? arr : (arr.items || []);
            contractIds = items.map(c => c.contractId || c.id).filter(Boolean);
        } catch (e) {
            console.error('계약서 파싱 실패:', e.message, String(rContracts.body).slice(0, 200));
        }
    }

    return { reportIds, registryIds, contractIds };
}

// -------------------- warm-up helper --------------------
// warm-up: 쿼리 기반 단건 조회로 리포트 생성/확보
function warmupReports(registryIds) {
    const ids = [];
    const target = registryIds.slice(0, Math.min(WARMUP_LIMIT, registryIds.length));

    for (const rid of target) {
        const res = getWithAuth(`${BASE}/api/reports?userId=${USER_ID}&registryId=${rid}`, { tags: { name: 'reports_warmup' } });
        if (res.status === 200) {
            const dto = JSON.parse(res.body);
            const id  = dto.reportId || dto.id;
            if (id) ids.push(id);
        } else {
            console.error('warm-up 실패:', res.status, res.body);
        }
    }
    return ids;
}

// -------------------- 액션 정의 --------------------
function getReportsList() {
    const res = getWithAuth(`${BASE}/api/reports/list?userId=${USER_ID}`, { tags: { name: 'reports_list' } });
    check(res, { 'GET /api/reports/list 200': r => r.status === 200 });
}

function getReportDetail(data) {
    if (!data.reportIds.length) return;
    const id  = pick(data.reportIds);
    const res = getWithAuth(`${BASE}/api/reports/${id}`, { tags: { name: 'reports_detail' } });
    check(res, { 'GET /api/reports/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

function getDiagnosisResult(data) {
    if (!data.registryIds.length) return;
    const id  = pick(data.registryIds);
    const res = getWithAuth(`${BASE}/api/diagnosis/result?registerId=${id}`, { tags: { name: 'diagnosis_result' } });
    check(res, { 'GET /api/diagnosis/result 200|404|400': r => r.status === 200 || r.status === 404 || r.status === 400 });
}

function getSafetyResult(data) {
    if (!data.registryIds.length) return;
    const id  = pick(data.registryIds);
    const res = getWithAuth(`${BASE}/api/safety-check/${id}`, { tags: { name: 'safety_result' } });
    check(res, { 'GET /api/safety-check/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

function getContractsList() {
    const res = getWithAuth(`${BASE}/api/contracts/list`, { tags: { name: 'contracts_list' } });
    check(res, { 'GET /api/contracts/list 200': r => r.status === 200 });
}

function getContractDetail(data) {
    if (!data.contractIds.length) return;
    const id  = pick(data.contractIds);
    const res = getWithAuth(`${BASE}/api/contracts/${id}`, { tags: { name: 'contracts_detail' } });
    check(res, { 'GET /api/contracts/{id} 200|404': r => r.status === 200 || r.status === 404 });
}

function getGuideChecklist() {
    const res = getWithAuth(`${BASE}/api/guide/checklist`, { tags: { name: 'guide_checklist' } });
    check(res, { 'GET /api/guide/checklist 200': r => r.status === 200 });
}

// 체크리스트 저장 (랜덤 9개, 과도한 POST 방지 확률 제어)
function saveChecklist(data) {
    if (!data.registryIds.length) return;
    // 과도한 데이터 변경 방지: 일정 확률로만 POST
    if (Math.random() > CHECKLIST_POST_RATE) return;

    const id = pick(data.registryIds);
    const payload = {
        userId: USER_ID,
        registryId: id,
        checked: Array.from({ length: 9 }, () => Math.random() > 0.5),
    };

    const res = postWithAuth(`${BASE}/api/checklist`, payload, { tags: { name: 'checklist_save' } });
    check(res, { 'POST /api/checklist 200|201': r => r.status === 200 || r.status === 201 });
}

// 랜덤 선택 유틸
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)]; }

// -------------------- VU 실행 루프 --------------------
const actions = [
    getReportsList,
    getReportDetail,
    getDiagnosisResult,
    getSafetyResult,
    getContractsList,
    getContractDetail,
    getGuideChecklist,
    saveChecklist,
];

export default function (data) {
    ensureToken();
    const fn = actions[Math.floor(Math.random() * actions.length)];
    if ([getReportDetail, getDiagnosisResult, getSafetyResult, getContractDetail, saveChecklist].includes(fn)) fn(data);
    else fn();
    sleep(1);
}
