import { TestBed } from '@angular/core/testing';
import { TenantRoleUserService } from './tenantRoleUser.service';


describe('TenantroleuserService', () => {
  let service: TenantRoleUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TenantRoleUserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
