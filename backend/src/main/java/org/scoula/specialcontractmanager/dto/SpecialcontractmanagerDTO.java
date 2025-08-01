package org.scoula.specialcontractmanager.dto;

public class SpecialcontractmanagerDTO {
    private Integer specialClauseId;
    private String category;
    private String importance;
    private String description;

    // 중요도 색상은 서버에서 자동 처리할 예정이라 DTO엔 안 넣음

    public Integer getSpecialClauseId() {
        return specialClauseId;
    }
    public void setSpecialClauseId(Integer specialClauseId) {
        this.specialClauseId = specialClauseId;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getImportance() {
        return importance;
    }
    public void setImportance(String importance) {
        this.importance = importance;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
