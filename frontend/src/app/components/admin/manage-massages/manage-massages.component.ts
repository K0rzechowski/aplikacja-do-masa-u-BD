import { ChangeDetectorRef, Component, EventEmitter, inject, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Massage } from '../../../models/massage';
import { map, Observable } from 'rxjs';
import { MassageService } from '../../../services/massage.service';
import { AuthenticationService } from '../../../services/authentication.service';
import { DialogData } from '../../../models/dialog-data';
import { DialogDeleteComponent } from '../../dialog-delete/dialog-delete.component';
import { CurrencyPipe } from '@angular/common';
import { MassageFormComponent } from '../massage-form/massage-form.component';

@Component({
  selector: 'app-manage-massages',
  imports: [MassageFormComponent],
  templateUrl: './manage-massages.component.html',
  styleUrl: './manage-massages.component.css'
})
export class ManageMassagesComponent {

  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);
  
  msgsInMyCompany: Massage[] = [];
  showMassageDetails: boolean = false;
  msgToEdit: Massage | undefined;
  symptomNamesForMassage: (string | undefined)[] = [];

  @Output() emitter: EventEmitter<Massage> = new EventEmitter();

  isAdmin$: Observable<boolean> | undefined;

  constructor(
    private msgService: MassageService,
    private authService: AuthenticationService
  ) {
    this.isAdmin$ = this.authService.currentUser.pipe(
      map(user => this.authService.isAdmin() === true)
    );

    this.isAdmin$.subscribe(isAdmin => {
      if (isAdmin) {
        this.loadMassagesInMyCompany();
      }});    
  }

  loadMassagesInMyCompany() {
    this.msgService.getMassagesByAdminsCompany().subscribe({
      next: (data: Massage[]) => {
        this.msgsInMyCompany = [...data];
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Error loading massages:', error);
      }
    });
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
            this.loadMassagesInMyCompany();
          }, 300);
          // reload massages after deletion
          this.loadMassagesInMyCompany();
        }
      });
    }
  }

  openCreateDialog(enterAnimationDuration: string, exitAnimationDuration: string): void {
    if (this.dialog) {
      const dialogRef = this.dialog.open(MassageFormComponent, {
        width: '500px',
        enterAnimationDuration,
        exitAnimationDuration
      });

      dialogRef.afterClosed().subscribe((newMassage: Massage | undefined) => {
        if (newMassage) { 
          setTimeout(() => {
            this.loadMassagesInMyCompany();
          }, 300);
        }
      });
    }
  }

  edit(msg: Massage): void {
    this.showMassageDetails = true;
    this.msgToEdit = { ...msg }; 
    this.emitter.emit(this.msgToEdit);
  }

  closeEdit(): void {
    this.showMassageDetails = false;
    this.msgToEdit = undefined;
    this.loadMassagesInMyCompany();
  }
}
