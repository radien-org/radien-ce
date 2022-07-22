import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IllnessComponent } from './illness.component';

describe('IllnessComponent', () => {
  let component: IllnessComponent;
  let fixture: ComponentFixture<IllnessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IllnessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IllnessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
