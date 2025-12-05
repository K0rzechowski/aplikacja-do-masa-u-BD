import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Reservation } from '../models/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private baseUrl = `${environment.baseUrl}/reservations`;

  constructor(
    private httpClient: HttpClient
  ) { }

  getReservationById(id: string): Observable<Reservation> {
    return this.httpClient.get<Reservation>(`${this.baseUrl}/${id}`);
  }

  createReservation(reservation: Partial<Reservation>): Observable<Reservation> {
    return this.httpClient.post<Reservation>(`${this.baseUrl}/create`, reservation);
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
}
