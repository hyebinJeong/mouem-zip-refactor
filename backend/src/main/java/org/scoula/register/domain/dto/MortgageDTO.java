package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MortgageDTO {
    private String rank;
    private String date;
    private String maxClaimAmount;
    private String mortgageHolder;
    private boolean canceled;
}
