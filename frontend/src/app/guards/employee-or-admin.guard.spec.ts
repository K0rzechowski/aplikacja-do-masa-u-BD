import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { employeeOrAdminGuard } from './employee-or-admin.guard';

describe('employeeOrAdminGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => employeeOrAdminGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
