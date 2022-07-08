import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AGBComponent } from './AGB.component';

describe('AGBComponent', () => {
  let component: AGBComponent;
  let fixture: ComponentFixture<AGBComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AGBComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AGBComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
