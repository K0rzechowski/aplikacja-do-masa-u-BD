import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SettingsComponent } from './components/settings/settings.component';
import { ManageEmployeesComponent } from './components/admin/manage-employees/manage-employees.component';
import { CompanyFormComponent } from './components/company-form/company-form.component';
import { ManageMassagesComponent } from './components/admin/manage-massages/manage-massages.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'settings', component: SettingsComponent,
      children: [
        { path: 'profile', component: ProfileComponent },
        { path: 'employees', component: ManageEmployeesComponent },
        { path: 'massages', component: ManageMassagesComponent },
      ]
    },
    { path: 'forCompanies', component: CompanyFormComponent }
];
