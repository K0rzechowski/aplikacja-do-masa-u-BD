import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/auth/user';
import { AuthenticationService } from '../../services/authentication.service';
import { UserRole } from '../../models/auth/user'; 
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-settings',
  imports: [AsyncPipe, RouterLink, RouterOutlet],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {

  currentUser: User | null = null;

  settingsChoices: { label: string; route: string }[] = [
    { label: 'Moje konto', route: 'profile' },
    { label: 'Wizyty', route: 'appointments' }
  ];


  get isAuthenticated$(): Observable<boolean> {
    return this.authService.isAuthenticated;
  }

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  getUserEmail(): string {
    return this.currentUser?.email || '';
  }

  constructor(private authService: AuthenticationService) {
    this.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.settingsChoices = [
        { label: 'Moje konto', route: 'profile' },
        { label: 'Wizyty', route: 'appointments' }
      ];
      if (user?.role == UserRole.ADMIN) {
        this.settingsChoices.push({ label: 'Zarządzaj pracownikami', route: 'employees' });
      } else if(user?.role == UserRole.EMPLOYEE) {
        this.settingsChoices.push({ label: 'Coś dla zwykłego robola', route: 'employee' });
      }
      console.log('Current user:', user);
    });
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
