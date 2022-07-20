import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeAreSorryComponent } from './we-are-sorry.component';

describe('WeAreSorryComponent', () => {
  let component: WeAreSorryComponent;
  let fixture: ComponentFixture<WeAreSorryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WeAreSorryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WeAreSorryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
