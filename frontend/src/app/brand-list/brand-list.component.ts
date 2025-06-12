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
  isLoading: boolean = true;
  error: string | null = null;

  constructor(private brandService: BrandService) { }

  ngOnInit(): void {
    this.brandService.getBrands().subscribe({
      next: (data) => {
        this.brands = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load brands. Please try again later.';
        console.error(err);
        this.isLoading = false;
      }
    });
  }
}
