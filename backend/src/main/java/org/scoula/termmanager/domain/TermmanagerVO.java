package org.scoula.termmanager.domain;

import lombok.Data;

@Data
public class TermmanagerVO {
    private Integer termId;
    private Integer categoryId;
    private String termName;
    private String termDefine;
    private String termExample;
    private String termCaution;

    private String categoryName;  // 조회용
    private String categoryColor; // 조회용
}
