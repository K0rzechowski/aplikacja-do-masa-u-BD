import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Availability } from '../models/availability';
import { Observable, ObservableInputTuple } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AvailabilityService {
  private baseUrl = `${environment.baseUrl}/availability`;

  constructor(private httpClient: HttpClient) { }

  setAvailability(request: Availability):Observable<void> {
    return this.httpClient.post<void>(`${this.baseUrl}/employee/${request.employeeId}`, request);
  }

  getAvailabilityForWeek(employeeId: string, startDate: Date): Observable<Availability[]> {
    const endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 7);

    const params = new HttpParams()
      .set('start', startDate.toISOString().substring(0, 10))
      .set('end', endDate.toISOString().substring(0, 10));

    return this.httpClient.get<Availability[]>(`${this.baseUrl}/employee/${employeeId}`, {params});
  }

  getAvailabilityByEmployeeAndStartDate(employeeId: string, startDate: string): Observable<Availability> {
    return this.httpClient.get<Availability>(`${this.baseUrl}/employee/${employeeId}/${startDate}`);
  }

  editAvailability(request: Availability): Observable<Availability> {
    return this.httpClient.put<Availability>(`${this.baseUrl}/employee/${request.employeeId}`,request);
  }
}
