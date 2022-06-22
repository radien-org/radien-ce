import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LegalDataPolicyComponent } from './legal-data-policy.component';

describe('LegalDataPolicyComponent', () => {
  let component: LegalDataPolicyComponent;
  let fixture: ComponentFixture<LegalDataPolicyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LegalDataPolicyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalDataPolicyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
