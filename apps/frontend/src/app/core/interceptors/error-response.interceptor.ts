import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../models/api-error.model';

function toApiError(response: HttpErrorResponse): ApiError {
  const problem = response.error;

  if (problem && typeof problem === 'object' && 'title' in problem && 'status' in problem) {
    return {
      type: problem.type ?? 'about:blank',
      title: problem.title,
      status: problem.status,
      detail: problem.detail,
      instance: problem.instance,
      code: problem.code,
    };
  }

  return {
    type: 'about:blank',
    title: response.statusText || 'Error inesperado',
    status: response.status,
  };
}

export const errorResponseInterceptor: HttpInterceptorFn = (request, next) =>
  next(request).pipe(
    catchError((response: HttpErrorResponse) => throwError(() => toApiError(response))),
  );
