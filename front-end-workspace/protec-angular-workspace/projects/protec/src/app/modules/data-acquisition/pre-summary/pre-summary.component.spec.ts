import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreSummaryComponent } from './pre-summary.component';

describe('PreSummaryComponent', () => {
  let component: PreSummaryComponent;
  let fixture: ComponentFixture<PreSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
