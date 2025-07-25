package org.scoula.category.service;

import org.scoula.category.domain.dto.CategoryDTO;
import org.scoula.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryMapper.findAll();
    }
}