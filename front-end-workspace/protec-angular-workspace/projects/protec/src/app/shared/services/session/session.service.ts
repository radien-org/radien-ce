import { Injectable } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import ActiveTenant from '../../models/tenant/activeTenant/ActiveTenant';
import { Tenant } from '../../models/tenant/Tenant';
import User from '../../models/user/User';
import { AuthorizationCheckService } from '../permission/authorization-check-service';
import { TenantRoleUserService } from '../role/tenantRoleUser/tenant-role-user.service';
import { ActiveTenantService } from '../tenant/activetenant/active-tenant.service';
import { UserService } from '../user/user.service';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  loggedIn: boolean = false;
  activeTenant?: Tenant;
  activeTenantObject?: ActiveTenant;
  availableTenants: Tenant[] = [];
  permissions: Map<[string, string], boolean> = new Map();
  user?: User;
  
  private initialized: boolean = false;

  constructor(
    private keycloak: AuthService,
    private userService: UserService,
    private tenantRoleUserService: TenantRoleUserService,
    private activeTenantService: ActiveTenantService) {}

  init() {
    if(!this.initialized) {
      let loggedUser = this.keycloak.getLoggedUser();
      if(loggedUser) {
        this.loggedIn = true;
        this.userService.getAllUsers(loggedUser ? loggedUser['email'] : undefined )
          .then(result => {
            this.user = result.results[0];
            this.tenantRoleUserService.getTenants(String(this.user.id))
              .then(result => {
                this.availableTenants = result;
                this.activeTenantService.getActiveTenantByUser(String(this.user?.id))
                  .then(result => {
                    if(result) {
                      this.activeTenantObject = result;
                      this.activeTenant = this.availableTenants.filter(t => Number(t.id) == result.tenantId)[0];
                    } else {
                      this.activeTenant = this.availableTenants[0];
                      this.activeTenantObject = new ActiveTenant(Number(this.user?.id), Number(this.activeTenant.id));
                      this.activeTenantService.saveActiveTenant(this.activeTenantObject);
                    }
              });
            });
        });
        this.initialized = true;
      }
    }
  }

  updateActiveTenant() {
    if(this.activeTenantObject && this.activeTenant) {
      this.activeTenantObject.tenantId = Number(this.activeTenant.id);
      this.activeTenantService.saveActiveTenant(this.activeTenantObject);
    }
  }


  destroy() {
    this.loggedIn = false;
    this.activeTenant = undefined;
    this.availableTenants = [];
    this.user = undefined;
  }
}
