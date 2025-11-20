import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatHint, MatInputModule, MatLabel} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {provideNativeDateAdapter} from '@angular/material/core'
import { CommonModule, registerLocaleData } from '@angular/common';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { LocalDate } from 'local-date';
import { RegisterRequest } from '../../models/auth/registerRequest';
import PasswordValidator from '../../validators/password.validator';
import localePl from '@angular/common/locales/pl';
import 'moment/locale/pl';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { AlertService } from '../../services/alert.service';
import { ShowPasswordDirective } from '../../directives/show-password.directive';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

registerLocaleData(localePl);

@Component({
  selector: 'app-register',
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
    }, {useUtc: true}),
    
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel, ShowPasswordDirective, MatIconModule, MatTooltipModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})

export class RegisterComponent {
  
  formModel: FormGroup;
  errorMessage: string = '';

  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router,
    private alertService: AlertService
  ) {
    this.formModel = this.formBuilder.group({
      name:['', Validators.required],
      surname:['', Validators.required],
      dateOfBirth: [null, Validators.required],
      email: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', Validators.required],
      confirmPassword: ['', [Validators.required, PasswordValidator.matchPassword]],
      remember: [true]
    });
  }

  register(name:string, surname:string, dateOfBirth: LocalDate, email: string, password: string): void {
    const credentials: RegisterRequest = {name, surname, dateOfBirth: dateOfBirth.toISOString().split('T')[0], email, password} as RegisterRequest;
    this.authService.register(credentials).subscribe({
      next: (response) => {
        console.log("Succesfully registered");
        this.router.navigate(['/']);
        this.alertService.success("Rejestracja nastąpiła poprawnie");
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        console.log(this.errorMessage);
        this.alertService.error(this.errorMessage);
      } 
    });
  }

  submitRegisterForm(): void {
    if (this.formModel.valid) {
      if (this.formModel.value.remember) {
        localStorage.setItem("email", this.formModel.value.email);
      }

      this.register(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.dateOfBirth,
        this.formModel.value.email,
        this.formModel.value.password      
      );
    } 
  }
}
