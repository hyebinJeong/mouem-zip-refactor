package org.scoula.register.service;

import org.scoula.register.domain.dto.SeizureDTO;

import java.util.List;

public interface SeizureService {
    // 등기 데이터 중 압류 정보만 추출
    List<SeizureDTO> extractSeizureInfos(List<List<String>> tableData);
}
