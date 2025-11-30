import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import {MatCardModule} from '@angular/material/card';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { CommonModule } from '@angular/common';
import {ChangeDetectionStrategy, model} from '@angular/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { AvailabilityFormComponent } from '../availability-form/availability-form.component';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { User } from '../../../models/auth/user';
import { AuthenticationService } from '../../../services/authentication.service';
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-availability-calendar',
  templateUrl: './availability-calendar.component.html',
  providers: [provideNativeDateAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: 'pl' },
    provideMomentDateAdapter({
      parse: {
        dateInput: 'DD/MM/YYYY',
      },
      display: {
        dateInput:'DD/MM/YYYY',
        monthYearLabel: 'MMM YYYY',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'MMMM YYYY',
      }
    }, {useUtc: true}),
  ],
  imports: [MatCardModule, MatDatepickerModule, CommonModule, MatFormFieldModule, MatFormFieldModule, MatIcon],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './availability-calendar.component.css'
})
export class AvailabilityCalendarComponent {

  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);
  selected = model<Date | null>(null);
  employeeId: string = '';

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  constructor(private authService: AuthenticationService) {
    this.currentUser$.subscribe(u => {
      if (u !== null) {
        this.employeeId = u.id;
      }
    });
  }

  onDaySelected(date: Date) {
    this.openAvailabilityDialog('0ms', '0ms', date);
  }

  openAvailabilityDialog(enterAnimationDuration: string, exitAnimationDuration: string, day: Date) {
    if (this.dialog && this.employeeId) {
      const dialogRef = this.dialog.open(AvailabilityFormComponent, {
        width: '350px',
        enterAnimationDuration,
        exitAnimationDuration,
        data: { day: day, employeeId: this.employeeId }
      });
    }
  }
}
