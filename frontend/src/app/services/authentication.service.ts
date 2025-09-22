import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/auth/user';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { AuthenticationResponse } from '../models/auth/authenticationResponse';
import { AuthenticationRequest } from '../models/auth/authenticationRequest';
import { RegisterRequest } from '../models/auth/registerRequest';


@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {
  
  private baseUrl = environment.userAuthUrl;
  private currentUserSubject = new BehaviorSubject<AuthenticationRequest | null>(null);
  public currentUser = this.currentUserSubject.asObservable();

  constructor(private httpClient: HttpClient) { 
    // this.checkAuthenticationStatus();
  }

  register(request: RegisterRequest): Observable<AuthenticationResponse> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    };

    return this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/register`, request, httpOptions).pipe(
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
        if(response.status === 'success'){
          this.currentUserSubject.next(response.user);
        } 
      }),
      catchError(error => {
        console.error('Authenticationentication error:', error);
        return throwError(() => error.error || 'Authenticationentication failed');
      })
    );
  }

  // checkAuthenticationStatus(): void {
  //   this.httpClient.get(`${this.baseUrl}/user`).subscribe({
  //     next: (user: any) => this.currentUserSubject.next(user),
  //     error: () => this.currentUserSubject.next(null)
  //   })
  // }

  // logout(): Observable<any> {
  //   const httpOptions = {
  //     withCredentials: true
  //   };

  //   return this.httpClient.post(`${this.baseUrl}/logout`, {}, httpOptions).pipe(
  //     tap(() => {
  //       this.currentUserSubject.next(null);
  //     }),
  //     catchError(error => {
  //       console.error('Logout error:', error);
  //       return throwError(() => error.error || 'Logout failed');
  //     })
  //   );
  // }

  isAuthenticationenticated(): boolean {
    return this.currentUserSubject.value != null;
  }

  // getCurrentUser(): 

}