package org.scoula.termmanager.dto;

import lombok.Data;

@Data
public class TermmanagerDTO {
    private Integer termId;
    private Integer categoryId;
    private String termName;
    private String termDefine;
    private String termExample;
    private String termCaution;
}
