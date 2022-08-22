import { Injectable } from "@angular/core";
import { TenantRoleService } from "../role/tenantRole/tenant-role.service";
import { PermissionService } from "./permission.service";

@Injectable({
  providedIn: 'root'
})
export class AuthorizationCheckService {
  constructor(private permissionService: PermissionService,
             private tenantRoleService: TenantRoleService) {
  }

  public async hasPermissionAccess(resource: string, action: string, userId?: number, tenant?: string): Promise<boolean> {
    let permissionId = await this.permissionService.getPermissionIdByResourceAndAction(resource, action);
    let result = false;
    if(permissionId) {
      result = await this.hasGrant(permissionId, tenant, userId);
    }

    let permissionForAll = await this.permissionService.getPermissionIdByResourceAndAction(resource, "ALL");
    if(permissionForAll) {
      result = await this.hasGrant(permissionForAll, tenant, userId);
    }

    return result;
  }

  private async hasGrant(permissionId: number, tenantId?: string, userId?: number): Promise<boolean> {
    if(userId) {
      return this.tenantRoleService.hasPermission(userId, permissionId, tenantId); 
    }
    return false;
  }
}
