import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Reservation } from '../../models/reservation';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservationService } from '../../services/reservation.service';
import { NoteFromVisit } from '../../models/note-from-visit';
import { AlertService } from '../../services/alert.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-note-form-dialog',
  imports: [MatDialogModule, MatFormFieldModule, ReactiveFormsModule, DatePipe],
  templateUrl: './note-form-dialog.component.html',
  styleUrl: './note-form-dialog.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoteFormDialogComponent {

  readonly dialogRef = inject(MatDialogRef<NoteFormDialogComponent>);
  readonly data = inject(MAT_DIALOG_DATA) as { reservation: Reservation };

  reservation: Reservation = this.data.reservation;
  currentNote: NoteFromVisit | undefined;
  formModel: FormGroup;
  reservationJSON = JSON.parse(JSON.stringify(this.reservation));
  
  constructor (
    private reservationService: ReservationService,
    private formBuilder: FormBuilder,
    private alertService: AlertService
  ) {
    this.formModel = this.formBuilder.group({
      content: [null, Validators.required]
    })

    this.reservationService.getNoteByReservation(this.reservation.id).subscribe(note => { 
      this.currentNote = note;
      this.formModel.patchValue({
        content: note.content
      });
    })
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return new DatePipe('pl-PL').transform(date, 'shortTime') || '';
  }

  editNote(noteId: string, content: string) {
    const updatedNote: Partial<NoteFromVisit> = {
      content
    };

    this.reservationService.updateNote(noteId, updatedNote).subscribe({
      next: () => {
        this.alertService.success('Notatka została zaktualizowana.');
        this.dialogRef.close(true);
      },
      error: () => {
        this.alertService.error('Wystąpił błąd podczas aktualizacji notatki.');
      }
    });
  }

  addNote(content: string) {
    const note: Partial<NoteFromVisit> = {
      reservationId: this.reservation.id,
      content
    };
    this.reservationService.createNote(note).subscribe({
      next: () => {
        this.alertService.success('Notatka została dodana.');
        this.dialogRef.close(true);
      },
      error: () => {
        this.alertService.error('Wystąpił błąd podczas dodawania notatki.');
      }
    });
  }

  submitForm() {
    if (!this.formModel.invalid && this.reservation) {
      if (this.currentNote) {
        this.editNote(this.currentNote.id, this.formModel.value.content);
      } else {
        this.addNote(this.formModel.value.content);
      }
    }
  }
}
