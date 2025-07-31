package org.scoula.specialclause.domain;

import lombok.Data;

@Data
public class SpecialClauseDTO {
    private int specialClauseId;
    private String category;
    private String importance;
    private String importanceColor;
    private String description;
}
