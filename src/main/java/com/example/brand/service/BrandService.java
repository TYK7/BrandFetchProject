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

    @PostConstruct
    public void initData() {
        // Populate sample data
        brandRepository.save(new Brand(1L, "Nike", "https://example.com/nike.png", "Sportswear and shoes"));
        brandRepository.save(new Brand(2L, "Apple", "https://example.com/apple.png", "Electronics and software"));
        brandRepository.save(new Brand(3L, "Samsung", "https://example.com/samsung.png", null));
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}
