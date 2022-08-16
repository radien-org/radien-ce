import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from '../../../shared/services/cookie/cookie.service';

@Component({
  selector: 'app-request-send',
  templateUrl: './request-send.component.html',
  styleUrls: ['./request-send.component.scss']
})
export class RequestSendComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('MEIN KONTO'),
    type: 'outline',
    link: '/data-acquisition/pre-summary'
  }

  checks = {
    color: 'primary'
  }


  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: []
    }
  }

  showCookieWindow = this.cookieService.getInLocal();

  cookieGuideLink = 'cookie-guide-line';

  constructor(private readonly translationService: TranslateService, private readonly cookieService : CookieService) {
    console.log('cookie status:', this.cookieService.getInLocal())
  }

  ngOnInit(): void {}

  changeVarCookieWindow(flag=false) {
    this.showCookieWindow = flag;
  }

}
