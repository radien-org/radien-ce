import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeginComponent } from './begin.component';

describe('BeginComponent', () => {
  let component: BeginComponent;
  let fixture: ComponentFixture<BeginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BeginComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BeginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
