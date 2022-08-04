import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestQuoteComponent } from './request-quote.component';

describe('RequestQuoteComponent', () => {
  let component: RequestQuoteComponent;
  let fixture: ComponentFixture<RequestQuoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestQuoteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestQuoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
