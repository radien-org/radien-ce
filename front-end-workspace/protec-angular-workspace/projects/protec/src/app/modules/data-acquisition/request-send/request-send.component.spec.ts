import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestSendComponent } from './request-send.component';

describe('RequestSendComponent', () => {
  let component: RequestSendComponent;
  let fixture: ComponentFixture<RequestSendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestSendComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestSendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
