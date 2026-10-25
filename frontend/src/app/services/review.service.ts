import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { Review } from '../models/review';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private baseUrl = `${environment.baseUrl}/reviews`;

  constructor(private httpClient: HttpClient) { }

  createReview(review: Review): Observable<Review> {
    return this.httpClient.post<Review>(`${this.baseUrl}/create`, review).pipe(
      catchError((error: HttpErrorResponse) => {
        const errorMessage = error.error?.message || error.error?.detail || 'Nie udało się dodać opinii';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  getReviewByReservation(reservationId: string): Observable<Review> {
    return this.httpClient.get<Review>(`${this.baseUrl}/reservation/${reservationId}`);
  }

  getReviewsByBranchOffice(branchOfficeId: string): Observable<Review[]> {
    return this.httpClient.get<Review[]>(`${this.baseUrl}/branch/${branchOfficeId}`);
  }

  deleteReview(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }
}
