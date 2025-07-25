package org.scoula.term.service;

import org.scoula.term.domain.TermDTO;
import java.util.List;

public interface TermService {
    List<TermDTO> findAll(String keyword, Integer categoryId);
    TermDTO findById(int termId);
}