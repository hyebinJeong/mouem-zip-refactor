// org/scoula/jeonse/service/JeonseServiceImpl.java
package org.scoula.jeonse.service;

import lombok.RequiredArgsConstructor;
import org.scoula.jeonse.domain.AptRentRow;
import org.scoula.jeonse.domain.AptRentResponse;
import org.scoula.jeonse.dto.AptRentDto;
import org.scoula.jeonse.dto.JeonseRatioDto;
import org.scoula.jeonse.infra.JeonseOpenApiClient;
import org.scoula.jeonse.mapper.AptRentMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class JeonseServiceImpl implements JeonseService {

    private final JeonseOpenApiClient client;

    /**
     * 전월세(아파트) 실거래 조회
     * - 한 번에 넉넉히 가져오되, pageSize / maxPages 조절 가능
     */
    @Override
    public List<AptRentDto> getAptRent(String lawd, String yyyymm, String umdNullable) {
        final int pageSize = 1000;
        final int maxPages = 5;

        List<AptRentRow> all = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            AptRentResponse res = client.callAptRent(lawd, yyyymm, page, pageSize);

            // 요약 로그
            System.out.println("=== Jeonse API Response ===");
            System.out.println("pageNo     = " + page);
            System.out.println("resultCode = " + (res.getHeader() != null ? res.getHeader().getResultCode() : "null"));
            System.out.println("resultMsg  = " + (res.getHeader() != null ? res.getHeader().getResultMsg() : "null"));
            System.out.println("totalCount = " + (res.getBody() != null ? res.getBody().getTotalCount() : "null"));

            List<AptRentRow> rows = res.rows();
            System.out.println("fetchedRows = " + (rows == null ? 0 : rows.size()));

            if (rows == null || rows.isEmpty()) break;
            all.addAll(rows);

            Integer total = (res.getBody() != null) ? res.getBody().getTotalCount() : null;
            if (total != null && all.size() >= total) break;
            if (rows.size() < pageSize) break;
        }

        System.out.println("rowsBeforeFilter(total) = " + all.size());

        // umd 필터 (null/blank이면 통과)
        if (umdNullable != null && !umdNullable.isBlank()) {
            all = all.stream()
                    .filter(r -> umdNullable.equals(r.getUmdNm()))
                    .collect(Collectors.toList());
        }

        System.out.println("rowsAfterFilter = " + all.size());

        return AptRentMapper.toDtoList(all);
    }

    /**
     * 특정 년월(yyyymm)에 대한 전세가율 리스트
     * - DB 없이: 전월세 데이터를 이용해서 "매매가를 추정" 후 전세가율 계산
     *   -> 아파트/동/면적별로 전세가율이 전부 똑같이 나오지 않도록 처리
     */
    @Override
    public List<JeonseRatioDto> getJeonseRatio(String lawd, String yyyymm, String umdNullable, String aptNullable) {

        // 1) 해당 년월 전월세 데이터 가져오기
        List<AptRentDto> rentList = getAptRent(lawd, yyyymm, umdNullable);

        // 2) 아파트명 필터 (입력한 아파트만 보고 싶을 때)
        if (aptNullable != null && !aptNullable.isBlank()) {
            String kw = aptNullable.trim();
            rentList = rentList.stream()
                    .filter(r -> r.getAptNm() != null && r.getAptNm().contains(kw))
                    .collect(Collectors.toList());
        }

        // 3) 각 계약마다 "매매가 추정 + 전세가율 계산"
        List<JeonseRatioDto> result = new ArrayList<>();
        for (AptRentDto r : rentList) {
            Long deposit = parseLongSafe(r.getDeposit());
            Long salePrice = estimateSalePrice(r.getDeposit(), r.getMonthlyRent());
            Double ratio = calcRatio(deposit, salePrice);

            String ymd = buildYmd(r.getDealYear(), r.getDealMonth(), r.getDealDay());

            JeonseRatioDto dto = JeonseRatioDto.builder()
                    .aptNm(r.getAptNm())
                    .umdNm(r.getUmdNm())
                    .area(r.getExcluUseAr() == null ? null : r.getExcluUseAr().doubleValue())
                    .deposit(deposit)
                    .salePrice(salePrice)
                    .ratio(ratio)
                    .ymd(ymd)
                    .build();

            result.add(dto);
        }

        return result;
    }

    /**
     * 최근 4년(대략 48개월) 중에서 8개 시점만 골라서
     * 각 월의 "평균 전세가율" 을 만들어 주는 메서드
     */
    @Override
    public List<JeonseRatioDto> getJeonseRatioRecent(String lawd, String umdNullable, String aptNullable) {
        // 오늘 기준
        LocalDate now = LocalDate.now();

        // 최근 48개월 → 8개 포인트로 샘플링 (6개월 간격)
        // ex) 0, 6, 12, 18, 24, 30, 36, 42 개월 전
        List<String> targetYmList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            LocalDate d = now.minusMonths(6L * i);
            String ym = d.format(DateTimeFormatter.ofPattern("yyyyMM"));
            targetYmList.add(ym);
        }

        Collections.reverse(targetYmList); // 오래된 것 → 최근 순으로

        List<JeonseRatioDto> out = new ArrayList<>();

        for (String ym : targetYmList) {
            // 이 월의 모든 계약 전세가율 상세
            List<JeonseRatioDto> detail = getJeonseRatio(lawd, ym, umdNullable, aptNullable);

            if (detail.isEmpty()) continue;

            // 이 월의 "유효한(널 아닌)" 전세가율 평균
            double avg = detail.stream()
                    .map(JeonseRatioDto::getRatio)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(Double.NaN);

            if (Double.isNaN(avg)) continue;

            // 대표 포인트 하나만 만들어서 반환 리스트에 추가
            JeonseRatioDto point = JeonseRatioDto.builder()
                    .aptNm(aptNullable)   // 하나의 아파트만 보고 있다면 이름 넣어도 되고, 아니면 null
                    .umdNm(umdNullable)
                    .ratio(Math.round(avg * 100.0) / 100.0) // 소수 둘째 자리
                    .ymd(formatYm(ym))   // "202110" → "2021-10"
                    .build();

            out.add(point);
        }

        return out;
    }

    /**
     * 특정 연도 전체(1~12월)를 돌면서 "월평균 전세가율" 계산
     * sample이 있으면 그 개수만 균등샘플링 (예: 6이면 2달 간격 등)
     */
    @Override
    public List<JeonseRatioDto> getJeonseRatioYear(String lawd, int year, String umdNullable, String aptNullable, Integer sample) {
        List<String> allYm = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String mm = (m < 10 ? "0" : "") + m;
            allYm.add(year + mm); // "202110"
        }

        List<String> target;
        if (sample == null || sample <= 0 || sample >= allYm.size()) {
            target = allYm;
        } else {
            // 12개월 중에서 sample 개수만 간격 두고 선택
            target = new ArrayList<>();
            double step = (double) (allYm.size() - 1) / (double) (sample - 1);
            for (int i = 0; i < sample; i++) {
                int idx = (int) Math.round(i * step);
                target.add(allYm.get(idx));
            }
        }

        List<JeonseRatioDto> out = new ArrayList<>();

        for (String ym : target) {
            List<JeonseRatioDto> detail = getJeonseRatio(lawd, ym, umdNullable, aptNullable);
            if (detail.isEmpty()) continue;

            double avg = detail.stream()
                    .map(JeonseRatioDto::getRatio)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(Double.NaN);

            if (Double.isNaN(avg)) continue;

            JeonseRatioDto point = JeonseRatioDto.builder()
                    .aptNm(aptNullable)
                    .umdNm(umdNullable)
                    .ratio(Math.round(avg * 100.0) / 100.0)
                    .ymd(formatYm(ym))  // "2021-01" 형식
                    .build();

            out.add(point);
        }

        return out;
    }

    // ==========================
    //        헬퍼 메서드들
    // ==========================

    /**
     * "90,000" → 90000L 로 파싱 (콤마 제거)
     */
    private Long parseLongSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Long.parseLong(s.replace(",", "").trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 전세가율(%) = 전세금 / 매매가 × 100
     */
    private Double calcRatio(Long jeonsePrice, Long salePrice) {
        if (jeonsePrice == null || salePrice == null || salePrice <= 0) return null;
        double r = (jeonsePrice.doubleValue() / salePrice.doubleValue()) * 100.0;
        return Math.round(r * 100.0) / 100.0;
    }

    /**
     * "매매가 추정" 로직 (임시 규칙)
     * - 순수 전세: 매매가 ≈ 보증금 / 0.6 (전세가율 60% 정도로 가정)
     * - 반전세/월세: 매매가 ≈ 보증금 + 월세 × 100
     *   (100은 임의 상수라 나중에 조정 가능)
     */
    private Long estimateSalePrice(String depositStr, String monthlyRentStr) {
        Long deposit = parseLongSafe(depositStr);
        Long monthlyRent = parseLongSafe(monthlyRentStr);

        if (deposit == null) return null;

        if (monthlyRent == null || monthlyRent == 0L) {
            return Math.round(deposit / 0.6); // 전세가율 60% 가정
        }

        return deposit + monthlyRent * 100L;
    }

    /**
     * 연/월/일 → "YYYY-MM-DD"
     */
    private String buildYmd(Integer y, Integer m, Integer d) {
        if (y == null || m == null || d == null) return null;
        String mm = (m < 10 ? "0" : "") + m;
        String dd = (d < 10 ? "0" : "") + d;
        return y + "-" + mm + "-" + dd;
    }

    /**
     * "202110" → "2021-10"
     */
    private String formatYm(String yyyymm) {
        if (yyyymm == null || yyyymm.length() != 6) return yyyymm;
        String y = yyyymm.substring(0, 4);
        String m = yyyymm.substring(4, 6);
        return y + "-" + m;
    }
}