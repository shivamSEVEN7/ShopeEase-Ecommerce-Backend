package com.ecommerce.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long categoryId;
    @NotBlank(message = "Category Name is Required")
   private String categoryName;
    @JsonIgnore
   @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
   private List<Product> products;

}
