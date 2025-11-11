// k6/cache-helpers1.js
import http from 'k6/http';

export const BASE = 'http://localhost:8080';

let accessToken = null;

export function refreshToken() {
    const res = http.get(`${BASE}/api/auth/test-token`);
    if (res.status === 200) {
        accessToken = JSON.parse(res.body).accessToken;
        console.log('새 토큰 발급:', accessToken);
    } else {
        console.error('토큰 발급 실패:', res.status, res.body);
    }
}

export function ensureToken() {
    if (!accessToken) refreshToken();
}

function withAuthParams(params = {}) {
    const headers = {
        Accept: 'application/json',
        Authorization: `Bearer ${accessToken}`,
        ...(params.headers || {}),
    };
    return { ...params, headers };
}

export function requestWithAuth(method, url, body = null, params = {}) {
    ensureToken();
    let p = withAuthParams(params);
    let res = send(method, url, body, p);
    if (res.status === 401) {
        refreshToken();
        p = withAuthParams(params);
        res = send(method, url, body, p);
    }
    return res;
}

// 래퍼들: 이제 두 번째 인자는 k6 params 전체(tags, headers 등)
export const getWithAuth  = (url, params = {}) => requestWithAuth('GET', url, null, params);
export const delWithAuth  = (url, params = {}) => requestWithAuth('DELETE', url, null, params);
export const postWithAuth = (url, json, params = {}) =>
    requestWithAuth('POST', url, json ? JSON.stringify(json) : null, { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) }});
export const putWithAuth  = (url, json, params = {}) =>
    requestWithAuth('PUT',  url, json ? JSON.stringify(json) : null, { ...params, headers: { 'Content-Type': 'application/json', ...(params.headers||{}) }});

function send(method, url, body, params) {
    if (method === 'GET')    return http.get(url, params);
    if (method === 'DELETE') return http.del(url, null, params);
    if (method === 'POST')   return http.post(url, body, params);
    if (method === 'PUT')    return http.put(url, body, params);
    throw new Error(`Unsupported method: ${method}`);
}
