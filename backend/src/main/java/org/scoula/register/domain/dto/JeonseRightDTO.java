package org.scoula.register.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JeonseRightDTO implements DatedCanceledItem {
    private String rank;
    private String date;
    private String deposit;
    private String mortgagor;
    private boolean canceled;
}
