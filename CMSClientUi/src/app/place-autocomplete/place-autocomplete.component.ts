// https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html
/// <reference types="@types/google.maps" />

import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-place-autocomplete',
  standalone: true,
  imports: [
    MatFormFieldModule,
    FormsModule, 
    ReactiveFormsModule,
    MatInputModule
  ],
  templateUrl: './place-autocomplete.component.html',
  styleUrl: './place-autocomplete.component.scss'
})
export class PlaceAutocompleteComponent implements AfterViewInit {
  //@Input() address1autocomplete: string;
  @Input() address1FormControl: FormControl;
  @Output() setAddress: EventEmitter<any> = new EventEmitter();
  @ViewChild('address1Field') address1Field: any;

  ngAfterViewInit(): void {
    this.getPlaceAutocomplete();
  }

  private getPlaceAutocomplete() {
    // Create the autocomplete object, restricting the search predictions to
    // addresses in Canada.
    const autocomplete : google.maps.places.Autocomplete = new google.maps.places.Autocomplete(this.address1Field.nativeElement,
    {
      componentRestrictions: { country: ["ca"] },
      fields: ["address_components", "geometry"],
      types: ["address"],
    });

    // nativeElement is a property of ElementRef that gives you direct access to the native DOM element. 
    // When you access nativeElement on an ElementRef instance, you're essentially bypassing Angular's abstraction 
    // and gaining direct access to the underlying DOM element.
    //this.address1Field.nativeElement.focus();
    this.address1Field.nativeElement.autocomplete = "off";

    google.maps.event.addListener(autocomplete, 'place_changed', () => {
        const place : google.maps.places.PlaceResult = autocomplete.getPlace();
        this.invokeEvent(place);
    });
  }

  private invokeEvent(place: google.maps.places.PlaceResult) {
    this.setAddress.emit(place);
  }
}
