import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { UpdateProfileRequest, User } from '../models/auth/user';
import { BehaviorSubject, catchError, finalize, map, Observable, of, tap, throwError } from 'rxjs';
import { AuthenticationResponse } from '../models/auth/authenticationResponse';
import { AuthenticationRequest } from '../models/auth/authenticationRequest';
import { RegisterRequest } from '../models/auth/registerRequest';
import { CompanyService } from './company.service';
import { Company } from '../models/company';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {
  
  private baseUrl = environment.userAuthUrl;

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private verififcationCompleteSubject = new BehaviorSubject<boolean>(false);
  private verificationInProgress = false;

  public currentUser = this.currentUserSubject.asObservable();
  public isAuthenticated = this.isAuthenticatedSubject.asObservable();
  public verificationComplete = this.verififcationCompleteSubject.asObservable(); 

  constructor(
    private httpClient: HttpClient,
    private companyService: CompanyService,
    private router: Router
  ) { 
    this.checkAuthStatus();
  }

  private verify(): Observable<User | null> {
    return this.httpClient.get<any>(`${this.baseUrl}/verify`).pipe(
      map((response: any) => {
        return response.status === "success" && response.user ? response.user : null;
      }),
      catchError(() => of(null))
    );
  }

  private checkRole(user: User) {
    if ((user.role === "ADMIN" || user.role === "EMPLOYEE") && user.companyId) {
      this.companyService.getCompanyById(user.companyId).subscribe((c: Company) => {
        this.companyService.setCurrentCompany(c);
      });
    }
  }

  private checkAuthStatus() {
    if (this.verificationInProgress) {
      return;
    }

    this.verificationInProgress = true;
    this.verify().pipe(
      tap((user) => {
        if (user) {
          this.currentUserSubject.next(user);
          this.isAuthenticatedSubject.next(true);
  
          this.checkRole(user);
          } else {
          this.isAuthenticatedSubject.next(false);
        }
      }),
      catchError(() => {
        this.isAuthenticatedSubject.next(false);
        return of(null);
      }),
      finalize(() => {
        this.verificationInProgress = false;
        this.verififcationCompleteSubject.next(true);
      })
    ).subscribe();
  }

  register(request: RegisterRequest): Observable<AuthenticationResponse> {
    return this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/register`, request).pipe(
      tap((response: any) => {
        this.currentUserSubject.next(response.user);
        this.isAuthenticatedSubject.next(true);
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
        this.isAuthenticatedSubject.next(true);

        this.checkRole(response.user);
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
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/']);
      }),
      catchError(error => {
        this.currentUserSubject.next(null);
        this.isAuthenticatedSubject.next(false);
        console.error('Logout error:', error);
        return throwError(() => error.error || 'Logout failed');
      })
    );
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.role === 'ADMIN' : false;
  }

  isEmployee(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.role === 'EMPLOYEE' : false;
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

  deleteUser(userId: string): Observable<any> {
    return this.httpClient.delete(`${this.baseUrl}/delete/${userId}`);
  }
}