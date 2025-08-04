package org.scoula.specialclause.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.specialclause.domain.SpecialClauseDTO;

import java.util.List;

@Mapper
public interface SpecialClauseMapper {
    List<SpecialClauseDTO> findAll();
    void insertSpecialClause(SpecialClauseDTO clause);
    void insertContractSpecialClauses(@Param("contractId") int contractId,
                                      @Param("specialClauseIds") List<Integer> specialClauseIds);
    void delete(int id);
}

