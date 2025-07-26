package org.scoula.jeonseRate.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.scoula.jeonseRate.util.DealItemListDeserializer;

import java.util.List;

/**
 * 국토부 실거래가 API의 전체 응답(JSON)을 매핑하는 DTO 클래스
 * 구조: response → body → items → item(List<DealDto>)
 */
@Getter
@Setter
public class DealResponseDTO {
    // 최상위 응답 객체 (JSON의 "response" 필드에 해당)
    private Response response;

    /**
     * 내부 클래스: 응답 객체의 "response" 구조를 매핑
     */
    @Getter @Setter
    public static class Response {
        private Body body;
    }

    /**
     * 내부 클래스: "body" 필드의 구조를 매핑
     */
    @Getter @Setter
    public static class Body {
        private Items items;
    }

    /**
     * 내부 클래스: "items" 필드의 구조를 매핑
     */
    @Getter @Setter
    public static class Items {
        /**
         * "item" 필드는 실제 거래 리스트 (List<DealDto>)에 해당
         * - 경우에 따라 문자열("")로 응답되기도 하므로
         * - 커스텀 Deserializer로 빈 문자열 대응
         */
        @JsonDeserialize(using = DealItemListDeserializer.class)
        private List<DealDTO> item;
    }
}
