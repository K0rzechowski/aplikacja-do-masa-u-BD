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
import { PriceListComponent } from './components/price-list/price-list.component';

export const routes: Routes = [
  { path: '', component:MainPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'settings', 
    loadComponent: () => import('./components/settings/settings.component').then(s => SettingsComponent),
    canActivate: [authGuard, adminGuard],
    children: [
      { path: 'profile', component: ProfileComponent },
      { path: 'employees', component: ManageEmployeesComponent },
      { path: 'massages', component: ManageMassagesComponent },
    ]
  },
  { path: 'forCompanies', component: CompanyFormComponent },
  { path: 'recommendations', component: RecommendationsComponent },
  { path: ':city/:name/massages', component: MassagesComponent },
  { path: ':city/:name/price', component: PriceListComponent }

];
