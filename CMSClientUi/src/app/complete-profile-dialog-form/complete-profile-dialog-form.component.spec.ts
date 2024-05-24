import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompleteProfileDialogFormComponent } from './complete-profile-dialog-form.component';

describe('CompleteProfileDialogFormComponent', () => {
  let component: CompleteProfileDialogFormComponent;
  let fixture: ComponentFixture<CompleteProfileDialogFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompleteProfileDialogFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CompleteProfileDialogFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
