package org.scoula.checklist.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RegistryAnalysisDTO {
    private int registryId;

    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date analysisDate;

    public int getRegistryId() {
        return registryId;
    }

    public void setRegistryId(int registryId) {
        this.registryId = registryId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(Date analysisDate) {
        this.analysisDate = analysisDate;
    }
}
