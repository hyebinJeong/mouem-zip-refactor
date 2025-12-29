package org.scoula.jeonse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptRentResponse {

    @JacksonXmlProperty(localName = "header")
    private ResponseHeader header;

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Data
    public static class ResponseHeader {
        @JacksonXmlProperty(localName = "resultCode") private String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")  private String resultMsg;
    }

    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "items") private Items items;
        @JacksonXmlProperty(localName = "totalCount") private Integer totalCount;
        @JacksonXmlProperty(localName = "pageNo") private Integer pageNo;
        @JacksonXmlProperty(localName = "numOfRows") private Integer numOfRows;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<AptRentRow> item;
    }

    public List<AptRentRow> rows() {
        if (body == null || body.getItems() == null || body.getItems().getItem() == null) return List.of();
        return body.getItems().getItem();
    }
}