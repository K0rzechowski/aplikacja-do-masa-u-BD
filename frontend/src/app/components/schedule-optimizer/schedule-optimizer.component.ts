import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OptimizedShift } from '../../models/optaplanner/optimized-shifts';
import { ScheduleService } from '../../services/schedule.service';
import { AlertService } from '../../services/alert.service';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthenticationService } from '../../services/authentication.service';
import { MatDialog } from '@angular/material/dialog';
import { CreateScheduleComponent } from '../admin/create-schedule/create-schedule.component';

@Component({
  selector: 'app-schedule-optimizer',
  imports: [CommonModule, DatePipe, ReactiveFormsModule],
  templateUrl: './schedule-optimizer.component.html',
  styleUrl: './schedule-optimizer.component.css'
})
export class ScheduleOptimizerComponent {

  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  isAdmin: boolean | undefined;
  isEmployee: boolean | undefined;

  optimizedShifts: OptimizedShift[] = [];
  optimizationResult: string = '';
  optimizationScore: string = '';
  isLoading = false;

  currentWeekStart: string = '';
  currentWeekEnd: string = '';

  constructor(
    private scheduleService: ScheduleService,
    private authService: AuthenticationService
  ) {
    this.isAdmin = this.authService.isAdmin();
    this.isEmployee = this.authService.isEmployee();
    this.calculateCurrentWeek();
    this.loadSchedule();
  }

  calculateCurrentWeek() {
    const now = new Date();
    const dayOfWeek = now.getDay(); // 0 (Sunday) - 6 (Saturday)
    const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1); // Adjust when day is Sunday

    const start = new Date(now.setDate(diff));
    const end = new Date(now.setDate(start.getDate() + 6));

    this.currentWeekStart = start.toISOString().split('T')[0];
    this.currentWeekEnd = end.toISOString().split('T')[0];
  }

  loadSchedule() {
    this.authService.currentUser.subscribe(user => {
      if (user && user.companyId) {
        this.isLoading = true;

        const startDateTime = new Date(this.currentWeekStart);
        startDateTime.setHours(0, 0, 0, 0);

        const endDateTime = new Date(this.currentWeekEnd);
        endDateTime.setHours(23, 59, 59, 999);

        this.scheduleService.getOptimizedSchedule(user.companyId, startDateTime.toISOString(), endDateTime.toISOString())
          .subscribe({
            next: (response) => {
              this.optimizedShifts = response.shifts;
              this.optimizationScore = response.score;
              this.isLoading = false;
              this.cdr.markForCheck();
            },
            error: (err) => {
              console.error('Failed to load schedule', err);
              this.isLoading = false;
            }
          });
      }
    });
  }

  openCreateScheduleDialog(enterAnimationDuration: string, exitAnimationDuration: string): void {
    if (this.dialog) {
      const dialogRef = this.dialog.open(CreateScheduleComponent, {
        width: '500px',
        enterAnimationDuration,
        exitAnimationDuration
      });

      dialogRef.afterClosed().subscribe(result => {
        this.loadSchedule();
      })
    }
  }

  getShiftsByDay(): Map<string, OptimizedShift[]> {
    const grouped = new Map<string, OptimizedShift[]>();

    // Sort shifts by start time first
    const sortedShifts = [...this.optimizedShifts].sort((a, b) =>
      new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
    );

    sortedShifts.forEach(shift => {
      const day = shift.startTime.split('T')[0];
      if (!grouped.has(day)) {
        grouped.set(day, []);
      }
      grouped.get(day)!.push(shift);
    });

    return grouped;
  }

  getSortedDays(): string[] {
    return Array.from(this.getShiftsByDay().keys()).sort();
  }

  hasResults(): boolean {
    return this.optimizedShifts.length > 0;
  }
}
