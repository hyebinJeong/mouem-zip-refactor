package org.scoula.specialcontractrecommendation.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.specialcontractrecommendation.domain.SpecialContractRecommendationDTO;
import org.scoula.specialcontractrecommendation.service.SpecialContractRecommendationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class SpecialContractRecommendationController {

    private final SpecialContractRecommendationService recommendationService;

    @GetMapping
    public List<SpecialContractRecommendationDTO> getAllRecommendations() {
        return recommendationService.getAllRecommendations();
    }
}
