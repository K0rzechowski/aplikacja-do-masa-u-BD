import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { NavbarComponent } from "./components/navbar/navbar.component";
import { AlertComponent } from "./components/alert/alert.component";
import { Company } from './models/company';
import { async, Observable } from 'rxjs';
import { CompanyService } from './services/company.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NavbarComponent, AlertComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'app';
  createdCompany: Company | undefined;
  
  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }

  constructor(
    private companyService: CompanyService,
    private router: Router) {
      this.currentCompany$.subscribe(company => {
        if (company && company.id !== undefined) {
          this.router.navigate(['/massages', company.city, company.name]);
        }
      });
  }

  getCreatedCompany(company: Company) {
    this.createdCompany = company;
  }
}
