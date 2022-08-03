import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsFigureComponent } from './details-figure.component';

describe('DetailsFigureComponent', () => {
  let component: DetailsFigureComponent;
  let fixture: ComponentFixture<DetailsFigureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailsFigureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsFigureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
