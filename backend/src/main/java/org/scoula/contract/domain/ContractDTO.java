package org.scoula.contract.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContractDTO {
    private int contractId;
    private int userId;
    private String contractName;
    private String lessorName;
    private String lesseeName;
    private String address;
    private String landCategory;
    private double landArea;
    private String buildingUsage;
    private double buildingArea;
    private String leasedPart;
    private double leasedArea;
    private long deposit;
    private long downPayment;
    private long balance;
    private long maintenanceCost;
    private Date leaseStart;
    private Date leaseEnd;

    // 특약 저장용
    private List<Integer> specialClauseIds;   // 기존 특약 선택
    private List<String> specialClauseTexts;  // 새로 입력한 특약
}
