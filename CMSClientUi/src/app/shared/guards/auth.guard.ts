import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { inject } from '@angular/core';
import { catchError, map } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuth().pipe(
    map(response => {
      if (response.isAuthenticated) {
        // User is authenticated, allow access
        return true; 
      }
       else {
        // User is not authenticated, redirect to login page
        authService.login();
        return false;
      }
    })
  );
}
