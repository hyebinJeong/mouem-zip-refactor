package org.scoula.service;

import org.scoula.domain.dto.MortgageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MortgageInfoService {

    // 등기 데이터 중 근저당권 정보만 추출
    public List<MortgageInfo> extractMortgageInfos(List<List<String>> tableData) {
        List<MortgageInfo> mortgageInfos = new ArrayList<>();

        for (List<String> row : tableData) {
            if (row.size() < 5) continue;

            String registrationPurpose = row.get(1);
            if (registrationPurpose != null && registrationPurpose.contains("근저당권")) {
                // "말소" 정보는 제외 (예: '기말소', '말소됨' 등)
                if (registrationPurpose.contains("말소")) continue;

                String rank = row.get(0);
                String registrationCause = row.get(3);
                String etc = row.get(4);

                String maxClaimAmountRaw = extractAfterKeyword(etc, "채권최고액");
                String maxClaimAmount = extractNumberOnly(maxClaimAmountRaw);
                String mortgageHolder = extractAfterKeyword(etc, "근저당권자");

                MortgageInfo info = new MortgageInfo();
                info.setRank(rank);
                info.setRegistrationPurpose(registrationPurpose);
                info.setRegistrationCause(registrationCause);
                info.setMaxClaimAmount(maxClaimAmount);
                info.setMortgageHolder(mortgageHolder);

                mortgageInfos.add(info);
            }
        }

        return mortgageInfos;
    }

    // 텍스트에서 특정 키워드 뒤에 오는 정보를 추출하는 유틸
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

    private String extractNumberOnly(String text) {
        if (text == null) return null;
        // 숫자와 쉼표만 남기고 모두 제거
        String cleaned = text.replaceAll("[^0-9,]", "");
        // 쉼표 제거
        cleaned = cleaned.replaceAll(",", "");
        return cleaned;
    }

    private int indexOrLength(String str, String delimiter) {
        int idx = str.indexOf(delimiter);
        return idx == -1 ? str.length() : idx;
    }
}
