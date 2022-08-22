import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { Tenant } from 'src/app/models/tenant/Tenant';
import TenantFactory from 'src/app/models/tenant/TenantFactory';

@Injectable({
  providedIn: 'root'
})
export class TenantRoleUserService {
  private serviceURL: string = "/api/role/tenantRoleUser";

  constructor(private keycloakService: KeycloakService,
              private tenantFactory: TenantFactory
             ) { }

  async getTenants(userId: string, roleId?: string): Promise<Tenant[]> {
    let url: string = `${this.serviceURL}/tenants?`;
    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });

    let queryParams = new URLSearchParams({
      userId: userId
    });
    if(roleId) {
      queryParams.append("roleId", roleId);
    }

    return fetch(url + queryParams, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${bearer}`
      }
    }).then(res => {
      if(res.ok) {
        return res.json();
      } else {
        throw new ReferenceError(JSON.stringify(res));
      }
    }).then(body => {
      let resultsArray = body as any[];
      return resultsArray.map(obj => { return this.tenantFactory.convert(obj); });
    }).catch(e => {
      console.log(e);
      return [];
    })
  } 
}
