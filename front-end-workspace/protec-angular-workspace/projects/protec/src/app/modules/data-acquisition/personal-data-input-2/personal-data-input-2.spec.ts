import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDataInputComponentTwo } from './personal-data-input-2.component';

describe('PersonalDataInputComponentTwo', () => {
  let component: PersonalDataInputComponentTwo;
  let fixture: ComponentFixture<PersonalDataInputComponentTwo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonalDataInputComponentTwo ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDataInputComponentTwo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
