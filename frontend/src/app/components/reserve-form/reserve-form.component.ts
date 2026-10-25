import { ReservationService } from '../../services/reservation.service';
import { AlertService } from '../../services/alert.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AsyncPipe, DatePipe, NgStyle } from '@angular/common';
import { MatDialogTitle } from '@angular/material/dialog';
import { A11yModule } from '@angular/cdk/a11y';
import { MatSelectModule } from '@angular/material/select';
import { Company } from '../../models/company';
import { CompanyService } from '../../services/company.service';
import { Massage } from '../../models/massage';
import { MassageService } from '../../services/massage.service';
import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee.service';
import { AuthenticationService } from '../../services/authentication.service';
import { Observable } from 'rxjs';
import { User } from '../../models/auth/user';
import { Shift } from '../../models/shift';
import { BranchOffice } from '../../models/branch-office';
import { BranchOfficeService } from '../../services/branch-office.service';
import { ScheduleService } from '../../services/schedule.service';
import { Reservation, ReservationStatus } from '../../models/reservation';

@Component({
  selector: 'app-reserve-form',
  imports: [ReactiveFormsModule, MatExpansionModule, MatIconModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatDatepickerModule, NgStyle, MatButtonModule, ReactiveFormsModule, MatFormFieldModule, MatOptionModule, A11yModule, MatSelectModule, AsyncPipe, MatPaginatorModule, DatePipe],
  templateUrl: './reserve-form.component.html',
  styleUrl: './reserve-form.component.css',
  providers: [provideNativeDateAdapter()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReserveFormComponent {

  private cdr = inject(ChangeDetectorRef);

  formModel: FormGroup;
  step = signal(0);
  allCompanies: Company[] = [];
  allBranchOffices: BranchOffice[] = [];
  allMassages: Massage[] = [];
  allEmployees: Employee[] = [];
  shiftTimes: string[] = [];

  pageSize: number = 10;
  pageIndex: number = 0;
  totalShifts: number = 0;
  paginatedShiftTimes: string[] = [];

  currentWeekShifts: Shift[] = [];
  nextWeekShifts: Shift[] = [];
  showNextWeek: boolean = false;
  allShifts: Shift[] = [];

  shiftMap: Map<string, Shift> = new Map();

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  get currentUserValue(): User | null {
    let currentUser: User | null = null;
    this.currentUser$.subscribe(user => currentUser = user);
    return currentUser;
  }

  // get currentCompany$(): Observable<Company | null> {
  //   return this.companyService.currentCompany;
  // }

  // get currentCompanyValue(): Company | null {
  //   let currentCompany: Company | null = null;
  //   this.currentCompany$.subscribe(company => currentCompany = company);
  //   return currentCompany;
  // }

  constructor(
    private reservationService: ReservationService,
    private alertService: AlertService,
    private formBuilder: FormBuilder,
    private companyService: CompanyService,
    private massageService: MassageService,
    private employeeService: EmployeeService,
    private authService: AuthenticationService,
    private scheduleService: ScheduleService,
    private branchOfficeService: BranchOfficeService
  ) {
    this.formModel = this.formBuilder.group({
      company: [null, Validators.required],
      branchOffice: [null, Validators.required],
      massage: [null, Validators.required],
      employee: [null, Validators.required],
      date: [null, Validators.required]
    });

    this.companyService.getAll().subscribe(data =>
      this.allCompanies = data
    );

    this.getCompanyChange();
    this.getBranchOfficeChange();
    this.getMassageChange();
    this.getEmployeeChange();
  }

  getCompanyChange() {
    this.formModel.get('company')?.valueChanges.subscribe(company => {
      this.formModel.patchValue({
        branchOffice: null,
        massage: null,
        employee: null
      }, { emitEvent: false });
      this.allBranchOffices = [];
      this.allMassages = [];
      this.allEmployees = [];
      this.clearShifts();

      if (company && company.id) {
        this.branchOfficeService.getBranchOfficesByCompany(company.id).subscribe(data => {
          this.allBranchOffices = data;
          if (data.length === 1) {
            this.formModel.patchValue({ branchOffice: data[0] });
          }
          this.cdr.markForCheck();
        });

        this.massageService.getMassagesByCompany(company.id).subscribe(data => {
          this.allMassages = data;
          this.cdr.markForCheck();
        });
      }
    });
  }

  getBranchOfficeChange() {
    this.formModel.get('branchOffice')?.valueChanges.subscribe(() => {
      this.formModel.patchValue({ employee: null }, { emitEvent: false });
      this.clearShifts();
      this.loadEmployees();
    });
  }

  loadEmployees() {
    const selectedCompany = this.formModel.get('company')?.value;
    const selectedBranch = this.formModel.get('branchOffice')?.value;
    const massage = this.formModel.get('massage')?.value;

    if (massage?.id && selectedCompany?.id && selectedBranch?.id) {
      this.employeeService.getEmployeesByCompany(selectedCompany.id).subscribe(employees => {
        this.allEmployees = employees.filter(employee =>
          String(employee.branchOfficeId) === String(selectedBranch.id) &&
          employee.massages?.some(m => m.id === massage.id)
        );
        this.cdr.markForCheck();
      });
    } else {
      this.allEmployees = [];
    }
  }

  getMassageChange() {
    this.formModel.get('massage')?.valueChanges.subscribe(() => {
      this.formModel.patchValue({ employee: null }, { emitEvent: false });
      this.clearShifts();
      this.loadEmployees();
    });
  }

  getEmployeeChange() {
    this.formModel.get('employee')?.valueChanges.subscribe(employee => {
      const selectedCompany = this.formModel.get('company')?.value;
      const selectedBranch = this.formModel.get('branchOffice')?.value;
      const selectedMassage = this.formModel.get('massage')?.value;

      this.clearShifts();

      if (employee?.id && selectedCompany?.id && selectedBranch?.id && selectedMassage?.id) {
        this.scheduleService.getWeeklyShifts(selectedBranch.id, selectedCompany.id).subscribe(result => {
          const isEmployeeShift = (shift: Shift) => String(shift.assignedEmployeeId) === String(employee.id);

          this.currentWeekShifts = result.currentWeekShifts.filter(shift => {
            const currentTime = new Date(Date.now()).getTime();
            return new Date(shift.startTime).getTime() > currentTime && isEmployeeShift(shift);
          });

          this.nextWeekShifts = result.nextWeekShifts.filter(isEmployeeShift);

          this.showNextWeek = this.nextWeekShifts.length > 0;

          this.allShifts = [...this.currentWeekShifts, ...this.nextWeekShifts]
            .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());
          this.totalShifts = this.allShifts.length;

          this.shiftMap.clear();
          this.shiftTimes = this.allShifts.map(shift => {
            const displayString = this.formatDateTime(shift);
            this.shiftMap.set(displayString, shift);
            return displayString;
          });

          this.updatePaginatedShifts();
          this.cdr.markForCheck();
        });
      }
    });
  }

  clearShifts() {
    this.formModel.patchValue({ date: null }, { emitEvent: false });
    this.shiftTimes = [];
    this.paginatedShiftTimes = [];
    this.currentWeekShifts = [];
    this.nextWeekShifts = [];
    this.showNextWeek = false;
    this.allShifts = [];
    this.totalShifts = 0;
    this.pageIndex = 0;
    this.shiftMap.clear();
  }

  setStep(index: number) {
    this.step.set(index);
  }

  nextStep() {
    this.step.update(i => i + 1);
  }

  prevStep() {
    this.step.update(i => i - 1);
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePaginatedShifts();
  }

  updatePaginatedShifts() {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedShiftTimes = this.shiftTimes.slice(startIndex, endIndex);
  }

  formatDateTime(shift: Shift): string {
    const startTime = new Date(shift.startTime);
    const endTime = new Date(shift.endTime);
    
    const formattedDate = new DatePipe('pl-PL').transform(startTime, 'fullDate');
    const formattedStartTime = new DatePipe('pl-PL').transform(startTime, 'shortTime');
    const formattedEndTime = new DatePipe('pl-PL').transform(endTime, 'shortTime');
    return `${formattedDate} ${formattedStartTime} - ${formattedEndTime}`;
  }

  selectTimeSlot(time: string) {
    this.formModel.patchValue({ date: time });
  }

  getSelectedShift(): Shift | undefined {
    const selectedTimeString = this.formModel.get('date')?.value;
    return selectedTimeString ? this.shiftMap.get(selectedTimeString) : undefined;
  }

  submitForm() {
    const selectedShift = this.getSelectedShift();
    const selectedMassage = this.formModel.get('massage')?.value;

    if (selectedShift && selectedMassage) {
      const reservation: Partial<Reservation> = {
        customer: this.currentUserValue!,
        status: ReservationStatus.RESERVED,
        shift: { id: selectedShift.id, startTime: selectedShift.startTime, endTime: selectedShift.endTime },
        massageForCompany: selectedMassage
      };
      
      this.reservationService.createReservation(reservation).subscribe({
        next: (response) => {
          this.alertService.success('Rezerwacja utworzona pomyślnie');
          this.formModel.reset();
          this.setStep(0);
          this.cdr.markForCheck();
        },
        error: (error: Error) => {
          this.alertService.error(error.message);
        }
      });
    } else {
      this.alertService.error('Wypełnij wszystkie pola formularza');
    }
  }
}
