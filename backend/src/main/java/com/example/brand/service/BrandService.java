package com.example.brand.service;

import com.example.brand.model.Brand;
import com.example.brand.repository.BrandRepository;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @PostConstruct
    public void initData() {
        // Populate sample data if repository is empty
        if (brandRepository.count() == 0) {
            brandRepository.save(new Brand(null, "Nike", "https://example.com/nike.png", "Sportswear and shoes",
                    Arrays.asList("#000000", "#FFFFFF"),
                    Arrays.asList("Futura Condensed Extra Black"),
                    Arrays.asList("https://example.com/nike-swoosh.png"),
                    Arrays.asList("https://example.com/nike-shoe-ad.jpg")));

            brandRepository.save(new Brand(null, "Apple", "https://example.com/apple.png", "Electronics and software",
                    Arrays.asList("#A3AAAE", "#000000", "#FFFFFF"),
                    Arrays.asList("San Francisco", "Myriad"),
                    Arrays.asList("https://example.com/apple-logo-white.png"),
                    Arrays.asList("https://example.com/iphone-promo.jpg", "https://example.com/macbook-shot.jpg")));

            brandRepository.save(new Brand(null, "Samsung", "https://example.com/samsung.png", "Consumer electronics", // Null description fixed
                    Arrays.asList("#1428A0", "#FFFFFF", "#000000"),
                    Arrays.asList("SamsungOne", "Helvetica"),
                    Arrays.asList("https://example.com/samsung-logo-blue-bg.png"),
                    Arrays.asList("https://example.com/samsung-galaxy-phone.jpg")));
            System.out.println("Sample brand data initialized.");
        }
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand fetchBrandDataFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36").get();

        Brand brand = new Brand();
        // ID will be set by JPA upon saving a new entity if null.
        // For fetched data, we might assign a temporary one for preview, but for saving, it should be null for auto-generation.
        // brand.setId(System.currentTimeMillis()); // Temporary ID for preview, if needed before save. Nullify before save for new entity.

        // Initialize collections for colors and fonts
        Set<String> foundColors = new HashSet<>();
        Set<String> foundFontFamilies = new HashSet<>();

        // A. Parse Inline Styles
        Elements styleTags = doc.select("style");
        for (Element styleTag : styleTags) {
            String styleContent = styleTag.data();
            extractColorsFromCss(styleContent, foundColors);
            extractFontFamiliesFromCss(styleContent, foundFontFamilies);
        }

        // B. Fetch and Parse Linked CSS Files
        Elements cssLinks = doc.select("link[rel=stylesheet]");
        for (Element link : cssLinks) {
            String cssUrl = link.absUrl("href");
            if (cssUrl != null && !cssUrl.isEmpty()) {
                try {
                    String cssContent = Jsoup.connect(cssUrl)
                                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                            .ignoreContentType(true)
                                            .timeout(5000)
                                            .execute().body();
                    if (cssContent != null) {
                        extractColorsFromCss(cssContent, foundColors);
                        extractFontFamiliesFromCss(cssContent, foundFontFamilies);
                    }
                } catch (IOException e) {
                    System.err.println("Error fetching CSS file: " + cssUrl + " - " + e.getMessage());
                } catch (Exception e) {
                     System.err.println("Non-IO Error fetching CSS file: " + cssUrl + " - " + e.getMessage());
                }
            }
        }

        Element themeColorMeta = doc.selectFirst("meta[name=theme-color]");
        if (themeColorMeta != null && !themeColorMeta.attr("content").isEmpty()) {
            foundColors.add(themeColorMeta.attr("content").trim());
        }
        brand.setColors(new ArrayList<>(foundColors));
        brand.setFonts(new ArrayList<>(foundFontFamilies));

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
                 Element siteNameMeta = doc.selectFirst("meta[name=application-name]");
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

        // Refined Logo URL Selection
        String logoUrl = null;
        Element ogImageElem = doc.selectFirst("meta[property=og:image]");
        if (ogImageElem != null) logoUrl = ogImageElem.absUrl("content");

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element twitterImageElem = doc.selectFirst("meta[name=twitter:image]");
            if (twitterImageElem != null) logoUrl = twitterImageElem.absUrl("content");
        }

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element appleIconSized = doc.selectFirst("link[rel=apple-touch-icon][sizes]");
            if (appleIconSized != null) {
                logoUrl = appleIconSized.absUrl("href");
            } else {
                Element appleIcon = doc.selectFirst("link[rel=apple-touch-icon]");
                if (appleIcon != null) logoUrl = appleIcon.absUrl("href");
            }
        }

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element iconPng = doc.selectFirst("link[rel=icon][type='image/png']");
            if (iconPng != null) logoUrl = iconPng.absUrl("href");
        }

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element iconSvg = doc.selectFirst("link[rel=icon][type='image/svg+xml']");
            if (iconSvg != null) logoUrl = iconSvg.absUrl("href");
        }

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element iconGeneric = doc.selectFirst("link[rel=icon]");
            if (iconGeneric != null) logoUrl = iconGeneric.absUrl("href");
        }

        if (logoUrl == null || logoUrl.isEmpty()) {
            Element shortcutIconElem = doc.selectFirst("link[rel=\"shortcut icon\"]");
            if (shortcutIconElem != null) logoUrl = shortcutIconElem.absUrl("href");
        }
        brand.setLogoUrl(logoUrl != null ? logoUrl.trim() : null);

        // Collect all <img> tag sources for imageUrls
        Set<String> allPageImageUrls = new HashSet<>();
        Elements imgTags = doc.select("img");
        for (Element imgTag : imgTags) {
            String imgSrc = imgTag.absUrl("src");
            if (imgSrc != null && !imgSrc.isEmpty() && (imgSrc.startsWith("http://") || imgSrc.startsWith("https://"))) {
                allPageImageUrls.add(imgSrc);
            }
            String dataSrc = imgTag.absUrl("data-src");
            if (dataSrc != null && !dataSrc.isEmpty() && (dataSrc.startsWith("http://") || dataSrc.startsWith("https://"))) {
                allPageImageUrls.add(dataSrc);
            }
        }
        brand.setImageUrls(new ArrayList<>(allPageImageUrls));

        // Ensure additionalLogoUrls is initialized
        if (brand.getAdditionalLogoUrls() == null) {
            brand.setAdditionalLogoUrls(Collections.emptyList());
        }

        // Before saving, ensure ID is null for auto-generation by JPA
        brand.setId(null);

        if (brand.getName() != null && !brand.getName().equals("Unknown Brand") && brand.getLogoUrl() != null && !brand.getLogoUrl().trim().isEmpty()) {
            try {
                brandRepository.save(brand);
                System.out.println("Saved fetched brand: " + brand.getName() + " with auto-generated ID: " + brand.getId());
            } catch (Exception e) {
                System.err.println("Error saving fetched brand: " + brand.getName() + " - " + e.getMessage());
            }
        } else {
            System.out.println("Skipped saving fetched brand due to missing essential data (name/logo): " + brand.getName());
        }

        return brand;
    }

    private void extractColorsFromCss(String cssContent, Set<String> foundColors) {
        if (cssContent == null || cssContent.isEmpty()) {
            return;
        }
        Pattern hexPattern = Pattern.compile("#([0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3})\\b");
        Matcher hexMatcher = hexPattern.matcher(cssContent);
        while (hexMatcher.find()) {
            foundColors.add(hexMatcher.group(0).toLowerCase());
        }

        Pattern rgbPattern = Pattern.compile("rgba?\\s*\\([^)]+\\)");
        Matcher rgbMatcher = rgbPattern.matcher(cssContent);
        while (rgbMatcher.find()) {
            foundColors.add(rgbMatcher.group(0).replaceAll("\\s+", "").toLowerCase());
        }

        Pattern hslPattern = Pattern.compile("hsla?\\s*\\([^)]+\\)");
        Matcher hslMatcher = hslPattern.matcher(cssContent);
        while (hslMatcher.find()) {
            foundColors.add(hslMatcher.group(0).replaceAll("\\s+", "").toLowerCase());
        }
    }

    private void extractFontFamiliesFromCss(String cssContent, Set<String> foundFontFamilies) {
        if (cssContent == null || cssContent.isEmpty()) {
            return;
        }
        Pattern pattern = Pattern.compile("font-family\\s*:\\s*([^;!}]+)");
        Matcher matcher = pattern.matcher(cssContent);
        while (matcher.find()) {
            String[] fonts = matcher.group(1).split(",");
            for (String font : fonts) {
                String cleanedFont = font.trim().replaceAll("^['\"]|['\"]$", "").trim();
                if (!cleanedFont.isEmpty() && cleanedFont.length() > 1) {
                    foundFontFamilies.add(cleanedFont);
                }
            }
        }
    }
}
