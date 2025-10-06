import { Component, ChangeDetectionStrategy, LOCALE_ID } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatHint, MatInputModule, MatLabel} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter'
import { CommonModule, registerLocaleData } from '@angular/common';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { LocalDate } from 'local-date';
import { RegisterRequest } from '../../models/auth/registerRequest';
import PasswordValidator from '../../validators/password.validator';
import localePl from '@angular/common/locales/pl';
import 'moment/locale/pl';
import {MAT_DATE_LOCALE} from '@angular/material/core';

registerLocaleData(localePl);

@Component({
  selector: 'app-profile',
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
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {

  formModel: FormGroup;

  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.formModel = this.formBuilder.group({
      name:['', Validators.required],
      surname:['', Validators.required],
      dateOfBirth: [null, Validators.required],
      email: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', Validators.required],
      confirmPassword: ['', [Validators.required, PasswordValidator.matchPassword]]
    });
  }

  register(name:string, surname:string, dateOfBirth: LocalDate, email: string, password: string): void {
    const credentials: RegisterRequest = {name, surname, dateOfBirth: dateOfBirth.toISOString().split('T')[0], email, password} as RegisterRequest;
    this.authService.register(credentials).subscribe(() => {
      console.log("Succesfully registered in");
      this.router.navigate(['/']);
    }) 
  }

  submitRegisterForm(): void {
    if (this.formModel.valid) {
      this.register(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.dateOfBirth,
        this.formModel.value.email,
        this.formModel.value.password      
      );
    } else {
      console.log("Cannot");
    } 
  }

}
