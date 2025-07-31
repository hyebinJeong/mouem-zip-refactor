package org.scoula.categorymanager.service.impl;

import org.scoula.categorymanager.domain.CategorymanagerVO;
import org.scoula.categorymanager.mapper.CategorymanagerMapper;
import org.scoula.categorymanager.service.CategorymanagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorymanagerServiceImpl implements CategorymanagerService {

    @Autowired
    private CategorymanagerMapper categorymanagerMapper;

    @Override
    public List<CategorymanagerVO> getAllCategories() {
        return categorymanagerMapper.getAllCategories();
    }

    @Override
    public CategorymanagerVO getCategoryById(int categoryId) {
        return categorymanagerMapper.getCategoryById(categoryId);
    }

    @Override
    public void insertCategory(CategorymanagerVO category) {
        categorymanagerMapper.insertCategory(category);
    }

    @Override
    public void updateCategory(CategorymanagerVO category) {
        categorymanagerMapper.updateCategory(category);
    }

    @Override
    public void deleteCategory(int categoryId) {
        categorymanagerMapper.deleteCategory(categoryId);
    }
}
