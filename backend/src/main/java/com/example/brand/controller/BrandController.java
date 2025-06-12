package com.example.brand.controller;

import com.example.brand.model.Brand;
import com.example.brand.service.BrandService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.brand.dto.FetchUrlRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular dev server
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @PostMapping("/fetch-from-url")
    public ResponseEntity<?> fetchBrandFromUrl(@RequestBody FetchUrlRequest request) {
        if (request == null || request.getUrl() == null || request.getUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL is required.");
        }
        try {
            Brand brand = brandService.fetchBrandDataFromUrl(request.getUrl());
            // The service method currently doesn't return null, it throws IOException or returns a Brand.
            // If it were to return null for "not found" type scenarios (without IO error):
            // if (brand == null) {
            //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not extract brand data from URL.");
            // }
            return ResponseEntity.ok(brand);
        } catch (IOException e) {
            // Consider using a proper logger in a real application
            System.err.println("Error fetching brand data from URL: " + request.getUrl() + " - " + e.getMessage());
            // Sanitize error message for client if necessary
            String clientErrorMessage = "Failed to fetch or parse data. Please check the URL or network connectivity.";
            if (e.getMessage() != null && e.getMessage().contains("timed out")) {
                clientErrorMessage = "Request to URL timed out. Please check if the site is reachable.";
            } else if (e.getMessage() != null && e.getMessage().contains("Too many redirects")) {
                clientErrorMessage = "Too many redirects from the URL. Please check the URL.";
            } else if (e.getMessage() != null && (e.getMessage().toLowerCase().contains("unresolved address") || e.getMessage().toLowerCase().contains("unknownhostexception"))) {
                clientErrorMessage = "Could not resolve the address. Please check the URL.";
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(clientErrorMessage + (e.getMessage() != null ? " (Details: " + e.getMessage() + ")" : ""));
        } catch (IllegalArgumentException e) {
             System.err.println("Malformed URL: " + request.getUrl() + " - " + e.getMessage());
            return ResponseEntity.badRequest().body("Malformed URL: " + e.getMessage());
        }
    }
}
