package org.scoula.term.domain.dto;
import lombok.Data;
@Data
public class TermDTO {
    private Integer termId;
    private String termName;
    private String termDefine;
    private String termExample;
    private String termCaution;

    // 조인된 데이터
    private String categoryName;
    private String categoryColor;
    // getter, setter
}