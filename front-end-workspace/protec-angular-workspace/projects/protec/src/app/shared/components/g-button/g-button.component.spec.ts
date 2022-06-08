import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GButtonComponent } from './g-button.component';

describe('GButtonComponent', () => {
  let component: GButtonComponent;
  let fixture: ComponentFixture<GButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
