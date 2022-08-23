import { Injectable } from '@angular/core';
import { CanLoad, Route, UrlSegment, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthorizationCheckService } from '../services/permission/authorization-check-service';
import { SessionService } from '../services/session/session.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanLoad {
  constructor(private authzChecker: AuthorizationCheckService,
             private sessionService: SessionService) {
  }

  canLoad(
    route: Route,
    segments: UrlSegment[]): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      if(route.data) {
        let resource = route.data['resource'];
        let action = route.data['action'];
        
        return this.authzChecker.hasPermissionAccess(resource, action, this.sessionService.activeTenant?.id);
      }
      console.log("MISSING PERMISSION INFO " + route);
      return false;
  }

  
}
