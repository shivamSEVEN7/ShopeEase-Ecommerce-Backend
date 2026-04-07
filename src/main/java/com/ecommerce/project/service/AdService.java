package com.ecommerce.project.service;

import com.ecommerce.project.model.Ad;
import com.ecommerce.project.model.AdRequest;
import com.ecommerce.project.model.AdResponse;

import java.util.List;

public interface AdService {
    AdResponse createAd(AdRequest request);
    List<AdResponse> getAllActiveAds();
}

