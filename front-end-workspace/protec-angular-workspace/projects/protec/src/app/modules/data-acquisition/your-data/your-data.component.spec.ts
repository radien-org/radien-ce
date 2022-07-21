import { ComponentFixture, TestBed } from '@angular/core/testing';

import { YourDataComponent } from './your-data.component';

describe('YourDataComponent', () => {
  let component: YourDataComponent;
  let fixture: ComponentFixture<YourDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ YourDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(YourDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
