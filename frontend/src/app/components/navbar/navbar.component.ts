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

  ngOnInit() {
    this.currentUser$.subscribe(user => console.log(user?.name));
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logged out successfully');
      },
      error: (err) => {
        console.error('Logout error:', err);
      }
    });
  }
}
