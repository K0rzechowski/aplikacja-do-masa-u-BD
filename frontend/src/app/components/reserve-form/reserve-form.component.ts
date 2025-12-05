import { ReservationService } from '../../services/reservation.service';
import { AlertService } from '../../services/alert.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatOptionModule, provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import { NgStyle } from '@angular/common';
import { MatDialogTitle } from '@angular/material/dialog';
import { A11yModule } from '@angular/cdk/a11y';
import { MatSelectModule } from '@angular/material/select';
import { Company } from '../../models/company';
import { CompanyService } from '../../services/company.service';
import { Massage } from '../../models/massage';
import { MassageService } from '../../services/massage.service';
import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-reserve-form',
  imports: [ReactiveFormsModule, MatExpansionModule, MatIconModule,MatFormFieldModule, MatInputModule, MatButtonModule, MatDatepickerModule, NgStyle, MatButtonModule, ReactiveFormsModule,  MatFormFieldModule, MatOptionModule, A11yModule, MatSelectModule],
  templateUrl: './reserve-form.component.html',
  styleUrl: './reserve-form.component.css',
  providers: [provideNativeDateAdapter()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReserveFormComponent {

  formModel: FormGroup; 
  step = signal(0);
  allCompanies: Company[] = [];
  allMassages: Massage[] = [];
  allEmployees: Employee[] = [];

  constructor(
    private reservationService: ReservationService,
    private alertService: AlertService,
    private formBuilder: FormBuilder,
    private companyService: CompanyService,
    private massageService: MassageService,
    private employeeService: EmployeeService
  ) {
    this.formModel = this.formBuilder.group({
      company: [null, Validators.required],
      massage: [null, Validators.required],
      employee: [null, Validators.required],
      date: [null, Validators.required],
      time: [null, Validators.required]
    });

    this.companyService.getAll().subscribe(data => 
      this.allCompanies = data
    );

    this.massageService.getMassagesByCompany("1").subscribe(data =>
      this.allMassages = data
    );

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

  submitForm(){}
}
