package org.scoula.jeonseRate.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.dto.AddressInfoDTO;
import org.scoula.jeonseRate.dto.LeaseRequestDTO;
import org.scoula.jeonseRate.service.AddressService;
import org.scoula.jeonseRate.service.DealSearchService;
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


    @PostMapping("/leasePer")
    public ResponseEntity<?> analyzeLease(@RequestBody LeaseRequestDTO request) {
        System.out.println(request);
        String keyword = request.getAddress();

        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().body("❌ 주소가 없습니다.");
        }

        // 카카오 API를 통해 주소 정보 조회
        AddressInfoDTO addressInfo = addressService.lookupAddress(keyword);

        // 최근 6개월의 'yyyyMM' 형식 문자열 목록
        List<String> recentMonths = getRecentMonths(6);

        // 법정동코드 앞 5자리만 사용 (LAWD_CD)
        String lawdCode = addressInfo.getAdmCd().substring(0, 5);

        // 지번 주소 앞번호 추출 (예: 595-28 → 595)
        String jibunHead  = addressInfo.getJibunAddr().split("-")[0];

        System.out.println("[요청] 주소: " + request.getAddress() + ", 보증금: " + request.getJeonsePrice());

        // 해당 주소에 대한 매매가 평균 조회
        Optional<Integer> averageDealPriceOpt = dealSearchService.getAverageDealAmount(
                lawdCode, jibunHead , recentMonths
        );

        // 유사 매물이 없을 경우 메시지 반환
        if (averageDealPriceOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "status", "NO_MATCHING_DEAL",
                    "message", "해당 주소에 대한 유사 매물 정보를 찾을 수 없습니다."
            ));
        }

        // 전세가율 계산 = (전세보증금 / 평균매매가) * 100
        double averageDealPrice = averageDealPriceOpt.get();            // 평균 매매가 (단위: 만원)
        double jeonse = Double.parseDouble(request.getJeonsePrice());   // 입력 전세 보증금
        double jeonseRate = (jeonse / averageDealPrice) * 100;          // 전세가율
        int roundedJeonseRate = (int) Math.round(jeonseRate);           // 전세가율 반올림하여 정수 처리

        // 응답 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.put("admCd", addressInfo.getAdmCd());            // 법정동 코드
        result.put("jibunAddr", addressInfo.getJibunAddr());    // 지번 주소
        result.put("jeonsePrice", jeonse);                      // 입력 전세 보증금
        result.put("expectedSalePrice", (int) averageDealPrice);// 예상 매매가
        result.put("jeonseRate", roundedJeonseRate);            // 전세가율 (%)

        return ResponseEntity.ok(result);
    }

    /**
     * 최근 N개월에 해당하는 "yyyyMM" 형식의 문자열 목록 반환
     */
    private List<String> getRecentMonths(int count) {
        List<String> months = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 0; i < count; i++) {
            LocalDate target = now.minusMonths(i);
            months.add(String.format("%d%02d", target.getYear(), target.getMonthValue()));
        }
        return months;
    }
}
