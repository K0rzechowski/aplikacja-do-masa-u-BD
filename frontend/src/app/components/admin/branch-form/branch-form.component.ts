import { Component, inject, Input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { BranchOffice } from '../../../models/branch-office';
import { AlertService } from '../../../services/alert.service';
import { AuthenticationService } from '../../../services/authentication.service';
import { BranchOfficeService } from '../../../services/branch-office.service';

@Component({
  selector: 'app-branch-form',
  imports: [ReactiveFormsModule],
  templateUrl: './branch-form.component.html',
  styleUrl: './branch-form.component.css'
})
export class BranchFormComponent {

  @Input() branchOffice?: BranchOffice;
  readonly dialogRef = inject(MatDialogRef, {optional: true});

  formModel: FormGroup;
  companyId: string = '';

  constructor(
    private branchOfficeService: BranchOfficeService,
    private alertService: AlertService,
    private authService: AuthenticationService,
    private formBuilder: FormBuilder
  ) {
    this.formModel = this.formBuilder.group({
      name: ['', Validators.required],
      city: ['', Validators.required],
      address: ['', Validators.required]
    });

    this.authService.currentUser.subscribe(user => {
      this.companyId = user?.companyId || '';
    });
  }

  ngOnInit() {
    if (this.branchOffice) {
      this.formModel.patchValue({
        name: this.branchOffice.name,
        city: this.branchOffice.city,
        address: this.branchOffice.address
      });
    }
  }

  create() {
    const newBranch: BranchOffice = {
      companyId: this.companyId,
      ...this.formModel.value
    };
    this.branchOfficeService.createBranchOffice(newBranch).subscribe({
      next: () => {
        this.alertService.success("Operacja dodania placówki powiodła się");
        this.dialogRef?.close(true);
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas dodawania placówki: " + error.message);
      }
    });
  }

  edit() {
    const updatedBranch: BranchOffice = {
      ...this.branchOffice!,
      ...this.formModel.value
    };
    this.branchOfficeService.editBranchOffice(updatedBranch).subscribe({
      next: () => {
        this.alertService.success("Operacja edycji powiodła się");
        this.alertService.info("Możesz zamknąć okno edycji");
      },
      error: (error: Error) => {
        this.alertService.error("Błąd podczas edycji: " + error.message);
      }
    });
  }

  submitForm() {
    if (this.formModel.invalid) {
      this.formModel.markAllAsTouched();
      return;
    }
    if (this.branchOffice) {
      this.edit();
    } else {
      this.create();
    }
  }
}
