import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkAccidentComponent } from './work-accident.component';

describe('WorkAccidentComponent', () => {
  let component: WorkAccidentComponent;
  let fixture: ComponentFixture<WorkAccidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkAccidentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkAccidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
