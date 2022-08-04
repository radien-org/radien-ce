import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppreciationComponent } from './appreciation.component';

describe('AppreciationComponent', () => {
  let component: AppreciationComponent;
  let fixture: ComponentFixture<AppreciationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppreciationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppreciationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
