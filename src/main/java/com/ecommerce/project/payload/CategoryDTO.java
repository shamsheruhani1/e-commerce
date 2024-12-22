package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {


    private Long categoryId;

    @NotBlank
    @Size(min = 5,message = "Category Name  contain at least 5 lettres")
    private String categoryName;
}
