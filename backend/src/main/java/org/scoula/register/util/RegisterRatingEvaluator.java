package org.scoula.register.util;

import org.scoula.register.domain.RegistryRating;
import org.scoula.register.domain.dto.DatedCanceledItem;
import org.scoula.register.domain.dto.RegisterAnalysisResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegisterRatingEvaluator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년M월d일");

    public static RegistryRating evaluateRiskLevel(RegisterAnalysisResponse response) {
        LocalDate now = LocalDate.now();

        boolean hasNotCanceled = false;
        boolean hasCanceledWithin1Year = false;
        boolean hasCanceledWithin2Years = false;

        // 각 리스트에 대해 처리
        for (List<? extends DatedCanceledItem> infoList : getAllInfoLists(response)) {
            for (DatedCanceledItem item : infoList) {
                String dateStr = item.getDate();

                if (dateStr == null || dateStr.isBlank()) {
                    // 날짜 정보 없으면 건너뛰기
                    continue;
                }

                LocalDate date = LocalDate.parse(item.getDate(), formatter);

                if (!item.isCanceled()) {
                    hasNotCanceled = true;
                } else {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(date, now);
                    if (days <= 365) {
                        hasCanceledWithin1Year = true;
                    } else if (days <= 730) {
                        hasCanceledWithin2Years = true;
                    }
                }
            }
        }

        if (hasNotCanceled) return RegistryRating.위험;
        if (hasCanceledWithin1Year) return RegistryRating.주의;
        if (hasCanceledWithin2Years) return RegistryRating.보통;
        return RegistryRating.안전;
    }

    private static List<List<? extends DatedCanceledItem>> getAllInfoLists(RegisterAnalysisResponse response) {
        return List.of(
                response.getMortgageInfos(),
                response.getSeizureInfos(),
                response.getProvisionalSeizureInfos(),
                response.getAuctionInfos(),
                response.getProvisionalRegistrationInfos(),
                response.getInjunctionInfos(),
                response.getJeonseRightInfos(),
                response.getTrustInfos()
        );
    }
}

