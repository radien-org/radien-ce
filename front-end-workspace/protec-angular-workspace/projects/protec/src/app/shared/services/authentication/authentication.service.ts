import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { map, pluck, switchMap, takeUntil, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService implements OnDestroy {

  public get currentUser(): AuthenticatedUser | null {}

  get token(): string {}

  get jwtToken(): string {}

  get rememberMe(): string {}

  constructor(private readonly http: HttpClient,private readonly router: Router) {}
  
  public login(data: { email: string; password: string; rememberMe: boolean }): Observable<boolean> {}

  public logout(): void {}

  public tokenExpired(token: string) {}
}
