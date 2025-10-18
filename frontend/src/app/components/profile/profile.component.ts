import { Component, ChangeDetectionStrategy, LOCALE_ID } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatHint, MatInputModule, MatLabel} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter'
import { CommonModule, registerLocaleData } from '@angular/common';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { LocalDate } from 'local-date';
import { RegisterRequest } from '../../models/auth/registerRequest';
import PasswordValidator from '../../validators/password.validator';
import localePl from '@angular/common/locales/pl';
import 'moment/locale/pl';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import { UpdateProfileRequest, User } from '../../models/auth/user';
import { AlertService } from '../../services/alert.service';

registerLocaleData(localePl);

@Component({
  selector: 'app-profile',
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pl' },
    provideMomentDateAdapter({
      parse: {
        dateInput: 'DD/MM/YYYY',
      },
      display: {
        dateInput:'DD/MM/YYYY',
        monthYearLabel: 'MMM YYYY',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'MMMM YYYY',
      }
    }, {useUtc: true}),
    
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatHint, MatLabel],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
 
  user: User | null | undefined;

  formModel: FormGroup;
  passwordForm: FormGroup
 
  constructor (
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router,
    private alertService: AlertService
  ) {    
    this.authService.currentUser.subscribe(user => {
      this.user = user;
    });

    this.formModel = this.formBuilder.group({
      name:[this.user?.name, Validators.required],
      surname:[this.user?.surname, Validators.required],
      dateOfBirth: [this.user?.dateOfBirth, Validators.required]
    });

    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', Validators.required]
    })
  }

  updateProfile(updateRequest: UpdateProfileRequest): void {
    console.log("About to update user: ", this.user)
    if (this.user) {
      console.log("id: " + this.user.id)
      this.authService.updateProfile(updateRequest, this.user).subscribe({
        next: (response) => {
          console.log("Succesfully edited", response);
          this.alertService.success("Operacja edycji profilu powiodła się");
        },
        error: (error: Error) => {
          console.log(error.message);
          this.alertService.error("Błąd podczas edycji: " + error.message);
        } 
      });
    }
  }

  submitEditForm(): void {
    if (this.formModel.valid) {
      console.log("lalall", this.formModel.valid)
      const request = {
        name: this.formModel.get('name')?.value,
        surname: this.formModel.get('surname')?.value,
        dateOfBirth: new Date(this.formModel.get('dateOfBirth')?.value).toISOString().split('T')[0]
      }
      console.log("request: ", request);

      this.updateProfile(request);
    } else {
      console.log("Cannot");
    } 
  }

}
