package org.scoula.jeonse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptTradeRow {
    @JacksonXmlProperty(localName = "aptNm")       private String aptNm;
    @JacksonXmlProperty(localName = "excluUseAr")  private Double excluUseAr;

    // 스키마가 dealAmount(영문) 또는 거래금액(한글)로 올 수 있어 둘 다 매핑
    @JacksonXmlProperty(localName = "dealAmount")  private String dealAmount;
    @JacksonXmlProperty(localName = "거래금액")       private String dealAmountKor;

    public String rawPrice() {
        return dealAmount != null ? dealAmount : dealAmountKor;
    }
}