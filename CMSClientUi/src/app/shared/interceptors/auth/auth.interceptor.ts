import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Unauthorized
      if (error.status === 401) { 
        authService.setAuthenticated(false);
        // Reset timer on user activity
        authService.userActivity();
        // User is not authenticated, redirect to login page
        authService.login();
      }
      // Re-throw the error for further handling
      return throwError(() => error); 
    })
  );
};