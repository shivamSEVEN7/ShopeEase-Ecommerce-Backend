package com.ecommerce.project.service;

import com.ecommerce.project.dto.FileInfo;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.AdRepo;
import com.ecommerce.project.dto.FileInfo;
import com.ecommerce.project.repositories.ProductRepo;
import com.ecommerce.project.repositories.SellerRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AdServiceImpl implements AdService{
    FileService fileService;
    AdRepo adRepo;
    ProductRepo productRepo;
    ModelMapper modelMapper;
    AuthUtil authUtil;
    SellerRepo sellerRepo;

    public AdServiceImpl(FileService fileService, AdRepo adRepo, ProductRepo productRepo, ModelMapper modelMapper, AuthUtil authUtil, SellerRepo sellerRepo) {
        this.fileService = fileService;
        this.adRepo = adRepo;
        this.productRepo = productRepo;
        this.modelMapper = modelMapper;
        this.authUtil = authUtil;
        this.sellerRepo = sellerRepo;
    }

    @Override
    public AdResponse createAd(AdRequest request) {
        Ad ad = new Ad();
        if(request.getType().equals(AdType.BANNER)){

            ad.setTitle(request.getTitle());
            ad.setRedirectUrl(request.getRedirectUrl());
            if (request.getBannerImage() != null && !request.getBannerImage().isEmpty()) {
                try {
                    FileInfo uploaderBanner = fileService.uploadAdBanner(request.getBannerImage());
                    ad.setBannerImage(uploaderBanner.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        else{
            ad.setTitle(request.getTitle());
            ad.setSubtitle(request.getSubtitle());
            ad.setDescription(request.getDescription());

            ad.setBackgroundColor(request.getBackgroundColor());
            ad.setCtaText(request.getCtaText());
            ad.setPriority(request.getPriority());
            ad.setActive(request.isActive());
            ad.setExpiryAt(OffsetDateTime.now().plusMonths(24));
            ad.setType(request.getType());
            if (request.getProductImage() != null && !request.getProductImage().isEmpty()) {
                try {
                    FileInfo uploaderImage = fileService.uploadAdImage(request.getProductImage());
                    ad.setProductImage(uploaderImage.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // Product relation
            if (request.getProductId() != null) {
                Product product = productRepo.findById(request.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                ad.setProduct(product);
                ad.setRedirectUrl("/product/" + product.getId() + "/" + product.getSlug());
            }
        }
        Seller currentSeller =sellerRepo.findByUser_UserId(authUtil.loggedInUserId());
        ad.setSeller(currentSeller);
        Ad savedAd = adRepo.save(ad);
        return modelMapper.map(savedAd, AdResponse.class);
    }

    @Override
    public List<AdResponse> getAllActiveAds(){
        List<Ad> ads = adRepo.findActiveAds(OffsetDateTime.now());
        return ads.stream()
                .map(ad -> {
                    AdResponse res = modelMapper.map(ad, AdResponse.class);
                    if (ad.getSeller() != null) {
                        res.setSeller(modelMapper.map(ad.getSeller(), SellerResponse.class));
                    }
                    return res;
                }).toList();
    }
}
