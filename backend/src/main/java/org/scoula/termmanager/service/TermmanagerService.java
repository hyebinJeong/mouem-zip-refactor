package org.scoula.termmanager.service;

import java.util.List;
import org.scoula.termmanager.domain.TermmanagerVO;

public interface TermmanagerService {
    List<TermmanagerVO> getAllTerms();
    TermmanagerVO getTermById(Integer termId);
    void insertTerm(TermmanagerVO term);
    void updateTerm(TermmanagerVO term);
    void deleteTerm(Integer termId);
}
