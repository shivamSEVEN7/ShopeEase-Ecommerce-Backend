package com.ecommerce.project.controller;

import com.ecommerce.project.model.Ad;
import com.ecommerce.project.model.AdRequest;
import com.ecommerce.project.model.AdResponse;
import com.ecommerce.project.service.AdService;
import com.ecommerce.project.service.AdServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")

public class AdController {
    AdService adService;
    @Autowired
    AdController(AdService adService){
        this.adService = adService;
    }
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/create")
    public ResponseEntity<AdResponse> createNewAd(@ModelAttribute AdRequest request) {
        AdResponse savedAd = adService.createAd(request);
        return ResponseEntity.ok(savedAd);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AdResponse>> getAllActiveAds() {
        List<AdResponse> activeAds = adService.getAllActiveAds();
        return ResponseEntity.ok(activeAds);
    }

}
