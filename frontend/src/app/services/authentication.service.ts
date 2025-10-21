import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { UpdateProfileRequest, User } from '../models/auth/user';
import { BehaviorSubject, catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { AuthenticationResponse } from '../models/auth/authenticationResponse';
import { AuthenticationRequest } from '../models/auth/authenticationRequest';
import { RegisterRequest } from '../models/auth/registerRequest';

@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {
  
  private baseUrl = environment.userAuthUrl;

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);

  public currentUser = this.currentUserSubject.asObservable();
  public isAuthenticated = this.isAuthenticatedSubject.asObservable();

  constructor(private httpClient: HttpClient) { 
    this.checkAuthStatus();
  }

  private checkAuthStatus() {
    this.verify().subscribe({
      next: (isAuth) => {
        this.isAuthenticatedSubject.next(isAuth);
      },
      error: () => {
        this.isAuthenticatedSubject.next(false);
      }
    })
  }

  register(request: RegisterRequest): Observable<AuthenticationResponse> {
    return this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/register`, request).pipe(
      tap((response: any) => {
        this.currentUserSubject.next(response.user);
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Rejestracja nie powiodła się';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 409 ) {
          errorMessage = 'Podany email jest już przypisany do konta';
        } else if (error.status === 400 ) {
          errorMessage = 'Wprowadzono niepoprawne dane';
        } else if (error.status === 0) {
          errorMessage = 'Nie można połączyć się z serwerem';
        } else if (error.status >= 500) {
          errorMessage = 'Wystąpił błąd serwera';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  auth(request: AuthenticationRequest): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/authenticate`, request).pipe(
      tap((response: any) => {
        this.currentUserSubject.next(response.user);
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Nieudana autoryzacja';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 401 || error.status === 403 ) {
          errorMessage = 'Wprowadzono niepoprawny email lub hasło';
        } else if (error.status === 0) {
          errorMessage = 'Nie można połączyć się z serwerem';
        } else if (error.status >= 500) {
          errorMessage = 'Wystąpił błąd serwera';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  logout(): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/logout`, {}).pipe(
      tap(() => {
        this.currentUserSubject.next(null);
      }),
      catchError(error => {
        console.error('Logout error:', error);
        return throwError(() => error.error || 'Logout failed');
      })
    );
  }

  private verify(): Observable<boolean> {
    return this.httpClient.get<any>(`${this.baseUrl}/verify`).pipe(
      map((response: { status: string; }) => response.status === "success"),
      catchError(() => of(false))
    );
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user? user.role === 'ADMIN' : false;
  }

  isEmployee(): boolean {
    const user = this.currentUserSubject.value;
    return user? user.role === 'EMPLOYEE' : false;
  }

  updateProfile(request: UpdateProfileRequest, user: User): Observable<any> {
    return this.httpClient.put(`${this.baseUrl}/update/${user.id}`, request).pipe(
      tap((updatedUser: UpdateProfileRequest) => {
        const currentUser = this.currentUserSubject.value;
        const mergedUser = { ...currentUser, ...updatedUser } as User;
        this.currentUserSubject.next(mergedUser);
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Nieudana edycja profilu';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 401 || error.status === 403 ) {
          errorMessage = 'Wprowadzono niepoprawne aktualne hasło';
        } else if (error.status === 0) {
          errorMessage = 'Nie można połączyć się z serwerem';
        } else if (error.status >= 500) {
          errorMessage = 'Wystąpił błąd serwera';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}