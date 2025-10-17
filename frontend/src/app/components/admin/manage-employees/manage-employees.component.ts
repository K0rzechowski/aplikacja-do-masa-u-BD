import { Component, inject, ViewChild } from '@angular/core';
import { EmployeeService } from '../../../services/employee.service';
import { Employee } from '../../../models/employee';
import { AuthenticationService } from '../../../services/authentication.service';
import { map, Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogDeleteComponent } from '../../dialog-delete/dialog-delete.component';
import { DialogData } from '../../../models/dialog-data';

@Component({
  selector: 'app-manage-employees',
  imports: [],
  templateUrl: './manage-employees.component.html',
  styleUrl: './manage-employees.component.css'
})
export class ManageEmployeesComponent {

  readonly dialog = inject(MatDialog);
  
  emplsInMyCompany: Employee[] = [];

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

  openDialog(enterAnimationDuration: string, exitAnimationDuration: string, toDelete: DialogData): void {
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
