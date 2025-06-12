import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; // Import HttpClientModule

import { AppComponent } from './app.component'; // Assuming app.component.ts will be created
import { BrandListComponent } from './brand-list/brand-list.component';
// No need to import BrandService here as it's providedIn: 'root'

@NgModule({
  declarations: [
    AppComponent, // Assuming app.component.ts will be created
    BrandListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule // Add HttpClientModule here
  ],
  providers: [],
  bootstrap: [AppComponent] // Assuming app.component.ts will be created
})
export class AppModule { }
