import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Employee } from '../models/employee';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  
  private baseUrl = `${environment.baseUrl}/employees`;

  constructor (
    private httpClient: HttpClient
  ) {}

  // TODO
  getAll(): Observable<Employee[]> {
    return this.httpClient.get<Employee[]>(`${this.baseUrl}/getAll`);
  }

  // TODO
  getEmployeesByAdminsCompany(): Observable<Employee[]> {
    return this.httpClient.get<Employee[]>(`${this.baseUrl}`);
  }

  deleteEmployee(employeeId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${employeeId}`);
  }
}
