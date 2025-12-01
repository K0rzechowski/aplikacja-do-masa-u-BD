import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { CredentialsInterceptor } from './interceptors/credentials.interceptor';
import { provideCloudinaryLoader } from '@angular/common';
import { RECAPTCHA_SETTINGS, RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings } from 'ng-recaptcha-2';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptorsFromDi()
    ),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CredentialsInterceptor,
      multi: true
    },
    provideCloudinaryLoader("https://res.cloudinary.com/dmx0gdmxh/"),
      {
        provide: RECAPTCHA_SETTINGS,
        useValue: {
          siteKey: environment.recaptcha.siteKey,
        } as RecaptchaSettings,
      },
  ]
};