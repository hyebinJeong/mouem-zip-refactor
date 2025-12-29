package org.scoula.jeonse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptRentRow {
    @JacksonXmlProperty(localName = "aptNm")       private String aptNm;
    @JacksonXmlProperty(localName = "buildYear")   private Integer buildYear;
    @JacksonXmlProperty(localName = "dealYear")    private Integer dealYear;
    @JacksonXmlProperty(localName = "dealMonth")   private Integer dealMonth;
    @JacksonXmlProperty(localName = "dealDay")     private Integer dealDay;
    @JacksonXmlProperty(localName = "deposit")     private String deposit;     // "90,000"
    @JacksonXmlProperty(localName = "monthlyRent") private String monthlyRent; // 필요시
    @JacksonXmlProperty(localName = "excluUseAr")  private Double excluUseAr;
    @JacksonXmlProperty(localName = "floor")       private Integer floor;
    @JacksonXmlProperty(localName = "jibun")       private String jibun;
    @JacksonXmlProperty(localName = "sggCd")       private String sggCd;
    @JacksonXmlProperty(localName = "umdNm")       private String umdNm;
}