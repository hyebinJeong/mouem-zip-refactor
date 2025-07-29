package org.scoula.checklist.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChecklistDTO {
    private int userId;
    private int registryId;
    private List<Boolean> checked; // 9개의 체크 여부
}
