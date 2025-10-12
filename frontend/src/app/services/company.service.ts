import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Company } from '../models/company';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private baseUrl = `${environment.baseUrl}/companies`;

  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'}),
    withCredentials: true
  };

  constructor(private httpClient: HttpClient) { }

  createCompany(request: Company): Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/create`, request, this.httpOptions);
  }

  getAll(): Observable<Company[]> {
    return this.httpClient.get<Company[]>(`${this.baseUrl}/getAll`, this.httpOptions);
  }
}
