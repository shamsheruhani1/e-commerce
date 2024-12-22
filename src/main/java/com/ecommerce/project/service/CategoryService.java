package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder);
    Category getCategory(Long categoryId);
    CategoryDTO updateQuery(CategoryDTO categoryDTO,Long categoryId);
}

