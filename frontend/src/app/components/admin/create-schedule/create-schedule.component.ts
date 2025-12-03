import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ScheduleService } from '../../../services/schedule.service';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DialogData } from '../../../models/dialog-data';
import { AlertService } from '../../../services/alert.service';
import { MatFormField, MatHint, MatInputModule, MatLabel } from '@angular/material/input';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { Observable, of, switchMap } from 'rxjs';
import { Company } from '../../../models/company';
import { CompanyService } from '../../../services/company.service';
import { OptimizedShift } from '../../../models/optaplanner/optimized-shifts';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-create-schedule',
  imports: [MatDialogModule, MatFormField, ReactiveFormsModule, MatLabel, MatInputModule, MatHint, MatDatepickerModule, MatProgressSpinnerModule],
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
    }, {useUtc: true}),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './create-schedule.component.html',
  styleUrl: './create-schedule.component.css'
})
export class CreateScheduleComponent {

  readonly dialogRef = inject(MatDialogRef<CreateScheduleComponent>);

  formModel: FormGroup;
  currentCompany: Company | undefined;
  isGenerating = false;
  isOptimizing = false;
  currentStep: "idle" | "generating" | "optimizing" | "completed" = "idle";

  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }

  constructor(
    private scheduleService: ScheduleService,
    private alertService: AlertService,
    private companyService: CompanyService,
    private formBuilder: FormBuilder
  ) {
    this.formModel = this.formBuilder.group({
      startTime: [null, Validators.required],
      endTime: [null, Validators.required]
    })

    this.currentCompany$.subscribe(c => {
      if (c !== null) {
        this.currentCompany = c;
      }
    });
  }

  anuluj() {
    this.dialogRef.close();
  }

  private formatDate(date: Date): string {
    const year = new Date(date).getFullYear();
    const month = String(new Date(date).getMonth() + 1).padStart(2, '0');
    const day = String(new Date(date).getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private formatToISOStartDateTime(date: Date): string {
    return `${this.formatDate(date)}T00:00:00`;
  }

  private formatToISOEndDateTime(date: Date): string {
    return `${this.formatDate(date)}T23:59:59`;
  }

  createSchedule() {
    if (this.currentCompany != null && this.currentCompany?.id && this.formModel.valid) {
      this.isGenerating = true;
      const startDate = this.formModel.value.startTime;
      const endDate = this.formModel.value.endTime;

      const startDateStr = this.formatDate(startDate);

      const startDateTime = this.formatToISOStartDateTime(startDate);
      const endDateTime = this.formatToISOEndDateTime(endDate);

      console.log("Generating shifts from ", startDateStr, " to ", endDateTime);

      this.scheduleService.generateShifts(this.currentCompany.id, startDateStr).pipe(
        switchMap(response => {
          this.isGenerating = false;
          this.alertService.success(response.message);

          this.isOptimizing = true;
          if (this.currentCompany?.id) {
            return this.scheduleService.optimizeSchedule(this.currentCompany.id, startDateTime, endDateTime);
          }
          return of();
        })
      )
      .subscribe({
        next: (optimizeResponse) => {
          this.isOptimizing = false;

          console.log("Optimization completed ", optimizeResponse.message, optimizeResponse.score);
          this.alertService.success(`${optimizeResponse.message}`);
          this.dialogRef.close();
        },
        error: (error) => {
          this.isGenerating = false;
          this.isOptimizing = false;

          console.error("Error", error);

          const errorMessage = error.error?.message || error.message || "Wystąpił błąd";
          this.alertService.error(errorMessage);
        }
      })
    }
  }

  isProcessing(): boolean {
    return this.isGenerating || this.isOptimizing;
  }

  getStatusMessage(): string {
    if (this.isGenerating) {
      return "Generowanie zmian...";
    }
    if (this.isOptimizing) {
      return "Optymalizacja w toku...";
    }
    return "Stwórz grafik";
  }
}
