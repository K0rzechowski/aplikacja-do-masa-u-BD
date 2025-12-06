import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { filter, firstValueFrom, take } from 'rxjs';

export const employeeOrAdminGuard: CanActivateFn = async (
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
) => {
    const router: Router = inject(Router);
    const authService = inject(AuthenticationService);

    await firstValueFrom(
        authService.verificationComplete.pipe(
            filter((complete) => complete),
            take(1)
        )
    );

    const isEmployee = authService.isEmployee();
    const isAdmin = authService.isAdmin();

    if (!isEmployee && !isAdmin) {
        router.navigate(['/login']);
        return false;
    }

    return true;
};
