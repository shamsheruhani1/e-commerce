package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class CategoryController {
    List<Category> categoryList= new ArrayList<>();

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public  ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORY_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_ORDER,required = false) String sortOrder
            ){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){

            CategoryDTO persistedCategoryDTO= categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(persistedCategoryDTO);

    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

            CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
            return  ResponseEntity.status(HttpStatus.OK).body(categoryDTO);


    }

    @GetMapping("/public/categories/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable Long categoryId){

        Category category    = categoryService.getCategory(categoryId);
        return  ResponseEntity.status(HttpStatus.OK).body(category);

    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){


        CategoryDTO category1 = categoryService.updateQuery(categoryDTO, categoryId);
            return ResponseEntity.ok(category1);


    }
}
