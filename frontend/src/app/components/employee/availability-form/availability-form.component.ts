import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogModule, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { Availability, AvailabilityType } from '../../../models/availability';
import { AvailabilityService } from '../../../services/availability.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatOption, MatOptionModule } from "@angular/material/core";
import { A11yModule } from "@angular/cdk/a11y";
import { MatSelectModule } from '@angular/material/select';
import { AlertService } from '../../../services/alert.service';
import { AvailabilityTypeLabels } from '../../../models/availability';

@Component({
  selector: 'app-availability-form',
  imports: [MatButtonModule, MatDialogTitle, MatDialogContent, MatDialogModule, MatDialogActions, MatDialogClose, ReactiveFormsModule,  MatFormFieldModule, MatOptionModule, A11yModule, MatSelectModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './availability-form.component.html',
  styleUrl: './availability-form.component.css'
})
export class AvailabilityFormComponent {

  readonly dialogRef = inject(MatDialogRef<AvailabilityFormComponent>);
  readonly data = inject(MAT_DIALOG_DATA) as { day: Date, employeeId: string };
  currentDayAvailability: Availability | undefined;
  formModel: FormGroup;
  day!: string;

  types = Object.values(AvailabilityType);
  AvailabilityTypeLabels = AvailabilityTypeLabels;

  constructor (
    private availabilityService: AvailabilityService,
    private formBuilder: FormBuilder,
    private alertService: AlertService
  ) {
    this.day = this.data.day
      ? this.data.day.toISOString().split("T")[0]
      : "";
    this.availabilityService.getAvailabilityByEmployeeAndStartDate(this.data.employeeId, this.day).subscribe( data => {
      this.currentDayAvailability = data;
    });

    this.formModel = this.formBuilder.group({
      type: [this.currentDayAvailability?.availabilityType, Validators.required]
    })
  }

  ngOnInit() {
    if (this.currentDayAvailability) {
      this.formModel.patchValue({ 
        type: this.currentDayAvailability.availabilityType})
    }
  }

  create(type: AvailabilityType) {
    const newAvailability = {
      employeeId: this.data.employeeId,
      date: this.day,
      availabilityType: type,
    };
    this.availabilityService.setAvailability(newAvailability).subscribe({
      next: (response) => {
        this.alertService.success("Operacja dodania dostępności powiodła się");
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas dodawania dostępności: " + error.message);
      } 
    });

    if (this.dialogRef) {
      this.dialogRef.close(true);
    }
  }

  edit(type: AvailabilityType) {
    if (this.currentDayAvailability) {
      const updatedAvailability = {
        id: this.currentDayAvailability.id,
        employeeId: this.currentDayAvailability.employeeId,
        date: this.currentDayAvailability.date,
        availabilityType: type,
      };
      this.availabilityService.editAvailability(updatedAvailability).subscribe({
        next: (response) => {
          this.alertService.success("Operacja edycji powiodła się");
        },
        error: (error: Error) => {
          this.alertService.error("Błąd podczas edycji: " + error.message);
        } 
      });
    }
  }

  submitForm() {
    if (this.currentDayAvailability && this.formModel.valid) {
      this.edit(this.formModel.value.type);
    } else if (!this.currentDayAvailability && this.formModel.valid) {
      this.create(this.formModel.value.type);
    }
  }
}
