import { HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../services';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {
  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly translate: TranslateService) {}

  public intercept(req: HttpRequest<any>, next: HttpHandler) {
    //let headers = new HttpHeaders().set('Content-Type', environment.contentType);
    let headers = req.headers;

    const jwtToken = this.authenticationService.jwtToken;

    const token = this.authenticationService.token;
    headers.set('Cache-Control', 'no-cache').set('Pragma', 'no-cache');
    if(this.translate.currentLang){
      headers = headers.set('lang', this.translate.currentLang);
    }

    const request = req.clone({
      headers,
    });
    return next.handle(request);
  }
}
