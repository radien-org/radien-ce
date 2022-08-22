import { TestBed } from '@angular/core/testing';

import { ActiveTenantService } from './active-tenant.service';

describe('ActiveTenantService', () => {
  let service: ActiveTenantService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActiveTenantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
