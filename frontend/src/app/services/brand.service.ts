import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Brand } from '../models/brand.model';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apiUrl = 'http://localhost:8080/api/brands';

  constructor(private http: HttpClient) { }

  getBrands(): Observable<Brand[]> {
    return this.http.get<Brand[]>(this.apiUrl);
  }

  // New method
  fetchBrandFromUrl(urlToFetch: string): Observable<Brand> {
    // The backend expects an object like { "url": "some-url" }
    const requestBody = { url: urlToFetch };
    return this.http.post<Brand>(`${this.apiUrl}/fetch-from-url`, requestBody);
  }
}
