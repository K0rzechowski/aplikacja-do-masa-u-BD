import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Reservation } from '../models/reservation';
import { NoteFromVisit } from '../models/note-from-visit';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private baseUrl = `${environment.baseUrl}/reservations`;
  private notesUrl = `${environment.baseUrl}/notes`;

  constructor(
    private httpClient: HttpClient
  ) { }

  getReservationById(id: string): Observable<Reservation> {
    return this.httpClient.get<Reservation>(`${this.baseUrl}/${id}`);
  }

  createReservation(reservation: Partial<Reservation>): Observable<Reservation> {
    return this.httpClient.post<Reservation>(`${this.baseUrl}/create`, reservation).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Nie utworzono rezerwacji';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 409 ) {
          errorMessage = 'Dana wizyta już została zarezerwowana';
        } else if (error.status >= 500) {
          errorMessage = 'Wystąpił błąd serwera';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  getReservationsByCompany(companyId: string): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(`${this.baseUrl}/company/${companyId}`);
  }

  removeReservation(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }

  cancelReservation(id: string): Observable<void> {
    return this.httpClient.put<void>(`${this.baseUrl}/cancel/${id}`, null);
  }

  setReservationAsCompleted(id: string): Observable<void> {
    return this.httpClient.put<void>(`${this.baseUrl}/complete/${id}`, null);
  }

  getReservationsByMassage(massageId: string): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(`${this.baseUrl}/massage/${massageId}`);
  }

  getReservationsByCustomer(customerId: string): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(`${this.baseUrl}/customer/${customerId}`);
  }

  getReservationsByEmployee(employeeId: string): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(`${this.baseUrl}/employee/${employeeId}`);
  }

  getReservationByShift(shiftId: string): Observable<Reservation> {
    return this.httpClient.get<Reservation>(`${this.baseUrl}/shift/${shiftId}`);
  }

  createNote(note: Partial<NoteFromVisit>): Observable<NoteFromVisit> {
    return this.httpClient.post<NoteFromVisit>(`${this.notesUrl}/create`, note);
  }

  getNoteById(id: string): Observable<NoteFromVisit> {
    return this.httpClient.get<NoteFromVisit>(`${this.notesUrl}/${id}`);
  }

  removeNote(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.notesUrl}/remove/${id}`);
  }

  updateNote(id: string, note: Partial<NoteFromVisit>): Observable<NoteFromVisit> {
    return this.httpClient.put<NoteFromVisit>(`${this.notesUrl}/update/${id}`, note);
  }

  getNoteByReservation(reservationId: string): Observable<NoteFromVisit> {
    return this.httpClient.get<NoteFromVisit>(`${this.notesUrl}/reservation/${reservationId}`);
  }

  getNotesByDate(dateOfCreation: string): Observable<NoteFromVisit[]> {
    return this.httpClient.get<NoteFromVisit[]>(`${this.notesUrl}/date?dateOfCreation=${dateOfCreation}`);
  }

  getNotesByCustomer(customerId: string): Observable<NoteFromVisit[]> {
    return this.httpClient.get<NoteFromVisit[]>(`${this.notesUrl}/customer/${customerId}`);
  }
}

