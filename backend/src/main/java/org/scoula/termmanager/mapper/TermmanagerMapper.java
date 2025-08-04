package org.scoula.termmanager.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.scoula.termmanager.domain.TermmanagerVO;

@Mapper
public interface TermmanagerMapper {
    List<TermmanagerVO> getAllTerms();
    TermmanagerVO getTermById(Integer termId);
    int insertTerm(TermmanagerVO term);
    int updateTerm(TermmanagerVO term);
    int deleteTerm(Integer termId);
}
