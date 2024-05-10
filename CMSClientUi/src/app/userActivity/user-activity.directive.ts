import { Directive, HostListener, Input } from '@angular/core';
import { AuthService } from '../shared/services/auth/auth.service';

@Directive({
  selector: '[appUserActivity]',
  standalone: true
})
export class UserActivityDirective {

  @Input() isActive = false;

  constructor(private authService: AuthService) { }

  @HostListener('click', ['$event'])
  @HostListener('keydown', ['$event'])
  @HostListener('mousemove', ['$event'])
  onUserActivity(event: Event) {
    //console.log("User Activity Detected!")
    // Reset timer on user activity if user is authenticated
    if (this.isActive) {
      this.authService.userActivity();  
    }         
  }

}


/*
The '$event' parameter (optional) in @HostListener allows you to capture the event object and access event-specific
information within the listener method. It's useful when you need to extract data from the event or perform 
context-based actions. 
*/