package org.scoula.jeonseRate.enums;

import java.util.*;
import java.util.stream.Collectors;

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
     * 사용자가 입력한 시/도명(rawName)을 KOSIS 기준으로 맞추기 (예: "서울특별시" → "서울")
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) return null;
        return rawName.replace("특별시", "")
                .replace("광역시", "")
                .replace("자치시", "")
                .replace("특별자치도", "")
                .replace("도", "")
                .trim();
    }

    /**
     * 시도명을 기반으로 enum 객체 찾기
     */
    public static Optional<KosisRegionCode> fromName(String name) {
        return Arrays.stream(values())
                .filter(r -> r.name.equals(name))
                .findFirst();
    }

    /**
     * 지역 코드(a7, b1 등)를 기반으로 enum 객체 찾기
     */
//    public static Optional<KosisRegionCode> fromCode(String code) {
//        return Arrays.stream(values())
//                .filter(r -> r.code.equals(code))
//                .findFirst();
//    }
}
