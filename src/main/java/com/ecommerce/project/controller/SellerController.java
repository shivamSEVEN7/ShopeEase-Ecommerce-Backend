package com.ecommerce.project.controller;

import com.ecommerce.project.dto.SellerDTO;
import com.ecommerce.project.dto.SellerDetailsDTO;
import com.ecommerce.project.dto.SellerStatusDTO;
import com.ecommerce.project.model.SellerStatus;
import com.ecommerce.project.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SellerController {
    SellerService sellerService;
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/sellers/register")
    ResponseEntity<SellerDetailsDTO> registerSeller(@RequestBody SellerDTO seller) {
        return new ResponseEntity<>(sellerService. registerSeller(seller), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/sellers/{sellerId}/status")
    ResponseEntity<?> updateSellerStatus(@RequestBody SellerStatusDTO sellerStatus, @PathVariable("sellerId") Long sellerId) {
       SellerDetailsDTO sellerDetailsDTO =  sellerService.updateSellerStatus(sellerId, sellerStatus);
       return new ResponseEntity<>(sellerDetailsDTO, HttpStatus.OK);
    }
}
