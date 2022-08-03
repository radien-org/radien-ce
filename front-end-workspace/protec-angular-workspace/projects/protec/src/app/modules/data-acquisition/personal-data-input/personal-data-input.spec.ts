import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDataInputComponent } from './personal-data-input.component';

describe('PersonalDataInputComponent', () => {
  let component: PersonalDataInputComponent;
  let fixture: ComponentFixture<PersonalDataInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonalDataInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDataInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
