package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            log.warn("Category with name '{}' already exists", category.getCategoryName());
            throw new APIException("Category already available");
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    categoryRepository.deleteById(categoryId);
                    log.info("Category deleted: {}", category.getCategoryName());
                    return modelMapper.map(category, CategoryDTO.class);
                })
                .orElseThrow(() -> {
                    log.error("Category with ID {} not found", categoryId);
                    throw new ResourceNotFoundException("category", "categoryId", categoryId);
                });
    }

    @Override
    public CategoryResponse getAllCategories(
            Integer pageNumber,Integer pageSize,String sortBy ,String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            log.warn("No categories found");
            throw new APIException("No categories are available");
        }

        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        log.info("Retrieved {} categories", categoryDTOs.size());

        return new CategoryResponse(
                categoryDTOs,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    @Override
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category with ID {} not found", categoryId);
                    throw new ResourceNotFoundException("category", "categoryId", categoryId);
                });
    }

    @Override
    public CategoryDTO updateQuery(CategoryDTO categoryDTO, Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category with ID {} not found for update", categoryId);
            throw new ResourceNotFoundException("category", "categoryId", categoryId);
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId); // Ensure the ID is set for the update

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", updatedCategory);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
