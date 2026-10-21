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
import { Observable } from 'rxjs';
import { Company } from '../../../models/company';
import { CompanyService } from '../../../services/company.service';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { checkIfMonday } from '../../../validators/date.validator';

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
      startTime: [null, [Validators.required, checkIfMonday]]
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

  createSchedule() {
    if (this.currentCompany != null && this.currentCompany?.id && this.formModel.valid) {
      this.isGenerating = true;
      const startDateStr = this.formatDate(this.formModel.value.startTime);

      this.scheduleService.generateShifts(this.currentCompany.id, startDateStr).subscribe({
        next: (response) => {
          this.isGenerating = false;

          this.alertService.success(response.message);
          this.dialogRef.close();
        },
        error: (error) => {
          this.isGenerating = false;

          console.error("Error", error);

          const errorMessage = error.error?.message || error.message || "Wystąpił błąd";
          this.alertService.error(errorMessage);
        }
      })
    }
  }

  isProcessing(): boolean {
    return this.isGenerating;
  }

  getStatusMessage(): string {
    if (this.isGenerating) {
      return "Generowanie zmian...";
    }
    return "Stwórz grafik";
  }
}
