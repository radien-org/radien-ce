import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDataInformationComponent } from './personal-data-information.component';

describe('PersonalDataInformationComponent', () => {
  let component: PersonalDataInformationComponent;
  let fixture: ComponentFixture<PersonalDataInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonalDataInformationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDataInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
