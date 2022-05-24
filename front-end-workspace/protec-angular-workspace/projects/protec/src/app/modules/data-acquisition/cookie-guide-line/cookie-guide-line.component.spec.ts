import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookieGuideLineComponent } from './cookie-guide-line.component';

describe('CookieGuideLineComponent', () => {
  let component: CookieGuideLineComponent;
  let fixture: ComponentFixture<CookieGuideLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CookieGuideLineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CookieGuideLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
