package com.example.brand.service;

import com.example.brand.model.Brand;
import com.example.brand.repository.BrandRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

import java.util.Arrays;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @PostConstruct
    public void initData() {
        // Populate sample data
        brandRepository.save(new Brand(1L, "Nike", "https://example.com/nike.png", "Sportswear and shoes",
                Arrays.asList("#000000", "#FFFFFF"),
                Arrays.asList("Futura Condensed Extra Black"),
                Arrays.asList("https://example.com/nike-swoosh.png"),
                Arrays.asList("https://example.com/nike-shoe-ad.jpg")));

        brandRepository.save(new Brand(2L, "Apple", "https://example.com/apple.png", "Electronics and software",
                Arrays.asList("#A3AAAE", "#000000", "#FFFFFF"),
                Arrays.asList("San Francisco", "Myriad"),
                Arrays.asList("https://example.com/apple-logo-white.png"),
                Arrays.asList("https://example.com/iphone-promo.jpg", "https://example.com/macbook-shot.jpg")));

        brandRepository.save(new Brand(3L, "Samsung", "https://example.com/samsung.png", null,
                Arrays.asList("#1428A0", "#FFFFFF", "#000000"),
                Arrays.asList("SamsungOne", "Helvetica"),
                Arrays.asList("https://example.com/samsung-logo-blue-bg.png"),
                Arrays.asList("https://example.com/samsung-galaxy-phone.jpg")));
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}
