import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Massage, MassageSymptom } from '../models/massage';
import { catchError, Observable, throwError } from 'rxjs';
import { Symptom } from '../models/symptom';

@Injectable({
  providedIn: 'root'
})
export class MassageService {

  private baseUrl = `${environment.baseUrl}/massages`;
  private symptomBaseUrl = `${environment.baseUrl}/symptoms`;

  constructor(private httpClient: HttpClient) { }

  private prepareFormData(request: Partial<Massage>, file: File): FormData {
    const formData: FormData = new FormData();

    formData.append('name', request.name || '');
    formData.append('price', request.price?.toString() || '0');
    
    if (request.symptomIds && request.symptomIds.length > 0) {
      request.symptomIds.forEach(id => formData.append('symptomIds', id));
    }
    
    formData.append('file', file);

    return formData;
  }

  createMassage(request: Partial<Massage>, file: File): Observable<Massage> {
    const formData = this.prepareFormData(request, file);
    
    return this.httpClient.post<Massage>(`${this.baseUrl}/create`, formData).pipe(
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

  editMassage(request: Massage, file: File): Observable<Massage> {
    const formData = this.prepareFormData(request, file);
    return this.httpClient.put<Massage>(`${this.baseUrl}/edit/${request.id}`, formData);
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

  getMassagesBySymptom(symptomId: string, city: string): Observable<MassageSymptom[]> {
    let params = {};
    if (city) {
      params = { city: city }
    }
    return this.httpClient.get<MassageSymptom[]>(`${this.baseUrl}/symptom/${symptomId}`,{params});
  }

  getMassagesByCity(city: string): Observable<Massage[]> {
    return this.httpClient.get<Massage[]>(`${this.baseUrl}/${city}`);
  }

  getMassageById(id: string): Observable<Massage> {
    return this.httpClient.get<Massage>(`${this.baseUrl}/${id}`);
  }

  deleteMassage(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }

  getAllSymptoms(): Observable<Symptom[]> {
    return this.httpClient.get<Symptom[]>(`${this.symptomBaseUrl}`);
  }

  getSymptomsByMassage(massageId: string): Observable<Symptom[]> {
    return this.httpClient.get<Symptom[]>(`${this.symptomBaseUrl}/massage/${massageId}`);
  }
}
