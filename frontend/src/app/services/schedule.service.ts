import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { catchError, forkJoin, map, Observable, of } from 'rxjs';
import { OptimizedScheduleResponse } from '../models/optaplanner/optimized-schedule-response';
import { GenerateShiftResponse } from '../models/optaplanner/generate-shift';
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

  generateShifts(companyId: string, startTime: string): Observable<GenerateShiftResponse> {
    const body = { companyId: companyId, startDate: startTime };
    return this.httpClient.post<GenerateShiftResponse>(`${this.shiftUrl}/generate`, body);
  }

  getOptimizedSchedule(companyId: string, startTime: string, endTime: string): Observable<OptimizedScheduleResponse> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.httpClient.get<OptimizedScheduleResponse>(`${this.baseUrl}/company/${companyId}`, { params: params });
  }

  getShiftsWithoutReservations(companyId: string, startTime: string, endTime: string): Observable<OptimizedScheduleResponse> {
    return forkJoin({
      schedule: this.getOptimizedSchedule(companyId, startTime, endTime),
      reservations: this.reservationService.getReservationsByCompany(companyId).pipe(
        catchError(() => of([]))
      )
    }).pipe(
      map(({ schedule, reservations }) => {
        const shiftIdsWithReservations = new Set(
          reservations
            .filter(reservation => reservation.status !== 'CANCELLED')
            .map(reservation => reservation.shift.id)
        );

        const shiftsWithoutReservations = schedule.shifts.filter(
          shift => !shiftIdsWithReservations.has(shift.id)
        );

        return {
          ...schedule,
          shifts: shiftsWithoutReservations,
          shiftsOptimized: shiftsWithoutReservations.length
        };
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

  getWeeklyShifts(companyId: string): Observable<{
    currentWeekShifts: any[],
    nextWeekShifts: any[],
    showNextWeek: boolean
  }> {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const endOfWeek = this.getEndOfWeek(today);
    const startOfNextWeek = this.getStartOfNextWeek(today);
    const endOfNextWeek = this.getEndOfNextWeek(today);

    return forkJoin({
      currentWeek: this.getShiftsWithoutReservations(
        companyId,
        today.toISOString(),
        endOfWeek.toISOString()
      ),
      nextWeek: this.getShiftsWithoutReservations(
        companyId,
        startOfNextWeek.toISOString(),
        endOfNextWeek.toISOString()
      )
    }).pipe(
      map(({ currentWeek, nextWeek }) => ({
        currentWeekShifts: currentWeek.shifts,
        nextWeekShifts: nextWeek.shifts,
        showNextWeek: nextWeek.shifts.length > 0
      }))
    );
  }
}
