package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionalRegistrationDTO implements DatedCanceledItem {
    private String rank;
    private String date;
    private String registeredRightHolder;
    private boolean canceled;
}
