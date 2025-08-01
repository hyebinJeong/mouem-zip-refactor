package org.scoula.categorymanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.categorymanager.domain.CategorymanagerVO;

import java.util.List;

@Mapper
public interface CategorymanagerMapper {
    List<CategorymanagerVO> getAllCategories();
    CategorymanagerVO getCategoryById(int categoryId);
    void insertCategory(CategorymanagerVO category);
    void updateCategory(CategorymanagerVO category);
    void deleteCategory(int categoryId);
}
