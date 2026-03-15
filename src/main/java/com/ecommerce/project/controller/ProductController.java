package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.dto.ProductDetailsDTO;
import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponse;
import com.ecommerce.project.service.ProductImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductImpl productService;

//    @PostMapping("/admin/categories/{categoryId}/product")
//    public ResponseEntity<ProductDTO> addProduct(@PathVariable int categoryId, @Valid @RequestBody ProductDTO productDTO) {
//            return new ResponseEntity<>(productService.addProduct(productDTO, categoryId), HttpStatus.CREATED);
//    }

    @PostMapping("/public/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable int categoryId, @Valid @RequestPart ProductRequestDTO product, @RequestPart MultipartFile mainImage, @RequestPart(required = false) List<MultipartFile> additionalImages) throws IOException {
        System.out.println("Request Recived");
        return new ResponseEntity<>(productService.addProduct(product, categoryId, mainImage, additionalImages), HttpStatus.CREATED);
    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "category", required = false) String category ,
                                                          @RequestParam(name = "keyword", required = false) String keyword,
                                                          @RequestParam(defaultValue = "0", name = "pageNumber", required = false) int page ,
                                                          @RequestParam(defaultValue = "8", name = "size", required = false) int size,
                                                          @RequestParam(required = false) Double minPrice,
                                                          @RequestParam(required = false) Double maxPrice,
                                                          @RequestParam(required = false) Double minDiscount,
                                                          @RequestParam(required = false) Double minRating,
                                                          @RequestParam(defaultValue = "Id", name = "sortBy", required = false) String sortBy,
                                                          @RequestParam(defaultValue = "desc", name = "sortOrder", required = false) String sortOrder) {
        return new ResponseEntity<>(productService.getAllProducts(page, size, sortBy, sortOrder, category, keyword, minPrice, maxPrice, minDiscount, minRating), HttpStatus.OK);
    }
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable long categoryId,
                                                                 @RequestParam(defaultValue = "0", name = "page", required = false) int page ,
                                                                 @RequestParam(defaultValue = "10", name = "size", required = false) int size,
                                                                 @RequestParam(defaultValue = "Id", name = "sortBy", required = false) String sortBy,
                                                                 @RequestParam(defaultValue = "asc", name = "sortOrder", required = false) String sortOrder) {
        return new ResponseEntity<>(productService.searchByCategory(categoryId, page, size, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(defaultValue = "0", name = "page", required = false) int page ,
                                                                @RequestParam(defaultValue = "10", name = "size", required = false) int size,
                                                                @RequestParam(defaultValue = "Id", name = "sortBy", required = false) String sortBy,
                                                                @RequestParam(defaultValue = "asc", name = "sortOrder", required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByKeyword(keyword, page, size, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDetailsDTO> getProductById(@PathVariable long productId) {
        ProductDetailsDTO productDetailsDTO = productService.searchByProductId(productId);
        return new ResponseEntity<>(productDetailsDTO,  HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable long productId,@Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.updateProduct(productId, productDTO), HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable long productId) {
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }

//    @PutMapping("/admin/products/{productId}/image")
//    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable long productId, @RequestBody MultipartFile file) throws IOException {
//        return new ResponseEntity<>(productService.updateImage(productId, file), HttpStatus.OK);
//    }

}
