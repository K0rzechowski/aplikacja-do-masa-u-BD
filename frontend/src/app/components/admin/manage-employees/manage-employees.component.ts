import { Component, EventEmitter, inject, Optional, Output, ViewChild } from '@angular/core';
import { EmployeeService } from '../../../services/employee.service';
import { Employee } from '../../../models/employee';
import { AuthenticationService } from '../../../services/authentication.service';
import { map, Observable } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DialogDeleteComponent } from '../../dialog-delete/dialog-delete.component';
import { DialogData } from '../../../models/dialog-data';
import { EmployeeFormComponent } from "../employee-form/employee-form.component";

@Component({
  selector: 'app-manage-employees',
  imports: [EmployeeFormComponent],
  templateUrl: './manage-employees.component.html',
  styleUrl: './manage-employees.component.css'
})
export class ManageEmployeesComponent {

  readonly dialog = inject(MatDialog);
  
  emplsInMyCompany: Employee[] = [];
  showEmployeeDetails: boolean = false;
  emplToEdit: Employee | undefined;

  @Output() emitter: EventEmitter<Employee> = new EventEmitter();

  isAdmin$: Observable<boolean> | undefined;

  constructor(
    private emplService: EmployeeService,
    private authService: AuthenticationService
  ) {
    this.isAdmin$ = this.authService.currentUser.pipe(
      map(user => user?.role === 'ADMIN')
    );

    this.isAdmin$.subscribe(isAdmin => {
      // console.log('Is Admin:', isAdmin);
      if (isAdmin) {
        this.loadEmployeesInMyCompany();
      }});    
  }

  loadEmployeesInMyCompany() {
    // console.log('Loading employees...');
    this.emplService.getEmployeesByAdminsCompany().subscribe({
      next: (data: Employee[]) => {
        this.emplsInMyCompany = data;
        // console.log('Employees loaded:', this.emplsInMyCompany);
      },
      error: (error) => {
        // console.error('Error loading employees:', error);
        // console.error('Error status:', error.status);
        // console.error('Error message:', error.error);
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
        if (result === true) { 
          // reload employees after deletion
          this.loadEmployeesInMyCompany();
        }
      });
    }
  }

  edit(empl: Employee): void {
    this.showEmployeeDetails = true;
    this.emplToEdit = { ...empl }; 
    this.emitter.emit(this.emplToEdit);
    console.log(this.showEmployeeDetails, this.emplToEdit)
  }

  closeEdit(): void {
    this.showEmployeeDetails = false;
    this.emplToEdit = undefined;
  }
}
