import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidenavBackdropComponent } from './sidenav-backdrop.component';

describe('SidenavBackdropComponent', () => {
  let component: SidenavBackdropComponent;
  let fixture: ComponentFixture<SidenavBackdropComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SidenavBackdropComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidenavBackdropComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
