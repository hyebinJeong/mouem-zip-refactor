package org.scoula.category.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.category.domain.dto.CategoryDTO;
import org.scoula.category.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> getCategories() {
        return categoryService.findAll();
    }
}