import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDataSummaryComponent } from './personal-data-summary.component';

describe('PersonalDataSummaryComponent', () => {
  let component: PersonalDataSummaryComponent;
  let fixture: ComponentFixture<PersonalDataSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonalDataSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDataSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
