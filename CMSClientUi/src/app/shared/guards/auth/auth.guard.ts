import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { inject } from '@angular/core';
import { catchError, map } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated().pipe(
    map(isAuthenticated => {
      if (!isAuthenticated) {
        // User is not authenticated, redirect to login page
        authService.login();
        return false;
      }

      if (!authService.getUserInfo()['is_profile_complete']) {
        // User is authenticated but has an incomplete customer profile, redirect to complete profile dialog
        router.navigateByUrl('complete-profile')
        return false;
      }

      // User is authenticated and has a complete customer profile, allow access
      return true; 
    })
  );
}
