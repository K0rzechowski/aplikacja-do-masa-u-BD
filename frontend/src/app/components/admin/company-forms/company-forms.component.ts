import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, inject, LOCALE_ID, Output } from '@angular/core';
import { CompanyService } from '../../../services/company.service';
import { Company, CompanyOpenTime } from '../../../models/company';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormField, MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatTimepickerModule} from '@angular/material/timepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { DialogDeleteComponent } from '../../dialog-delete/dialog-delete.component';
import { DialogData } from '../../../models/dialog-data';
import { Room } from '../../../models/room';
import { RoomFormComponent } from "../room-form/room-form.component";
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { registerLocaleData } from '@angular/common';
import localePl from '@angular/common/locales/pl';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { AlertService } from '../../../services/alert.service';
import { Moment } from 'moment';
import moment from 'moment';

registerLocaleData(localePl, 'pl');

@Component({
  selector: 'app-company-forms',
  providers: [provideNativeDateAdapter(), provideMomentDateAdapter(),
    { provide: LOCALE_ID, useValue: 'pl-PL'},
    { provide: MAT_DATE_LOCALE, useValue: 'pl-PL' }
  ],
  imports: [ReactiveFormsModule, CommonModule, MatFormField, MatFormFieldModule, MatTimepickerModule, MatInputModule, RoomFormComponent],
  templateUrl: './company-forms.component.html',
  styleUrl: './company-forms.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyFormsComponent {

  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }
  
  @Output() emitter: EventEmitter<Room> = new EventEmitter();
  
  isAdmin$: Observable<boolean> | undefined;
  company: Company | undefined;
  openForm: FormGroup;
  companyOpenTime: CompanyOpenTime | undefined;
  openTime: string | undefined;
  closeTime: string | undefined;
  roomsInMyCompany: Room[] = [];
  showRoomDetails: boolean = false;
  roomToEdit: Room | undefined;
  
  constructor (
    private companyService: CompanyService,
    private alertService: AlertService,
    private formBuilder: FormBuilder,
  ) {
    this.currentCompany$.subscribe(c => {
      if (c !== null) {
        this.company = c;

        this.loadOpenTime();        
      }
    });

    this.openForm = this.formBuilder.group({
      startTime: [this.companyOpenTime?.openTime, [Validators.required]],
      closeTime: [this.companyOpenTime?.closeTime, [Validators.required]]
    })

    this.loadRoomsInMyCompany();
  }


  loadOpenTime() {
    if (this.company?.id) {
      this.companyService.getCompanyOpenTime(this.company?.id).subscribe(data => {
        this.companyOpenTime = data;
        this.openTime = this.formatTimeForView(data.openTime);
        this.closeTime = this.formatTimeForView(data.closeTime);
      })
    }
  }

  formatTimeForView(time: string | undefined) {
    if (!time) {
      return '';
    }

    return time.substring(0, 5);
  }

  loadRoomsInMyCompany() {
    if (this.company?.id) {
      this.companyService.getCompanyRooms(this.company?.id).subscribe({
        next: (data: Room[]) => {
          this.roomsInMyCompany = [...data];
          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Error loading rooms:', error);
        }
      });
    }
  }

  openDeleteDialog(enterAnimationDuration: string, exitAnimationDuration: string, toDelete: DialogData): void {
    if (this.dialog) {
      const dialogRef = this.dialog.open(DialogDeleteComponent, {
        width: '250px',
        enterAnimationDuration,
        exitAnimationDuration,
        data: toDelete
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) { 
          setTimeout(() => {
            this.loadRoomsInMyCompany();
          }, 300);
          this.loadRoomsInMyCompany();
        }
      });
    }
  }

  openCreateDialog(enterAnimationDuration: string, exitAnimationDuration: string): void {
    if (this.dialog) {
      const dialogRef = this.dialog.open(RoomFormComponent, {
        width: '500px',
        enterAnimationDuration,
        exitAnimationDuration
      });

      dialogRef.afterClosed().subscribe((newRoom: Room | undefined) => {
        if (newRoom) { 
          setTimeout(() => {
            this.loadRoomsInMyCompany();
          }, 300);
        }
      });
    }
  }

  edit(room: Room): void {
    this.showRoomDetails = true;
    this.roomToEdit = { ...room }; 
    this.emitter.emit(this.roomToEdit);
  }

  closeEdit(): void {
    this.showRoomDetails = false;
    this.roomToEdit = undefined; 
    this.loadRoomsInMyCompany();
  }

  createOpenTime(openTime: string, closeTime: string) {
    if (this.company?.id) {
      console.log(this.company.id)
      const newTime: CompanyOpenTime = {
        companyId: this.company.id,
        openTime,
        closeTime
      };
      this.companyService.createCompanyOpenTime(newTime).subscribe({
        next: (response) => {
          this.alertService.success("Operacja dodania godzin otwarcia powiodła się");
        },
        error: (error: Error) => {
          this.alertService.error("Błąd podczas dodawania godzin otwarcia: " + error.message);
        } 
      })
    }
  }

  momentToTimeString(m: Moment | Date): string {
    if (!m) {
      return "";
    }

    const mnt = moment.isMoment(m) ? m : moment(m);
    if (!mnt.isValid()) {
      return "";
    }

    return mnt.format("HH:mm:ss");
  }

  submitOpenTime() {
    if (this.openForm.valid) {
      const startTime = this.momentToTimeString(this.openForm.value.startTime);
      const closeTime = this.momentToTimeString(this.openForm.value.closeTime);
      this.createOpenTime(startTime, closeTime);
    }
  }
}
