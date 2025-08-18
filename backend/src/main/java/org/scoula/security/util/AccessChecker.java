// src/main/java/org/scoula/security/util/AccessChecker.java
package org.scoula.security.util;

import lombok.RequiredArgsConstructor;
import org.scoula.contract.service.ContractService;
import org.scoula.finalreport.dto.FinalReportSummaryDTO;
import org.scoula.finalreport.service.FinalReportService;
import org.scoula.contract.domain.ContractDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("accessChecker")
@RequiredArgsConstructor
public class AccessChecker {

    private final ContractService contractService;
    private final FinalReportService finalReportService;


    public boolean canViewContract(Long contractId, Authentication authentication) {
        Long uid = currentUserId(authentication);
        if (uid == null) return false;

        ContractDTO c = contractService.getContractById(contractId.intValue());
        // ContractDTO.userId 는 int 이므로 uid.intValue() 로 비교
        return c != null && c.getUserId() == uid.intValue();
    }

    public boolean canViewFinalReport(Long reportId, Authentication authentication) {
        Long uid = currentUserId(authentication);
        if (uid == null) return false;

        List<FinalReportSummaryDTO> list = finalReportService.getReportListByUserId(uid);
        if (list == null) return false;

        for (FinalReportSummaryDTO s : list) {
            if (s != null && s.getReportId() != null &&
                    s.getReportId().longValue() == reportId.longValue()) {
                return true;
            }
        }
        return false;
    }


    public boolean isSelf(Long userId, Authentication authentication) {
        Long uid = currentUserId(authentication);
        return uid != null && uid.longValue() == userId.longValue();
    }


    private Long currentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try {
            return Long.valueOf(auth.getName());
        } catch (Exception e) {
            return null;
        }
    }
}
