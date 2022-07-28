import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateAccidentComponent } from './private-accident.component';

describe('PrivateAccidentComponent', () => {
  let component: PrivateAccidentComponent;
  let fixture: ComponentFixture<PrivateAccidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrivateAccidentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrivateAccidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
