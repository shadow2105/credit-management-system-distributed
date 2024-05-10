import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, interval, of, tap } from 'rxjs';
import { environment as env } from '../../../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authStatus = new BehaviorSubject<boolean>(false);
  private userInfo: any = null;
  private sessionTimeout = 300; // 5 minutes in seconds
  private activityInterval = 10000; // check every 10 seconds
  private lastActivityTime = new Date().getTime();

  constructor(private http: HttpClient, private router: Router) {
    this.startInactivityTimer();
   }

  login(): void {
    const url = `${env.clientUri}/oauth2/authorization/cms`;
    window.location.href = url;
  }

  signup() {
    const url = `${env.clientUri}/authn/signup`;
    window.location.href = url;
  }

  logout() {
    const url = `${env.clientUri}/logout`;
    window.location.href = url;
  }

  private startInactivityTimer() {
    interval(this.activityInterval).subscribe(() => {
      const currentTime = new Date().getTime();
      if (currentTime - this.lastActivityTime > this.sessionTimeout * 1000) {
        // Reset Authentication status
        this.authStatus.next(false);
        // Redirect to login on session timeout
        this.login(); 
      }
    });
  }

  userActivity() {
    // Reset timer on user activity - see this.initSession(), authInterceptor, UserActivityDirective
    this.lastActivityTime = new Date().getTime(); 
    //console.log("Resetting Timer.");
  }

  // https://stackoverflow.com/questions/27406994/http-requests-withcredentials-what-is-this-and-why-using-it
  // checkAuth(): Observable<any> {
  //   return this.http.get<any>(`${env.clientUri}/authn/status`, { withCredentials: true });
  // }

  initSession(): Observable<any> {
    return this.http.get(`${env.clientUri}/authn/status`, { withCredentials: true }).pipe(
      tap((response: any) => {
        const isAuthenticated = response.isAuthenticated;
        // Update auth status
        this.authStatus.next(isAuthenticated); 
        if (isAuthenticated) {
          this.userInfo = response.userData;
          // Reset timer on user activity
          this.userActivity();
        } 
        else {
          this.userInfo = null;
        } 
        // console.log("isAuthenticated: ", isAuthenticated);
        // console.log("userInfo: ", this.userInfo);
      }),
      catchError(() => {
        this.authStatus.next(false);
        return of(null);
      })
    );
  }

  isAuthenticated(): Observable<boolean> {
    return this.authStatus.asObservable();
  }

  getUserInfo(): any {
    return this.userInfo;
  }

  setAuthenticated(isAuthenticated: boolean) {
    this.authStatus.next(isAuthenticated);
  }
}
