package org.scoula.jeonseRate.enums;

import java.util.*;

/**
 * KOSIS API에서 사용하는 지역(objL2) 코드 Enum
 * - 사용자 입력 주소의 시/도명을 기반으로 KOSIS 지역 코드를 매핑하기 위해 사용
 * - 예: 서울특별시 → "서울" → "a7"
 */
public enum KosisRegionCode {
    서울("서울", "a7"),
    인천("인천", "a9"),
    부산("부산", "b1"),
    대구("대구", "b2"),
    광주("광주", "b3"),
    대전("대전", "b4"),
    울산("울산", "b5"),
    세종("세종", "b6"),
    강원("강원", "c1"),
    충북("충북", "c2"),
    충남("충남", "c3"),
    전북("전북", "c4"),
    전남("전남", "c5"),
    경북("경북", "c6"),
    경남("경남", "c7"),
    제주("제주", "c8");

    private final String name;
    private final String code;

    KosisRegionCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }



    /**
     * 주소 API 결과에서 받은 시/도명(rawName)을 기반으로 KOSIS 지역 코드 반환
     * @param rawName 예: "서울특별시"
     * @return 예: Optional.of("a7")
     */
    public static Optional<String> findCodeFromRawRegionName(String rawName) {
        String normalized = normalizeName(rawName);
        return fromName(normalized).map(KosisRegionCode::getCode);
    }

    /**
     * KOSIS 통계 API에서 사용하는 지역 이름(예: "서울", "경기")과
     * 사용자 주소 또는 법정동 코드에서 추출한 행정구역명(예: "서울특별시")을 매핑하기 위해 사용
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) return null;
        return rawName
                .replace("서울특별시", "서울")
                .replace("부산광역시", "부산")
                .replace("대구광역시", "대구")
                .replace("인천광역시", "인천")
                .replace("광주광역시", "광주")
                .replace("대전광역시", "대전")
                .replace("울산광역시", "울산")
                .replace("세종특별자치시", "세종")
                .replace("경기도", "경기")
                .replace("강원도", "강원")
                .replace("충청북도", "충북")
                .replace("충청남도", "충남")
                .replace("전라북도", "전북")
                .replace("전라남도", "전남")
                .replace("경상북도", "경북")
                .replace("경상남도", "경남")
                .replace("제주특별자치도", "제주")
                .trim();
    }

    // 시도명을 기반으로 enum 객체 찾기
    public static Optional<KosisRegionCode> fromName(String name) {
        return Arrays.stream(values())
                .filter(r -> r.name.equals(name))
                .findFirst();
    }
}
