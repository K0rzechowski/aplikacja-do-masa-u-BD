import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { User } from '../../models/auth/user';
import { Observable } from 'rxjs';
import { AuthenticationRequest } from '../../models/auth/authenticationRequest';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, AsyncPipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
}) 
export class NavbarComponent {

  get isAuthenticated$(): Observable<boolean> {
    return this.authService.isAuthenticated;
  }

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  constructor(private authService: AuthenticationService) {}
}
