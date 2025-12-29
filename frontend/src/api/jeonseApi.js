// 프록시(Express)에서 만든 엔드포인트 사용: http://localhost:5174/api/apt-rent
// params: { lawd, ym, umd?, apt?, road? }
// return: { count, rows: [...] }
export async function fetchJeonse({ lawd, ym, umd = "", apt = "", road = "" }) {
    if (!lawd || !ym) throw new Error("lawd(법정동5자리), ym(YYYYMM)은 필수입니다.");

    const qs = new URLSearchParams({
        lawd: String(lawd),
        ym: String(ym),
        onlyJeonse: "1", // 전세만
        umd,
        apt,
        road,
        rows: "1000",
    }).toString();

    const res = await fetch(`http://localhost:5174/api/apt-rent?${qs}`);
    const json = await res.json();
    if (!res.ok) throw new Error(json?.error || "실거래가 조회 실패");
    return json; // { count, rows }
}