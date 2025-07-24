package org.scoula.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.register.domain.dto.InjunctionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class InjunctionServiceImpl implements InjunctionService {
    @Override
    public List<InjunctionDTO> extractInjunctions(List<List<String>> tableData) {
        List<InjunctionDTO> injunctions = new ArrayList<>();
        List<String> canceledRanks = new ArrayList<>();
        List<List<String>> mergedTable = mergeRowsWithCanceled(tableData);

        // 말소된 등기 찾기
        for (List<String> row : mergedTable) {
            if (row.size() < 2) continue;

            String registrationPurpose = normalizeText(row.get(1));

            if (registrationPurpose != null && registrationPurpose.contains("가처분") && registrationPurpose.contains("말소")) {
//                System.out.println("읽어온 문장: " + registrationPurpose);
                List<String> canceled = extractCanceledRanks(registrationPurpose);
                canceledRanks.addAll(canceled);
            }
        }

        // 본 등기에서 말소된 순번은 제외하고 처리
        for (List<String> row : mergedTable) {
            if (row.size() < 5) continue;

            String rank = row.get(0).trim();    // 순위번호
            String registrationPurpose = normalizeText(row.get(1));    // 등기목적

            if (registrationPurpose != null &&
                    registrationPurpose.contains("가처분") &&
                    !registrationPurpose.contains("말소") &&
                    !canceledRanks.contains(rank)) {

                String registrationCause = trimAfterParenthesis(row.get(3));  // 등기 원인
                String etc = row.get(4);                // 권리자 및 기타사항

                String creditor = extractCreditorOrRightHolder(etc);

                InjunctionDTO info = new InjunctionDTO();
                info.setRank(rank);
                info.setRegistrationPurpose(registrationPurpose);
                info.setRegistrationCause(registrationCause);
                info.setCreditor(creditor);

                injunctions.add(info);
                System.out.println(info);
//                System.out.println("현재 등기 순위: '" + rank + "'");
//                System.out.println("말소 목록: " + canceledRanks);
//                System.out.println("필터링 통과 여부: " + !canceledRanks.contains(rank));
//                System.out.println("청구금액: " + maxClaimAmount + " 채권자: " + creditor);
            }
        }

        return injunctions;
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

    private List<String> extractCanceledRanks(String registrationPurpose) {
        List<String> ranks = new ArrayList<>();

        // "5-1번", "5-2번", "7번" 등 패턴 찾기
        Pattern pattern = Pattern.compile("(\\d+(?:-\\d+)?)[ ]?번");
        Matcher matcher = pattern.matcher(registrationPurpose);

        while (matcher.find()) {
            ranks.add(matcher.group(1).trim()); // "5-1", "5-2" 등
        }

        return ranks;
    }

    // 괄호 앞 자르기
    private String trimAfterParenthesis(String text) {
        if (text == null) return null;
        int idx = text.indexOf('(');
        return (idx != -1) ? text.substring(0, idx).trim() : text.trim();
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

    private String extractCreditorOrRightHolder(String text) {
        if (text == null || !(text.contains("채권자") || text.contains("권리자"))) {
            return null;
        }

        // 채권자/권리자 중 먼저 등장하는 쪽을 찾기
        int creditorIdx = text.indexOf("채권자");
        int holderIdx = text.indexOf("권리자");

        int targetIdx;
        String keyword;
        if (creditorIdx != -1 && (holderIdx == -1 || creditorIdx < holderIdx)) {
            targetIdx = creditorIdx;
            keyword = "채권자";
        } else {
            targetIdx = holderIdx;
            keyword = "권리자";
        }

        String after = text.substring(targetIdx + keyword.length()).trim();

        // 사업자번호 또는 주민번호 기준으로 자르기 (이게 이름 뒤에 바로 나옴)
        Pattern pattern = Pattern.compile("^(.*?)\\d{3}-\\d{2}-\\d{5}");
        Matcher matcher = pattern.matcher(after);
        if (matcher.find()) {
            return matcher.group(1).trim(); // 이름 부분만 반환
        }

        // 위 패턴이 없을 경우: 띄어쓰기 기준 첫 단어만 (예외 처리)
        String[] tokens = after.split("\\s+");
        if (tokens.length > 0) {
            return tokens[0].trim();
        }

        return after;
    }
}
