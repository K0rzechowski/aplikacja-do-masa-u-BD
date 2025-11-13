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

  getEmployeesByAdminsCompany(): Observable<Employee[]> {
    return this.httpClient.get<Employee[]>(`${this.baseUrl}`);
  }

  deleteEmployee(employeeId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${employeeId}`);
  }

  editEmployee(employee: Employee): Observable<Employee> {
    return this.httpClient.put<Employee>(`${this.baseUrl}/edit/${employee.id}`, employee);
  }

  createEmployee(employee: Partial<Employee>): Observable<Employee> {
    const createRequest = {
      name: employee.name,
      surname: employee.surname,
      phoneNumber: employee.phoneNumber,
      dateOfBirth: employee.dateOfBirth,
      email: employee.email,
      password: employee.password,
      massageIds: employee.massageIds || []
    };
        
    return this.httpClient.post<Employee>(`${this.baseUrl}/create`, createRequest);
  }
}
