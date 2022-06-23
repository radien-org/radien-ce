import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DieDetailsComponent } from './die-details.component';

describe('DieDetailsComponent', () => {
  let component: DieDetailsComponent;
  let fixture: ComponentFixture<DieDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DieDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DieDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
