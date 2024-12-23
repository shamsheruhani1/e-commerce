package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        // Retrieve the category and throw an exception if not found
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        log.info("Category with ID {} found: {}", categoryId, category);

        // Map ProductDTO to Product and set additional properties
        Product product = modelMapper.map(productDTO, Product.class);
        product.setSpeciealPrice(calculateSpecialPrice(productDTO));
        product.setCategory(category);
        product.setImage(product.getImage() != null ? product.getImage() : "default.png");

        // Persist the product entity
        Product persistedProduct = productRepository.save(product);

        // Return the persisted product as a DTO
        return modelMapper.map(persistedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProduct(Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder) {

        Sort sortByAndOrder=   sortOrder.equalsIgnoreCase("asc")
                     ?Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageable= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findAll(pageable);


        List<Product> productList=productPage.getContent();

        List<ProductDTO> productDTOs = productList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();


        return new ProductResponse(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    public ProductResponse getProductsByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> productList = productRepository.findByCategoryOrderByPriceAsc(category);
        if (productList == null || productList.isEmpty()) {
            throw new APIException("No products available for the category ID: " + categoryId);
        }

        List<ProductDTO> productDTOs = productList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        return ProductResponse.builder()
                .content(productDTOs)
                .build();
    }


    /**
     * Calculates the special price for a product.
     *
     * @param productDTO the product DTO containing price and discount
     * @return the calculated special price
     */
    private Double calculateSpecialPrice(ProductDTO productDTO) {
        if (productDTO.getDiscount() == null || productDTO.getDiscount() <= 0) {
            return productDTO.getPrice();
        }
        return productDTO.getPrice() - (productDTO.getPrice() * productDTO.getDiscount() / 100);
    }
}