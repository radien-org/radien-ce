import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccidentTypeComponent } from './accident-type.component';

describe('AccidentTypeComponent', () => {
  let component: AccidentTypeComponent;
  let fixture: ComponentFixture<AccidentTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccidentTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccidentTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
