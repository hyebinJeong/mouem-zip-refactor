package org.scoula.categorymanager.service;

import org.scoula.categorymanager.domain.CategorymanagerVO;

import java.util.List;

public interface CategorymanagerService {
    List<CategorymanagerVO> getAllCategories();
    CategorymanagerVO getCategoryById(int categoryId);
    void insertCategory(CategorymanagerVO category);
    void updateCategory(CategorymanagerVO category);
    void deleteCategory(int categoryId);
}
