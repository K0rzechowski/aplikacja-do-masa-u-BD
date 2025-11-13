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
  symptomForm = new FormControl<Symptom | null>(null);
  massageBeforeUpdate!: Massage;
  allSymptoms: Symptom[] = [];
  selectedSymptoms = signal<Symptom[]>([]);
  
  @Input() massage?: Massage;

  announcer = inject(LiveAnnouncer);

  constructor(
    private msgService: MassageService,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {
    this.formModel = this.formBuilder.group({
      name:[this.massage?.name, Validators.required],
      minutes: [this.massage?.minutes, Validators.required],
      price:[this.massage?.price, Validators.required]
    })

    this.loadSymptoms();

    this.symptomForm.valueChanges.subscribe(symptom => {
      if (symptom) {
        this.addSymptom(symptom);
        this.symptomForm.setValue(null);
    }});
  }

  ngOnInit() {
    if (this.massage) {
      this.massageBeforeUpdate = {...this.massage};
      this.formModel.patchValue({ 
        name: this.massage.name,
        minutes: this.massage.minutes,
        price: this.massage.price })
    }
    this.loadAlreadySelectedSymptoms();
  }

  loadSymptoms() {
    this.msgService.getAllSymptoms().subscribe((data: Symptom[]) => {
      this.allSymptoms = data;
      // console.log("All symptoms:", this.allSymptoms)
      this.cdr.detectChanges(); 
    });
  }

  // TODO
  loadAlreadySelectedSymptoms() {
    if (this.massage && this.massage.symptoms) {
      this.msgService.getSymptomsByMassage(this.massage.id!).subscribe((data: Symptom[]) => {
        this.selectedSymptoms.set(data);
        this.cdr.detectChanges(); 
      });
    }
  }

  addSymptom(symptom: Symptom) {
    const isAlreadySelected = this.massage?.symptoms?.some(s => s.id === symptom.id);

    if (!isAlreadySelected) {
      this.selectedSymptoms.update(symptoms => [...symptoms, symptom]);
      this.announcer.announce(`Dodano symptom: ${symptom.name}`);
    } else {
      this.announcer.announce(`Symptom: ${symptom.name} jest już dodany`);
    }
  }

  removeSymptom(symptom: Symptom) {
    this.selectedSymptoms.update(symptoms => {
      const filteredSymptoms = symptoms.filter(m => m.id !== symptom.id);
      this.announcer.announce(`Usunięto symptom: ${symptom.name}`);
      return filteredSymptoms;
    });
  }

  getSelectedSymptomsIds(): string[] {
    return this.selectedSymptoms().map(symptom => symptom.id!);
  }

  getAvaiableSymptoms(): Symptom[] {
    const selectedIds = this.getSelectedSymptomsIds();
    // console.log("aelected: ", selectedIds)
    const view = this.allSymptoms.filter(symptom => !selectedIds.includes(symptom.id!));
    // console.log("View", view);
    return this.allSymptoms.filter(symptom => !selectedIds.includes(symptom.id!));
  }

  edit(name: string, minutes: number, price: number) {
    const updatedMassage: Massage = {
      id: this.massage?.id,
      name,
      price,
      minutes,
      company: this.massage?.company!,
      symptomIds: this.getSelectedSymptomsIds(),  
    } as Massage;
    this.msgService.editMassage(updatedMassage).subscribe({
      next: (response) => {
        console.log(response)
        this.alertService.success("Operacja edycji powiodła się");
        this.alertService.info("Możesz zamknąć okno edycji");
      },
      error: (error: Error) => {
        console.log(error.message);
        this.alertService.error("Błąd podczas edycji: " + error.message);
      } 
    });
  }

  create(name: string, minutes: number, price: number,) {
    console.log(this.massage);
    const upperName = name.charAt(0).toUpperCase() + name.slice(1)
    const newMassage = {
      name: upperName,
      price,
      minutes,
      symptomIds: this.getSelectedSymptomsIds()
    };
    console.log(newMassage);
    this.msgService.createMassage(newMassage).subscribe({
      next: (response) => {
        this.alertService.success("Operacja dodania masażu powiodła się");
      },
      error: (error: Error) => {
        console.log(error.message);
        this.alertService.error("Błąd podczas dodawania masażu: " + error.message);
      } 
    });

    if (this.dialogRef) {
      this.dialogRef.close(true);
    }
  }

  submitForm(): void {
    // console.log("Submitting...", this.massage, this.formModel.valid)
    if (this.massage && this.formModel.valid) {
      this.edit(
        this.formModel.value.name,
        this.formModel.value.minutes,
        this.formModel.value.price
      );
    } else if (!this.massage && this.formModel.valid) {
      this.create(
        this.formModel.value.name,
        this.formModel.value.minutes,
        this.formModel.value.price
      );
    }
  }
}