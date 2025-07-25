package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JeonseRightDTO {
    private String rank;
    private String registrationPurpose;
    private String registrationCause;
    private String deposit;
    private String mortgagor;
}
