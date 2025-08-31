package org.scoula.jeonseRate.service.kosis;

import org.scoula.jeonseRate.dto.JeonseRateDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KosisJeonseRateService {

    /**
     * KOSIS에서 전세가율 데이터를 조회
     * @param averageDealPriceOpt 매물 유형(아파트/오피스텔/연립)을 담은 DTO Optional
     * @param objL2 지역코드(시/구)
     * @return KOSIS 응답 리스트
     */
    List<Map<String, Object>> fetchKosisData(Optional<JeonseRateDTO> averageDealPriceOpt, String objL2);
}
