import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BodyFigureComponent } from './body-figure.component';

describe('BodyFigureComponent', () => {
  let component: BodyFigureComponent;
  let fixture: ComponentFixture<BodyFigureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BodyFigureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BodyFigureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
