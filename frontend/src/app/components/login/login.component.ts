import { Component } from '@angular/core';
import { FormBuilder,FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { AuthenticationRequest } from '../../models/auth/authenticationRequest';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  formModel: FormGroup;
  errorMessage: string = '';
  
  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService, 
    private router: Router  
  ) {
    let rememberedEmail = localStorage.getItem("email");
    this.formModel = this.formBuilder.group({
      email: [rememberedEmail, [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', Validators.required],
      remember: [false]
    });
  }

  login(email: string, password: string): void {
    const credentials: AuthenticationRequest = {email, password} as AuthenticationRequest;
    this.authService.auth(credentials).subscribe({
      next: (response) => {
        console.log("Succesfully logged in");
        this.router.navigate(['/']);
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        console.log(error.message);
      } 
    });
  }

  submitLoginForm(): void {
    if(this.formModel.valid) {
      if (this.formModel.value.remember) {
        localStorage.setItem("email", this.formModel.value.email);
      }

      this.login(this.formModel.value.email, this.formModel.value.password);
    }
  }
}