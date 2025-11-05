import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import {  MatHint, MatInputModule, MatLabel } from '@angular/material/input';
import { CompanyService } from '../../services/company.service';
import { AdminService } from '../../services/admin.service';
import { Admin } from '../../models/admin';
import { Company } from '../../models/company';
import { LocalDate } from 'local-date';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';

@Component({
  selector: 'app-company-form',
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pl' },
    provideMomentDateAdapter({
      parse: {
        dateInput: 'DD/MM/YYYY',
      },
      display: {
        dateInput:'DD/MM/YYYY',
        monthYearLabel: 'MMM YYYY',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'MMMM YYYY',
      }
    }, {useUtc: true}),],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './company-form.component.html',
  styleUrl: './company-form.component.css'
})
export class CompanyFormComponent {
  formModel: FormGroup;
  errorMessage: string = '';

  constructor (
    private formBuilder: FormBuilder,
    private companyService: CompanyService,
    private adminService: AdminService,
    private router: Router,
    private alertService: AlertService
  ) {
    this.formModel = this.formBuilder.group({
      companyName:['', Validators.required],
      companyCity:['', Validators.required],
      email: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      name:['', Validators.required],
      surname:['', Validators.required],
      dateOfBirth: ['', Validators.required],
    });
  }

  // createCompany(name: string, city: string): Company {
  //   const upperCaseCity = city.charAt(0).toUpperCase() + city.slice(1)
  //   const companyRequest: Company = {name, city: upperCaseCity} as Company;
  //   this.companyService.createCompany(companyRequest).subscribe({
  //     next: (response) => {
  //       console.log("Succesfully created company");
  //       this.router.navigate(['/']);
  //       this.alertService.success("Poprawnie dodano firmę");
  //       return response;
  //     },
  //     error: (error: Error) => {
  //       this.errorMessage = error.message;
  //       console.log(this.errorMessage);
  //       this.alertService.error(this.errorMessage);
  //     } 
  //   });
  //   return companyRequest;
  // }

  createAdmin(adminRequest: Admin): void {
    this.adminService.createAdmin(adminRequest).subscribe({
      next: (response) => {
        this.router.navigate(['/']);
        this.alertService.success("Poprawnie utworzono konto administratora. Sprawdź swoją skrzynkę pocztową");
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        console.log(this.errorMessage);
        this.alertService.error(this.errorMessage);
      } 
    });
  }

  createCompanyWithAdmin(companyName: string, city: string, email: string, name: string, surname: string, dateOfBirth:  LocalDate) {
      const upperCaseCity = city.charAt(0).toUpperCase() + city.slice(1)
      const companyRequest: Company = {name: companyName, city: upperCaseCity} as Company;

      this.companyService.createCompany(companyRequest).subscribe({
        next: (createdCompany) => {
          this.router.navigate(['/']);
          this.alertService.info("Utworzono firmę. Poczekaj na stworzenie administratora...");

          if (createdCompany?.id) {
          const adminRequest: Admin = {name, surname, email, dateOfBirth: new Date(dateOfBirth).toISOString().split('T')[0], companyId: createdCompany?.id } as Admin;
            this.createAdmin(adminRequest);
          } else {
            console.error("Company ID is undefined");
            this.alertService.error("Nie udało się stworzyć konta administratora");
          }
        },
        error: (error: Error) => {
          this.errorMessage = error.message;
          console.log(this.errorMessage);
          this.alertService.error(this.errorMessage);
        } 
      });
      return companyRequest;
  }

  submitForm(): void {
    if (this.formModel.valid) {
      this.createCompanyWithAdmin(
        this.formModel.value.companyName,
        this.formModel.value.companyCity,
        this.formModel.value.email,     
        this.formModel.value.name,     
        this.formModel.value.surname,     
        this.formModel.value.dateOfBirth  
      );
    } 
  }
}
