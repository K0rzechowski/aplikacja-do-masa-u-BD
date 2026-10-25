import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { Reservation } from '../../models/reservation';
import { Review } from '../../models/review';
import { ReviewService } from '../../services/review.service';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-review-form-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatIconModule],
  templateUrl: './review-form-dialog.component.html',
  styleUrl: './review-form-dialog.component.css'
})
export class ReviewFormDialogComponent {

  readonly dialogRef = inject(MatDialogRef<ReviewFormDialogComponent>);
  readonly data = inject<{ reservation: Reservation }>(MAT_DIALOG_DATA);

  formModel: FormGroup;
  stars = [1, 2, 3, 4, 5];
  isSaving = false;

  constructor(
    private reviewService: ReviewService,
    private alertService: AlertService,
    private formBuilder: FormBuilder
  ) {
    this.formModel = this.formBuilder.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', Validators.maxLength(1000)]
    });
  }

  setRating(rating: number) {
    this.formModel.patchValue({ rating });
    this.formModel.controls['rating'].markAsTouched();
  }

  submit() {
    if (this.formModel.invalid) {
      this.formModel.markAllAsTouched();
      return;
    }

    const review: Review = {
      reservationId: this.data.reservation.id,
      rating: this.formModel.value.rating,
      comment: this.formModel.value.comment?.trim() || undefined
    };

    this.isSaving = true;
    this.reviewService.createReview(review).subscribe({
      next: (created) => {
        this.alertService.success('Dziękujemy za opinię');
        this.dialogRef.close(created);
      },
      error: (error: Error) => {
        this.isSaving = false;
        this.alertService.error(error.message);
      }
    });
  }
}
