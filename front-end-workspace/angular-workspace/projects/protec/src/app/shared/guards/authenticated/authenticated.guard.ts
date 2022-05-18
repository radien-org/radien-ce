import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, CanLoad } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { AuthenticationService } from '../../services/authentication/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class AuthenticatedGuard implements CanActivate, CanActivateChild, CanLoad {
  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly toastService: ToastrService,
    private readonly translate: TranslateService
  ) {}

  public canActivate(): boolean {
    if (this.authenticationService.currentUser) {return true;}

    this.authenticationService.logout();

    if(!this.toastService.findDuplicate(this.translate.instant('generic.please_login_to_access'), true, false)){
      this.toastService.warning(this.translate.instant('generic.please_login_to_access'));
    }

    return false;
  }

  public canActivateChild(): boolean {
    return this.canActivate();
  }

  public canLoad(): boolean {
    return this.canActivate();
  }
}
