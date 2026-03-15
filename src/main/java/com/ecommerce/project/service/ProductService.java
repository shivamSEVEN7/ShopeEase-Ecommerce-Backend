package com.ecommerce.project.service;


import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.dto.ProductDetailsDTO;
import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponse searchByCategory(long categoryId, int page, int size, String sortBy, String sortOrder);

    ProductDTO addProduct(ProductRequestDTO product, long categoryId, MultipartFile mainImage, List<MultipartFile> additionalImages) throws IOException;

    ProductResponse getAllProducts(int page, int size, String sortBy, String sortOrder, String category, String keyword, Double minPrice, Double maxPrice, Double minDiscount, Double minRating) throws IOException;

    ProductResponse searchByKeyword(String keyword, int page, int size, String sortBy, String sortOrder);

    ProductDTO updateProduct(long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(long productId);

//    ProductDTO updateImage(long productId, MultipartFile file) throws IOException;

    ProductDetailsDTO searchByProductId(long productId);
}
