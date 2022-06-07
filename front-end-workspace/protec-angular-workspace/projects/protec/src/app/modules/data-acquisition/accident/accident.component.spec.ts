import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccidentComponent } from './accident.component';

describe('AccidentComponent', () => {
  let component: AccidentComponent;
  let fixture: ComponentFixture<AccidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccidentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
