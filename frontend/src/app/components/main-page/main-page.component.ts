import { Component } from '@angular/core';
import { Company } from '../../models/company';
import { AuthenticationService } from '../../services/authentication.service';
import { CompanyService } from '../../services/company.service';
import { MatIcon } from '@angular/material/icon';
import { RecommendationsComponent } from "../recommendations/recommendations.component";
import { MatFormField, MatInputModule, MatLabel } from "@angular/material/input";
import { MatOption } from "@angular/material/core";
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-main-page',
  imports: [MatIcon, RecommendationsComponent, MatFormField, MatLabel, MatOption, FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    AsyncPipe, RouterLink],
  templateUrl: './main-page.component.html',
  styleUrl: './main-page.component.css'
})
export class MainPageComponent {
  companies: Company[] = [];
  filteredCompanies: Company[] = [];
  searchTerm = '';

  constructor(private companyService: CompanyService) {
    this.companyService.getAll().subscribe((data: Company[]) => {
      this.companies = data;
      this.filteredCompanies = this.companies;
    });
  }

  applyFilter() {
    this.filteredCompanies = this.companies.filter((company) => {
      const matchName = this.searchTerm
        ? company.name.toLocaleLowerCase().includes(this.searchTerm)
        : true;

      const matchCity = this.searchTerm
        ? company.city.toLocaleLowerCase().includes(this.searchTerm)
        : true;
        
      return matchName || matchCity;
    });
  }

  filterByName(event: KeyboardEvent) {
    this.searchTerm = (event.target as HTMLInputElement).value.toLocaleLowerCase();
    this.applyFilter();
  }
}
