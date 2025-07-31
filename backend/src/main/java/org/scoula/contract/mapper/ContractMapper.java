package org.scoula.contract.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.contract.domain.ContractDTO;

@Mapper
public interface ContractMapper {
    void insertContract(ContractDTO contract);
}
