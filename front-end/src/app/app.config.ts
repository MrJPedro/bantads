import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { pt } from "primelocale/pt.json";
import { providePrimeNG } from 'primeng/config';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
    provideEnvironmentNgxMask(),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    providePrimeNG({
            translation: pt,
            theme: {
                preset: Aura,
                options: {
                  darkModeSelector: 'none'
                }
              }
        })
  ]
};
