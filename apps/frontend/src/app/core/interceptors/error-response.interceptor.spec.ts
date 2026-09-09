import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { ApiError } from '../models/api-error.model';
import { errorResponseInterceptor } from './error-response.interceptor';

describe('errorResponseInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorResponseInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should map a Problem+json error body into a typed ApiError, preserving its fields', () => {
    let capturedError: ApiError | undefined;

    http.get('/api/orders').subscribe({
      next: () => { /* empty */ },
      error: (error: ApiError) => (capturedError = error),
    });

    httpMock.expectOne('/api/orders').flush(
      {
        type: 'https://example.com/problems/insufficient-stock',
        title: 'Insufficient stock',
        status: 409,
        detail: 'Product 1 has only 1 unit available',
        instance: '/api/orders',
        code: 'INSUFFICIENT_STOCK',
      },
      { status: 409, statusText: 'Conflict' },
    );

    expect(capturedError).toEqual({
      type: 'https://example.com/problems/insufficient-stock',
      title: 'Insufficient stock',
      status: 409,
      detail: 'Product 1 has only 1 unit available',
      instance: '/api/orders',
      code: 'INSUFFICIENT_STOCK',
    });
  });

  it('should fall back to a generic ApiError when the response body is not a Problem shape', () => {
    let capturedError: ApiError | undefined;

    http.get('/api/orders').subscribe({
      next: () => {},
      error: (error: ApiError) => (capturedError = error),
    });

    httpMock
      .expectOne('/api/orders')
      .flush('unexpected plain text body', { status: 500, statusText: 'Internal Server Error' });

    expect(capturedError).toEqual({
      type: 'about:blank',
      title: 'Internal Server Error',
      status: 500,
    });
  });
});
