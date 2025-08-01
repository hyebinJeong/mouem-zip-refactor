package org.scoula.specialcontractmanager.domain;

public class SpecialcontractmanagerVO {
    private Integer specialClauseId; // special_clause_id
    private String category;
    private String importance;
    private String importanceColor;
    private String description;

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
    public String getImportanceColor() {
        return importanceColor;
    }
    public void setImportanceColor(String importanceColor) {
        this.importanceColor = importanceColor;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
