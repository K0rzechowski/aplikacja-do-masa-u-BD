import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { DatePipe, registerLocaleData } from '@angular/common';
import localePl from '@angular/common/locales/pl';
import { MatIconModule } from '@angular/material/icon';
import moment from 'moment';
import { Shift } from '../../../models/shift';
import { ScheduleService } from '../../../services/schedule.service';
import { AuthenticationService } from '../../../services/authentication.service';
import { EmployeeService } from '../../../services/employee.service';
import { AlertService } from '../../../services/alert.service';

registerLocaleData(localePl, 'pl-PL');

@Component({
  selector: 'app-shift-signup',
  imports: [DatePipe, MatIconModule],
  templateUrl: './shift-signup.component.html',
  styleUrl: './shift-signup.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShiftSignupComponent {

  private cdr = inject(ChangeDetectorRef);

  employeeId: string | undefined;
  branchOfficeId: string | undefined;
  branchOfficeName: string | undefined;

  weekStart: Date = this.getMonday(new Date());
  shifts: Shift[] = [];
  shiftsByDay: { day: Date, shifts: Shift[] }[] = [];
  pendingShiftId: string | null = null;
  isLoading: boolean = false;

  constructor(
    private scheduleService: ScheduleService,
    private authService: AuthenticationService,
    private employeeService: EmployeeService,
    private alertService: AlertService
  ) {
    this.authService.currentUser.subscribe(user => {
      if (user?.id && user.id !== this.employeeId) {
        this.employeeId = user.id;
        this.employeeService.getEmployeeById(user.id).subscribe(employee => {
          this.branchOfficeId = employee.branchOfficeId;
          this.branchOfficeName = employee.branchOfficeName;
          this.loadShifts();
        });
      }
    });
  }

  get weekEnd(): Date {
    return moment(this.weekStart).add(6, 'days').toDate();
  }

  getMonday(date: Date): Date {
    return moment(date).isoWeekday(1).startOf('day').toDate();
  }

  previousWeek() {
    this.weekStart = moment(this.weekStart).subtract(1, 'week').toDate();
    this.loadShifts();
  }

  nextWeek() {
    this.weekStart = moment(this.weekStart).add(1, 'week').toDate();
    this.loadShifts();
  }

  loadShifts() {
    if (!this.branchOfficeId) {
      return;
    }

    const start = moment(this.weekStart).format('YYYY-MM-DDTHH:mm:ss');
    const end = moment(this.weekStart).add(1, 'week').subtract(1, 'second').format('YYYY-MM-DDTHH:mm:ss');

    this.isLoading = true;
    this.scheduleService.getShiftsByBranchOffice(this.branchOfficeId, start, end).subscribe({
      next: (data) => {
        this.shifts = data;
        this.groupShifts();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.shifts = [];
        this.groupShifts();
        this.isLoading = false;
        this.alertService.error('Nie udało się pobrać zmian');
        this.cdr.markForCheck();
      }
    });
  }

  groupShifts() {
    const grouped = new Map<string, Shift[]>();
    [...this.shifts]
      .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
        || a.roomNumber.localeCompare(b.roomNumber))
      .forEach(shift => {
        const day = shift.startTime.split('T')[0];
        if (!grouped.has(day)) {
          grouped.set(day, []);
        }
        grouped.get(day)!.push(shift);
      });

    this.shiftsByDay = Array.from(grouped.entries()).map(([day, shifts]) => ({ day: new Date(day), shifts }));
  }

  isMine(shift: Shift): boolean {
    return !!shift.assignedEmployeeId && String(shift.assignedEmployeeId) === String(this.employeeId);
  }

  isPast(shift: Shift): boolean {
    return new Date(shift.startTime).getTime() < Date.now();
  }

  myShiftsCount(): number {
    return this.shifts.filter(shift => this.isMine(shift)).length;
  }

  signUp(shift: Shift) {
    this.pendingShiftId = shift.id;
    this.scheduleService.signUp(shift.id).subscribe({
      next: (updated) => this.replaceShift(updated, 'Zapisano na zmianę'),
      error: (error: Error) => this.handleError(error)
    });
  }

  signOff(shift: Shift) {
    this.pendingShiftId = shift.id;
    this.scheduleService.signOff(shift.id).subscribe({
      next: (updated) => this.replaceShift(updated, 'Wypisano ze zmiany'),
      error: (error: Error) => this.handleError(error)
    });
  }

  private replaceShift(updated: Shift, message: string) {
    this.shifts = this.shifts.map(shift => shift.id === updated.id ? updated : shift);
    this.groupShifts();
    this.pendingShiftId = null;
    this.alertService.success(message);
    this.cdr.markForCheck();
  }

  private handleError(error: Error) {
    this.pendingShiftId = null;
    this.alertService.error(error.message);
    this.loadShifts();
  }
}
