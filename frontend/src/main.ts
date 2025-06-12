import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module'; // Correct path to AppModule

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
