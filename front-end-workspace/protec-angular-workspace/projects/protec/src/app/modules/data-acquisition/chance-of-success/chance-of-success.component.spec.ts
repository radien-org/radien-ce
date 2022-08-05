import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChanceOfSuccessComponent } from './chance-of-success.component';

describe('ChanceOfSuccessComponent', () => {
  let component: ChanceOfSuccessComponent;
  let fixture: ComponentFixture<ChanceOfSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChanceOfSuccessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChanceOfSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
