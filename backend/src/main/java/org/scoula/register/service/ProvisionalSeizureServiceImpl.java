package org.scoula.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.register.domain.dto.ProvisionalSeizureDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProvisionalSeizureServiceImpl implements ProvisionalSeizureService {
    // 등기 데이터 중 가압류 정보만 추출
    @Override
    public List<ProvisionalSeizureDTO> extractProvisionalSeizureInfos(List<List<String>> tableData) {
        List<ProvisionalSeizureDTO> provisionalSeizures = new ArrayList<>();
        List<String> canceledRanks = new ArrayList<>();

        // 말소된 등기 찾기
        for (List<String> row : tableData) {
            if (row.size() < 2) continue;

            String registrationPurpose = normalizeText(row.get(1));

            if (registrationPurpose != null && registrationPurpose.contains("가압류") && registrationPurpose.contains("말소")) {
//                System.out.println("읽어온 문장: " + registrationPurpose);
                String canceledRank = extractCanceledRank(registrationPurpose).trim();  // 말소된 번호
                if (canceledRank != null) {
                    canceledRanks.add(canceledRank);
                }
            }
        }

        // 본 등기에서 말소된 순번은 제외하고 처리
        for (List<String> row : tableData) {
            if (row.size() < 5) continue;

            String rank = row.get(0).trim();    // 순위번호
            String registrationPurpose = row.get(1);    // 등기목적

            if (registrationPurpose != null &&
                    registrationPurpose.contains("가압류") &&
                    !registrationPurpose.contains("말소") &&
                    !canceledRanks.contains(rank)) {

                String registrationCause = trimAfterParenthesis(row.get(3));  // 등기 원인
                String etc = row.get(4);                // 권리자 및 기타사항

                String maxClaimAmountRaw = extractAfterKeyword(etc, "청구금액");
                String maxClaimAmount = extractNumberOnly(maxClaimAmountRaw);
                String creditor = extractCreditor(etc);

                ProvisionalSeizureDTO info = new ProvisionalSeizureDTO();
                info.setRank(rank);
                info.setRegistrationPurpose(registrationPurpose);
                info.setRegistrationCause(registrationCause);
                info.setMaxClaimAmount(maxClaimAmount);
                info.setCreditor(creditor);

                provisionalSeizures.add(info);
//                System.out.println("현재 등기 순위: '" + rank + "'");
//                System.out.println("말소 목록: " + canceledRanks);
//                System.out.println("필터링 통과 여부: " + !canceledRanks.contains(rank));
                System.out.println("청구금액: " + maxClaimAmount + " 채권자: " + creditor);
            }
        }

        return provisionalSeizures;
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

    private String extractCreditor(String text) {
        if (text == null || !text.contains("채권자")) return null;

        String after = text.substring(text.indexOf("채권자") + 4).trim();

        // 주민/사업자번호 패턴 (ex. 403004-1345223 or 200111-0503114)
        String[] splitById = after.split("\\d{6}-\\d{7}");
        if (splitById.length > 0) {
            return splitById[0].trim();
        }

        // 주소 시작 패턴이 나오는 경우로 자를 수도 있음 (선택)
        String[] addressTriggers = {"시", "도", "구", "동", "로", "길"};
        for (String trigger : addressTriggers) {
            int idx = after.indexOf(trigger);
            if (idx != -1 && idx > 0) {
                String part = after.substring(0, idx + 1); // 시까지 포함
                if (part.length() < 30) return part.trim(); // 주소 말고 이름일 경우만
            }
        }

        return after;
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
