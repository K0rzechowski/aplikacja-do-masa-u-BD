import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { Employee } from '../../../models/employee';
import { EmployeeService } from '../../../services/employee.service';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
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
import { AlertService } from '../../../services/alert.service';

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
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel, MatButtonModule, MatDialogTitle, MatDialogClose],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.css'
})
export class EmployeeFormComponent {
  
  readonly dialogRef = inject(MatDialogRef, {optional: true});
  readonly dialogData = inject<{employee: Employee}>(MAT_DIALOG_DATA, {optional: true});

  formModel: FormGroup;
  employeeBeforeUpdate!: Employee;
  
  @Input() employee?: Employee;

  constructor(
    private emplService: EmployeeService,
    private formBuilder: FormBuilder,
    private alertService: AlertService
  ) {
    this.formModel = this.formBuilder.group({
      name:[this.employee?.name, Validators.required],
      surname:[this.employee?.surname, Validators.required],
      dateOfBirth: [this.employee?.dateOfBirth, Validators.required],
      email: [this.employee?.email, [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      phoneNumber: [this.employee?.phoneNumber, [Validators.required, PhoneNumberValidator.checkPhoneNumberCharacters, PhoneNumberValidator.checkPhoneNumberLength]],
      // password: ['']
    })
  }

  ngOnInit() {
    if (this.employee) {
      this.employeeBeforeUpdate = {...this.employee};
      this.formModel.patchValue({ 
        name: this.employee.name,
        surname: this.employee.surname,
        email: this.employee.email,
        phoneNumber: this.employee.phoneNumber,
        dateOfBirth: this.employee.dateOfBirth })
    }
  }

  edit(name: string, surname:string, email:string, phoneNumber: string, dateOfBirth: LocalDate) {
    console.log(this.employee);
    const updatedEmployee: Employee = {id: this.employee?.id, name, surname, phoneNumber, dateOfBirth: new Date(dateOfBirth).toISOString().split('T')[0], email, role: this.employee?.role!, company: this.employee?.company!} as Employee;
    console.log("Updated employee: ", updatedEmployee);
    this.emplService.editEmployee(updatedEmployee).subscribe({
      next: (response) => {
        console.log("Succesfully edited", response);
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
    console.log(this.employee);
    const newEmployee = {
      name,
      surname,
      phoneNumber,
      dateOfBirth: new Date(dateOfBirth).toISOString().split('T')[0],
      email,
      // password
    };
    console.log(newEmployee);
    this.emplService.createEmployee(newEmployee).subscribe({
      next: (response) => {
        console.log("Succesfully added", response);
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
    console.log("Submitting...", this.employee, this.formModel.valid)
    if (this.employee && this.formModel.valid) {
      console.log("editing")
      this.edit(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.email,
        this.formModel.value.phoneNumber,
        this.formModel.value.dateOfBirth,
      );
    } else if (!this.employee && this.formModel.valid) {
      this.create(
        this.formModel.value.name,
        this.formModel.value.surname,
        this.formModel.value.email,
        // this.formModel.value.password,
        this.formModel.value.phoneNumber,
        this.formModel.value.dateOfBirth,
      );
    }
  }
}