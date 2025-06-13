import { Component, OnInit } from '@angular/core';
import { Brand } from '../models/brand.model';
import { BrandService } from '../services/brand.service';

@Component({
  selector: 'app-brand-list',
  templateUrl: './brand-list.component.html',
  styleUrls: ['./brand-list.component.css']
})
export class BrandListComponent implements OnInit {
  brands: Brand[] = [];
  isLoading: boolean = true; // For initial list loading
  error: string | null = null; // For initial list loading error

  urlToFetch: string = '';
  isFetching: boolean = false; // For URL fetching operation
  fetchError: string | null = null; // For URL fetching error
  fetchedBrand: Brand | null = null; // To store the successfully fetched brand

  // showAllColors: boolean = false; // Removed
  showAllFonts: boolean = false;
  showAllImages: boolean = false;

  constructor(private brandService: BrandService) { }

  get websiteColors(): string[] {
    if (this.fetchedBrand && this.fetchedBrand.colors) {
      return this.fetchedBrand.colors.slice(0, 10);
    }
    return [];
  }

  get logoColors(): string[] {
    if (this.fetchedBrand && this.fetchedBrand.colors && this.fetchedBrand.colors.length > 10) {
      return this.fetchedBrand.colors.slice(10);
    }
    // If we want to show *all* colors as "logo colors" if less than 10 were found in total,
    // this logic would need to change. For now, it's strictly "colors after the first 10".
    // To show all if total <=10, it would be:
    // if (this.fetchedBrand && this.fetchedBrand.colors && this.fetchedBrand.colors.length <= 10) {
    //   return this.fetchedBrand.colors;
    // }
    return [];
  }

  ngOnInit(): void {
    this.loadBrands();
  }

  loadBrands(): void {
    this.isLoading = true;
    this.error = null;
    this.brandService.getBrands().subscribe({
      next: (data) => {
        this.brands = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load existing brands. Please try again later.';
        console.error('Error loading brands:', err);
        this.isLoading = false;
      }
    });
  }

  onFetchBrand(): void {
    if (!this.urlToFetch || !this.urlToFetch.trim()) {
      this.fetchError = 'Please enter a URL.';
      return;
    }

    // Basic URL validation (very simple, can be improved)
    if (!this.urlToFetch.startsWith('http://') && !this.urlToFetch.startsWith('https://')) {
        this.fetchError = 'Please enter a valid URL (starting with http:// or https://).';
        return;
    }

    this.isFetching = true;
    this.fetchError = null;
    this.fetchedBrand = null;

    this.brandService.fetchBrandFromUrl(this.urlToFetch).subscribe({
      next: (brand) => {
        this.isFetching = false;
        this.fetchedBrand = brand;
        // this.showAllColors = false; // Removed
        this.showAllFonts = false;
        this.showAllImages = false;
        // console.log('Fetched brand:', brand);
      },
      error: (err) => {
        this.isFetching = false;
        // The backend error body might be a string or an object with a message property
        if (typeof err.error === 'string') {
            this.fetchError = `Error fetching brand: ${err.error}`;
        } else if (err.error && err.error.message) {
            this.fetchError = `Error fetching brand: ${err.error.message}`;
        } else {
            this.fetchError = `Error fetching brand: ${err.message || 'An unknown error occurred. Check console for details.'}`;
        }
        console.error('Error fetching brand from URL:', err);
      }
    });
  }

  addFetchedBrandToList(): void {
    if (this.fetchedBrand) {
      // Check for duplicates by name or logoUrl before adding
      if (!this.brands.some(b => b.name === this.fetchedBrand!.name && b.logoUrl === this.fetchedBrand!.logoUrl)) {
        this.brands.unshift(this.fetchedBrand); // Add to the beginning of the list
      }
      this.fetchedBrand = null; // Clear preview
      this.urlToFetch = ''; // Clear input
      // this.showAllColors = false; // Removed
      this.showAllFonts = false;
      this.showAllImages = false;
    }
  }

  clearFetchedBrand(): void {
    this.fetchedBrand = null;
    this.fetchError = null; // Also clear any fetch error related to this preview
    this.urlToFetch = ''; // Clear input
    // this.showAllColors = false; // Removed
    this.showAllFonts = false;
    this.showAllImages = false;
  }
}
