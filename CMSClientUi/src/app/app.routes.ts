import { Routes } from '@angular/router';
import { AboutComponent } from './about/about.component';
import { HomeComponent } from './home/home.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { authGuard } from './shared/guards/auth/auth.guard';
import { CompleteProfileDialogComponent } from './complete-profile-dialog/complete-profile-dialog.component';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'about', component: AboutComponent },
    { path: 'unauthorized', component: UnauthorizedComponent },
    { path: 'complete-profile', component: CompleteProfileDialogComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: '/' },
];
