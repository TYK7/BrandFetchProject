export interface Brand {
  id: number;
  name: string;
  logoUrl: string;
  description?: string;
  colors?: string[];
  fonts?: string[];
  additionalLogoUrls?: string[];
  imageUrls?: string[];
}
