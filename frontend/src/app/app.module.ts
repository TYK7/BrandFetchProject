import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // << Import FormsModule

import { AppComponent } from './app.component';
import { BrandListComponent } from './brand-list/brand-list.component';
// No need to import BrandService here as it's providedIn: 'root'

@NgModule({
  declarations: [
    AppComponent, // Assuming app.component.ts will be created
    BrandListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule // << Add FormsModule here
  ],
  providers: [],
  bootstrap: [AppComponent] // Assuming app.component.ts will be created
})
export class AppModule { }
