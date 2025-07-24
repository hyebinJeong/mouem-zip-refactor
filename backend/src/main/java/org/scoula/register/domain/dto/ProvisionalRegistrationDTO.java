package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionalRegistrationDTO {
    private String rank;
    private String registrationPurposes;
    private String registrationCause;
    private String RegisteredRightHolder;
}
