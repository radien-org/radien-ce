import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProspectsOfSuccess } from './prospects-of-success.component';

describe('ProspectsOfSuccess', () => {
  let component: ProspectsOfSuccess;
  let fixture: ComponentFixture<ProspectsOfSuccess>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProspectsOfSuccess ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProspectsOfSuccess);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
