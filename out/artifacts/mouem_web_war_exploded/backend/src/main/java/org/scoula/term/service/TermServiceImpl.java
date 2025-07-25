package org.scoula.term.service;

import org.scoula.term.domain.TermDTO;
import org.scoula.term.mapper.TermMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final TermMapper termMapper;

    @Override
    public List<TermDTO> findAll(String keyword, Integer categoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("categoryId", categoryId);
        return termMapper.findAll(params);
    }

    @Override
    public TermDTO findById(int termId) {
        return termMapper.findById(termId);
    }
}