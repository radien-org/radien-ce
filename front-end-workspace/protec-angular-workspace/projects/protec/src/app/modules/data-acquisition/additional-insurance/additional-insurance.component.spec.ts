import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditionalInsuranceComponent } from './additional-insurance.component';

describe('AdditionalInsuranceComponent', () => {
  let component: AdditionalInsuranceComponent;
  let fixture: ComponentFixture<AdditionalInsuranceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdditionalInsuranceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdditionalInsuranceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
