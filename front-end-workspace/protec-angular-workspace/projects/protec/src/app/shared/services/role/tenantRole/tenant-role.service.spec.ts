import { TestBed } from '@angular/core/testing';

import { TenantRoleService } from './tenant-role.service';

describe('TenantRoleService', () => {
  let service: TenantRoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TenantRoleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
