import { Component, inject, Input } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Room } from '../../../models/room';
import { CompanyService } from '../../../services/company.service';
import { AlertService } from '../../../services/alert.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Company } from '../../../models/company';

@Component({
  selector: 'app-room-form',
  imports: [FormsModule],
  templateUrl: './room-form.component.html',
  styleUrl: './room-form.component.css'
})
export class RoomFormComponent {
  
  @Input() room?: Room;
  companyId: string | undefined;  
  roomNumber: string = '';
  readonly dialogRef = inject(MatDialogRef, {optional: true});
  readonly dialogData = inject<{room: Room}>(MAT_DIALOG_DATA, {optional: true});

  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }
  
  constructor (
    private companyService: CompanyService,
    private alertService: AlertService
  ) {
    this.currentCompany$.subscribe(c => {
      if (c !== null) {
        this.companyId = c.id;
      }
    });
  }

  create(roomNumber: string) {
    if (this.companyId) {
      const newRoom = {
        roomNumber,
        companyId: this.companyId
      };
      this.companyService.createCompanyRoom(newRoom).subscribe({
        next: (response) => {
          this.alertService.success("Operacja dodania sali powiodła się");
        },
        error: (error: Error) => {
          this.alertService.error("Błąd podczas dodawania sali: " + error.message);
        } 
      });
  
      if (this.dialogRef) {
        this.dialogRef.close(true);
      }
    }
  }

  edit(roomNumber: string) {
    const updatedRoom: Room = {
      id: this.room?.id,
      roomNumber,
      companyId: this.room?.companyId || ''
    };
    this.companyService.editCompanyRoom(updatedRoom).subscribe({
      next: (response) => {
        this.alertService.success("Operacja edycji powiodła się");
        this.alertService.info("Możesz zamknąć okno edycji");
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas edycji: " + error.message);
      } 
    });
  }

  submitForm(form: NgForm) {
    if (this.room && form.valid) {
      this.edit(form.value.number);
    } else if (!this.room && form.valid) {
      this.create(form.value.number);
    }
  }
}
