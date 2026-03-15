package com.ecommerce.project.service;

import com.ecommerce.project.dto.SellerDTO;
import com.ecommerce.project.dto.SellerDetailsDTO;
import com.ecommerce.project.dto.SellerStatusDTO;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.SellerRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SellerServiceImpl implements SellerService {
    SellerRepo sellerRepository;
    AuthUtil authUtil;
    ModelMapper modelMapper;
    public SellerServiceImpl(SellerRepo sellerRepository, AuthUtil authUtil, ModelMapper modelMapper) {
        this.sellerRepository = sellerRepository;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }
    @Override
    public SellerDetailsDTO registerSeller(SellerDTO seller) {
        Seller seller1 = Seller.builder().
                businessName(seller.getBusinessName())
                .user(authUtil.loggedInUser())
                .sellerCode(generateSellerCode(seller.getBusinessName()))
                .businessEmail(seller.getBusinessEmail())
                .businessPhone(seller.getBusinessPhone())
                .gstNumber(seller.getGstNumber())
                .panNumber(seller.getPanNumber())
                .bankAccountNumber(seller.getBankAccountNumber())
                .ifscCode(seller.getIfscCode())
                .status(SellerStatus.PENDING_VERIFICATION)
                .build();
        Seller savedSeller = sellerRepository.save(seller1);
        return modelMapper.map(savedSeller, SellerDetailsDTO.class);

    }

    @Override
    public SellerDetailsDTO updateSellerStatus(Long sellerId, SellerStatusDTO sellerStatus) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller Id", sellerId, "Seller"));
        User user = seller.getUser();
        switch (sellerStatus.getStatus().toUpperCase()) {
            case "ACTIVE":
                seller.setStatus(SellerStatus.ACTIVE);
                user.getRoles().add(new Role(RoleName.SELLER));
                break;

            case "INACTIVE":
                seller.setStatus(SellerStatus.INACTIVE);
                break;

            case "REJECTED":
                seller.setStatus(SellerStatus.REJECTED);
                break;

            case "SUSPENDED":
                seller.setStatus(SellerStatus.SUSPENDED);
                user.getRoles().remove(new Role(RoleName.SELLER));
                break;

            case "PENDING_VERIFICATION":
                seller.setStatus(SellerStatus.PENDING_VERIFICATION);
                break;

            default:
                throw new IllegalArgumentException("Invalid seller status: " + sellerStatus);
        }
        Seller updatedSeller = sellerRepository.save(seller);
        return modelMapper.map(updatedSeller, SellerDetailsDTO.class);
    }


    public static String generateSellerCode(String businessName) {
        String prefix = businessName.length() >= 3
                ? businessName.substring(0, 3).toUpperCase()
                : businessName.toUpperCase();
        String random = UUID.randomUUID()
                .toString()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, 6)
                .toUpperCase();

        return prefix + "-" + random;
    }
}
