package org.scoula.specialcontractrecommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.specialcontractrecommendation.domain.SpecialContractRecommendationDTO;

import java.util.List;

@Mapper
public interface SpecialContractRecommendationMapper {
    List<SpecialContractRecommendationDTO> getAllSpecialClauses();
}
