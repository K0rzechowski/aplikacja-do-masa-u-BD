import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { BranchOffice } from '../models/branch-office';
import { CompanyOpenTime } from '../models/company';
import { Room } from '../models/room';

@Injectable({
  providedIn: 'root'
})
export class BranchOfficeService {

  private baseUrl = `${environment.baseUrl}/branches`;

  constructor(private httpClient: HttpClient) { }

  private handleError(defaultMessage: string) {
    return (error: HttpErrorResponse) => {
      const errorMessage = error.error?.message || error.error?.detail || defaultMessage;
      return throwError(() => new Error(errorMessage));
    };
  }

  createBranchOffice(branchOffice: BranchOffice): Observable<BranchOffice> {
    return this.httpClient.post<BranchOffice>(`${this.baseUrl}/company/${branchOffice.companyId}`, branchOffice).pipe(
      catchError(this.handleError('Błąd podczas tworzenia placówki'))
    );
  }

  getBranchOfficesByCompany(companyId: string): Observable<BranchOffice[]> {
    return this.httpClient.get<BranchOffice[]>(`${this.baseUrl}/company/${companyId}`);
  }

  getBranchOfficeById(id: string): Observable<BranchOffice> {
    return this.httpClient.get<BranchOffice>(`${this.baseUrl}/${id}`);
  }

  editBranchOffice(branchOffice: BranchOffice): Observable<BranchOffice> {
    return this.httpClient.put<BranchOffice>(`${this.baseUrl}/${branchOffice.id}`, branchOffice).pipe(
      catchError(this.handleError('Błąd podczas edycji placówki'))
    );
  }

  deleteBranchOffice(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`).pipe(
      catchError(this.handleError('Błąd podczas usuwania placówki'))
    );
  }

  setOpenTime(openTime: CompanyOpenTime): Observable<CompanyOpenTime> {
    return this.httpClient.post<CompanyOpenTime>(`${this.baseUrl}/${openTime.branchOfficeId}/addOpenTime`, openTime).pipe(
      catchError(this.handleError('Błąd podczas zapisywania godzin otwarcia'))
    );
  }

  editOpenTime(openTime: CompanyOpenTime): Observable<CompanyOpenTime> {
    return this.httpClient.put<CompanyOpenTime>(`${this.baseUrl}/${openTime.branchOfficeId}/editOpenTime`, openTime).pipe(
      catchError(this.handleError('Błąd podczas zapisywania godzin otwarcia'))
    );
  }

  getOpenTime(branchOfficeId: string): Observable<CompanyOpenTime> {
    return this.httpClient.get<CompanyOpenTime>(`${this.baseUrl}/${branchOfficeId}/openTime`);
  }

  createRoom(room: Room): Observable<Room> {
    return this.httpClient.post<Room>(`${this.baseUrl}/${room.branchOfficeId}/room`, room).pipe(
      catchError(this.handleError('Błąd podczas dodawania sali'))
    );
  }

  getRooms(branchOfficeId: string): Observable<Room[]> {
    return this.httpClient.get<Room[]>(`${this.baseUrl}/${branchOfficeId}/rooms`);
  }

  editRoom(room: Room): Observable<Room> {
    return this.httpClient.put<Room>(`${this.baseUrl}/${room.branchOfficeId}/room/edit/${room.id}`, room).pipe(
      catchError(this.handleError('Błąd podczas edycji sali'))
    );
  }

  deleteRoom(branchOfficeId: string, roomId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${branchOfficeId}/room/remove/${roomId}`).pipe(
      catchError(this.handleError('Błąd podczas usuwania sali'))
    );
  }
}
