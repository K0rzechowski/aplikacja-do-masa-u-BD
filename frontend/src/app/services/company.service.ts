import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Company, CompanyOpenTime } from '../models/company';
import { BehaviorSubject, catchError, Observable, ObservableInputTuple, tap, throwError } from 'rxjs';
import { Room } from '../models/room';


@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private baseUrl = `${environment.baseUrl}/companies`;

  private currentCompanySubject = new BehaviorSubject<Company | null>(null);
  public currentCompany = this.currentCompanySubject.asObservable();

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

  getCompanyByNameAndCity(name: string, city: string): Observable<Company> {
    return this.httpClient.get<Company>(`${this.baseUrl}/${city}/${name}`);
  }

  getCompaniesByCity(city: string): Observable<Company[]> {
    return this.httpClient.get<Company[]>(`${this.baseUrl}/city/${city}`);
  }

  deleteCompany(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/remove/${id}`);
  }

  setCurrentCompany(company: Company) {
    this.currentCompanySubject.next(company);
  }

  createCompanyOpenTime(openTime: CompanyOpenTime): Observable<CompanyOpenTime> {
    return this.httpClient.post<CompanyOpenTime>(`${this.baseUrl}/${openTime.companyId}/addOpenTime`, openTime);
  }

  editCompanyOpenTime(openTime: CompanyOpenTime): Observable<CompanyOpenTime> {
    return this.httpClient.put<CompanyOpenTime>(`${this.baseUrl}/${openTime.companyId}/editOpenTime`, openTime);
  }

  getCompanyOpenTime(companyId: string): Observable<CompanyOpenTime> {
    return this.httpClient.get<CompanyOpenTime>(`${this.baseUrl}/${companyId}/openTime`);
  }

  createCompanyRoom(room: Room): Observable<Room> {
    return this.httpClient.post<Room>(`${this.baseUrl}/${room.companyId}/room`, room);
  }

  getCompanyRoomByName(room: Room): Observable<Room> {
    return this.httpClient.get<Room>(`${this.baseUrl}/${room.companyId}/room${room.id}`);
  }

  getCompanyRooms(companyId: String): Observable<Room[]> {
    return this.httpClient.get<Room[]>(`${this.baseUrl}/${companyId}/rooms`);
  }

  editCompanyRoom(room: Room): Observable<Room> {
    return this.httpClient.put<Room>(`${this.baseUrl}/${room.companyId}/room/edit/${room.id}`, room);
  }

  deleteRoom(companyId: string, roomId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${companyId}/room/remove/${roomId}`);
  }
}
