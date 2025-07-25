package org.scoula.term.mapper;

import org.scoula.term.domain.TermDTO;
import java.util.List;
import java.util.Map;

public interface TermMapper {
    List<TermDTO> findAll(Map<String, Object> params);
    TermDTO findById(int termId);
}