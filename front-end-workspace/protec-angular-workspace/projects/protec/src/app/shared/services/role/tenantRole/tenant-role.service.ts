import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class TenantRoleService {

  private serviceUrl: string = "/api/role/tenantrole";

  constructor(private keycloakService: KeycloakService) { }

  async hasPermission(userId: number, permissionId: number, tenantId?: string): Promise<boolean> {
    let url: string = `${this.serviceUrl}/exists/permission?`;
    let queryParams = new URLSearchParams({
      userId: String(userId),
      permissionId: String(permissionId),
    });
    if(tenantId) {
      queryParams.append(
        "tenantId", String(tenantId)
      );
    }

    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });

    return fetch(url + queryParams, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${bearer}`,
      }
    }).then(res => {
      if(res.ok) {
        return res.json();
      } else {
        console.log(res);
        return false;
      }
    }).then(result => {
      console.log(result);
      return result;
    });
  }
}
