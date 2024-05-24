/// <reference types="@types/google.maps" />

import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, Validators, ReactiveFormsModule, NgForm, FormGroup, Form, FormBuilder } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { AuthService } from '../shared/services/auth/auth.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatButtonModule } from '@angular/material/button';
import { PlaceAutocompleteComponent } from '../place-autocomplete/place-autocomplete.component';
import { Router } from '@angular/router';
import { CompleteProfileService } from '../shared/services/complete-profile/complete-profile.service';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-complete-profile-dialog-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatDatepickerModule,
    MatButtonToggleModule,
    MatButtonModule,
    PlaceAutocompleteComponent,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './complete-profile-dialog-form.component.html',
  styleUrl: './complete-profile-dialog-form.component.scss'
})
export class CompleteProfileDialogFormComponent implements OnInit {
  userInfo: any = null;
  address1: string = ' ';
  completeProfileForm: FormGroup;
  address1FormControl: FormControl;

  @ViewChild('address2Field') address2Field: any; 

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private completeProfileService: CompleteProfileService,
  ) { }
  
  ngOnInit(): void {
    this.userInfo = this.authService.getUserInfo();
    this.address1FormControl = new FormControl('', Validators.required);
    this.initForm(this.userInfo);
  }

  initForm(userInfo: any) : void {
    this.completeProfileForm = this.fb.group({
      preferredName: [userInfo.preferred_username],
      email: [userInfo.email, [Validators.required, Validators.email]],
      dob: ['', Validators.required],
      gender: ['', Validators.required],
      timeZone: [userInfo.zoneinfo, Validators.required],
      locale: [userInfo.locale],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      address1: [''],
      address2: [''],
      locality: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required],
      sin: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      occupation: ['']
    });
  }

  clearForm() : void {
    //console.log("Clearing Form");
    this.completeProfileForm.reset();
    this.address1FormControl.reset();
  }

  redirectToHomeOnDialogClose() : void {
    this.router.navigateByUrl('home');
  }

  submitForm() : void {
    // console.log("Submitting Form");
    this.completeProfileForm.get('address1')?.setValue(this.address1FormControl.value);
    if (this.completeProfileForm.valid) {
      
      console.log(this.completeProfileForm.value);

      this.completeProfileService.updateProfile(
        this.userInfo.sub,
        this.completeProfileForm.value
      ).subscribe({
        next: (v) => console.log('Profile updated successfully', v),
        error: (e) => this.handleError(e),
        complete: () => console.info('complete') 
    })
      
      this.completeProfileForm.reset();
    }
  }

    fillInAddress(place: google.maps.places.PlaceResult) : void {
    // Get the place details from the autocomplete object in PlaceAutocompleteComponent.
    let address1 = "";
    let postcode = "";
  
    // Get each component of the address from the place details,
    // and then fill-in the corresponding field on the form.
    // place.address_components are google.maps.GeocoderAddressComponent objects
    for (const component of place.address_components as google.maps.GeocoderAddressComponent[]) {
      const componentType = component.types[0];
  
      switch (componentType) {
        case "street_number": {
          address1 = `${component.long_name} ${address1}`;
          break;
        }
  
        case "route": {
          address1 += component.short_name;
          break;
        }
  
        case "postal_code": {
          postcode = `${component.long_name}${postcode}`;
          break;
        }
  
        case "postal_code_suffix": {
          postcode = `${postcode}-${component.long_name}`;
          break;
        }
  
        case "locality":
          //this.localityFormControl.setValue(component.long_name);
          this.completeProfileForm.controls['locality'].setValue(component.long_name);
          break;

        case "administrative_area_level_1":
          //this.stateFormControl.setValue(component.short_name);
          this.completeProfileForm.controls['state'].setValue(component.short_name);
          break;

        case "country":
          //this.countryFormControl.setValue(component.long_name);
          this.completeProfileForm.controls['country'].setValue(component.long_name);
          break;
      }
    }
  
    //this.address1 = address1;
    this.address1FormControl.setValue(address1);
    //this.postalCodeFormControl.setValue(postcode);
    this.completeProfileForm.controls['postalCode'].setValue(postcode);
  
    // After filling the form with address components from the Autocomplete
    // prediction, set cursor focus on the second address line to encourage
    // entry of subpremise information such as apartment, unit, or floor number.
    this.address2Field.nativeElement.focus();

  }

  private handleError(error: HttpErrorResponse) {
    // A client-side or network error occurred
    if (error.error instanceof ErrorEvent) {
      console.error("An error ocurred: ", error.error.message);
    }
    // server returned an unsuccessful response code
    else {
      console.error('Error status: ', error.status);
      console.error('Error details: ', error.error);
    }

    return throwError(() => new Error('Something bad happend, please try again later.'));
  }
}
