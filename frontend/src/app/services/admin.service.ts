import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Admin } from '../models/admin';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  
  private baseUrl = `${environment.baseUrl}/admins`;

  constructor(private httpClient: HttpClient) { }

  createAdmin(request: Admin): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/create`, request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Nieudana autoryzacja';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 409 ) {
          errorMessage = 'Użytkownik o podanym emailu już istnieje';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
