package org.scoula.termmanager.service.impl;

import java.util.List;

import org.scoula.termmanager.domain.TermmanagerVO;
import org.scoula.termmanager.mapper.TermmanagerMapper;
import org.scoula.termmanager.service.TermmanagerService;
import org.springframework.stereotype.Service;

@Service
public class TermmanagerServiceImpl implements TermmanagerService {

    private final TermmanagerMapper termMapper;

    public TermmanagerServiceImpl(TermmanagerMapper termMapper) {
        this.termMapper = termMapper;
    }

    @Override
    public List<TermmanagerVO> getAllTerms() {
        return termMapper.getAllTerms();
    }

    @Override
    public TermmanagerVO getTermById(Integer termId) {
        return termMapper.getTermById(termId);
    }

    @Override
    public void insertTerm(TermmanagerVO term) {
        termMapper.insertTerm(term);
    }

    @Override
    public void updateTerm(TermmanagerVO term) {
        termMapper.updateTerm(term);
    }

    @Override
    public void deleteTerm(Integer termId) {
        termMapper.deleteTerm(termId);
    }
}
