import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee.service';
import { DialogData } from '../../models/dialog-data';

@Component({
  selector: 'app-dialog',
  imports: [
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './dialog-delete.component.html',
  styleUrl: './dialog-delete.component.css'
})
export class DialogDeleteComponent {
  readonly dialogRef = inject(MatDialogRef<DialogDeleteComponent>);
  readonly data = inject(MAT_DIALOG_DATA) as DialogData;

  idToDelete: string | undefined = '';
  thingToDelete: String | undefined = '';

  constructor(
    private emplService: EmployeeService
  ) {
    this.checkType();
  }

  checkType(): String {
    console.log(this.data)
    console.log(this.data.type)
    switch(this.data.type){
      case 'employee':
        const employee = this.data.item as Employee;
        this.idToDelete = employee.id ?? '';
        this.thingToDelete = `pracownika ${employee.name} ${employee.surname}`;
        return this.data.type;
      default:
        return "";
    }
  }

  delete(): void {
    console.log(this.data)
    if(this.data.type === 'employee'){ 
      if (this.idToDelete) {
        this.emplService.deleteEmployee(this.idToDelete).subscribe({
          next: () => {
            console.log(`Employee with ID ${this.idToDelete} deleted successfully.`);
          },
          error: (error) => {
            console.error(`Error deleting employee with ID ${this.idToDelete}:`, error);
          }
        });
      } 
    } 
    this.dialogRef.close(true);
  }
}