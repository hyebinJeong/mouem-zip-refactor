package org.scoula.specialcontractrecommendation.service;

import lombok.RequiredArgsConstructor;
import org.scoula.specialcontractrecommendation.domain.SpecialContractRecommendationDTO;
import org.scoula.specialcontractrecommendation.mapper.SpecialContractRecommendationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialContractRecommendationServiceImpl implements SpecialContractRecommendationService {

    private final SpecialContractRecommendationMapper recommendationMapper;

    @Override
    public List<SpecialContractRecommendationDTO> getAllRecommendations() {
        return recommendationMapper.getAllSpecialClauses();
    }
}
