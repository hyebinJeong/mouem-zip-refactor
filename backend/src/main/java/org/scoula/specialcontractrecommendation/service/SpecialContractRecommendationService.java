package org.scoula.specialcontractrecommendation.service;

import org.scoula.specialcontractrecommendation.domain.SpecialContractRecommendationDTO;

import java.util.List;

public interface SpecialContractRecommendationService {
    List<SpecialContractRecommendationDTO> getAllRecommendations();
}
