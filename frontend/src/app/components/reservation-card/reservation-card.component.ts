import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Reservation, ReservationStatus, ReservationStatusLabels } from '../../models/reservation';
import { DatePipe } from '@angular/common';
import { registerLocaleData } from '@angular/common';
import localePl from '@angular/common/locales/pl';
import { StatusColorDirective } from '../../directives/status-color.directive';
import { AlertService } from '../../services/alert.service';
import { ReservationService } from '../../services/reservation.service';

registerLocaleData(localePl, 'pl-PL');

@Component({
  selector: 'app-reservation-card',
  imports: [DatePipe, StatusColorDirective],
  templateUrl: './reservation-card.component.html',
  styleUrl: './reservation-card.component.css'
})
export class ReservationCardComponent {
  @Input() reservation: Reservation | undefined;
  @Output() reservationCancelled = new EventEmitter<string>();
  reservationData: any;

  status = Object.values(ReservationStatus);
  ReservationStatusLabels = ReservationStatusLabels;

  constructor(
    private alertService: AlertService,
    private reservationService: ReservationService
  ) { }

  ngOnInit() {
    this.reservationData = JSON.parse(JSON.stringify(this.reservation));
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return new DatePipe('pl-PL').transform(date, 'MMMM, y') || '';
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return new DatePipe('pl-PL').transform(date, 'shortTime') || '';
  }

  cancelReservation() {
    if (this.reservation) {
      const reservation = this.reservation;
      const reservationTime = new Date(reservation.shift.startTime).getTime();
      const currentDateTime = new Date(Date.now());
      if ((reservationTime - currentDateTime.getTime()) < 24 * 60 * 60 * 1000) {
        this.alertService.error('Nie można anulować rezerwacji mniej niż 24h przed rozpoczęciem rezerwacji.');
      } else {
        this.reservationService.cancelReservation(reservation.id).subscribe({
          next: (response) => {
            this.alertService.success("Poprawnie anulowano rezerwację");
            reservation.status = ReservationStatus.CANCELLED;
            this.reservationCancelled.emit(reservation.id);
          },
          error: (error: Error) => {
            this.alertService.error("Błąd podczas anulowania: " + error.message);
          }
        });
      }
    }
  }
}
