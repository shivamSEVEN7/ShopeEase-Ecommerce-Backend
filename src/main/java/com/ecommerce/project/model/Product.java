package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(min = 3, message = "Product Name must be longer")
    private String productName;
    private String slug;
    @NotBlank
    @Size(min = 6, message = "Description must be longer")
    @Column(length = 1000)
    private String description;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "main_image_id")
//    private Image image;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    private int quantity;
    private Double discount;
    private Double price;
    private Double specialPrice;
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer reviewCount = 0;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductHighlight> highlights = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;



    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Image getPrimaryImage(){
        if (images == null || images.isEmpty()) return null;
        for (Image image : images){
            if(image.getIsPrimary())
                return image;
        }
        return null;
    }

}
