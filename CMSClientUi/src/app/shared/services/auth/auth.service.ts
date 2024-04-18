import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment as env } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(): void {
    const url = `${env.clientUri}/oauth2/authorization/shd`;
    window.location.href = url;
  }

  // https://stackoverflow.com/questions/27406994/http-requests-withcredentials-what-is-this-and-why-using-it
  checkAuth(): Observable<any> {
    return this.http.get<any>(`${env.clientUri}/authn/status`, { withCredentials: true });
  }

  signup() {
    const url = `${env.clientUri}/authn/signup`;
    window.location.href = url;
  }

  logout() {
    const url = `${env.clientUri}/logout`;
    window.location.href = url;
  }
}
