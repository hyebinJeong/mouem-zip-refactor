package org.scoula.checklist.service;

import org.scoula.checklist.domain.dto.RegistryAnalysisDTO;

import java.util.List;

public interface RegistryAnalysisService {
    List<RegistryAnalysisDTO> getRegistryByUserId(int userId);
}
