import { AfterViewChecked, AfterViewInit, Component, OnInit } from '@angular/core';
import { NavigationStart, Router, RouterModule, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { CommonModule } from '@angular/common';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { environment } from '../environments/environment';
import { AuthService } from './shared/services/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  imports: [
    RouterOutlet,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatToolbarModule,
    MatMenuModule,
    MatDividerModule,
    CommonModule

  ],
})
export class AppComponent implements OnInit {
  title: string = 'Credit Managment System';
  isAuthenticated: boolean = false;
  userData: any = null;

  //constructor(private oidcSecurityService: OidcSecurityService) { }
  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.authService.checkAuth()
      .subscribe(({ isAuthenticated, userData }) => {
        this.isAuthenticated = isAuthenticated;
        this.userData = userData;

        // console.log("authenticated: ", isAuthenticated);
        // console.log("userData:", userData);

        if (this.isAuthenticated) {
          this.router.navigateByUrl('dashboard');
        }
      });
  }

  login() {
    this.authService.login();

    //this.oidcSecurityService.authorize();
  }

  signup() {
    this.authService.signup();
  }

  // https://stackoverflow.com/questions/70735327/spring-oauth2-authorization-server-unable-to-logout-users/70777564#70777564
  logout() {
    this.authService.logout();

    // this.oidcSecurityService
    //   .logoff()
    //   .subscribe((result) => console.log(result));
  }
}
