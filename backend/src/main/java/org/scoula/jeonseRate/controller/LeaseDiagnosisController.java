package org.scoula.jeonseRate.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.domain.JeonseAnalysisVO;
import org.scoula.jeonseRate.dto.AddressInfoDTO;
import org.scoula.jeonseRate.dto.JeonseRateDTO;
import org.scoula.jeonseRate.enums.KosisRegionCode;
import org.scoula.jeonseRate.enums.KosisRegionDistrictCode;
import org.scoula.jeonseRate.enums.SafetyGrade;
import org.scoula.jeonseRate.mapper.JeonseAnalysisMapper;
import org.scoula.jeonseRate.service.AddressService;
import org.scoula.jeonseRate.service.DealSearchService;
import org.scoula.jeonseRate.service.KosisJeonseRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class LeaseDiagnosisController {

    private final AddressService addressService;
    private final DealSearchService dealSearchService;
    private final KosisJeonseRateService kosisJeonseRateService;
    private final JeonseAnalysisMapper jeonseAnalysisMapper;


    @PostMapping("/leasePer")
    public ResponseEntity<?> analyzeLease(@RequestBody JeonseRateDTO request) {
        String keyword = request.getAddress();
        int registerId = request.getRegisterId();

        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().body("주소가 없습니다.");
        }

        // 1. 주소 정보 조회
        AddressInfoDTO addressInfo = addressService.lookupAddress(keyword);

        // 2. 최근 6개월 (yyyyMM) 목록 생성
        List<String> recentMonths = getRecentMonths(6);

        // 3. 법정동코드 앞 5자리
        String lawdCode = addressInfo.getAdmCd().substring(0, 5);

        // 4. 지번 주소 (예: 595-28)
        String jibun  = addressInfo.getJibunAddr();

        // 5. 건물명
        String buildingName = addressInfo.getBdNm();

        // 6. 해당 주소에 대한 매매가 평균 조회
        Optional<JeonseRateDTO> averageDealPriceOpt = dealSearchService.getDealAmount(
                lawdCode, jibun, buildingName, recentMonths
        );

        // 조회된 매물이 없을 경우 메시지 반환
        if (averageDealPriceOpt.isEmpty()) {
            SafetyGrade grade = SafetyGrade.판단보류;
            JeonseAnalysisVO vo = new JeonseAnalysisVO();
            vo.setRegistryId(registerId);
            vo.setUserId(request.getUserId());
            vo.setExpectedSellingPrice(-1);
            vo.setDeposit(request.getJeonsePrice());
            vo.setJeonseRatio(-1);
            vo.setRegionAvgJeonseRatio(-1);
            vo.setJeonseRatioRating(grade);
            jeonseAnalysisMapper.insertJeonseAnalysis(vo);
            return ResponseEntity.ok(Map.of(
                    "status", "NO_MATCHING_DEAL",
                    "message", "해당 주소에 대한 유사 매물 정보를 찾을 수 없습니다.",
                    "grade", grade.name(),
                    "jeonsePrice", request.getJeonsePrice(),
                    "expectedSalePrice", -1,
                    "jeonseRate", -1,
                    "avgKosisRate", -1,
                    "deviation", -1,
                    "admCd", addressInfo.getAdmCd(),
                    "jibunAddr", addressInfo.getJibunAddr()
            ));
        }

        // 7. 전세가율 계산
        int averageDealPrice = averageDealPriceOpt.get().getAreaAVGPrice();            // 평균 매매가 (단위: 만원)
        int jeonse = request.getJeonsePrice();                          // 입력 전세 보증금
        double jeonseRate = ((double) jeonse / averageDealPrice) * 100; // 전세가율
        int roundedJeonseRate = (int) Math.round(jeonseRate);           // 전세가율 반올림하여 정수 처리

        // 8. KOSIS에서 시/구 기준 전세가율 조회
        String objL2 = KosisRegionDistrictCode.findBySiDoAndSgg(addressInfo.getSiNm(), addressInfo.getSggNm())
                .map(code -> code.getCode()) // 구 단위 지역 코드가 있으면 사용
                .orElseGet(() ->
                        KosisRegionCode.findCodeFromRawRegionName(addressInfo.getSiNm())
                                .orElse(null) // 없으면 시도 단위 코드 사용
                );

        if (objL2 == null) {
            return ResponseEntity.badRequest().body("해당 지역의 KOSIS 지역 코드를 찾을 수 없습니다.");
        }

        // 9. KOSIS에서 해당 지역 평균 전세가율 조회
        List<Map<String, Object>> kosisData = kosisJeonseRateService.fetchKosisData(averageDealPriceOpt, objL2);

        double avgKosisRate = kosisData.stream()
                .map(m -> Double.parseDouble(m.get("DT").toString()))
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        // 10. 편차 계산 및 등급 판별
        int deviation = roundedJeonseRate - (int) Math.round(avgKosisRate);
        SafetyGrade grade = SafetyGrade.fromDeviation(kosisData.isEmpty() ? null : deviation);

        //11. DB 저장
        JeonseAnalysisVO vo = new JeonseAnalysisVO();
        vo.setRegistryId(registerId);                 // 등기부등본 번호
        vo.setUserId(request.getUserId());            // 유저 번호
        vo.setExpectedSellingPrice(averageDealPrice); // 평균 매매가
        vo.setDeposit(jeonse);                        // 사용자가 입력한 전세보증금
        vo.setJeonseRatio(roundedJeonseRate);         // 전세가율
        vo.setRegionAvgJeonseRatio((int) Math.round(avgKosisRate)); // 지역 평균 전세가율
        vo.setJeonseRatioRating(grade); // 등급

        jeonseAnalysisMapper.insertJeonseAnalysis(vo);

        return ResponseEntity.noContent().build();
    }

    //최근 N개월에 해당하는 "yyyyMM" 형식의 문자열 목록 반환
    private List<String> getRecentMonths(int count) {
        List<String> months = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 0; i < count; i++) {
            LocalDate target = now.minusMonths(i);
            months.add(String.format("%d%02d", target.getYear(), target.getMonthValue()));
        }
        return months;
    }

    // 예상 전세가율 조회
    @GetMapping("/result")
    public ResponseEntity<?> getJeonseAnalysisResult(@RequestParam("registerId") int registerId) {
        Integer ratio  = jeonseAnalysisMapper.findJeonseRatioByRegisterId(registerId);

        if (ratio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("전세가율 정보가 없습니다.");
        }

        return ResponseEntity.ok(Map.of("jeonseRate", ratio));
    }
}
