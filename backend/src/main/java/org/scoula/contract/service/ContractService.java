package org.scoula.contract.service;

import lombok.RequiredArgsConstructor;
import org.scoula.contract.domain.ContractDTO;
import org.scoula.contract.mapper.ContractMapper;
import org.scoula.specialclause.domain.SpecialClauseDTO;
import org.scoula.specialclause.mapper.SpecialClauseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractMapper contractMapper;
    private final SpecialClauseMapper specialClauseMapper;

    @Transactional
    public void saveContractWithClauses(ContractDTO contractDTO) {
        // 1. 계약서 저장
        contractMapper.insertContract(contractDTO);

        int contractId = contractDTO.getContractId();

        // 2. 기존 특약 연결
        if (contractDTO.getSpecialClauseIds() != null && !contractDTO.getSpecialClauseIds().isEmpty()) {
            specialClauseMapper.insertContractSpecialClauses(contractId, contractDTO.getSpecialClauseIds());
        }

        // 3. 새 입력 특약 추가 후 연결
        if (contractDTO.getSpecialClauseTexts() != null && !contractDTO.getSpecialClauseTexts().isEmpty()) {
            for (String text : contractDTO.getSpecialClauseTexts()) {
                if (text == null || text.trim().isEmpty()) continue;

                // 새 특약 INSERT
                SpecialClauseDTO newClause = new SpecialClauseDTO();
                newClause.setCategory("기타");
                newClause.setImportance("중");
                newClause.setImportanceColor("#000000");
                newClause.setDescription(text);

                specialClauseMapper.insertSpecialClause(newClause);

                // 계약서-특약 연결
                specialClauseMapper.insertContractSpecialClauses(contractId,
                        java.util.List.of(newClause.getSpecialClauseId()));
            }
        }
    }
}
