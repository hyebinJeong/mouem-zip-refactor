package org.scoula.term.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.term.domain.dto.TermDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface TermMapper {
    List<TermDTO> findAll(Map<String, Object> params);
    TermDTO findById(int termId);
}