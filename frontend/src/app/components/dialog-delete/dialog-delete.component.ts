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
import { AlertService } from '../../services/alert.service';
import { MatSingleDateSelectionModel } from '@angular/material/datepicker';
import { MassageService } from '../../services/massage.service';
import { Massage } from '../../models/massage';

@Component({
  selector: 'app-dialog',
  imports: [
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './dialog-delete.component.html',
  styleUrl: './dialog-delete.component.css'
})
export class DialogDeleteComponent {
  readonly dialogRef = inject(MatDialogRef<DialogDeleteComponent>);
  readonly data = inject(MAT_DIALOG_DATA) as DialogData;

  idToDelete: string | undefined = '';
  thingToDelete: String | undefined = '';

  constructor (
    private emplService: EmployeeService,
    private msgService: MassageService,
    private alertService: AlertService
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
      case 'massage':
        const massage = this.data.item as Massage;
        this.idToDelete = massage.id ?? '';
        this.thingToDelete = `${massage.name}`;
        return this.data.type;
      default:
        return "";
    }
  }

  delete(): void {
    console.log(this.data)
    if (this.idToDelete) {
      if(this.data.type === 'employee'){ 
          this.emplService.deleteEmployee(this.idToDelete).subscribe({
            next: () => {
              this.alertService.success(`Poprawnie usunięto pracownika o ID ${this.idToDelete}`);
            },
            error: (error) => {
              this.alertService.error(`Nieudana operacja usunięcia pracownika o ID ${this.idToDelete}`);
            }
          });
      } else if(this.data.type === 'massage'){ 
        this.msgService.deleteMassage(this.idToDelete).subscribe({
          next: () => {
            this.alertService.success(`Poprawnie usunięto masaż o ID ${this.idToDelete}`);
          },
          error: (error) => {
            this.alertService.error(`Nieudana operacja usunięcia masażu o ID ${this.idToDelete}`);
          }
        });
      } 
    } 
    this.dialogRef.close(true);
  }
}