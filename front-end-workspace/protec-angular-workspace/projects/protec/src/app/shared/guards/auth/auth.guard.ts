import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable()
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override router: Router,
    protected keycloak: KeycloakService
  ) {
    super(router, keycloak);
  }

  public async isAccessAllowed(router: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    if(!this.authenticated) {
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url,
      });
    }
    return this.authenticated;
  }
}
