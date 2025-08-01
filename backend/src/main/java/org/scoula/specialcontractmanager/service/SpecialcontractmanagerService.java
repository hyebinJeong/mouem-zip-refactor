package org.scoula.specialcontractmanager.service;

import java.util.List;

import org.scoula.specialcontractmanager.domain.SpecialcontractmanagerVO;

public interface SpecialcontractmanagerService {
    List<SpecialcontractmanagerVO> getAllSpecialClauses();

    SpecialcontractmanagerVO getSpecialClauseById(Integer id);

    boolean addSpecialClause(SpecialcontractmanagerVO vo);

    boolean updateSpecialClause(SpecialcontractmanagerVO vo);

    boolean deleteSpecialClause(Integer id);
}
