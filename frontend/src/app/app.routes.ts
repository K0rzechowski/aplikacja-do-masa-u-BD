import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SettingsComponent } from './components/settings/settings.component';
import { ManageEmployeesComponent } from './components/admin/manage-employees/manage-employees.component';
import { CompanyFormComponent } from './components/company-form/company-form.component';
import { ManageMassagesComponent } from './components/admin/manage-massages/manage-massages.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { RecommendationsComponent } from './components/recommendations/recommendations.component';
import { MassagesComponent } from './components/massages/massages.component';
import { authGuard } from './guards/auth-guard.guard';
import { adminGuard } from './guards/admin.guard';
import { employeeGuard } from './guards/employee.guard';
import { employeeOrAdminGuard } from './guards/employee-or-admin.guard';
import { PriceListComponent } from './components/price-list/price-list.component';
import { CompanyFormsComponent } from './components/admin/company-forms/company-forms.component';
import { AvailabilityCalendarComponent } from './components/employee/availability-calendar/availability-calendar.component';
import { ScheduleOptimizerComponent } from './components/schedule-optimizer/schedule-optimizer.component';
import { EmployeesComponent } from './components/employees/employees.component';
import { ReserveFormComponent } from './components/reserve-form/reserve-form.component';
import { ReservationHistoryComponent } from './components/reservation-history/reservation-history.component';
import { ManageReservationsComponent } from './components/employee/manage-reservations/manage-reservations.component';
import { StatisticsComponent } from './components/admin/statistics/statistics.component';
import { RegulaminComponent } from './components/regulamin/regulamin.component';
import { PolitykaPrywatnosciComponent } from './components/polityka-prywatnosci/polityka-prywatnosci.component';

export const routes: Routes = [
  { path: '', component: MainPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'settings',
    loadComponent: () => import('./components/settings/settings.component').then(s => SettingsComponent),
    canActivate: [authGuard],
    children: [
      { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
      { path: 'reservations', component: ReservationHistoryComponent, canActivate: [authGuard] },
      { path: 'manageReservations', component: ManageReservationsComponent, canActivate: [employeeGuard] },
      { path: 'availabilities', component: AvailabilityCalendarComponent, canActivate: [employeeGuard] },
      { path: 'employees', component: ManageEmployeesComponent, canActivate: [adminGuard] },
      { path: 'massages', component: ManageMassagesComponent, canActivate: [adminGuard] },
      { path: 'companySettings', component: CompanyFormsComponent, canActivate: [adminGuard] },
      { path: 'statistics', component: StatisticsComponent, canActivate: [employeeOrAdminGuard] },
      { path: 'schedule', component: ScheduleOptimizerComponent, canActivate: [employeeOrAdminGuard] },
    ]
  },
  { path: 'forCompanies', component: CompanyFormComponent },
  { path: 'recommendations', component: RecommendationsComponent },
  { path: ':city/:name/massages', component: MassagesComponent },
  { path: ':city/:name/employees', component: EmployeesComponent },
  { path: ':city/:name/price', component: PriceListComponent },
  { path: 'reservation', component: ReserveFormComponent, canActivate: [authGuard] },
  { path: 'regulamin', component: RegulaminComponent },
  { path: 'politykaPrywatnosci', component: PolitykaPrywatnosciComponent },
];
