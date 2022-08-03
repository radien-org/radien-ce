import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsIntroComponent } from './details-intro.component';

describe('DetailsIntroComponent', () => {
  let component: DetailsIntroComponent;
  let fixture: ComponentFixture<DetailsIntroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailsIntroComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsIntroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
