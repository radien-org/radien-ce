import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private serviceURL: string = "/api/permission";

  constructor(private keycloakService: KeycloakService) { }

  async getPermissionIdByResourceAndAction(resourceId: string, actionId: string): Promise<number | undefined> {
    let url: string = `${this.serviceURL}/id?`;
    let queryparams = new URLSearchParams({
      "resource": resourceId,
      "action": actionId
    });

    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });
    return fetch(url + queryparams, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${bearer}`
      }
    }).then(res => {
      if(res.ok) {
        return res.json();
      }
      return undefined;
    }).then(result => {
      return result;
    });
  }
}
