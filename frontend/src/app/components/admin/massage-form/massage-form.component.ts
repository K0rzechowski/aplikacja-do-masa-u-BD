import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, inject, signal } from '@angular/core';
import { Massage } from '../../../models/massage';
import { MassageService } from '../../../services/massage.service';
import { Form, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, registerLocaleData } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatHint, MatInputModule, MatLabel } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MatButtonModule} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import {MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import { AlertService } from '../../../services/alert.service';
import { Symptom } from '../../../models/symptom';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { SearchComponent } from "../../search/search.component";

@Component({
  selector: 'app-massage-form',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatButtonModule, MatDialogTitle, MatDialogClose, MatButtonModule, MatChipsModule, MatIconModule, MatSelectModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './massage-form.component.html',
  styleUrl: './massage-form.component.css'
})
export class MassageFormComponent {

  readonly dialogRef = inject(MatDialogRef, {optional: true});
  readonly dialogData = inject<{massage: Massage}>(MAT_DIALOG_DATA, {optional: true});

  formModel: FormGroup;
  massageBeforeUpdate!: Massage;
  existingSymptoms: Symptom[] = [];
  selectedSymptoms: Symptom[] = [];
  selectedFile: File | null = null;

  @Input() massage?: Massage;

  constructor(
    private msgService: MassageService,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {
    this.formModel = this.formBuilder.group({
      name:[this.massage?.name, Validators.required],
      price:[this.massage?.price, [Validators.required, Validators.min(1)]]
    })
  }

  ngOnInit() {
    if (this.massage) {
      this.massageBeforeUpdate = {...this.massage};
      this.formModel.patchValue({
        name: this.massage.name,
        file: this.massage.imageUrl,
        price: this.massage.price
      });
    }
    // this.loadAlreadySelectedSymptoms();
  }

  // getSelectedSymptoms(selected: Symptom[]) {
  //   this.selectedSymptoms = selected;
  // }

  // loadAlreadySelectedSymptoms() {
  //   if (this.massage && this.massage.symptoms) {
  //     this.msgService.getSymptomsByMassage(this.massage.id!).subscribe((data: Symptom[]) => {
  //       this.existingSymptoms = data;
  //       this.cdr.detectChanges(); 
  //     });
  //   }
  // }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  edit(name: string, price: number, file: File | null) {
    const updatedMassage: Massage = {
      id: this.massage?.id,
      name,
      price,
      company: this.massage?.company!,
      companyId: this.massage?.companyId!,
      symptomIds: this.selectedSymptoms.map(s => s.id!),
    } as Massage;
    this.msgService.editMassage(updatedMassage, file).subscribe({
      next: (response) => {
        this.alertService.success("Operacja edycji powiodła się");
        this.alertService.info("Możesz zamknąć okno edycji");
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas edycji: " + error.message);
      }
    });
  }

  create(name: string, price: number, file: File) {
    const upperName = name.charAt(0).toUpperCase() + name.slice(1)
    const newMassage = {
      name: upperName,
      price,
      symptomIds: this.selectedSymptoms.map(s => s.id!)
    };
    this.msgService.createMassage(newMassage, file).subscribe({
      next: (response) => {
        this.alertService.success("Operacja dodania masażu powiodła się");
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas dodawania masażu: " + error.message);
      }
    });

    if (this.dialogRef) {
      this.dialogRef.close(true);
    }
  }

  submitForm(): void {
    if (this.massage && this.formModel.valid) {
      this.edit(
        this.formModel.value.name,
        this.formModel.value.price,
        this.selectedFile
      );
    } else if (!this.massage && this.formModel.valid && this.selectedFile) {
      this.create(
        this.formModel.value.name,
        this.formModel.value.price,
        this.selectedFile
      );
    }
  }
}