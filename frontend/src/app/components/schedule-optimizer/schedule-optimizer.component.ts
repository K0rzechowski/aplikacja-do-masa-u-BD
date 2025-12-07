import { ChangeDetectorRef, Component, inject, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Timeline } from 'vis-timeline/peer';
import { DataSet } from 'vis-data/peer';
import { OptimizedShift } from '../../models/optaplanner/optimized-shifts';
import { ScheduleService } from '../../services/schedule.service';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../services/authentication.service';
import { MatDialog } from '@angular/material/dialog';
import { CreateScheduleComponent } from '../admin/create-schedule/create-schedule.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-schedule-optimizer',
  imports: [CommonModule, ReactiveFormsModule, MatIconModule, MatTabsModule],
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
  currentEmployeeShifts: OptimizedShift[] = [];

  currentWeekStart: string = '';
  currentWeekEnd: string = '';

  @ViewChild('employeeTimelineContainer') employeeTimelineContainer!: ElementRef;
  @ViewChild('locationTimelineContainer') locationTimelineContainer!: ElementRef;

  activeTab: 'employee' | 'location' = 'employee';
  selectedTabIndex: number = 0;

  private employeeTimeline: any;
  private locationTimeline: any;

  private groupsEmployee: any;
  private itemsEmployee: any;
  private groupsLocation: any;
  private itemsLocation: any;

  constructor(
    private scheduleService: ScheduleService,
    private authService: AuthenticationService
  ) {
    this.isAdmin = this.authService.isAdmin();
    this.isEmployee = this.authService.isEmployee();
    this.calculateCurrentWeek();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.initTimelines();

      if (this.isEmployee) {
        this.loadCurrentEmployeeSchedule();
      } else {
        this.loadSchedule(this.currentWeekStart, this.currentWeekEnd);
      }

      if (this.employeeTimeline) {
        this.employeeTimeline.redraw();
      }
      if (this.locationTimeline) {  
        this.locationTimeline.redraw();
      }
    });
  }

  loadCurrentEmployeeSchedule() {
    this.authService.currentUser.subscribe(user => {
      if (user && user.companyId) {
        const startDateTime = new Date(this.currentWeekStart);
        startDateTime.setHours(0, 0, 0, 0);
        const endDateTime = new Date(this.currentWeekEnd);
        endDateTime.setHours(23, 59, 59, 999);

        this.scheduleService.getOptimizedSchedule(user.companyId, startDateTime.toISOString(), endDateTime.toISOString()).subscribe({
          next: (response) => {
            this.currentEmployeeShifts = response.shifts.filter(shift => shift.employeeName === `${user.name} ${user.surname}`);

            this.optimizedShifts = response.shifts;
            this.optimizationScore = response.score;
            this.cdr.markForCheck();

            this.updateTimelineData();
          },
          error: (err) => {
            console.error('Failed to load employee schedule', err);
          }
        });
      }
    });
  }

  loadSchedule(startDate?: string, endDate?: string) {
    const start = startDate || this.currentWeekStart;
    const end = endDate || this.currentWeekEnd;

    this.authService.currentUser.subscribe(user => {
      if (user && user.companyId) {
        const startDateTime = new Date(start);
        startDateTime.setHours(0, 0, 0, 0);
        const endDateTime = new Date(end);
        endDateTime.setHours(23, 59, 59, 999);

        this.scheduleService.getOptimizedSchedule(user.companyId, startDateTime.toISOString(), endDateTime.toISOString()).subscribe({
          next: (response) => {
            this.optimizedShifts = response.shifts;
            this.optimizationScore = response.score;
            this.cdr.markForCheck();
            this.updateTimelineData();
          },
          error: (err) => {
            console.error('Failed to load schedule', err);
          }
        });
      }
    });
  }

  getEmployeeShiftsByDay(): Map<string, OptimizedShift[]> {
    const grouped = new Map<string, OptimizedShift[]>();

    const sortedShifts = [...this.currentEmployeeShifts].sort((a, b) =>
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

  getSortedEmployeeDays(): string[] {
    return Array.from(this.getEmployeeShiftsByDay().keys()).sort();
  }

  getDayName(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('pl-PL', { weekday: 'long' });
  }

  formatTime(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' });
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('pl-PL');
  }

  calculateCurrentWeek() {
    const now = new Date();
    const dayOfWeek = now.getDay();
    const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);

    const start = new Date(now.setDate(diff));
    const end = new Date(now.setDate(start.getDate() + 7));
    this.currentWeekStart = start.toISOString().split('T')[0];
    this.currentWeekEnd = end.toISOString().split('T')[0];
  }

  onRangeChanged(properties: any) {
    const start = new Date(properties.start);
    const end = new Date(properties.end);

    this.currentWeekStart = start.toISOString().split('T')[0];
    this.currentWeekEnd = end.toISOString().split('T')[0];

    this.loadSchedule(start.toISOString(), end.toISOString());
  }

  updateTimelineData() {
    if (!this.itemsEmployee || !this.itemsLocation) {
      return;
    }

    this.itemsEmployee.clear();
    this.itemsLocation.clear();
    this.groupsEmployee.clear();
    this.groupsLocation.clear();

    const employeeSet = new Set<string>();
    const locationSet = new Set<string>();

    this.optimizedShifts.forEach((shift, index) => {
      if (shift.employeeName && !employeeSet.has(shift.employeeName)) {
        employeeSet.add(shift.employeeName);
        this.groupsEmployee.add({ id: shift.employeeName, content: shift.employeeName });
      }
      if (!locationSet.has(shift.location)) {
        locationSet.add(shift.location);
        this.groupsLocation.add({ id: shift.location, content: shift.location });
      }

      const start = new Date(shift.startTime);
      const end = new Date(shift.endTime);
      const content = `<div>${shift.location}</div>`;
      const contentLoc = `<div>${shift.employeeName || 'Nie przypisany'}</div>`;
      const style = `background-color: #7dd3fc; border-color: #7dd3fc; color: #1e293b;`;

      if (shift.employeeName) {
        this.itemsEmployee.add({
          id: shift.id,
          group: shift.employeeName,
          content: content,
          start: start,
          end: end,
          style: style
        });
      }

      this.itemsLocation.add({
        id: shift.id,
        group: shift.location,
        content: contentLoc,
        start: start,
        end: end,
        style: shift.employeeName ? style : 'background-color: #be123c; color: white;'
      });
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

  initTimelines() {
    this.groupsEmployee = new DataSet();
    this.itemsEmployee = new DataSet();
    this.groupsLocation = new DataSet();
    this.itemsLocation = new DataSet();

    const options = {
      stack: false,
      start: this.currentWeekStart,
      end: this.currentWeekEnd,
      editable: false,
      zoomable: true,
      moveable: true,
      selectable: true,
      margin: {
        item: 10,
        axis: 5
      },
      orientation: 'top',
      minHeight: '200px',
    };

    if (this.employeeTimelineContainer) {
      this.employeeTimeline = new Timeline(this.employeeTimelineContainer.nativeElement, this.itemsEmployee, this.groupsEmployee, options);

      this.employeeTimeline.on('rangechanged', (properties: any) => {
        this.onRangeChanged(properties);
      });
    }

    if (this.locationTimelineContainer) {
      this.locationTimeline = new Timeline(this.locationTimelineContainer.nativeElement, this.itemsLocation, this.groupsLocation, options);

      this.locationTimeline.on('rangechanged', (properties: any) => {
        this.onRangeChanged(properties);
      });
    }
  }

  getShiftsByDay(): Map<string, OptimizedShift[]> {
    const grouped = new Map<string, OptimizedShift[]>();

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

  onTabChange(index: number) {
    this.selectedTabIndex = index;
    this.activeTab = index === 0 ? 'employee' : 'location';
    setTimeout(() => {
      if (this.employeeTimeline) {
        this.employeeTimeline.redraw();
      }
      if (this.locationTimeline) {  
        this.locationTimeline.redraw();
      }
    }, 100);
  }
}
