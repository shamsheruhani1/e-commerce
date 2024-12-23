package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO,Long categoryId);
    ProductResponse getAllProduct(Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder);
    ProductResponse getProductsByCategoryId(Long categoryId);
}
