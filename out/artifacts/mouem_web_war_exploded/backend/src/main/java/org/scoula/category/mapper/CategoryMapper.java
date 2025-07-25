package org.scoula.category.mapper;

import org.scoula.category.domain.CategoryVO;
import java.util.List;

public interface CategoryMapper {
    List<CategoryVO> findAll();
}