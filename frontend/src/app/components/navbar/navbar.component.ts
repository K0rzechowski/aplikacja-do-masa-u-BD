import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { User } from '../../models/auth/user';
import { Observable } from 'rxjs';
import { AuthenticationRequest } from '../../models/auth/authenticationRequest';
import { AsyncPipe } from '@angular/common';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/company';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, AsyncPipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
}) 
export class NavbarComponent {
  companies: Company[] = [];

  get isAuthenticated$(): Observable<boolean> {
    return this.authService.isAuthenticated;
  }

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }

  constructor(
    private authService: AuthenticationService,
    private companyService: CompanyService,
    private router: Router
  ) {
    this.companyService.getAll().subscribe((data: Company[]) => {
      this.companies = data;
    });
  }

  setCurrentCompany(company: Company) {
    this.companyService.setCurrentCompany(company);
    this.router.navigate(['/massages', company.city, company.name]);
  }
}
