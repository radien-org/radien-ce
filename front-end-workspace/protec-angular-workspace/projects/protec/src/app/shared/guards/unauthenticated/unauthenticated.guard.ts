import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, CanLoad, Router } from '@angular/router';
import { AuthenticationService } from '../../services/authentication/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class UnauthenticatedGuard implements CanActivate, CanActivateChild, CanLoad {
  constructor(private readonly authenticationService: AuthenticationService, private readonly router: Router) {}

  public canActivate(): boolean {
    if (this.authenticationService.currentUser) {
      //:TODO
      this.router.navigate(['home']);

      return false;
    }
    return true;
  }

  public canActivateChild(): boolean {
    return this.canActivate();
  }

  public canLoad(): boolean {
    return this.canActivate();
  }
}
