package org.scoula.finalreport.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChecklistJsonWrapper {
    private List<Boolean> checked;
}
