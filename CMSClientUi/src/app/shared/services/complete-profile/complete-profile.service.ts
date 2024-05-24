import { Injectable } from '@angular/core';
import { environment as env } from '../../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CompleteProfileService {
 
  constructor(private http: HttpClient) { }

  updateProfile(customerId: string, profileData: any): Observable<any> {
    const uri = `${env.clientUri}/api/v1/customer/${customerId}/profile`;

    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http.patch(uri, profileData, { headers });
  }
}
