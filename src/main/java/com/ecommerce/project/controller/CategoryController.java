package com.ecommerce.project.controller;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    private CategoryService catergoryService;

    @Autowired
    public CategoryController(CategoryService cs, CategoryService categoryService) {
        this.catergoryService = cs;
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(defaultValue = "0",name = "page", required = false) Integer page,
            @RequestParam(defaultValue = "5", name = "size", required = false) Integer size,
            @RequestParam(defaultValue = "categoryId", name = "sortBy", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", name = "sortOrder", required = false) String sortOrder
    ) {
        return new ResponseEntity<>(catergoryService.getAllCategories(page, size, sortBy, sortOrder), HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = catergoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable long id) {
        CategoryDTO categoryDTO = catergoryService.deleteCategory(id);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable long categoryId) {
        CategoryDTO updateCategoryDTO = new CategoryDTO();
        updateCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updateCategoryDTO, HttpStatus.OK);

    }

}
