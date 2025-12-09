import { Component } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';
import { Alert, AlertService } from '../../services/alert.service';
import { Reservation } from '../../models/reservation';
import { User } from '../../models/auth/user';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../../services/authentication.service';
import { ReservationCardComponent } from "../reservation-card/reservation-card.component";
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-reservation-history',
  imports: [ReservationCardComponent, MatTabsModule],
  templateUrl: './reservation-history.component.html',
  styleUrl: './reservation-history.component.css'
})
export class ReservationHistoryComponent {

  cancelledReservations: Reservation[] = [];
  pastReservations: Reservation[] = [];
  upcomingReservations: Reservation[] = [];

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  get currentUserValue(): User | null {
    let currentUser: User | null = null;
    this.currentUser$.subscribe(user => currentUser = user);
    return currentUser;
  }

  constructor(
    private reservationService: ReservationService,
    private alertService: AlertService,
    private authService: AuthenticationService
  ) {
    this.loadReservations();
  }

  loadCancelledReservations(allReservations: Reservation[]) {
    allReservations.forEach(reservation => {
      if (reservation.status === 'CANCELLED') {
        this.cancelledReservations.push(reservation);
      }
    });
  }

  loadPastReservations(allReservations: Reservation[]) {
    const currentDate = new Date();
    allReservations.forEach(reservation => {
      const reservationDate = new Date(reservation.shift.startTime);
      if ((reservationDate < currentDate || reservation.status === 'COMPLETED') && reservation.status !== 'CANCELLED') {
        this.pastReservations.push(reservation);
      }
    });
  }

  loadUpcomingReservations(allReservations: Reservation[]) {
    const currentDate = new Date();
    allReservations.forEach(reservation => {
      const reservationDate = new Date(reservation.shift.startTime);
      if (reservationDate >= currentDate && reservation.status === 'RESERVED') {
        this.upcomingReservations.push(reservation);
      }
    });
  }

  loadReservations() {
    if (this.currentUserValue?.id) {
      this.reservationService.getReservationsByCustomer(this.currentUserValue?.id!).subscribe(data => {
        this.loadCancelledReservations(data);
        this.loadPastReservations(data);
        this.loadUpcomingReservations(data);
      })
    }
  }

  onReservationCancelled(reservationId: string) {
    const upcomingIndex = this.upcomingReservations.findIndex(r => r.id === reservationId);
    if (upcomingIndex !== -1) {
      const cancelledReservation = this.upcomingReservations.splice(upcomingIndex, 1)[0];
      this.cancelledReservations.push(cancelledReservation);
      return;
    }

    const pastIndex = this.pastReservations.findIndex(r => r.id === reservationId);
    if (pastIndex !== -1) {
      const cancelledReservation = this.pastReservations.splice(pastIndex, 1)[0];
      this.cancelledReservations.push(cancelledReservation);
    }
  }

  onReservationCompleted(reservationId: string) {
    const upcomingIndex = this.upcomingReservations.findIndex(r => r.id === reservationId);
    if (upcomingIndex !== -1) {
      const completedReservation = this.upcomingReservations.splice(upcomingIndex, 1)[0];
      this.pastReservations.push(completedReservation);
      return;
    }
  }
}
