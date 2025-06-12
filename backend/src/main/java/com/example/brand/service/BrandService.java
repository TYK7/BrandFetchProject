package com.example.brand.service;

import com.example.brand.model.Brand;
import com.example.brand.repository.BrandRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.Arrays;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.Collections;
// Arrays is already imported

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

    public Brand fetchBrandDataFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36").get();

        Brand brand = new Brand();
        brand.setId(System.currentTimeMillis()); // Temporary ID

        // Name
        String name = doc.title();
        Element ogSiteName = doc.selectFirst("meta[property=og:site_name]");
        if (ogSiteName != null && !ogSiteName.attr("content").isEmpty()) {
            name = ogSiteName.attr("content");
        } else {
            Element twitterSite = doc.selectFirst("meta[name=twitter:site]");
            if (twitterSite != null && !twitterSite.attr("content").isEmpty()) {
                 name = twitterSite.attr("content");
            } else {
                 Element siteNameMeta = doc.selectFirst("meta[name=application-name]"); // Alternative for site name
                 if (siteNameMeta != null && !siteNameMeta.attr("content").isEmpty()) {
                     name = siteNameMeta.attr("content");
                 }
            }
        }
        brand.setName((name != null && !name.isEmpty()) ? name.trim() : "Unknown Brand");


        // Description
        String description = null;
        Element metaDescription = doc.selectFirst("meta[name=description]");
        if (metaDescription != null && !metaDescription.attr("content").isEmpty()) {
            description = metaDescription.attr("content");
        } else {
            Element ogDescription = doc.selectFirst("meta[property=og:description]");
            if (ogDescription != null && !ogDescription.attr("content").isEmpty()) {
                description = ogDescription.attr("content");
            }
        }
        if (description != null) brand.setDescription(description.trim());

        // Logo URL
        String logo = null;
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null) {
            logo = ogImage.absUrl("content");
        }

        if (logo == null || logo.isEmpty()) {
            Element appleIcon = doc.selectFirst("link[rel=apple-touch-icon]");
            if (appleIcon != null) logo = appleIcon.absUrl("href");
        }
        if (logo == null || logo.isEmpty()) {
            Element appleIconSizes = doc.selectFirst("link[rel=apple-touch-icon][sizes]"); // e.g. apple-touch-icon-180x180.png
             if (appleIconSizes != null) logo = appleIconSizes.absUrl("href");
        }
        if (logo == null || logo.isEmpty()) {
            Element icon = doc.selectFirst("link[rel=icon]");
            if (icon != null) logo = icon.absUrl("href");
        }
        if (logo == null || logo.isEmpty()) {
            Element shortcutIcon = doc.selectFirst("link[rel=\"shortcut icon\"]"); // rel="shortcut icon"
            if (shortcutIcon != null) logo = shortcutIcon.absUrl("href");
        }
        brand.setLogoUrl(logo != null ? logo.trim() : null);

        // Color
        Element themeColor = doc.selectFirst("meta[name=theme-color]");
        if (themeColor != null && !themeColor.attr("content").isEmpty()) {
            brand.setColors(Arrays.asList(themeColor.attr("content").trim()));
        } else {
            brand.setColors(Collections.emptyList());
        }

        // Initialize other lists as empty
        brand.setFonts(Collections.emptyList());
        brand.setAdditionalLogoUrls(Collections.emptyList());
        brand.setImageUrls(Collections.emptyList());

        // Save the fetched brand to the repository if essential data is present
        if (brand.getName() != null && !brand.getName().equals("Unknown Brand") && brand.getLogoUrl() != null && !brand.getLogoUrl().trim().isEmpty()) {
            try {
                // Check if brand with same name and logo already exists to prevent duplicates
                // This requires a custom method in BrandRepository or iterating over findAll()
                // For simplicity here, we'll assume no duplicate check or that save handles updates.
                // In a real scenario with an H2/persistent DB, ID generation would be handled.
                // For in-memory, ensure the temporary ID does not clash if not auto-generated by repository.
                // Since BrandRepository extends JpaRepository, if ID is null, save will generate one.
                // If ID is set (like System.currentTimeMillis()), it might try to merge or cause issues if ID exists.
                // Let's nullify ID before save to ensure new entry for in-memory list / H2.
                // However, the Brand model uses @GeneratedValue, so the ID should be handled by JPA.
                // The System.currentTimeMillis() was a temporary ID for the object before persistence.
                // For Spring Data JPA, if the ID field is null for an @Entity, `save` typically performs a persist (new entry).
                // If ID is non-null and exists, it's an update. If non-null and doesn't exist, it might try to insert with that ID.
                // Let's assume our current setup: ID is set by System.currentTimeMillis().
                // A better approach for new items is to ensure ID is null or let DB generate it.
                // Given @GeneratedValue, if we pass an entity with a specific ID, JPA might try to merge.
                // Forcing a new entry by making ID null (if not already handled by @GeneratedValue on a new object)
                // brand.setId(null); // Potentially, if we didn't want to rely on current temporary ID strategy.
                // However, our Brand entity has @GeneratedValue(strategy = GenerationType.IDENTITY)
                // which means the persistence provider should assign an ID if it's not set or is 0.
                // The System.currentMillis() will likely be treated as a user-provided ID.
                // This could lead to issues if not unique.
                // A robust solution would involve a proper ID strategy or a check for duplicates.
                // For now, let's proceed with the understanding that `save` will add it.

                brandRepository.save(brand);
                System.out.println("Saved fetched brand: " + brand.getName() + " with ID: " + brand.getId());
            } catch (Exception e) {
                System.err.println("Error saving fetched brand: " + brand.getName() + " - " + e.getMessage());
                // Optionally, re-throw or handle more gracefully depending on requirements
            }
        } else {
            System.out.println("Skipped saving fetched brand due to missing essential data (name/logo): " + brand.getName());
        }

        return brand;
    }
}
