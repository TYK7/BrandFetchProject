package com.example.brand.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Objects;

@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String logoUrl;
    private String description;

    @ElementCollection
    private List<String> colors;

    @ElementCollection
    private List<String> fonts;

    @ElementCollection
    private List<String> additionalLogoUrls;

    @ElementCollection
    private List<String> imageUrls;

    public Brand() {
    }

    public Brand(Long id, String name, String logoUrl, String description, List<String> colors, List<String> fonts, List<String> additionalLogoUrls, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.description = description;
        this.colors = colors;
        this.fonts = fonts;
        this.additionalLogoUrls = additionalLogoUrls;
        this.imageUrls = imageUrls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getFonts() {
        return fonts;
    }

    public void setFonts(List<String> fonts) {
        this.fonts = fonts;
    }

    public List<String> getAdditionalLogoUrls() {
        return additionalLogoUrls;
    }

    public void setAdditionalLogoUrls(List<String> additionalLogoUrls) {
        this.additionalLogoUrls = additionalLogoUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return Objects.equals(id, brand.id) && Objects.equals(name, brand.name) && Objects.equals(logoUrl, brand.logoUrl) && Objects.equals(description, brand.description) && Objects.equals(colors, brand.colors) && Objects.equals(fonts, brand.fonts) && Objects.equals(additionalLogoUrls, brand.additionalLogoUrls) && Objects.equals(imageUrls, brand.imageUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, logoUrl, description, colors, fonts, additionalLogoUrls, imageUrls);
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", description='" + description + '\'' +
                ", colors=" + colors +
                ", fonts=" + fonts +
                ", additionalLogoUrls=" + additionalLogoUrls +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
