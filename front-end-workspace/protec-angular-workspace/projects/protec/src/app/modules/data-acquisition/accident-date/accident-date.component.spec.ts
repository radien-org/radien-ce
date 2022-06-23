import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccidentDateComponent } from './accident-date.component';

describe('AccidentDateComponent', () => {
  let component: AccidentDateComponent;
  let fixture: ComponentFixture<AccidentDateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccidentDateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccidentDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
