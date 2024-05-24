import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CompleteProfileDialogFormComponent } from '../complete-profile-dialog-form/complete-profile-dialog-form.component';

@Component({
  selector: 'app-complete-profile-dialog',
  standalone: true,
  imports: [MatDialogModule],
  templateUrl: './complete-profile-dialog.component.html',
  styleUrl: './complete-profile-dialog.component.scss'
})
export class CompleteProfileDialogComponent implements OnInit {
  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    this.openDialog();
  }

  openDialog() : void {
    const dialogRef = this.dialog.open(CompleteProfileDialogFormComponent, {
      width: '800px',
      height: '600px',
      disableClose: true,
    });

    // dialogRef.afterClosed().subscribe(result => {
    //   console.log(`Dialog result: ${result}`);
    // });
  }
}