import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatHint, MatInputModule, MatLabel} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {provideNativeDateAdapter} from '@angular/material/core'
import { CommonModule } from '@angular/common';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { LocalDate } from 'local-date';
import { RegisterRequest } from '../../models/auth/registerRequest';
import PasswordValidator from '../../validators/password.validator';

@Component({
  selector: 'app-register',
  providers: [provideNativeDateAdapter()],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})

export class RegisterComponent {
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
