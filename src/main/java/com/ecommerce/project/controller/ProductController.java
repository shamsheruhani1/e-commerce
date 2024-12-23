package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> saveProduct
            (@RequestBody ProductDTO product, @PathVariable Long categoryId){

        ProductDTO productDTO= productService.addProduct(product,categoryId);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORY_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_ORDER,required = false) String sortOrder
    ){

        return new ResponseEntity<>(productService.getAllProduct
                (pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);
    }

    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategoryId(@PathVariable Long categoryId){

        ProductResponse productResponse= productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(productResponse);
    }
}
