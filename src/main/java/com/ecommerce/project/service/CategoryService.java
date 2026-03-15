package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.model.Category;



public interface CategoryService {
    CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(long id);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);
}
