package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrustDTO {
    private String rank;
    private String date;
    private String trustee;
    private boolean canceled;
}
