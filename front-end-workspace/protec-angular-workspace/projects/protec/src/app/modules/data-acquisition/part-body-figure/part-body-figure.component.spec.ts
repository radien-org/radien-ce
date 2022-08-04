import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PartBodyFigureComponent } from './part-body-figure.component';

describe('PartBodyFigureComponent', () => {
  let component: PartBodyFigureComponent;
  let fixture: ComponentFixture<PartBodyFigureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PartBodyFigureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PartBodyFigureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
