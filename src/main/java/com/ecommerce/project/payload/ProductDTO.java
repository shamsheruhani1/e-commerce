package com.ecommerce.project.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank
    @Size(min = 4,message = "Product Name  contain at least 4 lettres")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 10,message = "Product Description  contain at least 10 lettres")
    private String description;


    @Min(value = 1,message = " minimum quality should be 1")
    @Max(value = 5,message = " maximum quality should be 5")
    private Integer quantity;


    @Min(value = 10,message = "price should be at least 10 Rs")
    private Double price;

    private Double discount;
    private Double speciealPrice;

}
