package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.model.Category;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO createCategory(String name, MultipartFile icon);
    CategoryDTO deleteCategory(long id);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);
}
