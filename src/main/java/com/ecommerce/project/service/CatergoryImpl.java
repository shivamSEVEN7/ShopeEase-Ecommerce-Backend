package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatergoryImpl implements CategoryService{
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortingDetails = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortingDetails);
        Page<Category> categoriesPage = categoryRepo.findAll(pageable);
        List<Category> categories = categoriesPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("No Categories Found");
        }
        List<CategoryDTO> categoryDTOS =  categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoriesPage.getNumber());
        categoryResponse.setPageSize(categoriesPage.getSize());
        categoryResponse.setTotalPages(categoriesPage.getTotalPages());
        categoryResponse.setTotalElements(categoriesPage.getTotalElements());
        categoryResponse.setIsLast(categoriesPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
        if (existingCategory != null) {
            throw new APIException("Duplicate Category Name");
        }
        Category savedCategory =  categoryRepo.save(modelMapper.map(categoryDTO, Category.class));
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(long id) {
//        Category c = categories.stream().filter(category -> category.getCategoryId() == id).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if(!categoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("CategoryID", id, "Category");
        }
        Category category = categoryRepo.findById(id).get();
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryRepo.deleteById(id);
        return categoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId){
//        Category existingCategory = categories.stream().filter(category1 -> category1.getCategoryId() == categoryId).findFirst().orElse(null);
        boolean existingCategoryStatus = categoryRepo.existsById(categoryId);
        if (existingCategoryStatus) {
            Category existingCategory = categoryRepo.getReferenceById(categoryId);
            existingCategory.setCategoryName(categoryDTO.getCategoryName());
           Category updatedCategory =  categoryRepo.save(existingCategory);
            return modelMapper.map(updatedCategory, CategoryDTO.class);
        }
        else {
            throw new ResourceNotFoundException("CategoryID", categoryId, "Category");
        }
    }

}
