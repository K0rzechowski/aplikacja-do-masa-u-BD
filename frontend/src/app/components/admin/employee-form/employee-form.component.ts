import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, inject, signal } from '@angular/core';
import { Employee } from '../../../models/employee';
import { EmployeeService } from '../../../services/employee.service';
import { Form, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, registerLocaleData } from '@angular/common';
import { LocalDate } from 'local-date';
import PhoneNumberValidator from '../../../validators/phone-number.validator';
import {provideNativeDateAdapter} from '@angular/material/core'
import { RegisterRequest } from '../../../models/auth/registerRequest';
import PasswordValidator from '../../../validators/password.validator';
import localePl from '@angular/common/locales/pl';
import 'moment/locale/pl';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatHint, MatInputModule, MatLabel } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MatButtonModule} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import {MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import { AlertService } from '../../../services/alert.service';
import { MassageService } from '../../../services/massage.service';
import { Massage } from '../../../models/massage';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { SearchComponent } from "../../search/search.component";
import { Observable } from 'rxjs';
import { User, UserRole } from '../../../models/auth/user';
import { AuthenticationService } from '../../../services/authentication.service';

@Component({
  selector: 'app-employee-form',
  providers: [
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
    }, {useUtc: true}),],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel, MatButtonModule, MatDialogTitle, MatDialogClose, MatButtonModule, MatChipsModule, MatIconModule, MatSelectModule, SearchComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.css'
})
export class EmployeeFormComponent {
  
  readonly dialogRef = inject(MatDialogRef, {optional: true});
  readonly dialogData = inject<{employee: Employee}>(MAT_DIALOG_DATA, {optional: true});

  formModel: FormGroup;
  massageForm = new FormControl<Massage | null>(null);
  employeeBeforeUpdate!: Employee;
  selectedMassages: Massage[] = [];
  existingMassages: Massage[] = [];
  companyId: string = '';
  
  @Input() employee?: Employee;

  constructor(
    private emplService: EmployeeService,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private massageService: MassageService,
    private cdr: ChangeDetectorRef,
    private authService: AuthenticationService,
  ) {
    this.formModel = this.formBuilder.group({
      name:[this.employee?.name, Validators.required],
      surname:[this.employee?.surname, Validators.required],
      dateOfBirth: [this.employee?.dateOfBirth, Validators.required],
      email: [this.employee?.email, [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      phoneNumber: [this.employee?.phoneNumber, [Validators.required, PhoneNumberValidator.checkPhoneNumberCharacters, PhoneNumberValidator.checkPhoneNumberLength]],
      role: [this.employee?.role]
    })
  }

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  ngOnInit() {
    this.currentUser$.subscribe(user => {
      this.companyId = user?.companyId || '';
    });
    if (this.employee) {
      this.employeeBeforeUpdate = {...this.employee};
      this.formModel.patchValue({ 
        name: this.employee.name,
        surname: this.employee.surname,
        email: this.employee.email,
        phoneNumber: this.employee.phoneNumber,
        dateOfBirth: this.employee.dateOfBirth,
        role: "EMPLOYEE"})
    }
    this.loadAlreadySelectedMassages();
  }

  getSelectedMassages(selected: Massage[]) {
    this.selectedMassages = selected;
  }

  loadAlreadySelectedMassages() {
    if (this.employee && this.employee.massages) {
      this.massageService.getMassagesByEmployee(this.employee.id!).subscribe((data: Massage[]) => {
        this.existingMassages = data;
        this.cdr.detectChanges(); 
      });
    }
  }

  edit(name: string, surname:string, email:string, phoneNumber: string, dateOfBirth: LocalDate, role: UserRole) {
    const updatedEmployee: Employee = {
      id: this.employee?.id,
      name,
      surname,
      phoneNumber,
      dateOfBirth: new Date(dateOfBirth).toISOString().split('T')[0],
      email,
      role: role,
      company: this.employee?.company!,
      massageIds: this.selectedMassages.map(m => m.id!),  
    } as Employee;
    this.emplService.editEmployee(updatedEmployee).subscribe({
      next: (response) => {
        this.alertService.success("Operacja edycji powiodła się");
        this.alertService.info("Możesz zamknąć okno edycji");
      },
      error: (error: Error) => {
        console.log(error.message);
        this.alertService.error("Błąd podczas edycji: " + error.message);
      } 
    });
  }

  create(name: string, surname:string, email:string, phoneNumber: string, dateOfBirth: LocalDate) {
    const newEmployee = {
      name,
      surname,
      phoneNumber,
      dateOfBirth: new Date(dateOfBirth).toISOString().split('T')[0],
      email,
      massageIds: this.selectedMassages.map(m => m.id!),  
    };
    this.emplService.createEmployee(newEmployee).subscribe({
      next: (response) => {
        this.alertService.success("Operacja dodania pracownika powiodła się");
      },
      error: (error: Error) => {
        console.log(error.message);
        this.alertService.error("Błąd podczas dodawania pracownika: " + error.message);
      } 
    });

    if (this.dialogRef) {
      this.dialogRef.close(true);
    }
  }

  submitForm(): void {
    if (this.employee && this.formModel.valid) {
      this.edit(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.email,
        this.formModel.value.phoneNumber,
        this.formModel.value.dateOfBirth,
        this.formModel.value.role
      );
    } else if (!this.employee && this.formModel.valid) {
      this.create(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.email,
        this.formModel.value.phoneNumber,
        this.formModel.value.dateOfBirth,
      );
    }
  }
}