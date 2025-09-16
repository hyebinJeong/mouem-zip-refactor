// k6/smoke.js
import { check, sleep } from 'k6';
import { BASE, ensureToken, getWithAuth } from './helpers.js';

export const options = {
    vus: 5,
    duration: '1m', // 짧게 확인용
};

export default function () {
    ensureToken();

    // 보호된 API 호출 예시 (/api/user/me)
    // 👉 필요 시 아래 URL만 바꿔서 다른 엔드포인트 확인
    const res = getWithAuth(`${BASE}/api/user/me`);

    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);
}
