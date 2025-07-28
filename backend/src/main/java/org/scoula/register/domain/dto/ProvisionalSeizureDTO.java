package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionalSeizureDTO {
    private String rank;
    private String date;
    private String maxClaimAmount;
    private String creditor;
    private boolean canceled;
}