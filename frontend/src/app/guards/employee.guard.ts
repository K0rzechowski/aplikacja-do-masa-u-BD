import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateChildFn, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { EMPLOYEE_ROUTES } from '../constants';
import { AuthenticationService } from '../services/authentication.service';
import { filter, firstValueFrom, take } from 'rxjs';

export const employeeGuard: CanActivateFn = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router: Router = inject(Router);
  const protectedRoutes: string[] = EMPLOYEE_ROUTES;
  const authService = inject(AuthenticationService);

  await firstValueFrom(
    authService.verificationComplete.pipe(
      filter((complete) => complete),
      take(1)
    )
  );
  
  const isEmployee = authService.isEmployee();

  const isProtected = protectedRoutes.some(r => state.url.includes(r));

  if (isProtected && !isEmployee) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};