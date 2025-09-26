import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/auth/user';
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
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    };

    return this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/register`, request, httpOptions).pipe(
      tap((response: any) => {
        this.currentUserSubject.next(response.user);
        console.log(response.user);
      }),
      catchError(error => {
        console.error('Register error:', error);
        return throwError(() => error.error || 'Register failed');
      })
    );
  }

  auth(request: AuthenticationRequest): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    };

    return this.httpClient.post(`${this.baseUrl}/authenticate`, request, httpOptions).pipe(
      tap((response: any) => {
        this.currentUserSubject.next(response.user);
      }),
      catchError(error => {
        console.error('Authenticationentication error:', error);
        return throwError(() => error.error || 'Authenticationentication failed');
      })
    );
  }

  logout(): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    };

    return this.httpClient.post(`${this.baseUrl}/logout`, {}, httpOptions).pipe(
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
    const httpOptions = {
      withCredentials: true
    };

    return this.httpClient.get<any>(`${this.baseUrl}/verify`,  httpOptions).pipe(
      map((response: { status: string; }) => response.status === "success"),
      catchError(() => of(false))
    );
  }

}