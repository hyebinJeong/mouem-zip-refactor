package org.scoula.register.service;

import org.scoula.register.domain.dto.ProvisionalSeizureDTO;

import java.util.List;

public interface ProvisionalSeizureService {
    // 등기 데이터 중 가압류 정보만 추출
    List<ProvisionalSeizureDTO> extractProvisionalSeizureInfos(List<List<String>> tableData);
}
