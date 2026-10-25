import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, forkJoin, map, Observable, of, throwError } from 'rxjs';
import { OptimizedScheduleResponse } from '../models/optaplanner/optimized-schedule-response';
import { GenerateShiftRequest, GenerateShiftResponse } from '../models/optaplanner/generate-shift';
import { Shift } from '../models/shift';
import { ReservationService } from './reservation.service';
import { Reservation } from '../models/reservation';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  private baseUrl = `${environment.baseUrl}/schedule`;
  private shiftUrl = `${environment.baseUrl}/shifts`;

  constructor(
    private httpClient: HttpClient,
    private reservationService: ReservationService
  ) { }

  generateShifts(branchOfficeId: string, startDate: string): Observable<GenerateShiftResponse> {
    const body: GenerateShiftRequest = { branchOfficeId, startDate };
    return this.httpClient.post<GenerateShiftResponse>(`${this.shiftUrl}/generate`, body);
  }

  getOptimizedSchedule(companyId: string, startTime: string, endTime: string): Observable<OptimizedScheduleResponse> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.get<OptimizedScheduleResponse>(`${this.baseUrl}/company/${companyId}`, { params: params });
  }

  getShiftsByBranchOffice(branchOfficeId: string, startTime: string, endTime: string): Observable<Shift[]> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.get<Shift[]>(`${this.shiftUrl}/branch/${branchOfficeId}`, { params: params });
  }

  getMyShifts(startTime: string, endTime: string): Observable<Shift[]> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.get<Shift[]>(`${this.shiftUrl}/my`, { params: params });
  }

  signUp(shiftId: string): Observable<Shift> {
    return this.httpClient.post<Shift>(`${this.shiftUrl}/${shiftId}/signup`, null).pipe(
      catchError(this.handleError('Nie udało się zapisać na zmianę'))
    );
  }

  signOff(shiftId: string): Observable<Shift> {
    return this.httpClient.delete<Shift>(`${this.shiftUrl}/${shiftId}/signup`).pipe(
      catchError(this.handleError('Nie udało się wypisać ze zmiany'))
    );
  }

  private handleError(defaultMessage: string) {
    return (error: HttpErrorResponse) => {
      const errorMessage = error.error?.message || error.error?.detail || defaultMessage;
      return throwError(() => new Error(errorMessage));
    };
  }

  getShiftsWithoutReservations(branchOfficeId: string, companyId: string, startTime: string, endTime: string): Observable<Shift[]> {
    return forkJoin({
      shifts: this.getShiftsByBranchOffice(branchOfficeId, startTime, endTime),
      reservations: this.reservationService.getReservationsByCompany(companyId).pipe(
        catchError(() => of([] as Reservation[]))
      )
    }).pipe(
      map(({ shifts, reservations }) => {
        const shiftIdsWithReservations = new Set(
          reservations
            .filter(reservation => reservation.status !== 'CANCELLED')
            .map(reservation => reservation.shift.id)
        );

        return shifts.filter(shift => !shiftIdsWithReservations.has(shift.id));
      })
    );
  }

  getEndOfWeek(date: Date): Date {
    const end = new Date(date);
    const day = end.getDay();
    const diff = 7 - day;
    end.setDate(end.getDate() + diff);
    end.setHours(23, 59, 59, 999);
    return end;
  }

  getStartOfNextWeek(date: Date): Date {
    const endOfWeek = this.getEndOfWeek(date);
    const start = new Date(endOfWeek);
    start.setDate(start.getDate() + 1);
    start.setHours(0, 0, 0, 0);
    return start;
  }

  getEndOfNextWeek(date: Date): Date {
    const startOfNextWeek = this.getStartOfNextWeek(date);
    return this.getEndOfWeek(startOfNextWeek);
  }

  getWeeklyShifts(branchOfficeId: string, companyId: string): Observable<{
    currentWeekShifts: Shift[],
    nextWeekShifts: Shift[],
    showNextWeek: boolean
  }> {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const endOfWeek = this.getEndOfWeek(today);
    const startOfNextWeek = this.getStartOfNextWeek(today);
    const endOfNextWeek = this.getEndOfNextWeek(today);

    return forkJoin({
      currentWeek: this.getShiftsWithoutReservations(
        branchOfficeId,
        companyId,
        today.toISOString(),
        endOfWeek.toISOString()
      ),
      nextWeek: this.getShiftsWithoutReservations(
        branchOfficeId,
        companyId,
        startOfNextWeek.toISOString(),
        endOfNextWeek.toISOString()
      )
    }).pipe(
      map(({ currentWeek, nextWeek }) => ({
        currentWeekShifts: currentWeek,
        nextWeekShifts: nextWeek,
        showNextWeek: nextWeek.length > 0
      }))
    );
  }
}
