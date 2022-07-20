import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreInjuriesComponent } from './more-injuries.component';

describe('MoreInjuriesComponent', () => {
  let component: MoreInjuriesComponent;
  let fixture: ComponentFixture<MoreInjuriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoreInjuriesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoreInjuriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
