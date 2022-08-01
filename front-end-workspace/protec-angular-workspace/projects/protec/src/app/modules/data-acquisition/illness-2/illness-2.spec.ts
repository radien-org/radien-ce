import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Illness2Component } from './illness-2.component';

describe('Illness2Component', () => {
  let component: Illness2Component;
  let fixture: ComponentFixture<Illness2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Illness2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Illness2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
