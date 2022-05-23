import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderDesktopComponent } from './header-desktop.component';

describe('HeaderDesktopComponent', () => {
  let component: HeaderDesktopComponent;
  let fixture: ComponentFixture<HeaderDesktopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeaderDesktopComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderDesktopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
