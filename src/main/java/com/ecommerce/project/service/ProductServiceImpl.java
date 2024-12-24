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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileService fileService;

    @Value("${projects.image.path}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        if (productRepository.existsByProductName(productDTO.getProductName())) {
            log.warn("Product with name '{}' already exists", productDTO.getProductName());
            throw new APIException("Product already available");
        }
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

        if (productList.isEmpty()) {
            log.warn("No product found");
            throw new APIException("There is no product available");
        }

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
    public ProductResponse getProductsByCategoryId(Long categoryId,Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Page<Product> pageInfo = productRepository.findByCategory(category, pageable);

        if (pageInfo.isEmpty()) {
            throw new APIException("No products available for the category ID: " + categoryId);
        }

        List<ProductDTO> productDTOs = pageInfo.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductResponse(
                productDTOs,
                pageInfo.getNumber(),
                pageInfo.getSize(),
                pageInfo.getTotalElements(),
                pageInfo.getTotalPages(),
                pageInfo.isLast()
        );
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword,Integer pageNumber,Integer pageSize, String sortBy ,String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> pageInfo = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);

        if (pageInfo.isEmpty()) {
            throw new APIException("No products available for the given keyword: " + keyword);
        }

        List<ProductDTO> productDTOs = pageInfo.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductResponse(
                productDTOs,
                pageInfo.getNumber(),
                pageInfo.getSize(),
                pageInfo.getTotalElements(),
                pageInfo.getTotalPages(),
                pageInfo.isLast()
        );
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product productFromDB=productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        log.info("Product with ID {} found: {}", productId, productFromDB);
       productFromDB.setProductName(productDTO.getProductName());
       productFromDB.setDescription(productDTO.getDescription());
       productFromDB.setQuantity(productDTO.getQuantity());
       productFromDB.setDiscount(productDTO.getDiscount());
       productFromDB.setPrice(productDTO.getPrice());
       productFromDB.setSpeciealPrice( productDTO.getPrice()- (productDTO.getDiscount() * 0.01) * productDTO.getPrice());

       Product updatedProduct=productRepository.save(productFromDB);
        log.info("updated Product : {}", productFromDB);
        return modelMapper.map(updatedProduct,ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDB=productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(productFromDB);
        return modelMapper.map(productFromDB,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDB= productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );

        String fileName= fileService.uploadImage( path, image);
        productFromDB.setImage(fileName);
        Product updatedProduct= productRepository.save(productFromDB);
        return modelMapper.map(updatedProduct,ProductDTO.class);
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