import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateChildFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { ADMIN_ROUTES } from '../constants';

export const adminGuard: CanActivateChildFn = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router: Router = inject(Router);
  const protectedRoutes: string[] = ADMIN_ROUTES;
  const authService = inject(AuthenticationService);
  const isAdmin = authService.isAdmin();
  return protectedRoutes.includes(state.url) && !isAdmin
    ? router.navigate(['/login'])
    : true;
};
