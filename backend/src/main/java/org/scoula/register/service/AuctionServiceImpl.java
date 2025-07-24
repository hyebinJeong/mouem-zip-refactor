package org.scoula.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.register.domain.dto.AuctionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    // 등기 데이터 중 경매 관련 정보만 추출
    @Override
    public List<AuctionDTO> extractAuctionInfos(List<List<String>> tableData) {
        List<AuctionDTO> auctions = new ArrayList<>();

        for (List<String> row : tableData) {
            if (row.size() < 5) continue;

            String rank = row.get(0).trim();    // 순위번호
            String registrationPurpose = row.get(1);    // 등기목적

            if (registrationPurpose != null &&
                    registrationPurpose.contains("경매")) {

                String registrationCause = trimAfterParenthesis(row.get(3));  // 등기 원인
                String etc = row.get(4);                // 권리자 및 기타사항
                
                String creditor = extractCreditor(etc);

                AuctionDTO info = new AuctionDTO();
                info.setRank(rank);
                info.setRegistrationPurpose(registrationPurpose);
                info.setRegistrationCause(registrationCause);
                info.setCreditor(creditor);

                auctions.add(info);
            }
        }

        return auctions;
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
        String[] splitById = after.split("\\d{6}-[\\d*]{7}");
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
}
