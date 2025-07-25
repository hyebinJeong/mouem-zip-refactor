package org.scoula.register.service;

import org.scoula.register.domain.dto.MortgageDTO;

import java.util.List;

public interface MortgageService {
    List<MortgageDTO> extractMortgageInfos(List<List<String>> tableData);
}
