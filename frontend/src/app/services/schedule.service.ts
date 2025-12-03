import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, map, Observable, of } from 'rxjs';
import { OptimizedScheduleResponse } from '../models/optaplanner/optimized-schedule-response';
import { GenerateShiftResponse } from '../models/optaplanner/generate-shift';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  private baseUrl = `${environment.baseUrl}/schedule`;
  private shiftUrl = `${environment.baseUrl}/shifts`;

  constructor(private httpClient: HttpClient) { }

  generateShifts(companyId: string, startTime: string): Observable<GenerateShiftResponse> {
    const body = {companyId: companyId, startDate: startTime};
    return this.httpClient.post<GenerateShiftResponse>(`${this.shiftUrl}/generate`, body);
  }

  optimizeSchedule(companyId: string, startTime: string, endTime: string): Observable<OptimizedScheduleResponse> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.post<OptimizedScheduleResponse>(`${this.baseUrl}/optimize/${companyId}`, null, { params: params});
  }

  getOptimizedSchedule(companyId: string, startTime: string, endTime: string): Observable<OptimizedScheduleResponse> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.get<OptimizedScheduleResponse>(`${this.baseUrl}/company/${companyId}`, { params: params});
  }
}
