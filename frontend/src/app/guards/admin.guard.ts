import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { ADMIN_ROUTES } from '../constants';
import { filter, firstValueFrom, take } from 'rxjs';

export const adminGuard: CanActivateFn = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router: Router = inject(Router);
  const protectedRoutes: string[] = ADMIN_ROUTES;
  const authService = inject(AuthenticationService);

  await firstValueFrom(
    authService.verificationComplete.pipe(
      filter((complete) => complete),
      take(1)
    )
  );
  
  const isAdmin = authService.isAdmin();

  const isProtected = protectedRoutes.some(r => state.url.includes(r));

  if (isProtected && !isAdmin) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
