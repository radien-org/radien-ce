import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AuthenticationService } from '../services';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private readonly toastr: ToastrService,
    private readonly authenticationService: AuthenticationService,
    private readonly toastService: ToastrService,
    private readonly translate: TranslateService,
    private readonly router: Router
  ) {}

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      tap((response: any) => {
        const body = response.body;

        if (body && body.inError) {
          throw new HttpErrorResponse({
            status: body.responseCode,
            statusText: body.message,
            url: response.url,
          });
        }
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = '';

        if (error.status === 498 || error.status === 401) {
          // TODO: Test the 498 Error Code
          console.log(error);
          
          if(error.url.search('login') === -1){
            if(this.authenticationService.token || error.error.errorCode == 99){
              this.authenticationService.logout();
              if(!this.toastService.findDuplicate(this.translate.instant('auth.authExpired'), true, false)){
                this.toastService.error(this.translate.instant('auth.authExpired'));
              }
              return throwError(error);
            }
          }
          else{
            return throwError(error);
          }
          this.router.navigate(['/']);
        } else if (error.error instanceof ErrorEvent) {
          // client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
          errorMessage = `Error Code: ${error.status} Message: ${error.message}`;

          if(!this.toastService.findDuplicate(errorMessage, true, false)){
            this.toastService.error(errorMessage);
          }
        }

        //return throwError(errorMessage);
        return throwError(error);
      })
    );
  }
}
