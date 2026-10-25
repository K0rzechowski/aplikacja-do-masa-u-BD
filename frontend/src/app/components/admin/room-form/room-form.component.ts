import { Component, inject, Input } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Room } from '../../../models/room';
import { AlertService } from '../../../services/alert.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BranchOfficeService } from '../../../services/branch-office.service';

@Component({
  selector: 'app-room-form',
  imports: [FormsModule],
  templateUrl: './room-form.component.html',
  styleUrl: './room-form.component.css'
})
export class RoomFormComponent {

  @Input() room?: Room;
  roomNumber: string = '';
  readonly dialogRef = inject(MatDialogRef, {optional: true});
  readonly dialogData = inject<{branchOfficeId: string}>(MAT_DIALOG_DATA, {optional: true});

  constructor (
    private branchOfficeService: BranchOfficeService,
    private alertService: AlertService
  ) { }

  create(roomNumber: string) {
    const branchOfficeId = this.dialogData?.branchOfficeId;
    if (branchOfficeId) {
      const newRoom: Room = {
        roomNumber,
        branchOfficeId
      };
      this.branchOfficeService.createRoom(newRoom).subscribe({
        next: () => {
          this.alertService.success("Operacja dodania sali powiodła się");
          this.dialogRef?.close(true);
        },
        error: (error: Error) => {
          this.alertService.error("Błąd podczas dodawania sali: " + error.message);
        }
      });
    }
  }

  edit(roomNumber: string) {
    if (!this.room) {
      return;
    }
    const updatedRoom: Room = {
      id: this.room.id,
      roomNumber,
      branchOfficeId: this.room.branchOfficeId
    };
    this.branchOfficeService.editRoom(updatedRoom).subscribe({
      next: () => {
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
