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
import { UserActivityDirective } from './userActivity/user-activity.directive';

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
    CommonModule,
    UserActivityDirective
  ],
})
export class AppComponent implements OnInit {
  title: string = 'Credit Managment System';
  isAuthenticated : boolean = false;

  //constructor(private oidcSecurityService: OidcSecurityService) { }
  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    // Initialize session and then check auth status to redirect if needed
    this.authService.initSession().subscribe(() => {
      this.authService.isAuthenticated().subscribe((isAuthenticated) => {
        this.isAuthenticated = isAuthenticated;
        if (isAuthenticated) {
          // Redirect to the dashboard
          this.router.navigateByUrl('dashboard'); 
        }
      });
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

