import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { filter, firstValueFrom, take } from 'rxjs';
import { AUTH_ROUTES } from '../constants';

export const authGuard: CanActivateFn = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router: Router = inject(Router);
  const protectedRoutes: string[] = AUTH_ROUTES;
  const authService = inject(AuthenticationService);

  await firstValueFrom(
    authService.verificationComplete.pipe(
      filter((complete) => complete),
      take(1)
    )
  );

  const authenticated = await firstValueFrom(authService.isAuthenticated);

  return protectedRoutes.includes(state.url) && !authenticated
    ? router.navigate(['/login'])
    : true;
};
