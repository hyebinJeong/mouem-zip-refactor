package org.scoula.category.service;

import org.scoula.category.domain.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();
}