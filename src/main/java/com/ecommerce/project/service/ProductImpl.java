package com.ecommerce.project.service;


import com.ecommerce.project.dto.*;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.*;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.List;


@Service
public class ProductImpl implements ProductService {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepo cartRepository;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private ProductHighlightRepo productHighlightRepo;
    @Autowired
    private ImageRepo imageRepo;
    @Value("${project.images}")
    String path;
    @Value("${image.base.url}")
    String imageBaseUrl;

    @Override
    public ProductDTO addProduct(ProductRequestDTO product, long categoryId, MultipartFile mainImage, List<MultipartFile> additionalImages) {
        Category category = categoryRepo.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("CategoryId", categoryId, "Category"));
        Product currentProduct = modelMapper.map(product, Product.class);
        currentProduct.setCategory(category);
        User currentUser = authUtil.loggedInUser();
        Seller seller = sellerRepo.findByUser(currentUser);
        currentProduct.setSeller(seller);
        currentProduct.setSpecialPrice(product.getPrice() - (product.getPrice()*product.getDiscount()*0.01));
        currentProduct.setSlug(generateSlug(product.getProductName()));
        Product savedProduct =  productRepo.save(currentProduct);
        try{
            FileInfo imageInfo = fileService.uploadImage(mainImage);
            Image image = new Image(savedProduct, imageInfo.getPublicId(), imageInfo.getUrl(), true);
            Image savedImage = imageRepo.save(image);
            currentProduct.getImages().add(savedImage);
        }
        catch (IOException e) {
            throw new RuntimeException("Error saving product image", e);
        }

        for (ProductHighlightRequestDTO productHighlightRequestDTO : product.getHighlights()) {
            ProductHighlight productHighlight = new ProductHighlight(productHighlightRequestDTO.getText(), savedProduct);
            productHighlightRepo.save(productHighlight);
        }
        if(additionalImages != null) {
            for(MultipartFile additionalImage: additionalImages) {
                try{
                    FileInfo imageInfo = fileService.uploadImage(additionalImage);
                    Image image = new Image(currentProduct, imageInfo.getPublicId(), imageInfo.getUrl(), false);
                    Image savedAdditionalImage = imageRepo.save(image);
                    savedProduct.getImages().add(savedAdditionalImage);
                }
                catch (IOException e) {
                    throw new RuntimeException("Error saving product image", e);
                }
            }
        }
        return modelMapper.map(productRepo.save(savedProduct), ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(int page, int size, String sortBy, String sortOrder, String category, String keyword, Double minPrice, Double maxPrice, Double minDiscount, Double minRating) {
        Specification<Product> spec = Specification.allOf(
                (root, query, builder) -> category == null ? builder.conjunction() :
                        builder.equal(builder.lower(root.get("category").get("categoryName")), category.toLowerCase()),
                (root, query, builder) -> keyword == null ? builder.conjunction() :
                        builder.like(builder.lower(root.get("productName")), "%" + keyword + "%"),
                (root, query, builder) -> {
                    if(minPrice == null && maxPrice == null) return builder.conjunction();
                    if (minPrice != null && maxPrice != null)
                    return builder.between(root.get("specialPrice"), minPrice, maxPrice);
                    else if (minPrice != null)
                    return builder.greaterThanOrEqualTo(root.get("specialPrice"), minPrice);
                    else
                    return builder.lessThanOrEqualTo(root.get("specialPrice"), maxPrice);
                },
                (root, query, builder) -> minDiscount == null ? builder.conjunction() :
                        builder.greaterThanOrEqualTo(root.get("discount"), minDiscount),
                (root, query, builder) -> minRating == null ? builder.conjunction() :
                        builder.greaterThanOrEqualTo(root.get("averageRating"), minRating)
        );
        ProductResponse productResponse = new ProductResponse();
        if (sortBy.equals("newestArrivals")) {
            sortBy = "createdAt";
        }
        else if(sortBy.equals("rating")){
            sortBy = "averageRating";
        }
        Sort sortingDetails = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortingDetails);
        Page<Product> productsPage = productRepo.findAll(spec ,pageable);
        List<Product> products = productsPage.getContent();

        List<ProductDTO> productsDtos = products.stream().map(product ->
        {ProductDTO pd =  modelMapper.map(product, ProductDTO.class);
            for(Image image : product.getImages()) {
                if (image.getIsPrimary()){
                    pd.setImage(image.getUrl());
                }
            }
        return pd;}).
                toList();
        productResponse.setContent(productsDtos);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setIsLast(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Product> productsPage = productRepo.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageable);
        List<Product> products = productsPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products found");
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList());
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setIsLast(productsPage.isLast());
        return productResponse;
    }



    @Override
    public ProductResponse searchByCategory(long categoryId, int page, int size, String sortBy, String sortOrder) {
        if(!categoryRepo.existsById(categoryId)){
            throw new ResourceNotFoundException("CategoryId", categoryId, "Category");
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Product> productsPage = productRepo.findByCategory_CategoryId(categoryId, pageable);
        ProductResponse productResponse = new ProductResponse();
        List<Product> products = productsPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        productResponse.setContent(products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList());
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setIsLast(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(long productId, ProductDTO productDTO) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "Product Id"));
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setDiscount(productDTO.getDiscount());
        product.setPrice(productDTO.getPrice());
        product.setSpecialPrice(product.getPrice() - (product.getPrice()*product.getDiscount()*0.01));
        productRepo.save(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "Product Id"));
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(productId));
        productRepo.deleteById(productId);
        return modelMapper.map(product, ProductDTO.class);
    }


//    @Override
//    public ProductDTO updateImage(long productId, MultipartFile file) throws IOException {
//        String uploadedFileName = fileService.uploadFile(path, file);
//        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "Product Id"));
//        product.setImage(uploadedFileName);
//        productRepo.save(product);
//        return modelMapper.map(product, ProductDTO.class);
//    }

    @Override
    public ProductDetailsDTO searchByProductId(long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "Product Id"));
        ProductDetailsDTO productDetailsDTO = modelMapper.map(product, ProductDetailsDTO.class);

        productDetailsDTO.setSeller(new SellerResponseDTO(product.getSeller().getSellerCode(), product.getSeller().getBusinessName()));
        for(Image image : product.getImages()){
            if(image.getIsPrimary()==true){
                productDetailsDTO.setImage(image.getUrl());

            }
            else{
                productDetailsDTO.getAdditionalImages().add(image.getUrl());

            }
        }


        return productDetailsDTO;
    }

    private String constructImageUrl(String imageName){
        return imageBaseUrl + imageName;
    }
    private String generateSlug(String name) {
        return name
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")   // remove special chars
                .replaceAll("\\s+", "-")           // replace spaces with dash
                .replaceAll("-+", "-");            // collapse multiple dashes
    }


}
