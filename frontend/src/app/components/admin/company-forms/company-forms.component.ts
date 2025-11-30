import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, inject, LOCALE_ID, Output } from '@angular/core';
import { CompanyService } from '../../../services/company.service';
import { Company, CompanyOpenTime } from '../../../models/company';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormField, MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import {MAT_TIMEPICKER_CONFIG, MatTimepickerModule} from '@angular/material/timepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { DialogDeleteComponent } from '../../dialog-delete/dialog-delete.component';
import { DialogData } from '../../../models/dialog-data';
import { Room } from '../../../models/room';
import { RoomFormComponent } from "../room-form/room-form.component";

@Component({
  selector: 'app-company-forms',
  providers: [provideNativeDateAdapter(),
    { provide: LOCALE_ID, useValue:'pl-PL'}
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
  openTime: CompanyOpenTime | undefined;
  roomsInMyCompany: Room[] = [];
  showRoomDetails: boolean = false;
  roomToEdit: Room | undefined;
  
  constructor (
    private companyService: CompanyService,
    private formBuilder: FormBuilder,
  ) {
    this.currentCompany$.subscribe(c => {
      if (c !== null) {
        this.company = c;
      }
    });

    if (this.company?.id) {
      this.companyService.getCompanyOpenTime(this.company?.id).subscribe(data => {
        this.openTime = data;
      })
    }

    this.openForm = this.formBuilder.group({
      startTime: [this.openTime?.openTime, [Validators.required]],
      closeTime: [this.openTime?.endTime, [Validators.required]]
    })

    this.loadRoomsInMyCompany();
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

  //TODO
  submitOpenTime() {}
}
