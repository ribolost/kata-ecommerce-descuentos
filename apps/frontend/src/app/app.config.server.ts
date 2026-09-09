import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { appConfig } from './app.config';
import { API_BASE_URL } from './core/config/api-base-url.token';
import { serverRoutes } from './app.routes.server';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    { provide: API_BASE_URL, useValue: process.env['API_BASE_URL'] ?? 'http://localhost:8080/api' },
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
