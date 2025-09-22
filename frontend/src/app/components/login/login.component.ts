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
  
  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService, 
    private router: Router  
  ) {
    this.formModel = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', Validators.required]
    });
  }

  login(email: string, password: string): void {
    const credentials: AuthenticationRequest = {email, password} as AuthenticationRequest;
    this.authService.auth(credentials).subscribe(() => {
        console.log("Succesfully logged in");
        this.router.navigate(['/']);
    }) 
  }

  submitLoginForm(): void {
    this.login(this.formModel.value.email, this.formModel.value.password);
  }

}