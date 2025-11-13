import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Company } from '../models/company';
import { catchError, Observable, tap, throwError } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private baseUrl = `${environment.baseUrl}/companies`;

  constructor(private httpClient: HttpClient) { }

  createCompany(request: Company): Observable<Company> {
    return this.httpClient.post<Company>(`${this.baseUrl}/create`, request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Błąd podczas tworzenia firmy';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 409 ) {
          errorMessage = 'Firma o danej nazwie i danym mieście już istnieje';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  getAll(): Observable<Company[]> {
    return this.httpClient.get<Company[]>(`${this.baseUrl}/getAll`);
  }

  getCompanyById(id: string): Observable<Company> {
    return this.httpClient.get<Company>(`${this.baseUrl}/${id}`);
  }

  deleteCompany(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }
}
