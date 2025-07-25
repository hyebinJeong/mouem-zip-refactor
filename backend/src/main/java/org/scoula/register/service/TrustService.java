package org.scoula.register.service;

import org.scoula.register.domain.dto.TrustDTO;

import java.util.List;

public interface TrustService {
    List<TrustDTO> extractTrustInfos(List<List<String>> tableData);
}
