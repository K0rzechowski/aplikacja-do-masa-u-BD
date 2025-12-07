import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/auth/user';
import { AuthenticationService } from '../../services/authentication.service';
import { UserRole } from '../../models/auth/user';
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AlertService } from '../../services/alert.service';

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

  constructor (
    private authService: AuthenticationService,
    private alertService: AlertService
  ) {
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      this.settingsChoices = [
        { label: 'Moje konto', route: 'profile' },
        { label: 'Wizyty', route: 'reservations' }
      ];
      if (authService.isAdmin() == true) {
        this.settingsChoices.push({ label: 'Zarządzaj pracownikami', route: 'employees' });
        this.settingsChoices.push({ label: 'Zarządzaj masażami', route: 'massages' });
        this.settingsChoices.push({ label: 'Zarządzaj swoją firmą', route: 'companySettings' });
        this.settingsChoices.push({ label: 'Zarządzaj grafikami', route: 'schedule' });
        this.settingsChoices.push({ label: 'Statystyki', route: 'statistics' });
      } else if(authService.isEmployee() == true) {
        this.settingsChoices.push({ label: 'Zarządzaj dostępnością na dzień', route: 'availabilities' });
        this.settingsChoices.push({ label: 'Grafiki', route: 'schedule' });
        this.settingsChoices.push({ label: 'Zarządzaj rezerwacjami klientów', route: 'manageReservations' });
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.alertService.success("Wylogowanie powiodło się")
      },
      error: (err) => {
        this.alertService.error(err);
      }
    });
  }
}
