package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> saveProduct
            (@Valid @RequestBody ProductDTO product, @PathVariable Long categoryId){

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
    public ResponseEntity<ProductResponse> getProductsByCategoryId(@PathVariable Long categoryId,
                                                                   @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                   @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
                                                                   @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORY_BY,required = false) String sortBy,
                                                                   @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_ORDER,required = false) String sortOrder
    ){

        ProductResponse productResponse= productService.getProductsByCategoryId(
                categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public  ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                 @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORY_BY,required = false) String sortBy,
                                                                 @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_ORDER,required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getProductsByKeyword(
                keyword,pageNumber,pageSize,sortBy,sortOrder);
      return  new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }


    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct
            (@RequestBody ProductDTO productDTO, @PathVariable Long productId){

        ProductDTO productDTOResponse=  productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(productDTOResponse, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct
            ( @PathVariable Long productId){

        ProductDTO productDTO=  productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {


       ProductDTO productDTO = productService.updateProductImage(productId, image);
       return ResponseEntity.ok(productDTO);
    }
}
