package org.scoula.checklist.service;

import org.scoula.checklist.domain.dto.RegistryAnalysisDTO;
import org.scoula.checklist.mapper.RegistryAnalysisMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistryAnalysisServiceImpl implements RegistryAnalysisService {

    private final RegistryAnalysisMapper mapper;

    public RegistryAnalysisServiceImpl(RegistryAnalysisMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<RegistryAnalysisDTO> getRegistryByUserId(int userId) {
        return mapper.selectRegistryByUserId(userId);
    }
}
