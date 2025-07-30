package org.scoula.categorymanager.controller;

import org.scoula.categorymanager.domain.CategorymanagerVO;
import org.scoula.categorymanager.dto.CategorymanagerDTO;
import org.scoula.categorymanager.service.CategorymanagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorymanagerController {

    @Autowired
    private CategorymanagerService categorymanagerService;

    @GetMapping
    public List<CategorymanagerVO> getAllCategories() {
        return categorymanagerService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategorymanagerVO getCategoryById(@PathVariable("id") int id) {
        return categorymanagerService.getCategoryById(id);
    }

    @PostMapping
    public void addCategory(@RequestBody CategorymanagerDTO dto) {
        CategorymanagerVO vo = new CategorymanagerVO();
        vo.setCategoryName(dto.getCategoryName());
        vo.setDescription(dto.getDescription());
        vo.setCategoryColor(dto.getCategoryColor());
        categorymanagerService.insertCategory(vo);
    }

    @PutMapping("/{id}")
    public void updateCategory(@PathVariable("id") int id, @RequestBody CategorymanagerDTO dto) {
        CategorymanagerVO vo = new CategorymanagerVO();
        vo.setCategoryId(id);
        vo.setCategoryName(dto.getCategoryName());
        vo.setDescription(dto.getDescription());
        vo.setCategoryColor(dto.getCategoryColor());
        categorymanagerService.updateCategory(vo);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") int id) {
        categorymanagerService.deleteCategory(id);
    }
}
