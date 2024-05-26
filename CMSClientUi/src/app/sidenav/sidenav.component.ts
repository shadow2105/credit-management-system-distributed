import { Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    RouterLink,
    MatButtonModule
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent {
  @Input() profilePicture: string = '';
  @Input() preferredName: string = '';
  @Input() email: string = '';
  links = [
    {"label": "Credit Accounts", "icon": "account_balance_wallet", "path": "dashboard"},
    {"label": "Transactions", "icon": "attach_money", "path": "transactions"},
    {"label": "Credit Statements", "icon": "file_copy", "path": "credit-statements"},
  ]

  constructor() { }
}
