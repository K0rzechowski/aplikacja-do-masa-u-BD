import { Component } from '@angular/core';
import { FormBuilder,FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { AuthenticationRequest } from '../../models/auth/authenticationRequest';
import { CommonModule } from '@angular/common';
import { AlertComponent } from "../alert/alert.component";
import { AlertService } from '../../services/alert.service';
import { MatIconModule } from "@angular/material/icon";
import { ShowPasswordDirective } from "../../directives/show-password.directive";
import {TooltipPosition, MatTooltipModule} from '@angular/material/tooltip';
import { RECAPTCHA_SETTINGS, RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings } from 'ng-recaptcha-2';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule, MatIconModule, ShowPasswordDirective, MatTooltipModule, FormsModule, RecaptchaModule, RecaptchaFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  formModel: FormGroup;
  errorMessage: string = '';
  
  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService, 
    private router: Router,
    private alertService: AlertService
  ) {

    let rememberedEmail = localStorage.getItem("email");

    this.formModel = this.formBuilder.group({
      email: [rememberedEmail, [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', Validators.required],
      remember: [true],
      recaptcha: [null, Validators.required]
    });
  }

  login(email: string, password: string, recaptchaToken: string): void {
    const credentials: AuthenticationRequest = {
      email,
      password,
      recaptchaResponse: recaptchaToken
    } as AuthenticationRequest;
    this.authService.auth(credentials).subscribe({
      next: (response) => {
        this.router.navigate(['/']);
        this.alertService.success("Logowanie nastąpiło poprawnie");
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        this.alertService.error(this.errorMessage);
        this.formModel.patchValue({ recaptcha: null })
      } 
    });
  }

  submitLoginForm(): void {
    if(this.formModel.valid) {
      if (this.formModel.value.remember) {
        localStorage.setItem("email", this.formModel.value.email);
      } else {
        localStorage.removeItem("email");
      }

      this.login(
        this.formModel.value.email,
        this.formModel.value.password,
        this.formModel.value.recaptcha);
    }
  }
}