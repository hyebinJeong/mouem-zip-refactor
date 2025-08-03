package org.scoula.contract.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.contract.domain.ContractDTO;

import java.util.List;

@Mapper
public interface ContractMapper {
    void insertContract(ContractDTO contract);
    List<ContractDTO> selectContractsByUserId(int userId);
    ContractDTO selectContractById(int contractId);
    List<String> selectSpecialClausesByContractId(int contractId);
}
