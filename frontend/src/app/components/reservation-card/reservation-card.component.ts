import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, ChangeDetectorRef, inject } from '@angular/core';
import { Reservation, ReservationStatus, ReservationStatusLabels } from '../../models/reservation';
import { DatePipe } from '@angular/common';
import { registerLocaleData } from '@angular/common';
import localePl from '@angular/common/locales/pl';
import { StatusColorDirective } from '../../directives/status-color.directive';
import { AlertService } from '../../services/alert.service';
import { ReservationService } from '../../services/reservation.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NoteFormDialogComponent } from '../note-form-dialog/note-form-dialog.component';

registerLocaleData(localePl, 'pl-PL');

@Component({
  selector: 'app-reservation-card',
  imports: [DatePipe, StatusColorDirective, MatDialogModule],
  templateUrl: './reservation-card.component.html',
  styleUrl: './reservation-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationCardComponent {

  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  @Input() reservation: Reservation | undefined;
  @Output() reservationCancelled = new EventEmitter<string>();
  @Output() reservationCompleted = new EventEmitter<string>();
  @Input() isEmployee: boolean = false;
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

  setReservationAsCompleted() {
    if (this.reservation) {
      const reservation = this.reservation;
      this.reservationService.setReservationAsCompleted(reservation.id).subscribe({
        next: (response) => {
          this.alertService.success("Poprawnie zmieniono status rezerwacji");
          reservation.status = ReservationStatus.COMPLETED;
          this.reservationCompleted.emit(reservation.id);
          this.cdr.markForCheck();
        },
        error: (error: Error) => {
          this.alertService.error("Błąd podczas zmiany statusu: " + error.message);
        }
      });
    }
  }

  showNote() {
    if (this.isEmployee) {
      if (this.dialog && this.reservation) {
        const dialogRef = this.dialog.open(NoteFormDialogComponent, {
          width: '350px',
          data: { reservation: this.reservation }
        });
      }
    }
  }
}
