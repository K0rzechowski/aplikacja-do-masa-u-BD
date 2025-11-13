import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Massage } from '../models/massage';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MassageService {

  private baseUrl = `${environment.baseUrl}/massages`;

  constructor(private httpClient: HttpClient) { }

  createMassage(request: Massage): Observable<Massage> {
    return this.httpClient.post<Massage>(`${this.baseUrl}/create`, request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Błąd podczas tworzenia masażu';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 409 ) {
          // errorMessage = 'Firma o danej nazwie i danym mieście już istnieje';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  getMassagesByAdminsCompany(): Observable<Massage[]> {
    return this.httpClient.get<Massage[]>(`${this.baseUrl}`);
  }

  getMassagesByCompany(companyId: string): Observable<Massage[]> {
    return this.httpClient.get<Massage[]>(`${this.baseUrl}/company/${companyId}`);
  }

  getMassagesByEmployee(employeeId: string): Observable<Massage[]> {
    return this.httpClient.get<Massage[]>(`${this.baseUrl}/employee/${employeeId}`);
  }

  getMassagesBySymptom(symptomId: string): Observable<Massage[]> {
    return this.httpClient.get<Massage[]>(`${this.baseUrl}/symptom/${symptomId}`);
  }

  getMassageById(id: string): Observable<Massage> {
    return this.httpClient.get<Massage>(`${this.baseUrl}/${id}`);
  }

  deleteMassage(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }
}
