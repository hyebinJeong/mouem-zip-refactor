package org.scoula.checklist.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.checklist.domain.dto.RegistryAnalysisDTO;

import java.util.List;

@Mapper
public interface RegistryAnalysisMapper {
    List<RegistryAnalysisDTO> selectRegistryByUserId(int userId);
}
