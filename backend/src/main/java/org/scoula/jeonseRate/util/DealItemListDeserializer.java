package org.scoula.jeonseRate.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoula.jeonseRate.dto.DealDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * // 공공데이터 API의 거래 정보(item)가
 * ""(빈값), 단일 객체, 배열인 경우 모두 처리하는 커스텀 리스트 역직렬화기
 */
public class DealItemListDeserializer extends JsonDeserializer<List<DealDTO>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<DealDTO> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();

        // 1. "" → 거래 없음 → 빈 리스트 반환
        if (token == JsonToken.VALUE_STRING && "".equals(p.getText())) {
            return Collections.emptyList();
        }
        // 2. 단일 객체 → DealDTO 하나를 리스트로 감싸 반환
        else if (token == JsonToken.START_OBJECT) {
            DealDTO single = mapper.readValue(p, DealDTO.class);
            return List.of(single); // 단일 객체 → 리스트로 변환
        }
        // 3. 배열 → DealDTO 리스트로 역직렬화
        else if (token == JsonToken.START_ARRAY) {
            return mapper.readValue(p, mapper.getTypeFactory().constructCollectionType(List.class, DealDTO.class));
        }
        // 그 외 예외 케이스 → 빈 리스트 반환
        return Collections.emptyList();
    }
}
