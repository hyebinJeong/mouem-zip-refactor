package org.scoula.specialclause.service;

import lombok.RequiredArgsConstructor;
import org.scoula.specialclause.domain.SpecialClauseDTO;
import org.scoula.specialclause.mapper.SpecialClauseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialClauseService {

    private final SpecialClauseMapper mapper;

    public List<SpecialClauseDTO> getAll() {
        return mapper.findAll();
    }

    public void create(SpecialClauseDTO clause) {
        mapper.insertSpecialClause(clause);
    }

    public void delete(int id) {
        mapper.delete(id);
    }
}
