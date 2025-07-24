package org.scoula.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.register.domain.dto.MortgageDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MortgageServiceImpl implements MortgageService {

    // 등기 데이터 중 근저당권 정보만 추출
    @Override
    public List<MortgageDTO> extractMortgageInfos(List<List<String>> tableData) {
        List<MortgageDTO> mortgages = new ArrayList<>();
        List<String> canceledRanks = new ArrayList<>();
        List<List<String>> mergedTable = mergeRowsWithCanceled(tableData);
        // 말소된 등기 찾기
        for (List<String> row : mergedTable) {
            if (row.size() < 2) continue;

            String registrationPurpose = normalizeText(row.get(1));

            if (registrationPurpose != null && registrationPurpose.contains("근저당권") && registrationPurpose.contains("말소")) {
                System.out.println("읽어온 문장: " + registrationPurpose);
                String canceledRank = extractCanceledRank(registrationPurpose).trim();  // 말소된 번호
                if (canceledRank != null) {
                    canceledRanks.add(canceledRank);
                }
            }
        }

        // 본 등기에서 말소된 순번은 제외하고 처리
        for (List<String> row : mergedTable) {
            if (row.size() < 5) continue;

            String rank = row.get(0).trim();    // 순위번호
            String registrationPurpose = row.get(1);    // 등기목적

            if (registrationPurpose != null &&
                    registrationPurpose.contains("근저당권") &&
                    !registrationPurpose.contains("말소") &&
                    !canceledRanks.contains(rank)) {

                String registrationCause = row.get(3);  // 등기 원인
                String etc = row.get(4);                // 권리자 및 기타사항

                String maxClaimAmountRaw = extractAfterKeyword(etc, "채권최고액");
                String maxClaimAmount = extractNumberOnly(maxClaimAmountRaw);
                String mortgageHolder = extractAfterKeyword(etc, "근저당권자");

                MortgageDTO info = new MortgageDTO();
                info.setRank(rank);
                info.setRegistrationPurpose(registrationPurpose);
                info.setRegistrationCause(registrationCause);
                info.setMaxClaimAmount(maxClaimAmount);
                info.setMortgageHolder(mortgageHolder);


                boolean isCanceled = registrationPurpose.contains("말소");
                info.setCanceled(isCanceled);

                mortgages.add(info);
                System.out.println(info);
//                System.out.println("현재 등기 순위: '" + rank + "'");
//                System.out.println("말소 목록: " + canceledRanks);
//                System.out.println("필터링 통과 여부: " + !canceledRanks.contains(rank));
            }
        }

        return mortgages;
    }

    // 다음 장으로 넘어간 셀 정보 합치기
    public List<List<String>> mergeRowsWithCanceled(List<List<String>> tableData) {
        List<List<String>> result = new ArrayList<>();
        // 표의 헤더인지 확인
        List<String> headerRow = List.of("순위번호", "등 기 목 적", "접 수", "등 기 원 인", "권리자 및 기타사항");

        for (int i = 0; i < tableData.size(); i++) {
            List<String> currentRow = tableData.get(i);
            // 정확히 헤더와 일치하거나, 헤더 키워드가 대부분 포함된 행은 무시
            long headerCount = headerRow.stream().filter(currentRow::contains).count();
            boolean isHeaderLike = headerCount >= 3;

            if (isHeaderLike) {
                continue; // 헤더 행은 무시
            }

            boolean isNewRow = currentRow.get(0) != null
                    && !currentRow.get(0).isBlank()
                    && !currentRow.get(0).equals("순위번호");

            // 순번이 있는 경우: 일반 등기 행으로 간주
            if (isNewRow) {
                // 복사해서 추가
                result.add(new ArrayList<>(currentRow));
            } else {
                // 순번이 없는 경우: 기말소/해지 등기일 가능성
                if (!result.isEmpty()) {
                    List<String> lastRow = result.get(result.size() - 1);
                    for (int j = 0; j < currentRow.size(); j++) {
                        String val = currentRow.get(j).trim();
                        if (!val.isEmpty()) {
                            String origin = lastRow.get(j);
                            lastRow.set(j, origin + val);
                        }
                    }
//                    System.out.println("합쳐진 행 (" + (result.size() - 1) + "): " + lastRow);
                }
            }
        }

        return result;
    }

    private String normalizeText(String text) {
        if (text == null) return null;
        return text.replaceAll("\\s+", ""); // 모든 공백 제거
    }

    // 말소 번호 추출
    private String extractCanceledRank(String text) {
        if (text == null) return null;
        int idx = text.indexOf("번");
        if (idx == -1) return null;
        return text.substring(0, idx).trim();
    }

    // 텍스트에서 특정 키워드 뒤에 오는 정보를 추출
    private String extractAfterKeyword(String text, String keyword) {
        if (text == null) return null;

        int idx = text.indexOf(keyword);
        if (idx == -1) return null;

        int start = idx + keyword.length();
        String after = text.substring(start).trim();

        // 공백, 쉼표, 줄바꿈 등을 기준으로 끊기
        int end = after.indexOf(" ");
        if (end == -1) end = after.length();

        return after.substring(0, end);
    }
    
    // 금액 부분 숫자만 남김
    private String extractNumberOnly(String text) {
        if (text == null) return null;
        // 숫자와 쉼표만 남기고 모두 제거
        String cleaned = text.replaceAll("[^0-9,]", "");
        // 쉼표 제거
        cleaned = cleaned.replaceAll(",", "");
        return cleaned;
    }
}
