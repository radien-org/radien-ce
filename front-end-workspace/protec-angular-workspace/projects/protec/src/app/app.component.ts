import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from '../app/shared/services/cookie/cookie.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'protec';

  showCookieWindow = this.cookieService.getInLocal();
  constructor(private readonly translationService: TranslateService, private readonly cookieService : CookieService) {
    console.log('cookie status:', this.cookieService.getInLocal())
  }
  changeVarCookieWindow(flag=false) {
    this.showCookieWindow = flag;
  }
}