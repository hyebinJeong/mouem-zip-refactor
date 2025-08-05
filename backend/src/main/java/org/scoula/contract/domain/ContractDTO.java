package org.scoula.contract.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date leaseStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date leaseEnd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // ✅ 특약 JSON 배열
    private List<String> specialClauses;
}
