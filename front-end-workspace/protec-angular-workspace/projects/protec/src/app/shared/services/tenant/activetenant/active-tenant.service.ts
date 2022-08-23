import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import ActiveTenant from 'src/app/models/tenant/activeTenant/ActiveTenant';
import ActiveTenantFactory from 'src/app/models/tenant/activeTenant/ActiveTenantFactory';

@Injectable({
  providedIn: 'root'
})
export class ActiveTenantService {

  private serviceURL : string = "/api/tenant/activeTenant"

  constructor(private keycloakService: KeycloakService,
             private activeTenantFactory: ActiveTenantFactory) { }

  async getActiveTenantByUser(userId: string): Promise<ActiveTenant | undefined> {
    let url: string = `${this.serviceURL}/find?`;
    let bearer: string = "";

    let queryParams = new URLSearchParams({
      userId: userId,
      isLogicalConjunction: "true",
    });

    await this.keycloakService.getToken().then(token => { bearer = token });
    
    return fetch(url + queryParams, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${bearer}`,
      }
    }).then(res => {
      if(res.ok) {
        return res.json();
      } else if(res.status == 404) {
        return undefined;
      } else {
        throw new ReferenceError(String(res.headers));
      }
    }).then(results => {
      if(results) {
        let resultsArray = results as any[];
        return this.activeTenantFactory.convert(resultsArray[0]);
      } else {
        return undefined;
      }
    }).catch(e => {
      console.log(e);
      return undefined;
    })
  }

  async saveActiveTenant(activeTenant: ActiveTenant): Promise<boolean> {
    if(activeTenant.id) {
      return this.updateActiveTenant(activeTenant); 
    } else {
      return this.createActiveTenant(activeTenant);
    } 
  }


  private async createActiveTenant(activeTenant: ActiveTenant): Promise<boolean> {
    let url: string = `${this.serviceURL}`;

    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });
    
    return fetch(url, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${bearer}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(activeTenant),
    }).then(res => {
      if(res.ok) {
        return true;;
      } else {
        return false;
      }
    }).catch(e => {
      console.log(e);
      return false;
    })
  }

  private async updateActiveTenant(activeTenant: ActiveTenant): Promise<boolean> {
    let url: string = `${this.serviceURL}/${activeTenant.id}`;

    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });
    
    return fetch(url, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${bearer}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(activeTenant),
    }).then(res => {
      console.log(res);
      if(res.ok) {
        return true;;
      } else {
        return false;
      }
    }).catch(e => {
      console.log(e);
      return false;
    })
  }
}
