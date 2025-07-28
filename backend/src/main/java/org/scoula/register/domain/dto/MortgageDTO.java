package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MortgageDTO implements DatedCanceledItem {
    private String rank;
    private String date;
    private String maxClaimAmount;
    private String mortgageHolder;
    private boolean canceled;
}
