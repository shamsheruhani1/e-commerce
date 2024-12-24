package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO,Long categoryId);
    ProductResponse getAllProduct(Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder);
    ProductResponse getProductsByCategoryId(Long categoryId,Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder);
    ProductResponse getProductsByKeyword(String Keyword,Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder);
    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
