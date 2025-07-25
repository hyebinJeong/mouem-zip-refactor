package org.scoula.category.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.category.domain.dto.CategoryDTO;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDTO> findAll();
}