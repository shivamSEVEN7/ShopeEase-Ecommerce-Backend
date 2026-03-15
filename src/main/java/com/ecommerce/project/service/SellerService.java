package com.ecommerce.project.service;

import com.ecommerce.project.dto.SellerDTO;
import com.ecommerce.project.dto.SellerDetailsDTO;
import com.ecommerce.project.dto.SellerStatusDTO;
import com.ecommerce.project.model.SellerStatus;
import org.springframework.web.bind.annotation.RequestBody;

public interface SellerService {
    SellerDetailsDTO registerSeller(SellerDTO seller);

    SellerDetailsDTO updateSellerStatus(Long sellerId, SellerStatusDTO sellerStatus);
}
